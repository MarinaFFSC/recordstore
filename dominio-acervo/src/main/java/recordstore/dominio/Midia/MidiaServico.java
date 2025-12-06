
package recordstore.dominio.acervo.midia;

import static org.apache.commons.lang3.Validate.notNull;

public class MidiaServico {
	private final MidiaRepositorio midiaRepositorio;

	public MidiaServico(MidiaRepositorio midiaRepositorio) {
		notNull(midiaRepositorio, "O repositório de midias não pode ser nulo");

		this.midiaRepositorio = midiaRepositorio;
	}

	public void salvar(Midia midia) {
		notNull(midia, "A midia não pode ser nulo");

		muidiaRepositorio.salvar(midia);
	}

	public Midia obter(CodigoBarra id) {
		notNull(id, "O Codigo de Barra da midia não pode ser nulo");

		return midiaRepositorio.obter(id);
	}
}
