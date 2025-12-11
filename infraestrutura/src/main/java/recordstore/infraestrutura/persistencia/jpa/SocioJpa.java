package recordstore.infraestrutura.persistencia.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "SOCIO")
public class SocioJpa {

    @Id
    int id;
    String senha;
    String nome;
    String email;

    @Override
    public String toString() {
        return nome;
    }
}
