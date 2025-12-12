package recordstore.infraestrutura.persistencia.jpa;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import recordstore.aplicacao.analise.EmprestimoAdminResumo;
import recordstore.aplicacao.analise.EmprestimoRegistroRepositorioAplicacao;
import recordstore.dominio.acervo.exemplar.Emprestimo;
import recordstore.dominio.acervo.exemplar.ExemplarId;
import recordstore.dominio.analise.emprestimo.EmprestimoRegistro;
import recordstore.dominio.analise.emprestimo.EmprestimoRegistroRepositorio;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// >>> ADD IMPORTS
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Entity
@Table(name = "EMPRESTIMO_REGISTRO")
@AttributeOverrides({
        @AttributeOverride(name = "emprestimo.periodo.inicio", column = @Column(name = "EMPRESTIMO_PERIODO_INICIO")),
        @AttributeOverride(name = "emprestimo.periodo.fim", column = @Column(name = "EMPRESTIMO_PERIODO_FIM")) })
class EmprestimoRegistroJpa {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    int id;

    @ManyToOne
    ExemplarJpa exemplar;

    @Embedded
    EmprestimoJpa emprestimo;

    LocalDate devolucao;
}

interface EmprestimoRegistroJpaRepository extends JpaRepository<EmprestimoRegistroJpa, Integer> {

    Optional<EmprestimoRegistroJpa> findByExemplarIdAndEmprestimoAndDevolucaoIsNull(
            int exemplarId,
            EmprestimoJpa emprestimo
    );

    @Modifying
    @Query("""
        delete from EmprestimoRegistroJpa er
         where er.exemplar.id in :exemplarIds
    """)
    void deleteByExemplarIds(@Param("exemplarIds") List<Integer> exemplarIds);

    @Query("""
        select er.id as id,
               ex.id as exemplarId,
               m.titulo as midiaTitulo,
               s.nome as socioNome,
               er.emprestimo.periodo.inicio as emprestimoInicio,
               er.emprestimo.periodo.fim as emprestimoFim,
               er.devolucao as devolucao
          from EmprestimoRegistroJpa er
          join er.exemplar ex
          join ex.midia m
          join er.emprestimo.tomador s
      order by er.emprestimo.periodo.inicio desc, er.id desc
    """)
    List<EmprestimoAdminResumo> listarParaAdmin();
}

@Repository
class EmprestimoRegistroRepositorioImpl
        implements EmprestimoRegistroRepositorio, EmprestimoRegistroRepositorioAplicacao {

    @Autowired
    EmprestimoRegistroJpaRepository repositorio;

    @Autowired
    JpaMapeador mapeador;

    @Override
    public void salvar(EmprestimoRegistro emprestimoRegistro) {
        var emprestimoRegistroJpa = mapeador.map(emprestimoRegistro, EmprestimoRegistroJpa.class);
        repositorio.save(emprestimoRegistroJpa);
    }

    @Override
    public EmprestimoRegistro buscar(ExemplarId exemplar, Emprestimo emprestimo) {
        var exemplarId = exemplar.getId();
        var emprestimoJpa = mapeador.map(emprestimo, EmprestimoJpa.class);
        var emprestimoRegistroJpa = repositorio
                .findByExemplarIdAndEmprestimoAndDevolucaoIsNull(exemplarId, emprestimoJpa)
                .orElseThrow();
        return mapeador.map(emprestimoRegistroJpa, EmprestimoRegistro.class);
    }

    @Override
    public List<EmprestimoAdminResumo> listarParaAdmin() {
        return repositorio.listarParaAdmin();
    }
}
