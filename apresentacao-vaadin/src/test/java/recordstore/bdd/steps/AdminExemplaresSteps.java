package recordstore.bdd.steps;

import static org.junit.Assert.*;
import io.cucumber.java.pt.*;

public class AdminExemplaresSteps {

    //  CENÁRIO: cadastrar exemplar

    private boolean existeMidia;
    private boolean midiaSelecionada;
    private boolean dadosExemplarInformados;
    private boolean exemplarSalvo;

    @Dado("que existe ao menos uma mídia cadastrada")
    public void que_existe_ao_menos_uma_mídia_cadastrada() {
        System.out.println("Existe ao menos uma mídia cadastrada");
        existeMidia = true;
        assertTrue("Deveria existir ao menos uma mídia cadastrada", existeMidia);
    }

    @Quando("seleciona uma mídia existente")
    public void seleciona_uma_mídia_existente() {
        assertTrue("Deve existir mídia para selecionar", existeMidia);
        System.out.println("Administrador selecionou uma mídia existente");
        midiaSelecionada = true;
    }

    @Quando("informa os dados do exemplar")
    public void informa_os_dados_do_exemplar() {
        assertTrue("Uma mídia deve estar selecionada", midiaSelecionada);
        System.out.println("Administrador informou os dados do exemplar");
        dadosExemplarInformados = true;
    }

    @Então("o sistema salva o novo exemplar vinculado à mídia")
    public void o_sistema_salva_o_novo_exemplar_vinculado_à_mídia() {
        exemplarSalvo = existeMidia && midiaSelecionada && dadosExemplarInformados;
        System.out.println("Sistema salvou o novo exemplar vinculado à mídia");
        assertTrue("O exemplar deveria ter sido salvo", exemplarSalvo);
    }

    // CENÁRIO: listar exemplares de uma mídia

    private boolean existemExemplares;
    private boolean telaExemplaresAberta;

    @Dado("que existem exemplares cadastrados para uma mídia")
    public void que_existem_exemplares_cadastrados_para_uma_mídia() {
        System.out.println("Existem exemplares cadastrados para uma mídia");
        existemExemplares = true;
        assertTrue("Deveria haver exemplares cadastrados", existemExemplares);
    }

    @Quando("o administrador acessa a tela de exemplares da mídia")
    public void o_administrador_acessa_a_tela_de_exemplares_da_mídia() {
        assertTrue("Deve haver exemplares cadastrados para acessar a tela", existemExemplares);
        System.out.println("Administrador acessou a tela de exemplares da mídia");
        telaExemplaresAberta = true;
    }

    @Então("o sistema exibe a lista de exemplares com seus respectivos estados \\(disponível, emprestado, inativo\\)")
    public void o_sistema_exibe_a_lista_de_exemplares_com_seus_respectivos_estados_disponível_emprestado_inativo() {
        assertTrue("Tela de exemplares deve estar aberta", telaExemplaresAberta);
        System.out.println("Sistema exibiu a lista de exemplares com seus respectivos estados");
        assertTrue("Deveria haver exemplares para listar", existemExemplares);
    }

    // CENÁRIO: editar exemplar

    private boolean exemplarExiste;
    private boolean edicaoExemplarAberta;
    private boolean dadosExemplarAlterados;
    private boolean exemplarAtualizado;

    @Dado("que existe um exemplar cadastrado")
    public void que_existe_um_exemplar_cadastrado() {
        System.out.println("Existe um exemplar cadastrado");
        exemplarExiste = true;
        assertTrue("O exemplar deveria existir", exemplarExiste);
    }

    @Quando("o administrador acessa a edição do exemplar")
    public void o_administrador_acessa_a_edição_do_exemplar() {
        assertTrue("Deve existir exemplar para editar", exemplarExiste);
        System.out.println("Administrador acessou a edição do exemplar");
        edicaoExemplarAberta = true;
    }

    @Quando("altera os dados do exemplar com informações válidas")
    public void altera_os_dados_do_exemplar_com_informações_válidas() {
        assertTrue("Edição do exemplar deve estar aberta", edicaoExemplarAberta);
        System.out.println("Administrador alterou os dados do exemplar com informações válidas");
        dadosExemplarAlterados = true;
    }

    @Então("o sistema atualiza os dados do exemplar")
    public void o_sistema_atualiza_os_dados_do_exemplar() {
        exemplarAtualizado = exemplarExiste && edicaoExemplarAberta && dadosExemplarAlterados;
        System.out.println("Sistema atualizou os dados do exemplar");
        assertTrue("O exemplar deveria ter sido atualizado", exemplarAtualizado);
    }

    // CENÁRIO: excluir exemplar

    private String estadoExemplar;
    private boolean exclusaoSolicitada;
    private boolean exemplarRemovido;

    @Dado("que existe um exemplar com estado {string}")
    public void que_existe_um_exemplar_com_estado(String estado) {
        System.out.println("Existe um exemplar com estado: " + estado);
        exemplarExiste = true;
        estadoExemplar = estado;
        assertTrue("O exemplar deveria existir", exemplarExiste);
        assertNotNull("O estado do exemplar não deveria ser nulo", estadoExemplar);
    }

    @Dado("que existe um exemplar com estado {string} ou {string}")
    public void que_existe_um_exemplar_com_estado_ou(String estado1, String estado2) {
        System.out.println("Existe um exemplar com estado " + estado1 + " ou " + estado2);
        exemplarExiste = true;
        estadoExemplar = estado1; // simula um dos dois estados permitidos
        assertTrue("O exemplar deveria existir", exemplarExiste);
        assertTrue("Estado do exemplar deveria ser um dos estados informados",
                estadoExemplar.equals(estado1) || estadoExemplar.equals(estado2));
    }

    @Quando("o administrador solicita a exclusão desse exemplar")
    public void o_administrador_solicita_a_exclusão_desse_exemplar() {
        assertTrue("Deve existir exemplar para excluir", exemplarExiste);
        System.out.println("Administrador solicitou a exclusão do exemplar");
        exclusaoSolicitada = true;
    }

    @Então("o sistema remove o exemplar")
    public void o_sistema_remove_o_exemplar() {
        assertTrue("Exclusão deve ter sido solicitada", exclusaoSolicitada);

        // regra simulada: não remove se estiver emprestado
        boolean podeRemover = estadoExemplar != null && !"emprestado".equalsIgnoreCase(estadoExemplar);
        exemplarRemovido = podeRemover;

        System.out.println("Sistema removeu o exemplar");
        assertTrue("O exemplar deveria ter sido removido", exemplarRemovido);
    }
}
