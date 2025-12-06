package recordstore.dominio.acervo.midia;

import org.apache.commons.validator.routines.CODIGOBARRAValidator;

class CodigoBarra10 extends CodigoBarra {
	CodigoBarra10(String codigo) {
		super(codigo);
	}

	@Override
	boolean testarCodigo(String codigo) {
		return ISBNValidator.getInstance().isValidCODIGOBARRA10(codigo);
	}
}