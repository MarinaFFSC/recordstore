package recordstore.aplicacao.acervo.exemplar;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Coleção de exemplares emprestados.
 * Aplica o padrão Iterator ao expor a iteração via Iterable/iterator()
 * sem expor a estrutura interna (List).
 */
public class ExemplaresEmprestados implements Iterable<ExemplarResumoExpandido> {

    private final List<ExemplarResumoExpandido> itens;

    public ExemplaresEmprestados(List<ExemplarResumoExpandido> itens) {
        this.itens = Objects.requireNonNull(itens, "itens não pode ser nulo");
    }

    @Override
    public Iterator<ExemplarResumoExpandido> iterator() {
        return itens.iterator();
    }

    /** Ajuda a continuar usando streams sem dor nas Views. */
    public Stream<ExemplarResumoExpandido> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    /** Opcional: retorna uma cópia imutável (pra evitar alguém modificar por fora). */
    public List<ExemplarResumoExpandido> asList() {
        return List.copyOf(itens);
    }
}
