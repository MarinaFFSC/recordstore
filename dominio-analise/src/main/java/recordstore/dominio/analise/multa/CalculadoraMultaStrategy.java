package recordstore.dominio.analise.multa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Strategy base para cálculo de multas.
 * Permite trocar algoritmos de cálculo sem alterar o restante do código.
 */
public abstract class CalculadoraMultaStrategy {

    /**
     * Calcula o valor da multa.
     *
     * @param dataPrevistaDevolucao Data que o exemplar deveria ter sido devolvido
     * @param referencia            Data base para calcular a multa (normalmente hoje)
     * @return Valor da multa em BigDecimal
     */
    public abstract BigDecimal calcular(LocalDate dataPrevistaDevolucao, LocalDate referencia);

    /**
     * Calcula quantos dias de atraso existem entre a data prevista e a data de referência.
     */
    public long calcularDiasAtraso(LocalDate dataPrevistaDevolucao, LocalDate referencia) {
        if (dataPrevistaDevolucao == null || referencia == null) {
            return 0L;
        }
        long dias = ChronoUnit.DAYS.between(dataPrevistaDevolucao, referencia);
        return Math.max(0L, dias);
    }
}
