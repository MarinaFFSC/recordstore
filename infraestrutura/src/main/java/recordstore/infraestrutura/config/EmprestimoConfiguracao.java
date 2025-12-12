package recordstore.infraestrutura.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import recordstore.aplicacao.acervo.exemplar.EmprestimoServicoProxy;
import recordstore.aplicacao.acervo.exemplar.ExemplarServicoAplicacao;
import recordstore.aplicacao.analise.MultaCalculadoraServico;
import recordstore.dominio.acervo.exemplar.EmprestimoOperacoes;
import recordstore.dominio.acervo.exemplar.EmprestimoServico;
import recordstore.dominio.acervo.exemplar.ExemplarRepositorio;

@Configuration
public class EmprestimoConfiguracao {

    /**
     * Expondo EmprestimoOperacoes como um PROXY.
     *
     * - O EmprestimoServico real continua sendo criado em VaadinAplicacao.
     * - Aqui nós “embrulhamos” ele no EmprestimoServicoProxy.
     */
    @Bean
    @Primary
    public EmprestimoOperacoes emprestimoOperacoes(EmprestimoServico emprestimoServico,
                                                   ExemplarRepositorio exemplarRepositorio,
                                                   MultaCalculadoraServico multaCalculadoraServico,
                                                   ExemplarServicoAplicacao exemplarServicoAplicacao) {

        return new EmprestimoServicoProxy(
                emprestimoServico,
                exemplarRepositorio,
                multaCalculadoraServico,
                exemplarServicoAplicacao
        );
    }
}
