package br.com.recordstore.socios;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.*;

@Entity @Table(name="socios", uniqueConstraints=@UniqueConstraint(name="uk_socio_email", columnNames="email"))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Socio {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;
  @NotBlank private String nome;
  @Email @NotBlank private String email;
  @Enumerated(EnumType.STRING) @Column(nullable=false)
  private StatusSocio status;
  @OneToMany(mappedBy="socio", cascade=CascadeType.ALL)
  private List<Emprestimo> emprestimos = new ArrayList<>();
  public boolean podeRealizarEmprestimo() { return status == StatusSocio.ATIVO; }
}
