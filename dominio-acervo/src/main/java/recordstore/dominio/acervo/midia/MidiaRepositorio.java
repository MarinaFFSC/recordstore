package recordstore.dominio.acervo.midia;

public interface MidiaRepositorio {
	void salvar(Midia midia);

	Midia obter(CodigoBarra codigoBarra);
	
	void excluir(CodigoBarra codigoBarra);
}