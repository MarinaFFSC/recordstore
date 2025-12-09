package recordstore.dominio.administracao;

import recordstore.dominio.administracao.socio.SocioId;

public interface SocioRepositorio {
	void salvar(Socio socio);

	Socio obter(SocioId id);
}
