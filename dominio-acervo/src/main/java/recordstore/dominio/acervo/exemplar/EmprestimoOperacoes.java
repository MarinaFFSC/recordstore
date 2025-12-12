package recordstore.dominio.acervo.exemplar;

import recordstore.dominio.acervo.midia.CodigoBarra;
import recordstore.dominio.administracao.socio.SocioId;

public interface EmprestimoOperacoes {

    void realizarEmprestimo(ExemplarId exemplarId, SocioId tomador);

    void realizarEmprestimo(CodigoBarra midiaId, SocioId tomador);

    void devolver(ExemplarId exemplarId);
}
