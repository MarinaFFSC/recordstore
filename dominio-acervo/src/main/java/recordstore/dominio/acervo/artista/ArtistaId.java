package recordstore.dominio.acervo.artista;

import static org.apache.commons.lang3.Validate.isTrue;

import java.util.Objects;

public class ArtistaId {

    private final int id;

    public ArtistaId(int id) {
        isTrue(id > 0, "O id deve ser positivo");
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) return true;
        if (outro == null || getClass() != outro.getClass()) return false;
        ArtistaId artistaId = (ArtistaId) outro;
        return id == artistaId.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }
}
