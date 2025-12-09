package recordstore.dominio.acervo.midia;

import static org.apache.commons.lang3.Validate.notNull;

public class CodigoBarraFabrica {

    public CodigoBarra construir(String codigo) {
        notNull(codigo, "O código não pode ser nulo");

        codigo = codigo.trim();

        try {
            return new CodigoBarra10(codigo);
        } catch (IllegalArgumentException e) {
            // ignora e tenta o próximo
        }

        try {
            return new CodigoBarra13(codigo);
        } catch (IllegalArgumentException e) {
            // ignora e cai na exception final
        }

        throw new IllegalArgumentException("Código inválido: deve ter 10 ou 13 dígitos numéricos");
    }
}
