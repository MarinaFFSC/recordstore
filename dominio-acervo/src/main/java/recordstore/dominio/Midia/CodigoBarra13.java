package recordstore.dominio.acervo.midia;

import org.apache.commons.validator.routines.CODIGOBARRAValidator;

class CodigoBarra13 extends CodigoBarra {
	CodigoBarra13(String codigo) {
		super(codigo);
	}

	@Override
	boolean testarCodigo(String codigo) {
		return CODIGOBARRAValidator.getInstance().isValidCODIGOBARRA13(codigo);
	}
}