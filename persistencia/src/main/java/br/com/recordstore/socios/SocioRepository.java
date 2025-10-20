package br.com.recordstore.socios;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface SocioRepository extends JpaRepository<Socio, Long>{
  Optional<Socio> findByEmail(String email);
}
