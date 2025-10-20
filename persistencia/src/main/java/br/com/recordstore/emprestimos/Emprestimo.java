package br.com.recordstore.emprestimos;
import br.com.recordstore.socios.Socio;
import br.com.recordstore.catalogo.Exemplar;
import br.com.recordstore.catalogo.StatusExemplar;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity @Table(name="emprestimos")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Emprestimo {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false) private Socio socio;
  @ManyToOne(optional=false) private Exemplar exemplar;
  private LocalDate dataEmprestimo;
  private LocalDate dataPrevistaDevolucao;
  private LocalDate dataDevolucaoReal;
  private Boolean danificado;
  @Embedded private Multa multa;
  @Enumerated(EnumType.STRING) private StatusEmprestimo status;

  public void renovar(LocalDate novaData){
    if (dataDevolucaoReal != null) throw new IllegalStateException("Empréstimo já finalizado");
    if (LocalDate.now().isAfter(dataPrevistaDevolucao)) throw new IllegalStateException("Não é possível renovar empréstimo em atraso");
    this.dataPrevistaDevolucao = novaData;
  }

  public void registrarDevolucao(LocalDate data, boolean danificado){
    this.dataDevolucaoReal = data;
    this.danificado = danificado;
    this.status = StatusEmprestimo.FINALIZADO;
  }

  public void calcularMulta(BigDecimal valorDiario){
    if (dataDevolucaoReal == null) return;
    int atraso = Math.max(0, (int)java.time.temporal.ChronoUnit.DAYS.between(dataPrevistaDevolucao, dataDevolucaoReal));
    if (atraso > 0){
      this.multa = Multa.builder()
        .valorPorDia(valorDiario)
        .diasAtraso(atraso)
        .valorTotal(valorDiario.multiply(BigDecimal.valueOf(atraso)))
        .build();
      this.status = StatusEmprestimo.ATRASADO;
    }
  }
}
