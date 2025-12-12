package recordstore.infraestrutura.persistencia.jpa;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import recordstore.aplicacao.acervo.midia.MidiaRepositorioAplicacao;
import recordstore.aplicacao.acervo.midia.MidiaResumo;
import recordstore.aplicacao.acervo.midia.MidiaResumoExpandido;
import recordstore.dominio.acervo.midia.CodigoBarra;
import recordstore.dominio.acervo.midia.Midia;
import recordstore.dominio.acervo.midia.MidiaRepositorio;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;

@Entity
@Table(name = "MIDIA")
class MidiaJpa {
    @Id
    String id;
    String descricao;
    String titulo;
    String subtitulo;

    @ManyToMany
    @JoinTable(
        name = "MIDIA_ARTISTA",
        joinColumns = @JoinColumn(name = "MIDIA_ID"),
        inverseJoinColumns = @JoinColumn(name = "ARTISTA_ID")
    )
    @OrderColumn(name = "ARTISTA_ORDEM")
    List<ArtistaJpa> artistas;

    @OneToMany(mappedBy = "midia")
    Set<ExemplarJpa> exemplares;

    @Override
    public String toString() {
        return titulo;
    }
}

interface MidiaJpaRepository extends JpaRepository<MidiaJpa, String> {
    List<MidiaResumo> findMidiaResumoBy();

    // @formatter:off
    @Query("""
            SELECT l AS midia,
                   a AS artista,
                   COUNT(e) AS exemplaresDisponiveis,
                   SIZE(l.exemplares) AS totalExemplares
              FROM MidiaJpa l
              JOIN l.artistas a
         LEFT JOIN l.exemplares e
                ON e.emprestimo IS NULL	    
             WHERE INDEX(a) = 0
          GROUP BY l, a
          ORDER BY l.titulo
            """)
    // @formatter:on	
    List<MidiaResumoExpandido> pesquisarResumosExpandidos();
}

@Repository
class MidiaRepositorioImpl implements MidiaRepositorio, MidiaRepositorioAplicacao {

    @Autowired
    MidiaJpaRepository repositorio;

    @Autowired
    JpaMapeador mapeador;

    @Override
    public void salvar(Midia midia) {
        var midiaJpa = mapeador.map(midia, MidiaJpa.class);
        repositorio.save(midiaJpa);
    }

    @Transactional
    @Override
    public Midia obter(CodigoBarra codigoBarra) {
        var codigo = codigoBarra.getCodigo();
        var midiaJpa = repositorio.findById(codigo).orElseThrow();
        return mapeador.map(midiaJpa, Midia.class);
    }

    // ===== NOVO: EXCLUIR MÍDIA =====
    @Transactional
    @Override
    public void excluir(CodigoBarra codigoBarra) {
        var codigo = codigoBarra.getCodigo();

        var midiaJpa = repositorio.findById(codigo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Mídia não encontrada para o código " + codigo));

        // regra de segurança: não deixa excluir se tiver exemplares vinculados
        if (midiaJpa.exemplares != null && !midiaJpa.exemplares.isEmpty()) {
            throw new IllegalStateException(
                    "Não é possível excluir a mídia porque existem exemplares cadastrados para ela.");
        }

        repositorio.delete(midiaJpa);
    }

    @Override
    public List<MidiaResumo> pesquisarResumos() {
        return repositorio.findMidiaResumoBy();
    }

    @Override
    public List<MidiaResumoExpandido> pesquisarResumosExpandidos() {
        return repositorio.pesquisarResumosExpandidos();
    }
}
