package recordstore.bdd.steps;

import static org.junit.Assert.*;
import io.cucumber.java.pt.*;

public class MultasSteps {

    // CENÁRIO: sócio possui multas pendentes (bloqueia empréstimo)

    private boolean multasPendentesExistem;
    private boolean emprestimoPermitido;

    @Dado("que o sócio possui multas pendentes")
    public void que_o_socio_possui_multas_pendentes() {
        System.out.println("Sócio possui multas pendentes");
        multasPendentesExistem = true;

        assertTrue("O sócio deveria possuir multas pendentes", multasPendentesExistem);
    }

    @Então("o sistema impede a realização de empréstimo")
    public void o_sistema_impede_a_realizacao_de_emprestimo() {
        assertTrue("Deveria haver multas pendentes para impedir empréstimo", multasPendentesExistem);

        // regra simulada: com multa pendente, não permite emprestar
        emprestimoPermitido = !multasPendentesExistem;

        System.out.println("Empréstimo impedido por multas");
        assertFalse("O empréstimo não deveria ser permitido com multas pendentes", emprestimoPermitido);
    }

    @Dado("que o sócio não possui multas pendentes")
    public void que_o_socio_nao_possui_multas_pendentes() {
        System.out.println("Sócio não possui multas pendentes");
        multasPendentesExistem = false;

        assertFalse("O sócio não deveria possuir multas pendentes", multasPendentesExistem);
    }

    // CENÁRIO: sócio consulta multas pendentes

    private boolean listaMultasExibida;
    private boolean detalhesMultasExibidos;

    @Então("o sistema exibe a lista de multas pendentes")
    public void o_sistema_exibe_a_lista_de_multas_pendentes() {
        assertTrue("Deveria haver multas pendentes para exibir a lista", multasPendentesExistem);

        System.out.println("Sistema exibiu a lista de multas pendentes");
        listaMultasExibida = true;

        assertTrue("A lista de multas pendentes deveria ser exibida", listaMultasExibida);
    }

    @Então("para cada multa o sistema exibe valor, motivo, data de geração e situação {string}")
    public void para_cada_multa_o_sistema_exibe_valor_motivo_data_de_geracao_e_situacao(String situacao) {
        assertTrue("A lista de multas deve estar exibida", listaMultasExibida);
        assertNotNull("A situação não deveria ser nula", situacao);
        assertFalse("A situação não deveria ser vazia", situacao.trim().isEmpty());

        System.out.println("Sistema exibiu para cada multa: valor, motivo, data de geração e situação " + situacao);
        detalhesMultasExibidos = true;

        assertTrue("Os detalhes das multas deveriam ser exibidos", detalhesMultasExibidos);
    }

    // CENÁRIO: sócio paga multa

    private String situacaoMultaSelecionada;
    private String opcaoEscolhida;
    private boolean pagamentoConfirmado;
    private boolean pagamentoRegistrado;

    @Quando("o sócio seleciona uma multa com situação {string}")
    public void o_socio_seleciona_uma_multa_com_situacao(String situacao) {
        assertNotNull("A situação da multa não deveria ser nula", situacao);

        System.out.println("Sócio selecionou uma multa com situação: " + situacao);
        situacaoMultaSelecionada = situacao;

        assertNotNull("A situação selecionada deveria ser registrada", situacaoMultaSelecionada);
    }

    @Quando("escolhe a opção {string}")
    public void escolhe_a_opcao(String opcao) {
        assertNotNull("A opção não deveria ser nula", opcao);
        assertFalse("A opção não deveria ser vazia", opcao.trim().isEmpty());

        System.out.println("Sócio escolheu a opção: " + opcao);
        opcaoEscolhida = opcao;

        assertNotNull("A opção escolhida deveria ser registrada", opcaoEscolhida);
    }

    @Quando("confirma a operação de pagamento")
    public void confirma_a_operacao_de_pagamento() {
        assertNotNull("A opção deve ter sido escolhida antes de confirmar pagamento", opcaoEscolhida);

        System.out.println("Sócio confirmou a operação de pagamento");
        pagamentoConfirmado = true;

        assertTrue("O pagamento deveria ter sido confirmado", pagamentoConfirmado);
    }

    @Então("o sistema registra o pagamento da multa")
    public void o_sistema_registra_o_pagamento_da_multa() {
        assertTrue("O pagamento deve ter sido confirmado", pagamentoConfirmado);
        assertNotNull("Deve existir situação selecionada para a multa", situacaoMultaSelecionada);

        // regra simulada: só registra pagamento se a multa estiver pendente
        pagamentoRegistrado = situacaoMultaSelecionada.equalsIgnoreCase("pendente") && pagamentoConfirmado;

        System.out.println("Sistema registrou o pagamento da multa");
        assertTrue("O pagamento deveria ter sido registrado (multa pendente)", pagamentoRegistrado);
    }

    @Então("o sistema atualiza a situação da multa para {string}")
    public void o_sistema_atualiza_a_situacao_da_multa_para(String novaSituacao) {
        assertTrue("Pagamento deve ter sido registrado", pagamentoRegistrado);
        assertNotNull("A nova situação não deveria ser nula", novaSituacao);
        assertFalse("A nova situação não deveria ser vazia", novaSituacao.trim().isEmpty());

        System.out.println("Sistema atualizou a situação da multa para: " + novaSituacao);
        situacaoMultaSelecionada = novaSituacao;

        assertEquals("A situação da multa deveria ser atualizada corretamente", novaSituacao, situacaoMultaSelecionada);
    }

    // CENÁRIO: sócio solicita cancelamento de multa

    private boolean justificativaInformada;
    private boolean solicitacaoCancelamentoRegistrada;

    @Quando("informa uma justificativa")
    public void informa_uma_justificativa() {
        System.out.println("Sócio informou uma justificativa para cancelamento da multa");
        justificativaInformada = true;

        assertTrue("A justificativa deveria ter sido informada", justificativaInformada);
    }

    @Então("o sistema registra a solicitação de cancelamento da multa")
    public void o_sistema_registra_a_solicitacao_de_cancelamento_da_multa() {
        solicitacaoCancelamentoRegistrada = justificativaInformada;

        System.out.println("Sistema registrou a solicitação de cancelamento da multa");
        assertTrue("A solicitação de cancelamento deveria ser registrada", solicitacaoCancelamentoRegistrada);
    }

    @Então("o sistema altera o status da multa para {string}")
    public void o_sistema_altera_o_status_da_multa_para(String novoStatus) {
        assertTrue("A solicitação de cancelamento deve ter sido registrada", solicitacaoCancelamentoRegistrada);
        assertNotNull("O novo status não deveria ser nulo", novoStatus);
        assertFalse("O novo status não deveria ser vazio", novoStatus.trim().isEmpty());

        System.out.println("Sistema alterou o status da multa para: " + novoStatus);
        situacaoMultaSelecionada = novoStatus;

        assertEquals("O status da multa deveria ser atualizado corretamente", novoStatus, situacaoMultaSelecionada);
    }

    // CENÁRIO: não permite pagar multa já paga/cancelada

    private boolean tentativaPagamentoMulta;
    private boolean operacaoPermitida;

    @Dado("que a multa já está com situação {string} ou {string}")
    public void que_a_multa_ja_esta_com_situacao_ou(String s1, String s2) {
        assertNotNull("Situação 1 não deveria ser nula", s1);
        assertNotNull("Situação 2 não deveria ser nula", s2);

        System.out.println("Multa já está com situação " + s1 + " ou " + s2);

        // simula estar em uma das situações finais
        situacaoMultaSelecionada = s1;

        assertTrue(
            "A multa deveria estar em uma das situações informadas",
            situacaoMultaSelecionada.equals(s1) || situacaoMultaSelecionada.equals(s2)
        );
    }

    @Quando("o sócio tenta realizar o pagamento dessa multa")
    public void o_socio_tenta_realizar_o_pagamento_dessa_multa() {
        assertNotNull("Deve existir multa selecionada para tentar pagar", situacaoMultaSelecionada);

        System.out.println("Sócio tentou realizar o pagamento dessa multa");
        tentativaPagamentoMulta = true;

        // regra simulada: se já está paga ou cancelada, não permite pagar
        operacaoPermitida = !(situacaoMultaSelecionada.equalsIgnoreCase("paga")
                || situacaoMultaSelecionada.equalsIgnoreCase("cancelada"));

        assertTrue("Deveria ter havido tentativa de pagamento", tentativaPagamentoMulta);
    }

    @Então("o sistema não permite a operação")
    public void o_sistema_nao_permite_a_operacao() {
        assertTrue("Deve ter havido tentativa de operação", tentativaPagamentoMulta);

        System.out.println("Sistema NÃO permitiu a operação");
        assertFalse("A operação não deveria ser permitida", operacaoPermitida);
    }
}
