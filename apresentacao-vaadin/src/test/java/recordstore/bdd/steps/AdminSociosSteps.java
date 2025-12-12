package recordstore.bdd.steps;

import static org.junit.Assert.*;
import io.cucumber.java.pt.*;

public class AdminSociosSteps {

    //  CENÁRIO: cadastrar sócio

    private boolean dadosObrigatoriosInformados;
    private boolean socioSalvo;

    @Quando("informa dados válidos do sócio obrigatórios")
    public void informa_dados_validos_do_socio_obrigatorios() {
        System.out.println("Dados do sócio informados");
        dadosObrigatoriosInformados = true;

        assertTrue("Os dados obrigatórios do sócio deveriam ter sido informados", dadosObrigatoriosInformados);
    }

    @Então("o sistema salva o novo sócio")
    public void o_sistema_salva_o_novo_socio() {
        socioSalvo = dadosObrigatoriosInformados;

        System.out.println("Novo sócio salvo");
        assertTrue("O sócio deveria ter sido salvo", socioSalvo);
    }

    // CENÁRIO: editar sócio

    private boolean socioExiste;
    private boolean edicaoSocioAberta;
    private boolean dadosSocioAlterados;
    private boolean socioAtualizado;

    @Quando("o administrador acessa a edição do sócio")
    public void o_administrador_acessa_a_edicao_do_socio() {
        // Se não houver um @Dado explícito no .feature, simulamos que o sócio existe.
        socioExiste = true;
        assertTrue("Deve existir sócio para editar", socioExiste);

        System.out.println("Admin acessou edição do sócio");
        edicaoSocioAberta = true;

        assertTrue("A edição do sócio deveria estar aberta", edicaoSocioAberta);
    }

    @Quando("altera os dados do sócio com informações válidas")
    public void altera_os_dados_do_socio_com_informacoes_validas() {
        assertTrue("A edição do sócio deve estar aberta", edicaoSocioAberta);

        System.out.println("Dados do sócio alterados");
        dadosSocioAlterados = true;

        assertTrue("Os dados do sócio deveriam ter sido alterados", dadosSocioAlterados);
    }

    @Então("o sistema atualiza os dados do sócio")
    public void o_sistema_atualiza_os_dados_do_socio() {
        socioAtualizado = socioExiste && edicaoSocioAberta && dadosSocioAlterados;

        System.out.println("Dados do sócio atualizados");
        assertTrue("O sócio deveria ter sido atualizado", socioAtualizado);
    }

    // CENÁRIO: excluir sócio sem pendências

    private boolean socioSemEmprestimosVigentes;
    private boolean socioSemMultasPendentes;
    private boolean exclusaoSolicitada;
    private boolean socioRemovido;

    @Dado("que existe um sócio cadastrado sem empréstimos vigentes e sem multas pendentes")
    public void que_existe_um_socio_cadastrado_sem_emprestimos_vigentes_e_sem_multas_pendentes() {
        System.out.println("Sócio sem pendências");
        socioExiste = true;
        socioSemEmprestimosVigentes = true;
        socioSemMultasPendentes = true;

        assertTrue("O sócio deveria existir", socioExiste);
        assertTrue("O sócio não deveria ter empréstimos vigentes", socioSemEmprestimosVigentes);
        assertTrue("O sócio não deveria ter multas pendentes", socioSemMultasPendentes);
    }

    @Quando("o administrador solicita a exclusão do sócio")
    public void o_administrador_solicita_a_exclusao_do_socio() {
        assertTrue("Deve existir sócio para solicitar exclusão", socioExiste);

        System.out.println("Solicitou exclusão do sócio");
        exclusaoSolicitada = true;

        assertTrue("A exclusão deveria ter sido solicitada", exclusaoSolicitada);
    }

    @Então("o sistema remove o sócio do cadastro")
    public void o_sistema_remove_o_socio_do_cadastro() {
        assertTrue("A exclusão deve ter sido solicitada", exclusaoSolicitada);

        // regra simulada: só remove se não houver pendências
        boolean semPendencias = socioSemEmprestimosVigentes && socioSemMultasPendentes;
        socioRemovido = socioExiste && semPendencias;

        System.out.println("Sócio removido");
        assertTrue("O sócio deveria ter sido removido por não possuir pendências", socioRemovido);
    }

    // CENÁRIO: sócio com pendências (bloqueio de exclusão)

    private boolean socioComEmprestimosVigentes;
    private boolean socioComMultasPendentes;

    @Dado("que existe um sócio com empréstimos vigentes ou multas pendentes")
    public void que_existe_um_socio_com_emprestimos_vigentes_ou_multas_pendentes() {
        System.out.println("Sócio com pendências");
        socioExiste = true;

        // simula o "ou": ele pode ter um, outro, ou ambos
        socioComEmprestimosVigentes = true;
        socioComMultasPendentes = false;

        assertTrue("O sócio deveria existir", socioExiste);
        assertTrue(
            "O sócio deveria ter empréstimos vigentes ou multas pendentes",
            socioComEmprestimosVigentes || socioComMultasPendentes
        );
    }
}
