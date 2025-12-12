package recordstore.dominio.analise.multa;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Implementação padrão da Strategy:
 * cobra uma multa fixa por dia de atraso.
 */
public class MultaFixaPorDiaStrategy extends CalculadoraMultaStrategy {

    private static final BigDecimal MULTA_POR_DIA = BigDecimal.valueOf(2.50);

    @Override
    public BigDecimal calcular(LocalDate dataPrevistaDevolucao, LocalDate referencia) {
        long diasAtraso = calcularDiasAtraso(dataPrevistaDevolucao, referencia);
        return MULTA_POR_DIA.multiply(BigDecimal.valueOf(diasAtraso));
    }
}
