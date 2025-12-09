package recordstore.apresentacao;

import java.util.List;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Component;

import recordstore.apresentacao.acervo.midia.MidiaFormulario.MidiaDto;
import recordstore.dominio.acervo.artista.ArtistaId;
import recordstore.dominio.acervo.exemplar.ExemplarId;
import recordstore.dominio.acervo.midia.CodigoBarra;
import recordstore.dominio.acervo.midia.CodigoBarraFabrica;
import recordstore.dominio.acervo.midia.Midia;
import recordstore.dominio.administracao.socio.SocioId;

@Component
public class BackendMapeador extends ModelMapper {

    private final CodigoBarraFabrica codigoBarraFabrica;

    public BackendMapeador() {
        this.codigoBarraFabrica = new CodigoBarraFabrica();

        // MidiaDto (do formulário) -> Midia (domínio)
        addConverter(new AbstractConverter<MidiaDto, Midia>() {
            @Override
            protected Midia convert(MidiaDto source) {
                if (source == null) {
                    return null;
                }

                CodigoBarra id = codigoBarraFabrica.construir(source.id);

                List<ArtistaId> artistas = map(
                        source.artistas, // List<Integer> de ids
                        new TypeToken<List<ArtistaId>>() {}.getType()
                );

                // descricao = null, pois não vem do formulário
                return new Midia(
                        id,
                        source.titulo,
                        source.subTitulo,
                        null,
                        artistas
                );
            }
        });

        // String -> CodigoBarra
        addConverter(new AbstractConverter<String, CodigoBarra>() {
            @Override
            protected CodigoBarra convert(String source) {
                return codigoBarraFabrica.construir(source);
            }
        });

        // Integer -> SocioId
        addConverter(new AbstractConverter<Integer, SocioId>() {
            @Override
            protected SocioId convert(Integer source) {
                return new SocioId(source);
            }
        });

        // Integer -> ExemplarId
        addConverter(new AbstractConverter<Integer, ExemplarId>() {
            @Override
            protected ExemplarId convert(Integer source) {
                return new ExemplarId(source);
            }
        });

        // Integer -> ArtistaId (necessário para mapear List<Integer> -> List<ArtistaId>)
        addConverter(new AbstractConverter<Integer, ArtistaId>() {
            @Override
            protected ArtistaId convert(Integer source) {
                return new ArtistaId(source);
            }
        });
    }

    @Override
    public <D> D map(Object source, Class<D> destinationType) {
        return source != null ? super.map(source, destinationType) : null;
    }
}
