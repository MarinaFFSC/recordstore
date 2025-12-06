package recordstore.dominio.acervo.midia;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import recordstore.dominio.acervo.artista.ArtistaId;

public class Midia {

    private final CodigoBarra id;

    private String titulo;
    private String subtitulo;
    private String descricao;

    private final List<ArtistaId> artistas = new ArrayList<>();

    public Midia(CodigoBarra id, String titulo, String subtitulo, String descricao,
                 Collection<ArtistaId> artistas) {

        notNull(id, "O código de barras não pode ser nulo");
        notBlank(titulo, "O título não pode estar em branco");
        notNull(artistas, "A lista de artistas não pode ser nula");
        notEmpty(artistas, "Deve haver pelo menos um artista");

        this.id = id;
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.descricao = descricao;
        this.artistas.addAll(artistas);
    }

    public CodigoBarra getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Collection<ArtistaId> getArtistas() {
        return new ArrayList<>(artistas);
    }

    @Override
    public String toString() {
        return titulo;
    }
}
