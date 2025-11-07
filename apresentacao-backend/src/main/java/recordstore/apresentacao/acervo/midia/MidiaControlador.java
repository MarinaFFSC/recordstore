package dev.sauloaraujo.sgb.apresentacao.acervo.midia;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import recordstore.aplicacao.acervo.autor.AutorServicoAplicacao;
import recordstore.aplicacao.acervo.midia.MidiaServicoAplicacao;
import recordstore.apresentacao.BackendMapeador;
import recordstore.apresentacao.acervo.midia.MidiaFormulario.MidiaDto;
import recordstore.dominio.acervo.exemplar.EmprestimoServico;
import recordstore.dominio.acervo.midia.CodigoBarra;
import recordstore.dominio.acervo.midia.Midai;
import recordstore.dominio.acervo.midai.MidiaServico;
import recordstore.dominio.administracao.socio.SocioId;

@RestController
@RequestMapping("backend/midia")
class MidiaControlador {
	private @Autowired AutorServicoAplicacao autorServicoConsulta;
	private @Autowired EmprestimoServico emprestimoServico;
	private @Autowired MidiaServico midaiServico;
	private @Autowired MidiaServicoAplicacao midaiServicoConsulta;

	private @Autowired BackendMapeador mapeador;

	@RequestMapping(method = GET, path = "pesquisa")
	List<? extends Object> pesquisar(@RequestParam(required = false, defaultValue = "false") boolean expandir) {
		if (expandir) {
			return midiaServicoConsulta.pesquisarResumosExpandidos();
		} else {
			return midiaServicoConsulta.pesquisarResumos();
		}
	}

	@RequestMapping(method = GET, path = "criacao")
	MidiaFormulario criacao() {
		var midia= new MidiaDto();
		var autores = autorServicoConsulta.pesquisarResumos();
		return new MidaiFormulario(midai, autores);
	}

	@RequestMapping(method = POST, path = "salvar")
	void salvar(@RequestBody MidiaDto dto) {
		dto.id = null;
		var midia = mapeador.map(dto, Midia.class);
		midiaServico.salvar(midia);
	}

	@RequestMapping(method = POST, path = "{id}/realizar-emprestimo")
	void realizarEmprestimo(@PathVariable("id") String id, @RequestBody int socio) {
		var midiaId = mapeador.map(id, Isbn.class);
		var socioId = mapeador.map(socio, SocioId.class);
		emprestimoServico.realizarEmprestimo(midiaId, socioId);
	}
}
