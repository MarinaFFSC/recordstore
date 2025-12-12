package recordstore.aplicacao.analise;

import java.time.LocalDate;

public interface EmprestimoAdminResumo {

    Integer getId();               // id do registro
    Integer getExemplarId();       // id do exemplar
    String getMidiaTitulo();       // título da mídia
    String getSocioNome();         // nome do sócio
    LocalDate getEmprestimoInicio();
    LocalDate getEmprestimoFim();
    LocalDate getDevolucao();
}
