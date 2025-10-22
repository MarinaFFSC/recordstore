package br.com.recordstore.emprestimos;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Embeddable @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Multa {
  private BigDecimal valorPorDia;
  private Integer diasAtraso;
  private BigDecimal valorTotal;
  private Boolean paga = Boolean.FALSE;
}
