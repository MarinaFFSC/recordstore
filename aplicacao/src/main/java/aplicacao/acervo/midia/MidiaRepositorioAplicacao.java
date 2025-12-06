package recordstore.aplicacao.acervo.midia;

import java.util.List;

public interface MidiaRepositorioAplicacao {

    List<MidiaResumo> pesquisarResumos();

    List<MidiaResumoExpandido> pesquisarResumosExpandidos();
}
