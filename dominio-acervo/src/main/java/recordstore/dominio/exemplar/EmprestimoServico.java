package recordstore.dominio.acervo.exemplar;

import static org.apache.commons.lang3.Validate.notNull;

import recordstore.dominio.acervo.midia.CodigoBarra;
import recordstore.dominio.administracao.socio.SocioId;
import recordstore.dominio.evento.EventoBarramento;

public class EmprestimoServico {
	private final ExemplarRepositorio exemplarRepositorio;
	private final EventoBarramento barramento;

	public EmprestimoServico(ExemplarRepositorio exemplarRepositorio, EventoBarramento barramento) {
		notNull(exemplarRepositorio, "O repositório de exemplares não pode ser nulo");
		notNull(barramento, "O barramento de eventos não pode ser nulo");

		this.exemplarRepositorio = exemplarRepositorio;
		this.barramento = barramento;
	}

	public void realizarEmprestimo(ExemplarId exemplarId, SocioId tomador) {
		notNull(exemplarRepositorio, "O id do exemplar não pode ser nulo");
		notNull(exemplarRepositorio, "O id do tomador não pode ser nulo");

		var exemplar = exemplarRepositorio.obter(exemplarId);
		var evento = exemplar.realizarEmprestimo(tomador);
		exemplarRepositorio.salvar(exemplar);
		barramento.postar(evento);
	}

	public void realizarEmprestimo(CodigoBarra midiaId, SocioId tomador) {
		notNull(midiaId, "O id da midia não pode ser nulo");
		notNull(tomador, "O id do tomador não pode ser nulo");

		var disponiveis = exemplarRepositorio.pesquisarDisponiveis(midiaId);
		if (disponiveis.isEmpty()) {
			throw new IllegalArgumentException("O midia não possui exemplares disponíveis para empréstimo");
		}

		var exemplar = disponiveis.get(0);
		var exemplarId = exemplar.getId();

		realizarEmprestimo(exemplarId, tomador);
	}

	public void devolver(ExemplarId exemplarId) {
		notNull(exemplarId, "O id do exemplar não pode ser nulo");

		var exemplar = exemplarRepositorio.obter(exemplarId);
		var evento = exemplar.devolver();
		exemplarRepositorio.salvar(exemplar);
		barramento.postar(evento);
	}
}