package recordstore.dominio.acervo.artista;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class Artista{ 
	private final ArtistaId id

	private String nome;

	public Artista(String nome){ 
		id = null;

		setNome(nome);
	}

    public Artista(ArtistaId, String nome) {
		notNull(id, "O id não pode ser nulo");
		this.id = id;

		setNome(nome);
	}

    public ArtistaId getId(){ 
		return id;
	}

	public void setNome(String nome) {
		notNull(nome, "O nome não pode ser nulo");
		notBlank(nome, "O nome não pode estar em branco");

		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	@Override
	public String toString() {
		return nome;
	}
}