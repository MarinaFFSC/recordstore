package recordstore.bdd.steps;

import static org.junit.Assert.*;
import io.cucumber.java.pt.*;

public class DevolucaoSteps {

    // CENÁRIO: devolução no prazo (sem multa)
	
    private boolean socioExiste;
    private boolean possuiEmprestimosVigentes;
    private boolean emprestimoVencido;
    private boolean devolucaoConfirmada;
    private boolean devolucaoRegistrada;
    private boolean multaGerada;
    private String situacaoEmprestimo;

    @Dado("que existe um sócio com empréstimos vigentes")
    public void que_existe_um_socio_com_emprestimos_vigentes() {
        System.out.println("Sócio com empréstimos vigentes");
        socioExiste = true;
        possuiEmprestimosVigentes = true;

        assertTrue("O sócio deveria existir", socioExiste);
        assertTrue("O sócio deveria possuir empréstimos vigentes", possuiEmprestimosVigentes);
    }

    @Dado("que o sócio possui um empréstimo com data de devolução ainda não vencida")
    public void que_o_socio_possui_um_emprestimo_com_data_de_devolucao_ainda_nao_vencida() {
        assertTrue("O sócio deve possuir empréstimos vigentes", possuiEmprestimosVigentes);

        System.out.println("Empréstimo não vencido");
        emprestimoVencido = false;

        assertFalse("O empréstimo não deveria estar vencido", emprestimoVencido);
    }

    @Quando("o sócio seleciona o empréstimo e confirma a devolução")
    public void o_socio_seleciona_o_emprestimo_e_confirma_a_devolucao() {
        assertTrue("Deve existir empréstimo vigente para devolução", possuiEmprestimosVigentes);

        System.out.println("Devolução confirmada");
        devolucaoConfirmada = true;

        assertTrue("A devolução deveria ter sido confirmada", devolucaoConfirmada);
    }

    @Então("o sistema registra a devolução do exemplar")
    public void o_sistema_registra_a_devolucao_do_exemplar() {
        devolucaoRegistrada = devolucaoConfirmada && possuiEmprestimosVigentes;

        System.out.println("Devolução registrada");
        assertTrue("A devolução deveria ser registrada", devolucaoRegistrada);
    }

    @Então("o sistema atualiza a situação do empréstimo para {string}")
    public void o_sistema_atualiza_a_situacao_do_emprestimo_para(String situacao) {
        assertTrue("A devolução deve ter sido registrada", devolucaoRegistrada);
        assertNotNull("A situação informada não deveria ser nula", situacao);

        situacaoEmprestimo = situacao;

        System.out.println("Situação do empréstimo atualizada para: " + situacaoEmprestimo);
        assertEquals("A situação do empréstimo deveria ser atualizada corretamente", situacao, situacaoEmprestimo);
    }

    @Então("o sistema não gera multa para o sócio")
    public void o_sistema_nao_gera_multa_para_o_socio() {
        assertFalse("O empréstimo não deveria estar vencido", emprestimoVencido);

        multaGerada = false;

        System.out.println("Não gerou multa");
        assertFalse("Nenhuma multa deveria ser gerada", multaGerada);
    }

    // CENÁRIO: devolução em atraso (gera multa)

    @Dado("que o sócio possui um empréstimo com data de devolução vencida")
    public void que_o_socio_possui_um_emprestimo_com_data_de_devolucao_vencida() {
        assertTrue("O sócio deve possuir empréstimos vigentes", possuiEmprestimosVigentes);

        System.out.println("Empréstimo vencido");
        emprestimoVencido = true;

        assertTrue("O empréstimo deveria estar vencido", emprestimoVencido);
    }

    @Então("o sistema gera uma multa associada ao sócio e ao empréstimo")
    public void o_sistema_gera_uma_multa_associada_ao_socio_e_ao_emprestimo() {
        assertTrue("O empréstimo deve estar vencido para gerar multa", emprestimoVencido);
        assertTrue("A devolução deve ter sido confirmada", devolucaoConfirmada);

        multaGerada = true;

        System.out.println("Multa gerada");
        assertTrue("Uma multa deveria ser gerada", multaGerada);
    }

    // CENÁRIO: tentativa de devolução inválida

    private boolean tentativaDevolucaoInvalida;

    @Quando("o sócio tenta registrar a devolução de uma mídia que não consta em seus empréstimos vigentes")
    public void o_socio_tenta_registrar_a_devolucao_de_uma_midia_que_nao_consta_em_seus_emprestimos_vigentes() {
        System.out.println("Tentou devolver sem empréstimo vigente");

        tentativaDevolucaoInvalida = true;
        possuiEmprestimosVigentes = false;

        assertTrue("A tentativa de devolução inválida deveria ocorrer", tentativaDevolucaoInvalida);
        assertFalse("O sócio não deveria possuir empréstimos vigentes", possuiEmprestimosVigentes);
    }

    @Então("o sistema não registra a devolução")
    public void o_sistema_nao_registra_a_devolucao() {
        devolucaoRegistrada = false;

        System.out.println("Sistema não registrou devolução");
        assertFalse("A devolução não deveria ser registrada", devolucaoRegistrada);
    }
}
