package br.com.recordstore.emprestimos;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long>{
  List<Emprestimo> findBySocioIdAndStatusIn(Long socioId, Collection<StatusEmprestimo> statuses);
  List<Emprestimo> findBySocioId(Long socioId);
  boolean existsBySocioIdAndStatus(Long socioId, StatusEmprestimo status);
  boolean existsBySocioIdAndMultaIsNotNull(Long socioId);
  boolean existsByExemplarIdAndStatus(Long exemplarId, StatusEmprestimo status);
}
