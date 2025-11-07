package recordstore.apresentacao.acervo.artista;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import recordstore.aplicacao.acervo.artista.ArtistaResumo;
import recordstore.aplicacao.acervo.artista.ArtistaServicoAplicacao;
import recordstore.apresentacao.BackendMapeador;
import recordstore.dominio.acervo.artista.ArtistaServico;

@RestController
@RequestMapping("backend/artista")
class ArtaistaControlador {
	private @Autowired ArtistaServico artistaServico;
	private @Autowired ArtistaServicoAplicacao artistaServicoConsulta;

	private @Autowired BackendMapeador mapeador;

	@RequestMapping(method = GET, path = "pesquisa")
	List<ArtistaResumo> pesquisa() {
		return artistaServicoConsulta.pesquisarResumos();
	}
}
