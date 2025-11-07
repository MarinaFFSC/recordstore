package recordstore.aplicacao.acervo.Artista;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

public class ArtistaServicoAplicacao {
	private ArtistaRepositorioAplicacao repositorio;

	public ArtistaServicoAplicacao(ArtistaRepositorioAplicacao repositorio) {
		notNull(repositorio, "O id não pode ser nulo");

		this.repositorio = repositorio;
	}

	public List<ArtistaResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}
}