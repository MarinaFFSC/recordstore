package recordstore.infraestrutura.persistencia.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import recordstore.dominio.administracao.Socio;
import recordstore.dominio.administracao.SocioRepositorio;
import recordstore.dominio.administracao.socio.SocioId;

@Repository
public class SocioRepositorioImpl implements SocioRepositorio {

    @Autowired
    SocioJpaRepository repositorio;

    @Autowired
    JpaMapeador mapeador;

    @Override
    public void salvar(Socio socio) {
        var socioJpa = mapeador.map(socio, SocioJpa.class);
        repositorio.save(socioJpa);
    }

    @Override
    public Socio obter(SocioId id) {
        var socioJpa = repositorio.findById(id.getId()).orElseThrow();
        return mapeador.map(socioJpa, Socio.class);
    }

    // ========== NOVO MÉTODO: EXCLUIR ==========
    @Override
    public void excluir(SocioId id) {
        repositorio.deleteById(id.getId());
    }
}
