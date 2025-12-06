
package recordstore.dominio.acervo.artista;

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
	private String subTitulo;

	private List<ArtistaId> atistas = new ArrayList<>();

	public Midia(CodigoBarra id, String titulo, String subTitulo, List<ArtistaId> artistas) {
		notNull(id, "O id não pode ser nulo");
		this.id = id;

		setTitulo(titulo);
		setSubTitulo(subTitulo);
		setArtistas(artistas);
	}

	public CodigoBarra getId() {
		return id;
	}

	public void setTitulo(String titulo) {
		notNull(titulo, "O título não pode ser nulo");
		notBlank(titulo, "O título não pode estar em branco");

		this.titulo = titulo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setSubTitulo(String subTitulo) {
		if (subTitulo != null) {
			notBlank(titulo, "O subtítulo não pode estar em branco");
		}
		this.subTitulo = subTitulo;
	}

	public String getSubTitulo() {
		return subTitulo;
	}

	private void setArtistas(Collection<ArtistaId> artistas) {
		notNull(artistas, "O vetor de artistas não pode ser nulo");
		notEmpty(artistas, "A midia deve ter pelo menos um artista");

		for (var artista : artistas) {
			adicionarArtista(artista);
		}
	}

	public Collection<ArtistaId> getArtistas() {
		var copia = new ArrayList<ArtistaId>();
		copia.addAll(artistas);
		return copia;
	}

	public void adicionarArtistas(ArtistaId artista) {
		notNull(artistas, "O artista no pode ser nulo");

		artistas.add(artista);
	}

	@Override
	public String toString() {
		return titulo;
	}
}
