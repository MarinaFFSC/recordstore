package recordstore.bdd.steps;

import static org.junit.Assert.*;
import io.cucumber.java.pt.*;

public class AdminArtistasSteps {

    // Estado do cenário

    // Cadastro
    private boolean dadosValidos;
    private boolean cadastroConfirmado;
    private boolean artistaSalvo;

    // Edição
    private boolean artistaExiste;
    private boolean edicaoAberta;
    private boolean dadosAlterados;
    private boolean artistaAtualizado;

    // Exclusão
    private boolean artistaSemMidias;
    private boolean exclusaoSolicitada;
    private boolean artistaRemovido;

    // Cadastro de artista

    @Quando("informa dados válidos do artista")
    public void informa_dados_validos_do_artista() {
        System.out.println("Administrador informou dados válidos do artista");
        dadosValidos = true;
    }

    @Quando("confirma o cadastro")
    public void confirma_o_cadastro() {
        System.out.println("Administrador confirmou o cadastro");
        cadastroConfirmado = true;

        // regra simulada
        artistaSalvo = dadosValidos && cadastroConfirmado;
    }

    @Então("o sistema salva o novo artista")
    public void o_sistema_salva_o_novo_artista() {
        System.out.println("Sistema salvou o novo artista");
        assertTrue("O artista deveria ter sido salvo", artistaSalvo);
    }

    // Edição de artista

    @Dado("que existe um artista cadastrado")
    public void que_existe_um_artista_cadastrado() {
        System.out.println("Existe um artista cadastrado no sistema");
        artistaExiste = true;
        assertTrue("O artista deveria existir", artistaExiste);
    }

    @Quando("o administrador acessa a edição do artista")
    public void o_administrador_acessa_a_edicao_do_artista() {
        assertTrue("Precisa existir artista para editar", artistaExiste);
        System.out.println("Administrador acessou a edição do artista");
        edicaoAberta = true;
    }

    @Quando("altera os dados do artista com informações válidas")
    public void altera_os_dados_do_artista_com_informacoes_validas() {
        assertTrue("A edição precisa estar aberta", edicaoAberta);
        System.out.println("Administrador alterou os dados do artista");
        dadosAlterados = true;
    }

    @Então("o sistema atualiza os dados do artista")
    public void o_sistema_atualiza_os_dados_do_artista() {
        // regra simulada
        artistaAtualizado = artistaExiste && edicaoAberta && dadosAlterados;

        System.out.println("Sistema atualizou os dados do artista");
        assertTrue("O artista deveria estar atualizado", artistaAtualizado);
    }

    // Exclusão de artista

    @Dado("que existe um artista sem mídias vinculadas")
    public void que_existe_um_artista_sem_midias_vinculadas() {
        System.out.println("Existe um artista sem mídias vinculadas");
        artistaExiste = true;
        artistaSemMidias = true;

        assertTrue("O artista deveria existir", artistaExiste);
        assertTrue("O artista deveria estar sem mídias", artistaSemMidias);
    }

    @Dado("que existe um artista com mídias cadastradas vinculadas a ele")
    public void que_existe_um_artista_com_midias_cadastradas_vinculadas_a_ele() {
        System.out.println("Existe um artista com mídias cadastradas vinculadas a ele");
        artistaExiste = true;
        artistaSemMidias = false;

        assertTrue("O artista deveria existir", artistaExiste);
        assertFalse("O artista não deveria estar sem mídias", artistaSemMidias);
    }

    @Quando("o administrador solicita a exclusão do artista")
    public void o_administrador_solicita_a_exclusao_do_artista() {
        assertTrue("Artista deve existir", artistaExiste);
        System.out.println("Administrador solicitou a exclusão do artista");
        exclusaoSolicitada = true;
    }

    @Então("o sistema remove o artista")
    public void o_sistema_remove_o_artista() {
        // regra simulada: só remove se não tiver mídias
        artistaRemovido = artistaExiste && exclusaoSolicitada && artistaSemMidias;

        System.out.println("Sistema removeu o artista");
        assertTrue("O artista deveria ser removido", artistaRemovido);
    }
}
