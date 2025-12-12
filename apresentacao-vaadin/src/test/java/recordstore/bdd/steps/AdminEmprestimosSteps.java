package recordstore.bdd.steps;

import static org.junit.Assert.*;
import io.cucumber.java.pt.*;

public class AdminEmprestimosSteps {

    // Estado do cenário

    private boolean emprestimoExiste;
    private boolean emprestimoSelecionado;
    private boolean dadosAlterados;
    private boolean emprestimoAtualizado;

    // CENÁRIO: editar empréstimo

    @Dado("que existe um empréstimo cadastrado")
    public void que_existe_um_emprestimo_cadastrado() {
        System.out.println("Existe um empréstimo cadastrado no sistema");
        emprestimoExiste = true;
        assertTrue("O empréstimo deveria existir", emprestimoExiste);
    }

    @Quando("o administrador seleciona o empréstimo para edição")
    public void o_administrador_seleciona_o_emprestimo_para_edicao() {
        assertTrue("Deve existir empréstimo para editar", emprestimoExiste);
        System.out.println("Administrador selecionou o empréstimo para edição");
        emprestimoSelecionado = true;
    }

    @Quando("altera dados permitidos do empréstimo \\(por exemplo, data de devolução prevista ou situação\\)")
    public void altera_dados_permitidos_do_emprestimo() {
        assertTrue(
            "Empréstimo deve estar selecionado para edição",
            emprestimoSelecionado
        );
        System.out.println("Administrador alterou dados permitidos do empréstimo");
        dadosAlterados = true;
    }

    @Então("o sistema atualiza os dados do empréstimo")
    public void o_sistema_atualiza_os_dados_do_emprestimo() {
        // regra simulada de atualização
        emprestimoAtualizado =
                emprestimoExiste &&
                emprestimoSelecionado &&
                dadosAlterados;

        System.out.println("Sistema atualizou os dados do empréstimo");
        assertTrue(
            "O empréstimo deveria ter sido atualizado",
            emprestimoAtualizado
        );
    }
}
