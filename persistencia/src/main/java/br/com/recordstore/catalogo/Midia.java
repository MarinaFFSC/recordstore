package br.com.recordstore.catalogo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.*;

@Entity @Table(name="midias")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Midia {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @NotBlank private String titulo;
  @NotBlank private String artista;
  private String genero;
  private int ano;
  @Enumerated(EnumType.STRING) private TipoMidia tipo;
  @OneToMany(mappedBy="midia", cascade=CascadeType.ALL, orphanRemoval=true)
  private List<Exemplar> exemplares = new ArrayList<>();
}
