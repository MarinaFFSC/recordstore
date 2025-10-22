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
public class StepsBloqueioAutomaticoSocio {

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
    @Dado("que o sócio {string} está com status {string} e sem multas")
    public void socioSemMultas(String nome, String status) {
        socio = new Socio(); socio.setNome(nome); socio.setEmail(uniqueEmail(nome));
        socio.setStatus(status.equalsIgnoreCase("ativo")? StatusSocio.ATIVO : StatusSocio.BLOQUEADO);
        socio = socioRepo.save(socio);
        empRepo.findBySocioId(socio.getId()).forEach(e -> { if (e.getMulta()!=null){ e.getMulta().setPaga(true); empRepo.save(e);} });
    }
    @Dado("ocorre uma devolução com atraso gerando multa pendente")
    public void devolucaoComAtrasoGerandoMulta() {
        midia = ensureMidia("Vinil - Kind of Blue");
        exemplar = ensureExemplarDisponivel(midia);
        emprestimo = emprestimoService.realizarEmprestimo(socio.getId(), midia.getId());
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().minusDays(1));
        emprestimo.setDataDevolucaoReal(LocalDate.now());
        empRepo.save(emprestimo);
        try {
            emprestimoService.registrarDevolucao(emprestimo.getId(), LocalDate.now(), CondicaoExemplar.BOM);
        } catch (BusinessException e) { mensagemErro = e.getMessage(); }
        socio = socioRepo.findById(socio.getId()).orElseThrow();
    }

    @Dado("que o sócio {string} está {string} por multa não paga")
    public void socioBloqueadoPorMulta(String nome, String status) {
        socio = new Socio(); socio.setNome(nome); socio.setEmail(uniqueEmail(nome));
        socio.setStatus(status.equalsIgnoreCase("bloqueado")? StatusSocio.BLOQUEADO : StatusSocio.ATIVO);
        socio = socioRepo.save(socio);
    }
    @Dado("existe uma mídia {string} com status {string}")
    public void existeMidia(String titulo, String status) {
        midia = ensureMidia(titulo.contains(" - ")?titulo.split(" - ",2)[1]:titulo);
        exemplar = ensureExemplarDisponivel(midia);
    }

    // WHEN
    @Quando("o sistema registrar a multa no empréstimo")
    public void sistemaRegistraMulta() {
        try {
            emprestimo.setDataPrevistaDevolucao(LocalDate.now().minusDays(3));
            emprestimo.setDataDevolucaoReal(LocalDate.now());
            empRepo.save(emprestimo);
            emprestimoService.registrarDevolucao(emprestimo.getId(), LocalDate.now(), CondicaoExemplar.BOM);
        } catch (BusinessException e) { mensagemErro = e.getMessage(); }
        socio = socioRepo.findById(socio.getId()).orElseThrow();
    }

    @Quando("o administrador solicita o aluguel da mídia")
    public void solicitaAluguelMidia() {
        try {
            emprestimo = emprestimoService.realizarEmprestimo(socio.getId(), midia.getId());
        } catch (BusinessException e) { mensagemErro = e.getMessage(); }
    }

    // THEN
    @Então("o sistema deve atualizar o status do sócio para {string}")
    public void atualizarStatusSocio(String status) {
        socio = socioRepo.findById(socio.getId()).orElseThrow();
        if (status.equalsIgnoreCase("bloqueado")) {
            org.junit.jupiter.api.Assertions.assertEquals(StatusSocio.BLOQUEADO, socio.getStatus());
        } else if (status.equalsIgnoreCase("ativo")){
            org.junit.jupiter.api.Assertions.assertEquals(StatusSocio.ATIVO, socio.getStatus());
        }
    }
    @Então("impedir novas locações e renovações")
    public void impedirNovasOperacoes() { Assertions.assertEquals(StatusSocio.BLOQUEADO, socio.getStatus()); }

    @Então("o sistema deve recusar a operação")
    public void recusarOperacao() { Assertions.assertNotNull(mensagemErro); }

    @Então("exibir a mensagem {string}")
    public void exibirMensagem(String msg) {
        Assertions.assertNotNull(mensagemErro);
        Assertions.assertTrue(mensagemErro.contains(msg));
    }
}
