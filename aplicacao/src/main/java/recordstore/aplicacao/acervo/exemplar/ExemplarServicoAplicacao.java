package recordstore.aplicacao.acervo.exemplar;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

public class ExemplarServicoAplicacao {

    private final ExemplarRepositorioAplicacao repositorio;

    public ExemplarServicoAplicacao(ExemplarRepositorioAplicacao repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public List<ExemplarResumo> pesquisarResumos() {
        return repositorio.pesquisarResumos();
    }

    public List<ExemplarResumoExpandido> pesquisarEmprestados() {
        return repositorio.pesquisarEmprestados();
    }

    /**
     * Padrão Iterator:
     * devolve uma coleção que implementa Iterable, ao invés de expor diretamente a List.
     */
    public ExemplaresEmprestados pesquisarEmprestadosIterable() {
        return new ExemplaresEmprestados(repositorio.pesquisarEmprestados());
    }

    public void criarExemplares(String codigoMidia, int quantidade) {
        repositorio.criarExemplares(codigoMidia, quantidade);
    }

    public void removerPorIds(List<String> ids) {
        repositorio.removerPorIds(ids);
    }
}
