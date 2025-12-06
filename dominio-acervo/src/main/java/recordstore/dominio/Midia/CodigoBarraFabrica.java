package recordstore.dominio.acervo.livro;

import static org.apache.commons.lang3.Validate.notNull;

public class CodigoBarraFabrica {
	public CodigoBarra construir(String codigo) {
		notNull(codigo, "O código não pode ser nulo");

		try {
			return new CodigoBarra10(codigo);
		} catch (IllegalArgumentException e) {
		}

		try {
			return new CodigoBarra13(codigo);
		} catch (IllegalArgumentException e) {
		}

		throw new IllegalArgumentException("Código inválido");
	}
}