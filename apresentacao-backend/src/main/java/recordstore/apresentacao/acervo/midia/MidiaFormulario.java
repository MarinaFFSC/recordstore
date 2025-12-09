package recordstore.apresentacao.acervo.midia;

import java.util.List;

import recordstore.aplicacao.acervo.artista.ArtistaResumo;

public class MidiaFormulario {

    public MidiaDto midia;
    public List<ArtistaResumo> artistas; // <-- aqui era "artista", corrigi pra "artistas"

    public MidiaFormulario(MidiaDto midia, List<ArtistaResumo> artistas) {
        this.midia = midia;
        this.artistas = artistas;
    }

    // DTO usado no formulário e no POST /salvar
    public static class MidiaDto {
        public String id;
        public String titulo;
        public String subTitulo;
        public List<Integer> artistas; // IDs dos artistas
    }
}
