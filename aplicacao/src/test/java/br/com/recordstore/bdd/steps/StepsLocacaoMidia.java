package br.com.recordstore.bdd.steps;

import br.com.recordstore.common.BusinessException;
import br.com.recordstore.catalogo.*;
import br.com.recordstore.emprestimos.*;
import br.com.recordstore.socios.*;
import io.cucumber.java.pt.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
public class StepsLocacaoMidia {

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

    private String uniqueEmail(String base) {
        String norm = base.toLowerCase().replace(" ", ".")
                .replace("ã","a").replace("á","a").replace("â","a")
                .replace("é","e").replace("ê","e")
                .replace("í","i").replace("ó","o").replace("ô","o")
                .replace("ú","u").replace("ç","c");
        return norm + "+" + UUID.randomUUID() + "@teste.com";
    }

    private Midia ensureMidia(String titulo) {
        return midiaRepo.findByTituloIgnoreCase(titulo).orElseGet(() -> {
            Midia m = new Midia();
            m.setTitulo(titulo);
            m.setArtista("Artista");
            m.setGenero("Gênero");
            m.setAno(2020);
            m.setTipo(TipoMidia.CD);
            return midiaRepo.save(m);
        });
    }

    private Exemplar ensureExemplarDisponivel(Midia m) {
        Exemplar ex = catalogoService.catalogarExemplar(m.getId(), CondicaoExemplar.BOM);
        catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.DISPONIVEL);
        return exRepo.findById(ex.getId()).orElseThrow();
    }

    private void setStatusExemplar(String statusTexto) {
        switch (statusTexto.toLowerCase()) {
            case "disponível" -> catalogoService.atualizarStatusExemplar(exemplar.getId(), StatusExemplar.DISPONIVEL);
            case "alugada", "emprestado", "emprestada" -> catalogoService.atualizarStatusExemplar(exemplar.getId(), StatusExemplar.EMPRESTADO);
            case "indisponível" -> catalogoService.atualizarStatusExemplar(exemplar.getId(), StatusExemplar.INDISPONIVEL);
            default -> throw new IllegalArgumentException("Status de mídia/exemplar inválido: " + statusTexto);
        }
        exemplar = exRepo.findById(exemplar.getId()).orElseThrow();
    }

    private void setStatusSocio(String statusTexto) {
        switch (statusTexto.toLowerCase()) {
            case "ativo" -> socio.setStatus(StatusSocio.ATIVO);
            case "bloqueado" -> socio.setStatus(StatusSocio.BLOQUEADO);
            case "suspenso" -> socio.setStatus(StatusSocio.SUSPENSO);
            default -> throw new IllegalArgumentException("Status de sócio inválido: " + statusTexto);
        }
        socio = socioRepo.save(socio);
    }

    // GIVENs
    @Dado("que existe uma mídia {string} com status {string}")
    public void existeMidiaComStatus(String titulo, String status) {
        midia = ensureMidia(titulo.contains(" - ") ? titulo.split(" - ",2)[1] : titulo);
        exemplar = ensureExemplarDisponivel(midia);
        setStatusExemplar(status);
    }

    @Dado("existe um sócio ativo logado no sistema")
    public void existeSocioAtivoLogado() {
        socio = new Socio();
        socio.setNome("Usuário Ativo");
        socio.setEmail(uniqueEmail("usuario.ativo"));
        socio.setStatus(StatusSocio.ATIVO);
        socio = socioRepo.save(socio);
    }

    // WHENs
    @Quando("o administrador solicita o aluguel da mídia")
    public void solicitaAluguelMidia() {
        mensagemErro = null;
        try {
            emprestimo = emprestimoService.realizarEmprestimo(socio.getId(), midia.getId());
            emprestimo = empRepo.findById(emprestimo.getId()).orElseThrow();
            exemplar  = exRepo.findById(emprestimo.getExemplar().getId()).orElseThrow();
        } catch (BusinessException e) {
            mensagemErro = e.getMessage();
        }
    }

    // THENs
    @Então("o sistema deve permitir a locação")
    public void sistemaPermiteLocacao() {
        Assertions.assertNotNull(emprestimo, "Empréstimo não foi criado");
        Assertions.assertEquals(StatusEmprestimo.ATIVO, emprestimo.getStatus());
    }

    @Então("o status da mídia deve mudar para {string}")
    public void statusMidiaDeveMudarPara(String esperado) {
        exemplar = exRepo.findById(exemplar.getId()).orElseThrow();
        if (esperado.equalsIgnoreCase("alugada")) {
            Assertions.assertEquals(StatusExemplar.EMPRESTADO, exemplar.getStatus());
        } else if (esperado.equalsIgnoreCase("disponível")) {
            Assertions.assertEquals(StatusExemplar.DISPONIVEL, exemplar.getStatus());
        } else if (esperado.equalsIgnoreCase("indisponível")) {
            Assertions.assertEquals(StatusExemplar.INDISPONIVEL, exemplar.getStatus());
        } else {
            Assertions.fail("Status inesperado: " + esperado);
        }
    }

    @Então("o sistema deve exibir uma mensagem de erro {string}")
    public void mensagemDeErro(String msg) {
        Assertions.assertNotNull(mensagemErro, "Esperava mensagem de erro");
        Assertions.assertTrue(mensagemErro.contains(msg));
    }

    @Então("a locação não deve ser criada")
    public void locacaoNaoCriada() {
        Assertions.assertNotNull(mensagemErro, "Esperava recusa");
        Assertions.assertTrue(emprestimo == null || emprestimo.getId() == null);
    }
}
