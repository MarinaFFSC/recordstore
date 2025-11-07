package recordstore.apresentacao;

import java.util.List;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Component;

import recordstore.apresentacao.acervo.midia.MidiaFormulario.MidiaDto;
import recordstore.dominio.acervo.artistas.ArtistaId;
import recordstore.dominio.acervo.exemplar.ExemplarId;
import recordstore.dominio.acervo.midia.CodigoBarra;
import recordstore.dominio.acervo.midia.Midia;
import recordstore.dominio.administracao.socio.SocioId;

@Component
public class BackendMapeador extends ModelMapper {
	private CepFabrica cepFabrica;

	BackendMapeador() {
		cepFabrica = new CepFabrica();

		addConverter(new AbstractConverter<MidiaDto, Midia>() {
			@Override
			protected Midia convert(MidiaDto source) {
				var id = map(source.id, CodigoBarra.class);
				List<ArtistaId> artistas = map(source.artistas, new TypeToken<List<ArtistaId>>() {
				}.getType());

				return new Midia(id, source.titulo, source.subTitulo, artistas);
			}
		});

		addConverter(new AbstractConverter<String, CodigoBarra>() {
			@Override
			protected CodigoBarra convert(String source) {
				return cepFabrica.construir(source);
			}
		});

		addConverter(new AbstractConverter<Integer, SocioId>() {
			@Override
			protected SocioId convert(Integer source) {
				return new SocioId(source);
			}
		});

		addConverter(new AbstractConverter<Integer, ExemplarId>() {
			@Override
			protected ExemplarId convert(Integer source) {
				return new ExemplarId(source);
			}
		});
	}

	@Override
	public <D> D map(Object source, Class<D> destinationType) {
		return source != null ? super.map(source, destinationType) : null;
	}
}