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
            return codigo.equals(codigoBarra.codigo);
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

    // 🔥 fábrica estática
    public static CodigoBarra criar(String codigo) {
        if (codigo == null) {
            throw new IllegalArgumentException("Código não pode ser nulo");
        }

        var valor = codigo.trim();

        if (valor.length() == 10) {
            return new CodigoBarra10(valor);
        } else if (valor.length() == 13) {
            return new CodigoBarra13(valor);
        } else {
            throw new IllegalArgumentException("Código deve ter 10 ou 13 dígitos numéricos");
        }
    }
}
