package recordstore.infraestrutura.persistencia.jpa;

import java.util.List;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import recordstore.dominio.analise.emprestimo.EmprestimoRegistro;
import recordstore.dominio.analise.emprestimo.EmprestimoRegistroId;
import recordstore.dominio.acervo.artista.Artista;
import recordstore.dominio.acervo.artista.ArtistaId;
import recordstore.dominio.acervo.exemplar.Emprestimo;
import recordstore.dominio.acervo.exemplar.Exemplar;
import recordstore.dominio.acervo.exemplar.ExemplarId;
import recordstore.dominio.acervo.exemplar.Periodo;
import recordstore.dominio.acervo.midia.CodigoBarra;
import recordstore.dominio.acervo.midia.CodigoBarraFabrica;
import recordstore.dominio.acervo.midia.Midia;
import recordstore.dominio.administracao.Email;
import recordstore.dominio.administracao.Socio;
import recordstore.dominio.administracao.socio.SocioId;

@Component
class JpaMapeador extends ModelMapper {
	private CodigoBarraFabrica codigoBarraFabrica;

	private @Autowired MidiaJpaRepository midiaRepositorio;
	private @Autowired SocioJpaRepository socioRepositorio;

	JpaMapeador() {
		codigoBarraFabrica = new CodigoBarraFabrica();

		var configuracao = getConfiguration();
		configuracao.setFieldMatchingEnabled(true);
		configuracao.setFieldAccessLevel(AccessLevel.PRIVATE);

		addConverter(new AbstractConverter<ArtistaJpa, Artista>() {
			@Override
			protected Artista convert(ArtistaJpa source) {
				var id = map(source.id, ArtistaId.class);
				return new Artista(id, source.nome);
			}
		});

		addConverter(new AbstractConverter<Integer, ArtistaId>() {
			@Override
			protected ArtistaId convert(Integer source) {
				return new ArtistaId(source);
			}
		});

		addConverter(new AbstractConverter<ArtistaJpa, ArtistaId>() {
			@Override
			protected ArtistaId convert(ArtistaJpa source) {
				return map(source.id, ArtistaId.class);
			}
		});

		addConverter(new AbstractConverter<MidiaJpa, Midia>() {
		    @Override
		    protected Midia convert(MidiaJpa source) {
		        var id = map(source.id, CodigoBarra.class);
		        List<ArtistaId> artistas = map(source.artistas, new TypeToken<List<ArtistaId>>() {
		        }.getType());
		        
		        return new Midia(
		                id,
		                source.titulo,
		                source.subtitulo,
		                null, 
		                artistas          
		        );
		    }
		});
		addConverter(new AbstractConverter<String, CodigoBarra>() {
			@Override
			protected CodigoBarra convert(String source) {
				return codigoBarraFabrica.construir(source);
			}
		});

		addConverter(new AbstractConverter<MidiaJpa, CodigoBarra>() {
			@Override
			protected CodigoBarra convert(MidiaJpa source) {
				return map(source.id, CodigoBarra.class);
			}
		});

		addConverter(new AbstractConverter<SocioJpa, Socio>() {
			@Override
			protected Socio convert(SocioJpa source) {
				var id = map(source.id, SocioId.class);
				var email = map(source.email, Email.class);
				return new Socio(id, source.nome, email);
			}
		});

		addConverter(new AbstractConverter<Integer, SocioId>() {
			@Override
			protected SocioId convert(Integer source) {
				return new SocioId(source);
			}
		});

		addConverter(new AbstractConverter<String, Email>() {
			@Override
			protected Email convert(String source) {
				return new Email(source);
			}
		});

		addConverter(new AbstractConverter<ExemplarJpa, Exemplar>() {
			@Override
			protected Exemplar convert(ExemplarJpa source) {
				var id = map(source.id, ExemplarId.class);
				var midia = map(source.midia, CodigoBarra.class);
				var emprestimo = map(source.emprestimo, Emprestimo.class);
				return new Exemplar(id, midia, emprestimo);
			}
		});

		addConverter(new AbstractConverter<Integer, ExemplarId>() {
			@Override
			protected ExemplarId convert(Integer source) {
				return new ExemplarId(source);
			}
		});

		addConverter(new AbstractConverter<CodigoBarra, MidiaJpa>() {
			@Override
			protected MidiaJpa convert(CodigoBarra source) {
				return midiaRepositorio.findById(source.getCodigo()).get();
			}
		});

		addConverter(new AbstractConverter<EmprestimoJpa, Emprestimo>() {
			@Override
			protected Emprestimo convert(EmprestimoJpa source) {
				var periodo = map(source.periodo, Periodo.class);
				var tomador = map(source.tomador, SocioId.class);
				return new Emprestimo(periodo, tomador);
			}
		});

		addConverter(new AbstractConverter<PeriodoJpa, Periodo>() {
			@Override
			protected Periodo convert(PeriodoJpa source) {
				return new Periodo(source.inicio, source.fim);
			}
		});

		addConverter(new AbstractConverter<SocioJpa, SocioId>() {
			@Override
			protected SocioId convert(SocioJpa source) {
				return map(source.id, SocioId.class);
			}
		});

		addConverter(new AbstractConverter<SocioId, SocioJpa>() {
			@Override
			protected SocioJpa convert(SocioId source) {
				return socioRepositorio.findById(source.getId()).get();
			}
		});

		addConverter(new AbstractConverter<EmprestimoRegistroJpa, EmprestimoRegistro>() {
			@Override
			protected EmprestimoRegistro convert(EmprestimoRegistroJpa source) {
				var id = map(source.id, EmprestimoRegistroId.class);
				var exemplar = map(source.exemplar.id, ExemplarId.class);
				var emprestimo = map(source.emprestimo, Emprestimo.class);
				return new EmprestimoRegistro(id, exemplar, emprestimo, source.devolucao);
			}
		});

		addConverter(new AbstractConverter<Integer, EmprestimoRegistroId>() {
			@Override
			protected EmprestimoRegistroId convert(Integer source) {
				return new EmprestimoRegistroId(source);
			}
		});
	}

	@Override
	public <D> D map(Object source, Class<D> destinationType) {
		return source != null ? super.map(source, destinationType) : null;
	}
}
