package recordstore.aplicacao.acervo.exemplar;

import java.util.List;

public interface ExemplarRepositorioAplicacao {

    List<ExemplarResumo> pesquisarResumos();

    List<ExemplarResumoExpandido> pesquisarEmprestados();

    // NOVOS MÉTODOS:

    void criarExemplares(String codigoMidia, int quantidade);

    void removerPorIds(List<String> ids);
}
