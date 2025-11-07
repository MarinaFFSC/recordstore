package recordstore.aplicacao.acervo.livro;

import recordstore.aplicacao.acervo.autor.AutorResumo;

public interface MidiaResumoExpandido {
	LivroResumo getLivro();

	AutorResumo getAutor();

	int getExemplaresDisponiveis();

	int getTotalExemplares();
}