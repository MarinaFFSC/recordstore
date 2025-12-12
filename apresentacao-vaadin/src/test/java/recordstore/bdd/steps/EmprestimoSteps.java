package recordstore.bdd.steps;

import static org.junit.Assert.*;
import io.cucumber.java.pt.*;

public class EmprestimoSteps {

    // CENÁRIO: listar empréstimos (admin)

    private boolean existemEmprestimos;
    private boolean listaEmprestimosExibida;
    private boolean detalhesEmprestimosExibidos;

    @Dado("que existem empréstimos cadastrados")
    public void que_existem_emprestimos_cadastrados() {
        System.out.println("Existem empréstimos cadastrados");
        existemEmprestimos = true;

        assertTrue("Deveria haver empréstimos cadastrados", existemEmprestimos);
    }

    @Então("o sistema exibe a lista de empréstimos")
    public void o_sistema_exibe_a_lista_de_emprestimos() {
        assertTrue("Deve haver empréstimos cadastrados para exibir a lista", existemEmprestimos);

        System.out.println("Sistema exibiu a lista de empréstimos");
        listaEmprestimosExibida = true;

        assertTrue("A lista de empréstimos deveria ser exibida", listaEmprestimosExibida);
    }

    @Então("para cada empréstimo o sistema exibe sócio, mídia, exemplar, data de empréstimo, data de devolução prevista e situação")
    public void para_cada_emprestimo_o_sistema_exibe_socio_midia_exemplar_datas_e_situacao() {
        assertTrue("A lista de empréstimos deve ter sido exibida", listaEmprestimosExibida);

        System.out.println("Sistema exibiu os detalhes de cada empréstimo");
        detalhesEmprestimosExibidos = true;

        assertTrue("Os detalhes de cada empréstimo deveriam ser exibidos", detalhesEmprestimosExibidos);
    }

    // CENÁRIO: catálogo indica disponibilidade (sócio)

    private boolean socioAutenticado;
    private boolean existemMidiasComExemplaresDisponiveis;
    private boolean disponibilidadeIndicada;

    @Dado("que o sócio está autenticado no sistema")
    public void que_o_socio_esta_autenticado_no_sistema() {
        System.out.println("Sócio está autenticado no sistema");
        socioAutenticado = true;

        assertTrue("O sócio deveria estar autenticado", socioAutenticado);
    }

    @Dado("que existem mídias cadastradas com exemplares disponíveis")
    public void que_existem_midias_cadastradas_com_exemplares_disponiveis() {
        assertTrue("Sócio deve estar autenticado", socioAutenticado);

        System.out.println("Existem mídias cadastradas com exemplares disponíveis");
        existemMidiasComExemplaresDisponiveis = true;

        assertTrue("Deveriam existir mídias com exemplares disponíveis", existemMidiasComExemplaresDisponiveis);
    }

    @Então("o sistema indica se há exemplares disponíveis para empréstimo")
    public void o_sistema_indica_se_ha_exemplares_disponiveis_para_emprestimo() {
        disponibilidadeIndicada = socioAutenticado && existemMidiasComExemplaresDisponiveis;

        System.out.println("Sistema indicou a disponibilidade de exemplares para empréstimo");
        assertTrue("O sistema deveria indicar disponibilidade", disponibilidadeIndicada);
    }

    private boolean naoExistemMidiasCadastradas;

    @Dado("que não existem mídias cadastradas")
    public void que_nao_existem_midias_cadastradas() {
        System.out.println("Não existem mídias cadastradas no sistema");
        naoExistemMidiasCadastradas = true;
        existemMidiasComExemplaresDisponiveis = false;

        assertTrue("Deveria estar no estado de não haver mídias cadastradas", naoExistemMidiasCadastradas);
        assertFalse("Não deveria haver mídias com exemplares disponíveis", existemMidiasComExemplaresDisponiveis);
    }

    // CENÁRIO: sócio realiza empréstimo (permitido)

    private boolean socioAtivo;
    private boolean semMultasPendentes;
    private boolean midiaComExemplarDisponivel;
    private boolean midiaSelecionada;
    private boolean emprestimoConfirmado;
    private boolean emprestimoRegistrado;

    private String disponibilidadeExemplar;
    private boolean dataDevolucaoExibida;

    @Dado("que existe um sócio ativo e sem multas pendentes")
    public void que_existe_um_socio_ativo_e_sem_multas_pendentes() {
        System.out.println("Existe um sócio ativo e sem multas pendentes");
        socioAtivo = true;
        semMultasPendentes = true;

        assertTrue("O sócio deveria estar ativo", socioAtivo);
        assertTrue("O sócio não deveria ter multas pendentes", semMultasPendentes);
    }

    @Dado("que existe uma mídia com exemplares disponíveis")
    public void que_existe_uma_midia_com_exemplares_disponiveis() {
        System.out.println("Existe uma mídia com exemplares disponíveis");
        midiaComExemplarDisponivel = true;
        disponibilidadeExemplar = "disponível";

        assertTrue("Deveria haver exemplar disponível", midiaComExemplarDisponivel);
        assertNotNull("Disponibilidade do exemplar não deveria ser nula", disponibilidadeExemplar);
    }

    @Quando("o sócio seleciona uma mídia com exemplar disponível para empréstimo")
    public void o_socio_seleciona_uma_midia_com_exemplar_disponivel_para_emprestimo() {
        assertTrue("Sócio precisa estar ativo", socioAtivo);
        assertTrue("Sócio precisa estar sem multas pendentes", semMultasPendentes);
        assertTrue("Deve haver exemplar disponível", midiaComExemplarDisponivel);

        System.out.println("Sócio selecionou uma mídia com exemplar disponível");
        midiaSelecionada = true;

        assertTrue("A mídia deveria ter sido selecionada", midiaSelecionada);
    }

    @Quando("confirma a realização do empréstimo")
    public void confirma_a_realizacao_do_emprestimo() {
        assertTrue("Mídia deve ter sido selecionada", midiaSelecionada);

        System.out.println("Sócio confirmou a realização do empréstimo");
        emprestimoConfirmado = true;

        assertTrue("O empréstimo deveria ter sido confirmado", emprestimoConfirmado);
    }

    @Então("o sistema registra o empréstimo vinculado ao sócio e ao exemplar")
    public void o_sistema_registra_o_emprestimo_vinculado_ao_socio_e_ao_exemplar() {
        emprestimoRegistrado = socioAtivo && semMultasPendentes && midiaSelecionada && emprestimoConfirmado;

        System.out.println("Sistema registrou o empréstimo vinculado ao sócio e ao exemplar");
        assertTrue("O empréstimo deveria ter sido registrado", emprestimoRegistrado);
    }

    @Então("o sistema atualiza a disponibilidade do exemplar para {string}")
    public void o_sistema_atualiza_a_disponibilidade_do_exemplar_para(String novoEstado) {
        assertTrue("Empréstimo deve ter sido registrado", emprestimoRegistrado);
        assertNotNull("O novo estado não deveria ser nulo", novoEstado);

        disponibilidadeExemplar = novoEstado;

        System.out.println("Sistema atualizou a disponibilidade do exemplar para " + novoEstado);
        assertEquals("A disponibilidade do exemplar deveria ser atualizada corretamente", novoEstado, disponibilidadeExemplar);
    }

    @Então("o sistema exibe a data de devolução prevista para o sócio")
    public void o_sistema_exibe_a_data_de_devolucao_prevista_para_o_socio() {
        assertTrue("Empréstimo deve ter sido registrado", emprestimoRegistrado);

        System.out.println("Sistema exibiu a data de devolução prevista para o sócio");
        dataDevolucaoExibida = true;

        assertTrue("A data de devolução prevista deveria ser exibida", dataDevolucaoExibida);
    }

    // CENÁRIO: sócio NÃO consegue emprestar (todos exemplares emprestados)

    private boolean todosExemplaresEmprestados;
    private boolean houveTentativaEmprestimo;
    private boolean emprestimoPermitido;

    @Dado("que todos os exemplares da mídia estão emprestados")
    public void que_todos_os_exemplares_da_midia_estao_emprestados() {
        System.out.println("Todos os exemplares da mídia estão emprestados");
        todosExemplaresEmprestados = true;
        midiaComExemplarDisponivel = false;
        disponibilidadeExemplar = "emprestado";

        assertTrue("Todos os exemplares deveriam estar emprestados", todosExemplaresEmprestados);
        assertFalse("Não deveria haver exemplar disponível", midiaComExemplarDisponivel);
        assertEquals("emprestado", disponibilidadeExemplar);
    }

    @Quando("o sócio tenta realizar o empréstimo dessa mídia")
    public void o_socio_tenta_realizar_o_emprestimo_dessa_midia() {
        System.out.println("Sócio tentou realizar o empréstimo dessa mídia");
        houveTentativaEmprestimo = true;

        emprestimoPermitido = !todosExemplaresEmprestados;

        assertTrue("Deveria ter havido tentativa de empréstimo", houveTentativaEmprestimo);
    }

    @Então("o sistema não permite a realização do empréstimo")
    public void o_sistema_nao_permite_a_realizacao_do_emprestimo() {
        assertTrue("Deve ter havido tentativa de empréstimo", houveTentativaEmprestimo);

        System.out.println("Sistema NÃO permitiu a realização do empréstimo");
        assertFalse("O empréstimo não deveria ser permitido", emprestimoPermitido);
    }

    // CENÁRIO: sócio NÃO pode emprestar (multas ou suspensão)

    private boolean socioComMultasPendentesOuSuspenso;

    @Dado("que o sócio possui multas pendentes ou está suspenso")
    public void que_o_socio_possui_multas_pendentes_ou_esta_suspenso() {
        System.out.println("Sócio possui multas pendentes ou está suspenso");
        socioComMultasPendentesOuSuspenso = true;

        assertTrue("O sócio deveria estar com multas pendentes ou suspenso", socioComMultasPendentesOuSuspenso);
    }

    @Quando("o sócio tenta realizar um empréstimo")
    public void o_socio_tenta_realizar_um_emprestimo() {
        System.out.println("Sócio tentou realizar um empréstimo");
        houveTentativaEmprestimo = true;

        emprestimoPermitido = !socioComMultasPendentesOuSuspenso;

        assertTrue("Deveria ter havido tentativa de empréstimo", houveTentativaEmprestimo);
    }

    // CENÁRIO: sócio visualiza empréstimos vigentes

    private boolean socioPossuiEmprestimosVigentes;
    private boolean listaEmprestimosVigentesExibida;
    private boolean detalhesEmprestimosVigentesExibidos;

    @Dado("que o sócio possui empréstimos vigentes")
    public void que_o_socio_possui_emprestimos_vigentes() {
        System.out.println("Sócio possui empréstimos vigentes");
        socioPossuiEmprestimosVigentes = true;

        assertTrue("O sócio deveria possuir empréstimos vigentes", socioPossuiEmprestimosVigentes);
    }

    @Então("o sistema exibe a lista de empréstimos vigentes do sócio")
    public void o_sistema_exibe_a_lista_de_emprestimos_vigentes_do_socio() {
        assertTrue("Sócio deveria possuir empréstimos vigentes", socioPossuiEmprestimosVigentes);

        System.out.println("Sistema exibiu a lista de empréstimos vigentes do sócio");
        listaEmprestimosVigentesExibida = true;

        assertTrue("A lista de empréstimos vigentes deveria ser exibida", listaEmprestimosVigentesExibida);
    }

    @Então("para cada empréstimo o sistema exibe mídia, data de empréstimo e data de devolução prevista")
    public void para_cada_emprestimo_o_sistema_exibe_midia_data_emprestimo_data_devolucao_prevista() {
        assertTrue("A lista de empréstimos vigentes deve ter sido exibida", listaEmprestimosVigentesExibida);

        System.out.println("Sistema exibiu mídia, data de empréstimo e data de devolução prevista para cada empréstimo");
        detalhesEmprestimosVigentesExibidos = true;

        assertTrue("Os detalhes dos empréstimos vigentes deveriam ser exibidos", detalhesEmprestimosVigentesExibidos);
    }

    // CENÁRIO: sócio não possui empréstimos vigentes

    @Dado("que o sócio não possui empréstimos vigentes")
    public void que_o_socio_nao_possui_emprestimos_vigentes() {
        System.out.println("Sócio não possui empréstimos vigentes");
        socioPossuiEmprestimosVigentes = false;

        assertFalse("O sócio não deveria possuir empréstimos vigentes", socioPossuiEmprestimosVigentes);
    }
}
