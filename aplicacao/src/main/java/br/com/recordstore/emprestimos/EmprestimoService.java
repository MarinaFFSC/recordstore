package br.com.recordstore.emprestimos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.recordstore.common.BusinessException;
import br.com.recordstore.socios.*;
import br.com.recordstore.catalogo.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

@Service
public class EmprestimoService {
  private final EmprestimoRepository repo;
  private final SocioRepository socioRepo;
  private final ExemplarRepository exRepo;

  public EmprestimoService(EmprestimoRepository r, SocioRepository s, ExemplarRepository e){
    this.repo=r; this.socioRepo=s; this.exRepo=e;
  }

  @Transactional
  public Emprestimo realizarEmprestimo(Long socioId, Long midiaId){
    Socio socio = socioRepo.findById(socioId).orElseThrow(() -> new BusinessException("Sócio não encontrado"));
    if (!socio.podeRealizarEmprestimo()) throw new BusinessException("Sócio não está ativo");
    if (repo.existsBySocioIdAndMultaIsNotNull(socioId)) { socio.setStatus(StatusSocio.BLOQUEADO); socioRepo.save(socio); throw new BusinessException("Sócio possui multas não pagas (bloqueado automaticamente)"); }

    Exemplar ex = exRepo.findFirstByMidiaIdAndStatus(midiaId, StatusExemplar.DISPONIVEL)
      .orElseThrow(() -> new BusinessException("Não há exemplar disponível desta mídia"));

    ex.marcarComoEmprestado(); exRepo.save(ex);
    Emprestimo emp = Emprestimo.builder()
      .socio(socio).exemplar(ex)
      .dataEmprestimo(LocalDate.now())
      .dataPrevistaDevolucao(LocalDate.now().plusDays(7))
      .status(StatusEmprestimo.ATIVO)
      .build();
    return repo.save(emp);
  }

  @Transactional
  public Emprestimo renovarEmprestimo(Long emprestimoId, LocalDate novaData){
    Emprestimo e = repo.findById(emprestimoId).orElseThrow(() -> new BusinessException("Empréstimo não encontrado"));
    e.renovar(novaData);
    return repo.save(e);
  }

  @Transactional
  public Emprestimo registrarDevolucao(Long emprestimoId, LocalDate dataDevolucao, boolean danificado){
    Emprestimo e = repo.findById(emprestimoId).orElseThrow(() -> new BusinessException("Empréstimo não encontrado"));
    e.registrarDevolucao(dataDevolucao, danificado);
    e.calcularMulta(new BigDecimal("2.50"));
    Exemplar ex = e.getExemplar();
    if (danificado) ex.setCondicao(CondicaoExemplar.REGULAR);
    ex.marcarComoDisponivel();
    exRepo.save(ex);
    return repo.save(e);
  }

  public List<Emprestimo> listarAtivosEAtrasadosPorSocio(Long socioId){
    return repo.findBySocioIdAndStatusIn(socioId, java.util.List.of(StatusEmprestimo.ATIVO, StatusEmprestimo.ATRASADO));
  }

  public List<Emprestimo> historicoPorSocio(Long socioId){ return repo.findBySocioId(socioId); }
}
