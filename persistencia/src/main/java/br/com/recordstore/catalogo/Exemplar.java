package br.com.recordstore.catalogo;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="exemplares", uniqueConstraints=@UniqueConstraint(name="uk_exemplar_midia_num", columnNames={"midia_id","numero"}))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Exemplar {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false) @JoinColumn(name="midia_id")
  private Midia midia;
  @Column(nullable=false) private Integer numero; // número sequencial por mídia (dup validation)
  @Enumerated(EnumType.STRING) private StatusExemplar status;
  @Enumerated(EnumType.STRING) private CondicaoExemplar condicao;
  public void marcarComoEmprestado(){ this.status = StatusExemplar.EMPRESTADO; }
  public void marcarComoDisponivel(){ this.status = StatusExemplar.DISPONIVEL; }
  public void marcarComoIndisponivel(){ this.status = StatusExemplar.INDISPONIVEL; }
  public void atualizarCondicao(CondicaoExemplar c){ this.condicao = c; }
}
