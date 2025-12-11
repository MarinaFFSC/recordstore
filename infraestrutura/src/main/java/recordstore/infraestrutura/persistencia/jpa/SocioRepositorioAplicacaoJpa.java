package recordstore.infraestrutura.persistencia.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import recordstore.aplicacao.administracao.socio.SocioRepositorioAplicacao;
import recordstore.aplicacao.administracao.socio.SocioResumo;

interface SocioJpaAplicacaoRepository extends JpaRepository<SocioJpa, Integer> {

    List<SocioResumo> findSocioResumoBy();
}

@Repository
class SocioRepositorioAplicacaoJpa implements SocioRepositorioAplicacao {

    @Autowired
    private SocioJpaAplicacaoRepository repositorio;

    @Override
    public List<SocioResumo> pesquisarResumos() {
        return repositorio.findSocioResumoBy();
    }

    @Override
    public boolean existePorId(Integer id) {
        return repositorio.existsById(id);
    }
}
