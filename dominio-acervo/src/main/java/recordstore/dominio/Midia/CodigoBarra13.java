
package recordstore.dominio.acervo.midia;

import org.apache.commons.validator.routines.CodigoBarraValidator;

class CodigoBarra13 extends CodigoBarra {
	CodigoBarra13(String codigo) {
		super(codigo);
	}

	@Override
	boolean testarCodigo(String codigo) {
		return CodigoBarraValidator.getInstance().isValidICodigoBarra13(codigo);
	}
}
