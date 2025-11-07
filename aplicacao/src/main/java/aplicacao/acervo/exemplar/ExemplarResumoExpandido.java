package recordstore.aplicacao.acervo.exemplar;

import recordstore.aplicacao.acervo.livro.LivroResumo;

public interface ExemplarResumoExpandido extends ExemplarResumo {
	String getId();

	LivroResumo getLivro();

	EmprestimoResumo getEmprestimo();
}