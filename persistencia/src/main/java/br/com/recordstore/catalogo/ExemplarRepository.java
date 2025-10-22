package br.com.recordstore.catalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ExemplarRepository extends JpaRepository<Exemplar, Long>{
  Optional<Exemplar> findFirstByMidiaIdAndStatus(Long midiaId, StatusExemplar status);
  boolean existsByMidiaIdAndNumero(Long midiaId, Integer numero);
  long countByMidiaId(Long midiaId);

  Optional<Exemplar> findByMidiaIdAndNumero(Long midiaId, Integer numero);
  List<Exemplar> findAllByMidiaId(Long midiaId);
  

  boolean existsByIdAndStatus(Long id, StatusExemplar status);
}
