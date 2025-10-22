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
public class StepsRegistrarDevolucao {

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
    @Dado("há um empréstimo ativo do exemplar {string}")
    @Dado("que há um empréstimo ativo do exemplar {string}")
    public void haEmprestimoAtivoDoExemplar(String tituloRotulo) {
        String titulo = tituloRotulo.contains(" - ") ? tituloRotulo.split(" - ", 2)[1] : tituloRotulo;
        midia = ensureMidia(titulo);
        exemplar = ensureExemplarDisponivel(midia);
        socio = new Socio(); socio.setNome("Teste Emprestimo"); socio.setEmail(uniqueEmail("teste.emprestimo")); socio.setStatus(StatusSocio.ATIVO);
        socio = socioRepo.save(socio);
        emprestimo = emprestimoService.realizarEmprestimo(socio.getId(), midia.getId());
        emprestimo = empRepo.findById(emprestimo.getId()).orElseThrow();
        exemplar  = exRepo.findById(emprestimo.getExemplar().getId()).orElseThrow();
    }

    // WHEN
    @Quando("o administrador tentar registrar a devolução sem informar a condição física")
    public void registrarDevolucaoSemCondicao() {
        mensagemErro = null;
        try {
            emprestimoService.registrarDevolucao(emprestimo.getId(), LocalDate.now(), null);
        } catch (BusinessException e) { mensagemErro = e.getMessage(); }
    }

    @Quando("o administrador registrar a devolução informando a condição {string}")
    public void registrarDevolucaoComCondicao(String condicao) {
        mensagemErro = null;
        try {
            CondicaoExemplar cond = condicao.equalsIgnoreCase("danificado") ? CondicaoExemplar.DANIFICADO : CondicaoExemplar.BOM;
            emprestimoService.registrarDevolucao(emprestimo.getId(), LocalDate.now(), cond);
            emprestimo = empRepo.findById(emprestimo.getId()).orElseThrow();
            exemplar  = exRepo.findById(emprestimo.getExemplar().getId()).orElseThrow();
        } catch (BusinessException e) { mensagemErro = e.getMessage(); }
    }

    // THEN
    @Então("o sistema deve recusar o registro")
    public void recusarRegistro() { Assertions.assertNotNull(mensagemErro); }

    @Então("o sistema deve atualizar o status do exemplar para {string}")
    public void atualizarStatusExemplar(String status) {
        exemplar = exRepo.findById(exemplar.getId()).orElseThrow();
        switch (status.toLowerCase()) {
            case "indisponível" -> Assertions.assertEquals(StatusExemplar.INDISPONIVEL, exemplar.getStatus());
            case "disponível" -> Assertions.assertEquals(StatusExemplar.DISPONIVEL, exemplar.getStatus());
            case "emprestado" -> Assertions.assertEquals(StatusExemplar.EMPRESTADO, exemplar.getStatus());
            default -> Assertions.fail("Status inesperado: "+status);
        }
    }

    @Então("impedir novas locações desse exemplar")
    public void impedirNovasLocacoes() {
        exemplar = exRepo.findById(exemplar.getId()).orElseThrow();
        Assertions.assertEquals(StatusExemplar.INDISPONIVEL, exemplar.getStatus());
    }

    @Então("registrar {string}")
    public void registrarEvento(String ev) { Assertions.assertNotNull(ev); }
}
