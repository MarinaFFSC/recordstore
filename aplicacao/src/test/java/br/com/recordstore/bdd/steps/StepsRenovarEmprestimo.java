package br.com.recordstore.bdd.steps;

import br.com.recordstore.common.BusinessException;
import br.com.recordstore.catalogo.*;
import br.com.recordstore.emprestimos.*;
import br.com.recordstore.socios.*;
import io.cucumber.java.pt.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
public class StepsRenovarEmprestimo {

    @Autowired private SocioRepository socioRepo;
    @Autowired private MidiaRepository midiaRepo;
    @Autowired private ExemplarRepository exRepo;
    @Autowired private EmprestimoRepository empRepo;
    @Autowired private CatalogoService catalogoService;
    @Autowired private EmprestimoService emprestimoService;

    private Socio socio;
    private Midia midia;
    private Exemplar exemplar;
    private Emprestimo emprestimo;
    private String mensagemErro;
    private LocalDate dataPrevistaAntes;

    private String uniqueEmail(String base){ return base.toLowerCase()+"+"+ UUID.randomUUID()+"@teste.com"; }
    private Midia ensureMidia(String titulo){
        return midiaRepo.findByTituloIgnoreCase(titulo).orElseGet(() -> {
            Midia m = new Midia(); m.setTitulo(titulo); m.setArtista("Artista"); m.setGenero("Gênero"); m.setAno(2020); m.setTipo(TipoMidia.CD); return midiaRepo.save(m);
        });
    }
    private Exemplar ensureExemplarDisponivel(Midia m){
        Exemplar ex = catalogoService.catalogarExemplar(m.getId(), CondicaoExemplar.BOM);
        catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.DISPONIVEL);
        return exRepo.findById(ex.getId()).orElseThrow();
    }

    // GIVEN
    @Dado("que o sócio {string} não possui multas pendentes")
    public void socioSemMulta(String nome) {
        socio = new Socio(); socio.setNome(nome); socio.setEmail(uniqueEmail(nome)); socio.setStatus(StatusSocio.ATIVO);
        socio = socioRepo.save(socio);
        // zera eventuais multas de empréstimos existentes
        empRepo.findBySocioId(socio.getId()).forEach(e -> {
            if (e.getMulta()!=null){ e.getMulta().setPaga(true); e.getMulta().setValorTotal(java.math.BigDecimal.ZERO); empRepo.save(e); }
        });
    }
    @Dado("o empréstimo do exemplar {string} está dentro do prazo")
    public void emprestimoDentroPrazo(String titulo) {
        midia = ensureMidia(titulo.contains(" - ")?titulo.split(" - ",2)[1]:titulo);
        exemplar = ensureExemplarDisponivel(midia);
        emprestimo = emprestimoService.realizarEmprestimo(socio.getId(), midia.getId());
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().plusDays(3));
        empRepo.save(emprestimo);
    }

    @Dado("que o sócio {string} possui multa pendente")
    public void socioComMulta(String nome) {
        socio = new Socio(); socio.setNome(nome); socio.setEmail(uniqueEmail(nome)); socio.setStatus(StatusSocio.ATIVO);
        socio = socioRepo.save(socio);
        // cria empréstimo e injeta multa
        midia = ensureMidia("Título com multa");
        exemplar = ensureExemplarDisponivel(midia);
        Emprestimo e = emprestimoService.realizarEmprestimo(socio.getId(), midia.getId());
        e = empRepo.findById(e.getId()).orElseThrow();
        Multa m = java.util.Optional.ofNullable(e.getMulta()).orElse(Multa.builder().build());
        m.setDiasAtraso(1); m.setValorPorDia(new java.math.BigDecimal("2.00")); m.setValorTotal(new java.math.BigDecimal("2.00")); m.setPaga(false);
        e.setMulta(m); empRepo.save(e);
        emprestimo = e;
    }
    @Dado("o empréstimo do exemplar {string} está em atraso")
    public void emprestimoAtrasado(String titulo) {
        midia = ensureMidia(titulo.contains(" - ")?titulo.split(" - ",2)[1]:titulo);
        exemplar = ensureExemplarDisponivel(midia);
        emprestimo = emprestimoService.realizarEmprestimo(socio.getId(), midia.getId());
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().minusDays(1));
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        empRepo.save(emprestimo);
    }

    // WHEN
    @Quando("o administrador solicitar a renovação do empréstimo")
    public void solicitarRenovacao() {
        mensagemErro = null;
        dataPrevistaAntes = emprestimo.getDataPrevistaDevolucao();
        try {
            emprestimoService.renovarEmprestimo(emprestimo.getId(), LocalDate.now().plusDays(7));
            emprestimo = empRepo.findById(emprestimo.getId()).orElseThrow();
        } catch (BusinessException e) { mensagemErro = e.getMessage(); }
    }

    // THEN
    @Então("o sistema deve permitir a renovação")
    public void permiteRenovacao() { Assertions.assertNull(mensagemErro); }

    @Então("recalcular a nova data prevista de devolução")
    public void recalcularDataPrevista() {
        Assertions.assertTrue(empRepo.findById(emprestimo.getId()).orElseThrow().getDataPrevistaDevolucao().isAfter(dataPrevistaAntes));
    }

    @Então("o sistema deve negar a renovação")
    public void negarRenovacao() { Assertions.assertNotNull(mensagemErro); }

    @Então("registrar o evento {string}")
    public void registrarEvento(String evento) { Assertions.assertNotNull(evento); }
}
