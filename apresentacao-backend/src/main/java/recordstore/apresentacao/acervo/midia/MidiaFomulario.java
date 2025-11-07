package recordstore.apresentacao.acervo.midia;

import java.util.List;

import recordstoreaplicacao.acervo.artista.ArtistaResumo;

public class MidiaFormulario {
	public MidiaDto midia;
	public List<ArtistaResumo> artista;

	public MidiaFormulario(MidiaDto midai, List<ArtistaResumo> artistas) {
		this.midia = midia;
		this.artistas = artistas;
	}

	public static class MidiaDto {
		public String id;
		public String titulo;
		public String subTitulo;
		public List<Integer> artistas;
	}
}