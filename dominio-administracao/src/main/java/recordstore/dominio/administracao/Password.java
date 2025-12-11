package recordstore.dominio.administracao;

import java.util.Objects;

public class Password {

    private final String valor;

    public Password(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Senha inválida");
        }
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Password)) return false;
        Password other = (Password) obj;
        return Objects.equals(valor, other.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        // NÃO USAMOS PARA SALVAR NO BANCO!
        return valor;
    }
}
