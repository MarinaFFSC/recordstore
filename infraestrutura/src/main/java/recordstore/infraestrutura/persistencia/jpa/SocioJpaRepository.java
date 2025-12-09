package recordstore.infraestrutura.persistencia.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import recordstore.aplicacao.administracao.socio.SocioResumo;

public interface SocioJpaRepository extends JpaRepository<SocioJpa, Integer> {

    // projection para camada de aplicação
    List<SocioResumo> findAllBy();
}
