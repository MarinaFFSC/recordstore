package br.com.recordstore.emprestimos;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.*;
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long>{
	boolean existsBySocioIdAndMultaPagaFalse(Long socioId);
  List<Emprestimo> findBySocioIdAndStatusIn(Long socioId, Collection<StatusEmprestimo> statuses);
  List<Emprestimo> findBySocioId(Long socioId);
  boolean existsBySocioIdAndStatus(Long socioId, StatusEmprestimo status);
  boolean existsBySocioIdAndMultaIsNotNull(Long socioId);
  boolean existsByExemplarIdAndStatus(Long exemplarId, StatusEmprestimo status);
  List<Emprestimo> findBySocioIdAndStatus(Long socioId, StatusEmprestimo status);

  List<Emprestimo> findByExemplarId(Long exemplarId);

  long countBySocioIdAndStatus(Long socioId, StatusEmprestimo status);

  Optional<Emprestimo> findFirstByExemplarIdAndStatus(Long exemplarId, StatusEmprestimo status);
}
