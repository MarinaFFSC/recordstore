package recordstore.dominio.acervo.midia;

public class CodigoBarra13 extends CodigoBarra {

    CodigoBarra13(String codigo) {
        super(codigo);
    }

    @Override
    boolean testarCodigo(String codigo) {
        if (codigo == null) {
            return false;
        }

        var valor = codigo.trim();

        // Apenas 13 dígitos numéricos
        return valor.matches("\\d{13}");
    }
}
