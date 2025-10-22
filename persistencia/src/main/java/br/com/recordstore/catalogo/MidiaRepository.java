package br.com.recordstore.catalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface MidiaRepository extends JpaRepository<Midia, Long>{
	Optional<Midia> findByTituloIgnoreCase(String titulo);
}
