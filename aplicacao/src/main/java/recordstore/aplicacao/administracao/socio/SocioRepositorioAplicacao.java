package recordstore.aplicacao.administracao.socio;

import java.util.List;

public interface SocioRepositorioAplicacao {

    List<SocioResumo> pesquisarResumos();

    boolean existePorId(Integer id);
}
