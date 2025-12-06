package recordstore.aplicacao.acervo.midia;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

public class MidiaServicoAplicacao {
	private MidiaRepositorioAplicacao repositorio;

	public MidiaServicoAplicacao(MidiaRepositorioAplicacao repositorio) {
		notNull(repositorio, "O id não pode ser nulo");

		this.repositorio = repositorio;
	}

	public List<MidiaResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}

	public List<MidiaResumoExpandido> pesquisarResumosExpandidos() {
		return repositorio.pesquisarResumosExpandidos();
	}
}