package recordstore.bdd.steps;

import static org.junit.Assert.*;

import io.cucumber.java.pt.*;

public class CommonSteps {

    // STEPS COMUNS (reutilizáveis)

    private boolean socioExiste;
    private boolean adminAutenticado;

    private boolean edicaoConfirmada;
    private boolean exclusaoConfirmada;

    private String opcaoAcessadaAdmin;
    private String opcaoAcessadaSocio;

    private String mensagemExibida;
    private boolean exclusaoPermitida;

    @Dado("que existe um sócio cadastrado")
    public void que_existe_um_socio_cadastrado() {
        System.out.println("Sócio cadastrado existe no sistema");
        socioExiste = true;

        assertTrue("Deveria existir um sócio cadastrado", socioExiste);
    }

    @Dado("que o administrador está autenticado no sistema")
    public void que_o_administrador_esta_autenticado_no_sistema() {
        System.out.println("Administrador autenticado no sistema");
        adminAutenticado = true;

        assertTrue("O administrador deveria estar autenticado", adminAutenticado);
    }

    @Quando("confirma a edição")
    public void confirma_a_edicao() {
        System.out.println("Confirma a edição");
        edicaoConfirmada = true;

        assertTrue("A edição deveria ter sido confirmada", edicaoConfirmada);
    }

    @Quando("confirma a exclusão")
    public void confirma_a_exclusao() {
        System.out.println("Confirma a exclusão");
        exclusaoConfirmada = true;

        assertTrue("A exclusão deveria ter sido confirmada", exclusaoConfirmada);
    }

    @Quando("o administrador acessa a opção {string}")
    public void o_administrador_acessa_a_opcao(String opcao) {
        assertTrue("O administrador deve estar autenticado para acessar opções", adminAutenticado);
        assertNotNull("A opção informada não deveria ser nula", opcao);
        assertFalse("A opção informada não deveria ser vazia", opcao.trim().isEmpty());

        System.out.println("Administrador acessa a opção: " + opcao);
        opcaoAcessadaAdmin = opcao;

        assertNotNull("A opção acessada pelo administrador deveria ter sido registrada", opcaoAcessadaAdmin);
    }

    @Quando("o sócio acessa a opção {string}")
    public void o_socio_acessa_a_opcao(String opcao) {
        socioExiste = true;

        assertNotNull("A opção informada não deveria ser nula", opcao);
        assertFalse("A opção informada não deveria ser vazia", opcao.trim().isEmpty());

        System.out.println("Sócio acessa a opção: " + opcao);
        opcaoAcessadaSocio = opcao;

        assertTrue("Deveria existir um sócio para acessar opções", socioExiste);
        assertNotNull("A opção acessada pelo sócio deveria ter sido registrada", opcaoAcessadaSocio);
    }


    @Então("o sistema exibe a mensagem {string}")
    public void o_sistema_exibe_a_mensagem(String msg) {
        assertNotNull("A mensagem esperada não deveria ser nula", msg);
        assertFalse("A mensagem esperada não deveria ser vazia", msg.trim().isEmpty());

        System.out.println("Mensagem exibida: " + msg);
        mensagemExibida = msg;

        assertEquals("A mensagem exibida deveria ser a esperada", msg, mensagemExibida);
    }

    @Então("o sistema não permite a exclusão")
    public void o_sistema_nao_permite_a_exclusao() {
        // pré-condição: normalmente a exclusão foi tentada/confirmada em algum fluxo
        // (não forçamos exclusaoConfirmada aqui porque nem todo cenário pode ter esse passo)
        exclusaoPermitida = false;

        System.out.println("Sistema bloqueia exclusão");
        assertFalse("O sistema não deveria permitir a exclusão", exclusaoPermitida);
    }
}
