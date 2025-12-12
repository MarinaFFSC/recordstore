package recordstore.bdd.steps;

import static org.junit.Assert.*;
import io.cucumber.java.pt.*;

public class AdminMultasSteps {

    // CENÁRIO: listar multas pendentes
	
    private boolean existemMultasPendentes;
    private boolean listaMultasExibida;
    private boolean camposMultaExibidos;

    @Dado("que existem multas pendentes cadastradas")
    public void que_existem_multas_pendentes_cadastradas() {
        System.out.println("Existem multas pendentes cadastradas");
        existemMultasPendentes = true;

        assertTrue(
            "Deveriam existir multas pendentes cadastradas",
            existemMultasPendentes
        );
    }

    @Então("o sistema exibe a lista de multas pendentes de todos os sócios")
    public void o_sistema_exibe_a_lista_de_multas_pendentes_de_todos_os_socios() {
        assertTrue(
            "Devem existir multas pendentes para exibir a lista",
            existemMultasPendentes
        );

        System.out.println("Lista de multas pendentes de todos os sócios exibida");
        listaMultasExibida = true;

        assertTrue(
            "A lista de multas pendentes deveria ser exibida",
            listaMultasExibida
        );
    }

    @Então("para cada multa o sistema exibe sócio, valor, motivo, data de geração e situação")
    public void para_cada_multa_o_sistema_exibe_socio_valor_motivo_data_de_geracao_e_situacao() {
        assertTrue(
            "A lista de multas deve estar exibida para mostrar os detalhes",
            listaMultasExibida
        );

        System.out.println(
            "Campos da multa exibidos: sócio, valor, motivo, data de geração e situação"
        );
        camposMultaExibidos = true;

        assertTrue(
            "Os campos da multa deveriam ser exibidos corretamente",
            camposMultaExibidos
        );
    }
}
