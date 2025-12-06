package recordstore.dominio.acervo.exemplar;

import java.util.List;

import recordstore.dominio.acervo.midia.CodigoBarra;

public interface ExemplarRepositorio {
	void salvar(Exemplar exemplar);

	Exemplar obter(ExemplarId id);

	List<Exemplar> pesquisarDisponiveis(CodigoBarra midia);
}