package recordstore.dominio.acervo.midia;

public class CodigoBarra10 extends CodigoBarra {

    CodigoBarra10(String codigo) {
        super(codigo);
    }

    @Override
    boolean testarCodigo(String codigo) {
        if (codigo == null) {
            return false;
        }

        var valor = codigo.trim();

        // Apenas 10 dígitos numéricos
        return valor.matches("\\d{10}");
    }
}
