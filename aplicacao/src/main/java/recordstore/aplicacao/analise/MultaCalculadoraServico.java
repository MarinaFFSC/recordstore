package recordstore.aplicacao.analise;

import static org.apache.commons.lang3.Validate.notNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import recordstore.dominio.analise.multa.CalculadoraMultaStrategy;

public class MultaCalculadoraServico {

    private final CalculadoraMultaStrategy strategy;

    public MultaCalculadoraServico(CalculadoraMultaStrategy strategy) {
        notNull(strategy, "A strategy de multa não pode ser nula");
        this.strategy = strategy;
    }

    /**
     * Quantos dias o usuário está com o exemplar (de início até referência).
     */
    public long calcularDiasComExemplar(LocalDate dataInicio, LocalDate referencia) {
        if (dataInicio == null || referencia == null) {
            return 0L;
        }
        long dias = ChronoUnit.DAYS.between(dataInicio, referencia);
        return Math.max(0L, dias);
    }

    /**
     * Quantos dias de atraso existem (de fim previsto até referência).
     */
    public long calcularDiasAtraso(LocalDate dataPrevistaDevolucao, LocalDate referencia) {
        return strategy.calcularDiasAtraso(dataPrevistaDevolucao, referencia);
    }

    /**
     * Valor da multa pendente para um empréstimo cujo prazo final é dataPrevistaDevolucao.
     */
    public double calcularMultaPendente(LocalDate dataPrevistaDevolucao, LocalDate referencia) {
        BigDecimal valor = strategy.calcular(dataPrevistaDevolucao, referencia);
        return valor.doubleValue();
    }
}
