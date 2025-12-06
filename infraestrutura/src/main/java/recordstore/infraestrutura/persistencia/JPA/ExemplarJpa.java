
package recordstore.infraestrutura.persistencia.jpa;

import java.time.LocalDate;
import java.util.List;

import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import recordstore.aplicacao.acervo.exemplar.ExemplarRepositorioAplicacao;
import recordstore.aplicacao.acervo.exemplar.ExemplarResumo;
import recordstore.aplicacao.acervo.exemplar.ExemplarResumoExpandido;
import recordstore.dominio.acervo.exemplar.Exemplar;
import recordstore.dominio.acervo.exemplar.ExemplarId;
import recordstore.dominio.acervo.exemplar.ExemplarRepositorio;
import recordstore.dominio.acervo.midia.CodigoBarra;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "EXEMPLAR")
@AttributeOverrides({
		@AttributeOverride(name = "emprestimo.periodo.inicio", column = @Column(name = "EMPRESTIMO_PERIODO_INICIO")),
		@AttributeOverride(name = "emprestimo.periodo.fim", column = @Column(name = "EMPRESTIMO_PERIODO_FIM")) })
class ExemplarJpa {
	@Id
	int id;

	@ManyToOne()
	MidiaJpa midia;

	@Embedded
	EmprestimoJpa emprestimo;
}

@Embeddable
class EmprestimoJpa {
	@Embedded
	PeriodoJpa periodo;

	@ManyToOne
	@JoinColumn(name = "EMPRESTIMO_TOMADOR_ID")
	SocioJpa tomador;
}

@Embeddable
class PeriodoJpa {
	LocalDate inicio;
	LocalDate fim;
}

interface ExemplarJpaRepository extends JpaRepository<ExemplarJpa, Integer> {
	List<ExemplarJpa> findByMidiaIdAndEmprestimoIsNull(String codigoBarra);

	List<ExemplarResumo> findExemplarResumoByOrderByMidiaTitulo();

	// @formatter:off
	@Query("""
			SELECT e
			  FROM ExemplarJpa e
		JOIN FETCH e.midia
	    JOIN FETCH e.emprestimo.tomador
          ORDER BY e.midia.titulo,
                   e.id
			""")
	// @formatter:on
	List<ExemplarResumoExpandido> findExemplarResumoExpandidoByEmprestimoIsNotNull();

}

@Repository
class ExemplarRepositorioImpl implements ExemplarRepositorio, ExemplarRepositorioAplicacao {
	@Autowired
	ExemplarJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public void salvar(Exemplar exemplar) {
		var exemplarJpa = mapeador.map(exemplar, ExemplarJpa.class);
		repositorio.save(exemplarJpa);
	}

	@Override
	public Exemplar obter(ExemplarId id) {
		var exemplarJpa = repositorio.findById(id.getId()).get();
		return mapeador.map(exemplarJpa, Exemplar.class);
	}

	@Override
	public List<Exemplar> pesquisarDisponiveis(Isbn midia) {
		var exemplares = repositorio.findByMidiaIdAndEmprestimoIsNull(midia.toString());
		return mapeador.map(exemplares, new TypeToken<List<Exemplar>>() {
		}.getType());
	}

	@Override
	public List<ExemplarResumo> pesquisarResumos() {
		return repositorio.findExemplarResumoByOrderByMidiaTitulo();
	}

	@Override
	public List<ExemplarResumoExpandido> pesquisarEmprestados() {
		return repositorio.findExemplarResumoExpandidoByEmprestimoIsNotNull();
	}
}
