package recordstore.aplicacao.analise;

import java.util.List;

import recordstore.dominio.analise.emprestimo.EmprestimoRegistro;
import recordstore.dominio.acervo.exemplar.Emprestimo;
import recordstore.dominio.acervo.exemplar.ExemplarId;

public interface EmprestimoRegistroRepositorioAplicacao {

    EmprestimoRegistro buscar(ExemplarId exemplar, Emprestimo emprestimo);

    List<EmprestimoAdminResumo> listarParaAdmin();
}
