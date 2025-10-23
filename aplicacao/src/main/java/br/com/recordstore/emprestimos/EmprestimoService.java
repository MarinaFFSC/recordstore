package br.com.recordstore.emprestimos;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.recordstore.common.BusinessException;
import br.com.recordstore.socios.Socio;
import br.com.recordstore.socios.SocioRepository;
import br.com.recordstore.socios.StatusSocio;
import br.com.recordstore.catalogo.Exemplar;
import br.com.recordstore.catalogo.ExemplarRepository;
import br.com.recordstore.catalogo.StatusExemplar;
import br.com.recordstore.catalogo.CondicaoExemplar;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

@Service
public class EmprestimoService {

  private final EmprestimoRepository repo;
  private final SocioRepository socioRepo;
  private final ExemplarRepository exRepo;

  // Limite usado pelos testes (3)
  private static final int LIMITE_ATIVOS_POR_SOCIO = 3;

  public EmprestimoService(EmprestimoRepository r, SocioRepository s, ExemplarRepository e){
    this.repo = r;
    this.socioRepo = s;
    this.exRepo = e;
  }

  @Transactional
  public Emprestimo realizarEmprestimo(Long socioId, Long midiaId){
    Socio socio = socioRepo.findById(socioId)
        .orElseThrow(() -> new BusinessException("Sócio não encontrado"));

    // Mensagem EXATA esperada quando o sócio já está bloqueado
    if (socio.getStatus() == StatusSocio.BLOQUEADO) {
      throw new BusinessException("Operação não permitida para sócio bloqueado");
    }

    // Se existir multa pendente, bloquear e devolver mensagem EXATA esperada
    if (repo.existsBySocioIdAndMultaPagaFalse(socioId)) {
      socio.setStatus(StatusSocio.BLOQUEADO);
      socioRepo.save(socio);
      throw new BusinessException("Sócio bloqueado por multa");
    }

    // Outras situações em que não pode realizar empréstimo
    if (!socio.podeRealizarEmprestimo()) {
      throw new BusinessException("Sócio não autorizado para locação");
    }

    long ativos = repo.countBySocioIdAndStatus(socioId, StatusEmprestimo.ATIVO);
    if (ativos >= LIMITE_ATIVOS_POR_SOCIO) {
      // Mensagem EXATA esperada pelos steps
      throw new BusinessException("Limite de empréstimos atingido");
    }

    Exemplar ex = exRepo.findFirstByMidiaIdAndStatus(midiaId, StatusExemplar.DISPONIVEL)
        .orElseThrow(() -> new BusinessException("Mídia indisponível para locação"));

    ex.marcarComoEmprestado();
    exRepo.save(ex);

    Emprestimo emp = Emprestimo.builder()
        .socio(socio)
        .exemplar(ex)
        .dataEmprestimo(LocalDate.now())
        .dataPrevistaDevolucao(LocalDate.now().plusDays(7))
        .status(StatusEmprestimo.ATIVO)
        .build();

    return repo.save(emp);
  }

  @Transactional
  public Emprestimo renovarEmprestimo(Long emprestimoId, LocalDate novaData) {
    Emprestimo e = repo.findById(emprestimoId)
        .orElseThrow(() -> new BusinessException("Empréstimo não encontrado"));

    Long socioId = e.getSocio().getId();
    boolean socioTemMultaPendente = repo.existsBySocioIdAndMultaPagaFalse(socioId);
    boolean estaMultaNaoPaga = (e.getMulta() != null && !Boolean.TRUE.equals(e.getMulta().getPaga()));

    if (socioTemMultaPendente || estaMultaNaoPaga) {
      // Os cenários de renovação aceitam esta mensagem
      throw new BusinessException("Renovação não permitida: multa pendente ou empréstimo em atraso");
    }

    LocalDate hoje = LocalDate.now();
    boolean emAtrasoPorData = (e.getDataPrevistaDevolucao() != null && hoje.isAfter(e.getDataPrevistaDevolucao()));
    boolean emAtrasoPorStatus = (e.getStatus() == StatusEmprestimo.ATRASADO);
    if (emAtrasoPorData || emAtrasoPorStatus) {
      throw new BusinessException("Renovação não permitida: multa pendente ou empréstimo em atraso");
    }

    try {
      e.renovar(novaData);
    } catch (IllegalStateException ex) {
      throw new BusinessException("Renovação não permitida: multa pendente ou empréstimo em atraso");
    }

    return repo.save(e);
  }

  @Transactional
  public Emprestimo registrarDevolucao(Long emprestimoId, LocalDate dataDevolucao, CondicaoExemplar condicao){
    Emprestimo e = repo.findById(emprestimoId)
        .orElseThrow(() -> new BusinessException("Empréstimo não encontrado"));

    // Mensagem EXATA exigida pelos cenários (02 e 08)
    if (condicao == null) {
      throw new BusinessException("Condição física obrigatória");
    }

    boolean danificado = (condicao == CondicaoExemplar.DANIFICADO);

    e.registrarDevolucao(dataDevolucao, danificado);
    e.calcularMulta(new BigDecimal("2.50"));

    // Bloqueio automático se gerou multa não paga
    if (e.getMulta() != null && !Boolean.TRUE.equals(e.getMulta().getPaga())) {
      var socio = e.getSocio();
      if (socio.getStatus() == StatusSocio.ATIVO) {
        socio.setStatus(StatusSocio.BLOQUEADO);
        socioRepo.save(socio);
      }
    }

    Exemplar ex = e.getExemplar();
    ex.atualizarCondicao(condicao);
    if (danificado) {
      ex.marcarComoIndisponivel();
    } else {
      ex.marcarComoDisponivel();
    }
    exRepo.save(ex);

    return repo.save(e);
  }

  public List<Emprestimo> listarAtivosEAtrasadosPorSocio(Long socioId){
    return repo.findBySocioIdAndStatusIn(
        socioId,
        java.util.List.of(StatusEmprestimo.ATIVO, StatusEmprestimo.ATRASADO)
    );
  }

  public List<Emprestimo> historicoPorSocio(Long socioId) {
    return repo.findBySocioId(socioId);
  }

  @Transactional
  public List<Emprestimo> historicoPorExemplar(Long exemplarId) {
    return repo.findByExemplarId(exemplarId);
  }

  @Transactional
  public List<Emprestimo> historicoPorMidia(Long midiaId) {
    List<Exemplar> exs = exRepo.findAllByMidiaId(midiaId);
    List<Emprestimo> out = new java.util.ArrayList<>();
    for (Exemplar ex : exs) {
      out.addAll(repo.findByExemplarId(ex.getId()));
    }
    return out;
  }
}
