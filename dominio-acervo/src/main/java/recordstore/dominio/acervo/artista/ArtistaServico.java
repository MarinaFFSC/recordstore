package recordstore.dominio.acervo.artista;

import static org.apache.commons.lang3.Validate.notNull;

public class ArtistaServico {
    private final ArtistaRepositorio artistaRepositorio;

    public ArtistaServico(ArtistaRepositorio artistaRepositorio) {
        notNull(artistaRepositorio, "O repositório de artistas não pode ser nulo");
        this.artistaRepositorio = artistaRepositorio;
    }

    public void salvar(Artista artista) {
        notNull(artista, "O artista não pode ser nulo");
        artistaRepositorio.salvar(artista);
    }

    public Artista obter(ArtistaId id) {
        notNull(id, "O id do artista não pode ser nulo");
        return artistaRepositorio.obter(id);
    }

    // NOVO: excluir
    public void excluir(ArtistaId id) {
        notNull(id, "O id do artista não pode ser nulo");
        artistaRepositorio.excluir(id);
    }
}
