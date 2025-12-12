package recordstore.aplicacao.analise;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serviço de aplicação responsável por registrar quais exemplares
 * tiveram solicitação de pagamento de multa feita pelo usuário.
 *
 * Não depende de Spring. É apenas uma classe de domínio/aplicação
 * que será exposta como bean pela camada de infraestrutura.
 */
public class MultaSolicitacaoServico {

    // Guarda IDs de exemplares com solicitação aberta
    private final Set<Integer> exemplaresComSolicitacao = ConcurrentHashMap.newKeySet();

    /** Marca que o usuário solicitou pagamento de multa para esse exemplar. */
    public void solicitarParaExemplar(int exemplarId) {
        exemplaresComSolicitacao.add(exemplarId);
    }

    /** Verifica se já existe solicitação para esse exemplar. */
    public boolean foiSolicitadaParaExemplar(int exemplarId) {
        return exemplaresComSolicitacao.contains(exemplarId);
    }

    /** Remove a solicitação (após o admin confirmar pagamento/devolução). */
    public void limparSolicitacao(int exemplarId) {
        exemplaresComSolicitacao.remove(exemplarId);
    }
}
