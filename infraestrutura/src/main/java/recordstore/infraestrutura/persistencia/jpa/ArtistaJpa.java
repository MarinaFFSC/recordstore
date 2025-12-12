package recordstore.infraestrutura.persistencia.jpa;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;

import recordstore.aplicacao.acervo.artista.ArtistaRepositorioAplicacao;
import recordstore.aplicacao.acervo.artista.ArtistaResumo;
import recordstore.dominio.acervo.artista.Artista;
import recordstore.dominio.acervo.artista.ArtistaId;
import recordstore.dominio.acervo.artista.ArtistaRepositorio;

@Entity
@Table(name = "ARTISTA")
class ArtistaJpa {
    @Id
    int id;

    String nome;

    @ManyToMany(mappedBy = "artistas")
    Set<MidiaJpa> midias;

    @Override
    public String toString() {
        return nome;
    }
}

interface ArtistaJpaRepository extends JpaRepository<ArtistaJpa, Integer> {

    List<ArtistaResumo> findArtistaResumoByOrderByNome();
}

@Repository
class ArtistaRepositorioImpl implements ArtistaRepositorio, ArtistaRepositorioAplicacao {

    @Autowired
    ArtistaJpaRepository repositorio;

    @Autowired
    JpaMapeador mapeador;

    @Override
    public void salvar(Artista artista) {
        var artistaJpa = mapeador.map(artista, ArtistaJpa.class);
        repositorio.save(artistaJpa);
    }

    @Override
    public Artista obter(ArtistaId id) {
        var artistaJpa = repositorio.findById(id.getId()).orElseThrow();
        return mapeador.map(artistaJpa, Artista.class);
    }

    // NOVO: excluir com validação de vínculo com mídias
    @Transactional
    @Override
    public void excluir(ArtistaId id) {
        var artistaJpa = repositorio.findById(id.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Artista não encontrado para o id " + id.getId()));

        // Regra: não deixa excluir se estiver vinculado a alguma mídia
        if (artistaJpa.midias != null && !artistaJpa.midias.isEmpty()) {
            throw new IllegalStateException(
                    "Não é possível excluir o artista pois existem mídias vinculadas a ele.");
        }

        repositorio.delete(artistaJpa);
    }

    @Override
    public List<ArtistaResumo> pesquisarResumos() {
        return repositorio.findArtistaResumoByOrderByNome();
    }
}
