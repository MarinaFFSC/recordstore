package recordstore.aplicacao.acervo.midia;

import java.util.List;

public interface MidiaRepositorioAplicacao {
	List<MidisResumo> pesquisarResumos();

	List<MidiaResumoExpandido> pesquisarResumosExpandidos();
}