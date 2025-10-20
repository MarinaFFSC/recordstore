package br.com.recordstore.emprestimos.dto;
import java.time.LocalDate;
import java.math.BigDecimal;
import br.com.recordstore.emprestimos.StatusEmprestimo;
public record EmprestimoDTO(Long id, Long socioId, Long exemplarId, LocalDate dataEmprestimo, LocalDate dataPrevistaDevolucao, LocalDate dataDevolucaoReal, Boolean danificado, StatusEmprestimo status, BigDecimal multa) {}
