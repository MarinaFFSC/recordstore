package recordstore.infraestrutura.persistencia.jpa;

import java.util.List;

import org.springframework.stereotype.Repository;

import recordstore.aplicacao.administracao.socio.SocioRepositorioAplicacao;
import recordstore.aplicacao.administracao.socio.SocioResumo;

@Repository
public class SocioRepositorioAplicacaoJpa implements SocioRepositorioAplicacao {

    private final SocioJpaRepository repositorio;

    public SocioRepositorioAplicacaoJpa(SocioJpaRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public List<SocioResumo> pesquisarResumos() {
        return repositorio.findAllBy();
    }
}
