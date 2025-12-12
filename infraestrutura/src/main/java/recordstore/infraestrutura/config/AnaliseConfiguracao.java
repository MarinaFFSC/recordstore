package recordstore.infraestrutura.config;

import java.math.BigDecimal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import recordstore.aplicacao.analise.MultaCalculadoraServico;
import recordstore.aplicacao.analise.MultaSolicitacaoServico;
import recordstore.dominio.analise.multa.CalculadoraMultaStrategy;
import recordstore.dominio.analise.multa.MultaCrescenteStrategy;

@Configuration
public class AnaliseConfiguracao {

    @Bean
    public CalculadoraMultaStrategy calculadoraMultaStrategy() {
        return new MultaCrescenteStrategy(
                BigDecimal.valueOf(2.50),
                BigDecimal.valueOf(2.50)
        );
    }

    @Bean
    public MultaCalculadoraServico multaCalculadoraServico(CalculadoraMultaStrategy strategy) {
        return new MultaCalculadoraServico(strategy);
    }

    /**
     * Bean da camada de aplicação para registrar solicitações de multa.
     * A classe em si não depende de Spring; apenas a infraestrutura expõe o bean.
     */
    @Bean
    public MultaSolicitacaoServico multaSolicitacaoServico() {
        return new MultaSolicitacaoServico();
    }
}
