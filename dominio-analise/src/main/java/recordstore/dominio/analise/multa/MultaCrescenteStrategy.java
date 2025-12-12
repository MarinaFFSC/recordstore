package recordstore.dominio.analise.multa;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Regra:
 *  - até a data prevista → 0
 *  - 1º dia de atraso   → multaBase
 *  - a partir do 2º dia → multaBase + (diasAtraso - 1) * valorPorDiaExtra
 */
public class MultaCrescenteStrategy extends CalculadoraMultaStrategy {

    private final BigDecimal multaBase;
    private final BigDecimal valorPorDiaExtra;

    public MultaCrescenteStrategy(BigDecimal multaBase, BigDecimal valorPorDiaExtra) {
        this.multaBase = multaBase != null ? multaBase : BigDecimal.ZERO;
        this.valorPorDiaExtra = valorPorDiaExtra != null ? valorPorDiaExtra : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calcular(LocalDate dataPrevista, LocalDate dataReferencia) {
        long diasAtraso = calcularDiasAtraso(dataPrevista, dataReferencia);

        if (diasAtraso <= 0) {
            return BigDecimal.ZERO;
        }
        if (diasAtraso == 1) {
            return multaBase;
        }

        long diasExtras = diasAtraso - 1;
        return multaBase.add(valorPorDiaExtra.multiply(BigDecimal.valueOf(diasExtras)));
    }
}
