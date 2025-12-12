package recordstore.aplicacao.acervo.exemplar;

import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDate;

import recordstore.aplicacao.analise.MultaCalculadoraServico;
import recordstore.aplicacao.acervo.exemplar.ExemplarServicoAplicacao;
import recordstore.aplicacao.acervo.exemplar.ExemplarResumoExpandido;
import recordstore.dominio.acervo.exemplar.EmprestimoOperacoes;
import recordstore.dominio.acervo.exemplar.EmprestimoServico;
import recordstore.dominio.acervo.exemplar.ExemplarId;
import recordstore.dominio.acervo.exemplar.ExemplarRepositorio;
import recordstore.dominio.acervo.midia.CodigoBarra;
import recordstore.dominio.administracao.socio.SocioId;

/**
 * Proxy para EmprestimoServico.
 *
 * Responsabilidades:
 *  - Bloquear NOVOS empréstimos se o sócio tiver multa pendente.
 *  - Bloquear DEVOLUÇÃO via usuário se o sócio tiver multa pendente.
 *  - Delegar operações válidas para o serviço real (EmprestimoServico).
 */
public class EmprestimoServicoProxy implements EmprestimoOperacoes {

    private final EmprestimoServico alvo;                  // serviço real de domínio
    private final ExemplarRepositorio exemplarRepositorio; // ainda disponível se precisar
    private final MultaCalculadoraServico multaServico;    // usa Strategy de multa
    private final ExemplarServicoAplicacao exemplarServicoAplicacao; // para consultar emprestados

    public EmprestimoServicoProxy(EmprestimoServico alvo,
                                  ExemplarRepositorio exemplarRepositorio,
                                  MultaCalculadoraServico multaServico,
                                  ExemplarServicoAplicacao exemplarServicoAplicacao) {
        notNull(alvo, "O serviço de empréstimo real não pode ser nulo");
        notNull(exemplarRepositorio, "O repositório de exemplares não pode ser nulo");
        notNull(multaServico, "O serviço de cálculo de multa não pode ser nulo");
        notNull(exemplarServicoAplicacao, "O serviço de exemplares (aplicação) não pode ser nulo");

        this.alvo = alvo;
        this.exemplarRepositorio = exemplarRepositorio;
        this.multaServico = multaServico;
        this.exemplarServicoAplicacao = exemplarServicoAplicacao;
    }

    @Override
    public void realizarEmprestimo(ExemplarId exemplarId, SocioId tomador) {
        notNull(exemplarId, "O id do exemplar não pode ser nulo");
        notNull(tomador, "O id do tomador não pode ser nulo");

        // REGRA DO PROXY: não permite novo empréstimo se houver multa pendente
        if (socioTemMultaPendente(tomador)) {
            throw new IllegalStateException(
                "Você possui multa pendente em empréstimos anteriores. " +
                "Regularize suas multas antes de fazer um novo empréstimo."
            );
        }

        // Se passou pela regra, delega para o serviço real
        alvo.realizarEmprestimo(exemplarId, tomador);
    }

    @Override
    public void realizarEmprestimo(CodigoBarra midiaId, SocioId tomador) {
        notNull(midiaId, "O id da mídia não pode ser nulo");
        notNull(tomador, "O id do tomador não pode ser nulo");

        // Mesma regra para o caso de empréstimo por Código de Barra
        if (socioTemMultaPendente(tomador)) {
            throw new IllegalStateException(
                "Você possui multa pendente em empréstimos anteriores. " +
                "Regularize suas multas antes de fazer um novo empréstimo."
            );
        }

        alvo.realizarEmprestimo(midiaId, tomador);
    }

    @Override
    public void devolver(ExemplarId exemplarId) {
        notNull(exemplarId, "O id do exemplar não pode ser nulo");

        // Descobre o empréstimo atual desse exemplar e o sócio tomador
        var resumoOpt = exemplarServicoAplicacao.pesquisarEmprestados().stream()
            .filter(ex -> {
                try {
                    return Integer.parseInt(ex.getId()) == exemplarId.getId();
                } catch (NumberFormatException e) {
                    return false;
                }
            })
            .findFirst();

        if (resumoOpt.isPresent()) {
            var resumo = resumoOpt.get();
            if (resumo.getEmprestimo() != null && resumo.getEmprestimo().getTomador() != null) {
                int idSocio = resumo.getEmprestimo().getTomador().getId();
                SocioId socioId = new SocioId(idSocio);

                // Se o sócio tiver qualquer multa pendente, não pode devolver via Proxy
                if (socioTemMultaPendente(socioId)) {
                    throw new IllegalStateException(
                        "Você possui multa pendente e não pode devolver este exemplar " +
                        "diretamente. Solicite o pagamento da multa e aguarde o administrador " +
                        "confirmar."
                    );
                }
            }
        }

        // Se não tem multa pendente, delega para o serviço real
        alvo.devolver(exemplarId);
    }

    /**
     * Verifica se o sócio tem qualquer empréstimo em atraso com multa > 0,
     * usando a mesma regra de multa (Strategy/MultaCalculadoraServico).
     */
    private boolean socioTemMultaPendente(SocioId socioId) {
        if (socioId == null) {
            return false;
        }

        int idSocio = socioId.getId();
        LocalDate hoje = LocalDate.now();

        return exemplarServicoAplicacao.pesquisarEmprestados().stream()
            // só empréstimos do sócio
            .filter(ex -> pertenceAoSocio(ex, idSocio))
            // verifica se a multa calculada pela Strategy é > 0
            .anyMatch(ex -> multaDoExemplarMaiorQueZero(ex, hoje));
    }

    private boolean pertenceAoSocio(ExemplarResumoExpandido ex, int idSocio) {
        if (ex.getEmprestimo() == null || ex.getEmprestimo().getTomador() == null) {
            return false;
        }
        // SocioResumo (apresentação) – assumindo que getTomador().getId() retorna int
        return ex.getEmprestimo().getTomador().getId() == idSocio;
    }

    private boolean multaDoExemplarMaiorQueZero(ExemplarResumoExpandido ex, LocalDate hoje) {
        var periodo = ex.getEmprestimo().getPeriodo();
        if (periodo == null || periodo.getFim() == null) {
            return false;
        }
        var fimPrevisto = periodo.getFim();
        double multa = multaServico.calcularMultaPendente(fimPrevisto, hoje);
        return multa > 0.0;
    }
}
