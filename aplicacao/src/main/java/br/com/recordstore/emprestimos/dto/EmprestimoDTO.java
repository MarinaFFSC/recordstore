package br.com.recordstore.emprestimos.dto;
import java.time.LocalDate;
import java.math.BigDecimal;
import br.com.recordstore.emprestimos.StatusEmprestimo;

import lombok.Data;

@Data
public class EmprestimoDTO {
    private Long id;
    private Long socioId;
    private Long exemplarId;
    private StatusEmprestimo status;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucaoReal;
    private BigDecimal valorTotal;
    private String mensagem; // campo de mensagem de retorno

    public EmprestimoDTO withMensagem(String m) {
        this.mensagem = m;
        return this;
    }
}
