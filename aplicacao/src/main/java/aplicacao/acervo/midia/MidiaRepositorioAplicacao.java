package recordstore.aplicacao.acervo.livro;

import java.util.List;

public interface MidiaRepositorioAplicacao {
	List<LivroResumo> pesquisarResumos();

	List<LivroResumoExpandido> pesquisarResumosExpandidos();
}