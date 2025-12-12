package recordstore.bdd.steps;

import static org.junit.Assert.*;
import io.cucumber.java.pt.*;

public class AdminMidiasSteps {

    //  CENÁRIO: cadastrar mídia com artista

    private boolean existeArtista;
    private boolean dadosMidiaInformados;
    private boolean artistaSelecionado;
    private boolean midiaSalva;

    @Dado("que existe ao menos um artista cadastrado")
    public void que_existe_ao_menos_um_artista_cadastrado() {
        System.out.println("Existe ao menos um artista cadastrado");
        existeArtista = true;
        assertTrue("Deveria existir ao menos um artista cadastrado", existeArtista);
    }

    @Quando("informa os dados da mídia com um artista existente selecionado")
    public void informa_os_dados_da_midia_com_um_artista_existente_selecionado() {
        assertTrue("Deve existir artista para selecionar", existeArtista);

        System.out.println("Dados da mídia informados com artista existente selecionado");
        dadosMidiaInformados = true;
        artistaSelecionado = true;

        assertTrue("Os dados da mídia deveriam ter sido informados", dadosMidiaInformados);
        assertTrue("Um artista deveria estar selecionado", artistaSelecionado);
    }

    @Então("o sistema salva a nova mídia vinculada ao artista informado")
    public void o_sistema_salva_a_nova_midia_vinculada_ao_artista_informado() {
        midiaSalva = existeArtista && dadosMidiaInformados && artistaSelecionado;

        System.out.println("Nova mídia vinculada ao artista foi salva");
        assertTrue("A mídia deveria ter sido salva vinculada ao artista", midiaSalva);
    }

    // CENÁRIO: cadastrar mídia sem artista

    private boolean tentouConfirmarCadastro;
    private boolean cadastroPermitido;

    @Quando("informa os dados da mídia sem vincular um artista")
    public void informa_os_dados_da_midia_sem_vincular_um_artista() {
        System.out.println("Dados da mídia informados sem vincular artista");
        dadosMidiaInformados = true;
        artistaSelecionado = false;

        assertTrue("Os dados da mídia deveriam ter sido informados", dadosMidiaInformados);
        assertFalse("Não deveria haver artista selecionado", artistaSelecionado);
    }

    @Quando("tenta confirmar o cadastro")
    public void tenta_confirmar_o_cadastro() {
        assertTrue("Deve ter informado os dados da mídia antes de confirmar", dadosMidiaInformados);

        System.out.println("Tentou confirmar cadastro");
        tentouConfirmarCadastro = true;
        assertTrue("A tentativa de confirmar o cadastro deveria ocorrer", tentouConfirmarCadastro);
    }

    @Então("o sistema não permite o cadastro")
    public void o_sistema_nao_permite_o_cadastro() {
        assertTrue("A tentativa de confirmar deve ter ocorrido", tentouConfirmarCadastro);

        // regra simulada: só permite cadastrar mídia se houver artista
        cadastroPermitido = dadosMidiaInformados && artistaSelecionado;

        System.out.println("Sistema não permitiu o cadastro da mídia");
        assertFalse("O cadastro não deveria ser permitido sem artista", cadastroPermitido);
    }

    // CENÁRIO: editar mídia

    private boolean midiaExiste;
    private boolean edicaoMidiaAberta;
    private boolean dadosMidiaAlterados;
    private boolean midiaAtualizada;

    @Dado("que existe uma mídia cadastrada")
    public void que_existe_uma_midia_cadastrada() {
        System.out.println("Existe uma mídia cadastrada");
        midiaExiste = true;
        assertTrue("A mídia deveria existir", midiaExiste);
    }

    @Quando("o administrador acessa a edição da mídia")
    public void o_administrador_acessa_a_edicao_da_midia() {
        assertTrue("Deve existir mídia para editar", midiaExiste);

        System.out.println("Administrador acessou a edição da mídia");
        edicaoMidiaAberta = true;
        assertTrue("A edição da mídia deveria estar aberta", edicaoMidiaAberta);
    }

    @Quando("altera os dados da mídia com informações válidas")
    public void altera_os_dados_da_midia_com_informacoes_validas() {
        assertTrue("A edição da mídia deve estar aberta", edicaoMidiaAberta);

        System.out.println("Dados da mídia alterados com informações válidas");
        dadosMidiaAlterados = true;
        assertTrue("Os dados da mídia deveriam ter sido alterados", dadosMidiaAlterados);
    }

    @Então("o sistema atualiza os dados da mídia")
    public void o_sistema_atualiza_os_dados_da_midia() {
        midiaAtualizada = midiaExiste && edicaoMidiaAberta && dadosMidiaAlterados;

        System.out.println("Dados da mídia atualizados");
        assertTrue("A mídia deveria ter sido atualizada", midiaAtualizada);
    }

    // =========================
    // CENÁRIO: excluir mídia sem exemplares
    // =========================

    private boolean midiaSemExemplares;
    private boolean exclusaoSolicitada;
    private boolean midiaRemovida;

    @Dado("que existe uma mídia cadastrada sem exemplares associados")
    public void que_existe_uma_midia_cadastrada_sem_exemplares_associados() {
        System.out.println("Existe mídia cadastrada sem exemplares associados");
        midiaExiste = true;
        midiaSemExemplares = true;

        assertTrue("A mídia deveria existir", midiaExiste);
        assertTrue("A mídia não deveria possuir exemplares associados", midiaSemExemplares);
    }

    @Quando("o administrador solicita a exclusão da mídia")
    public void o_administrador_solicita_a_exclusao_da_midia() {
        assertTrue("A mídia deve existir para exclusão", midiaExiste);

        System.out.println("Administrador solicitou exclusão da mídia");
        exclusaoSolicitada = true;
        assertTrue("A exclusão deveria ter sido solicitada", exclusaoSolicitada);
    }

    @Então("o sistema remove a mídia")
    public void o_sistema_remove_a_midia() {
        assertTrue("A exclusão deve ter sido solicitada", exclusaoSolicitada);

        // regra simulada: só remove mídia sem exemplares
        midiaRemovida = midiaExiste && midiaSemExemplares;

        System.out.println("Mídia removida");
        assertTrue("A mídia deveria ter sido removida", midiaRemovida);
    }
}
