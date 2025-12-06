package recordstore.dominio.acervo.midia;

import org.apache.commons.validator.routines.CodigoBarraValidator;

class CodigoBarra10 extends CodigoBarra {
	CodigoBarra10(String codigo) {
		super(codigo);
	}

	@Override
	boolean testarCodigo(String codigo) {
		return CodigoBarraValidator.getInstance().isValidCodigoBarra10(codigo);
	}
}