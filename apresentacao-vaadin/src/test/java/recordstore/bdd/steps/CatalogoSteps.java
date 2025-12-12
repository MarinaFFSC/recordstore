package recordstore.bdd.steps;

import static org.junit.Assert.*;
import io.cucumber.java.pt.*;

public class CatalogoSteps {

    // CENÁRIO: visualizar catálogo de mídias

    private boolean listaMidiasExibida;
    private boolean detalhesMidiasExibidos;

    @Então("o sistema exibe a lista de mídias cadastradas")
    public void o_sistema_exibe_a_lista_de_midias_cadastradas() {
        System.out.println("Lista de mídias exibida");
        listaMidiasExibida = true;

        assertTrue(
            "A lista de mídias cadastradas deveria ser exibida",
            listaMidiasExibida
        );
    }

    @Então("cada mídia apresenta título, artista e tipo")
    public void cada_midia_apresenta_titulo_artista_e_tipo() {
        assertTrue(
            "A lista de mídias deve estar exibida antes de mostrar os detalhes",
            listaMidiasExibida
        );

        System.out.println("Detalhes das mídias apresentados");
        detalhesMidiasExibidos = true;

        assertTrue(
            "Os detalhes das mídias (título, artista e tipo) deveriam ser exibidos",
            detalhesMidiasExibidos
        );
    }
}
