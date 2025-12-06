package recordstore.dominio.administracao.socio;

public interface SocioRepositorio {
	void salvar(Socio socio);

	Socio obter(SocioId id);
}
