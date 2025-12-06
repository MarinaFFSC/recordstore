package recordstore.aplicacao.acervo.exemplar;

import recordstore.aplicacao.acervo.midia.MidiaResumo;

public interface ExemplarResumoExpandido extends ExemplarResumo {
	String getId();

	MidiaResumo getMidia();

	EmprestimoResumo getEmprestimo();
}