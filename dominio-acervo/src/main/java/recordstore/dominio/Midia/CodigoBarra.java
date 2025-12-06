package recordstore.dominio.acervo.midia;

public abstract class CodigoBarra {
	private final String codigo;

	CodigoBarra(String codigo) {
		var passou = testarCodigo(codigo);
		if (!passou) {
			throw new IllegalArgumentException("Código inválido");
		}

		this.codigo = codigo;
	}

	abstract boolean testarCodigo(String codigo);

	public String getCodigo() {
		return codigo;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CodigoBarra) {
			var codigoBarra = (CodigoBarra) obj;
			return codigo.equals(codigoBaarra.codigo);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return codigo.hashCode();
	}

	@Override
	public String toString() {
		return codigo;
	}
}
