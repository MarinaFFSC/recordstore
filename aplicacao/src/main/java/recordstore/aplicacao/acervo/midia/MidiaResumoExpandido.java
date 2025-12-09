package recordstore.aplicacao.acervo.midia;

import recordstore.aplicacao.acervo.artista.ArtistaResumo;

public interface MidiaResumoExpandido {
	MidiaResumo getMidia();

	ArtistaResumo getArtista();

	int getExemplaresDisponiveis();

	int getTotalExemplares();
}