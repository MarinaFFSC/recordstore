package recordstore.dominio.acervo.exemplar;

import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDate;

import recordstore.dominio.acervo.midia.CodigoBarra;
import recordstore.dominio.administracao.socio.SocioId;

public class Exemplar {
	private final ExemplarId id;

	private CodigoBarra midia;
	private Emprestimo emprestimo;

	public Exemplar(CodigoBarra midia, Emprestimo emprestimo) {
		id = null;

		setMidia(midia);
		setEmprestimo(emprestimo);
	}

	public Exemplar(ExemplarId id, CodigoBarra midia, Emprestimo emprestimo) {
		notNull(id, "O id não pode ser nulo");
		this.id = id;

		setMidia(midia);
		setEmprestimo(emprestimo);
	}

	public ExemplarId getId() {
		return id;
	}

	private void setMidia(CodigoBarra midia) {
		notNull(id, "O midia não pode ser nulo");
		this.midia = midia;
	}

	public CodigoBarra getMidia() {
		return midia;
	}

	private void setEmprestimo(Emprestimo emprestimo) {
		this.emprestimo = emprestimo;
	}

	public Emprestimo getEmprestimo() {
		return emprestimo;
	}

	public boolean disponivel() {
		return emprestimo == null;
	}

	public boolean indisponivel() {
		return emprestimo != null;
	}

	public boolean emprestado() {
		return indisponivel();
	}

	@Override
	public String toString() {
		return id.toString();
	}

	public EmprestimoRealizadoEvento realizarEmprestimo(SocioId tomador) {
		if (indisponivel()) {
			throw new IllegalStateException("O exemplar não está disponível no momento");
		}

		var inicio = LocalDate.now();
		var fim = inicio.plusDays(7);
		var periodo = new Periodo(inicio, fim);
		emprestimo = new Emprestimo(periodo, tomador);

		return new EmprestimoRealizadoEvento(this);
	}

	public ExemplarDevolvidoEvento devolver() {
		if (!emprestado()) {
			throw new IllegalArgumentException("O exemplar não está emprestado");
		}

		var ultimoEmprestimo = emprestimo;
		emprestimo = null;
		return new ExemplarDevolvidoEvento(this, ultimoEmprestimo);
	}

	public static class ExemplarEvento {
		private final Exemplar exemplar;

		private ExemplarEvento(Exemplar exemplar) {
			this.exemplar = exemplar;
		}

		public Exemplar getExemplar() {
			return exemplar;
		}
	}

	public static class EmprestimoRealizadoEvento extends ExemplarEvento {
		private EmprestimoRealizadoEvento(Exemplar exemplar) {
			super(exemplar);
		}
	}

	public static class ExemplarDevolvidoEvento extends ExemplarEvento {
		private final Emprestimo emprestimo;

		private ExemplarDevolvidoEvento(Exemplar exemplar, Emprestimo emprestimo) {
			super(exemplar);

			this.emprestimo = emprestimo;
		}

		public Emprestimo getEmprestimo() {
			return emprestimo;
		}
	}

	public static class EmprestimoVencido extends ExemplarEvento {
		private EmprestimoVencido(Exemplar exemplar) {
			super(exemplar);
		}
	}
