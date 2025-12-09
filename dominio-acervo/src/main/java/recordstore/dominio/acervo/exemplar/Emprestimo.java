package recordstore.dominio.acervo.exemplar;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.Serializable;
import java.time.LocalDate;

import recordstore.dominio.administracao.socio.SocioId;

public class Emprestimo implements Serializable {

    private final Periodo periodo;
    private final SocioId tomador;

    public Emprestimo(Periodo periodo, SocioId tomador) {
        notNull(periodo, "O período do empréstimo não pode ser nulo");
        notNull(tomador, "O tomador do empréstimo não pode ser nulo");

        this.periodo = periodo;
        this.tomador = tomador;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public SocioId getTomador() {
        return tomador;
    }

    public LocalDate getInicio() {
        return periodo.getInicio();
    }

    public LocalDate getFim() {
        return periodo.getFim();
    }

    @Override
    public String toString() {
        return "Emprestimo{" +
                "periodo=" + periodo +
                ", tomador=" + tomador +
                '}';
    }

    @Override
    public int hashCode() {
        return periodo.hashCode() * 31 + tomador.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Emprestimo)) return false;
        Emprestimo other = (Emprestimo) obj;
        return periodo.equals(other.periodo)
                && tomador.equals(other.tomador);
    }
}
