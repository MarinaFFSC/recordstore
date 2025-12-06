
package recordstore.dominio.acervo.artista;

public interface ArtistaRepositorio {
	void salvar(Artista artista);

	Artista obter(ArtistaId id);
}
