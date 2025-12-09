package recordstore.aplicacao.acervo.exemplar;

import recordstore.aplicacao.administracao.socio.SocioResumo;

public interface EmprestimoResumo {
	PeriodoResumo getPeriodo();

	SocioResumo getTomador();
}