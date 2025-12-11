package recordstore.apresentacao.vaadin;

import com.vaadin.flow.server.VaadinSession;
import recordstore.dominio.administracao.Socio;

public class SessaoUsuario {

    private static final String ATTR_SOCIO = "usuario.logado";

    public static void setSocio(Socio socio) {
        VaadinSession.getCurrent().setAttribute(ATTR_SOCIO, socio);
    }

    public static Socio getSocio() {
        return (Socio) VaadinSession.getCurrent().getAttribute(ATTR_SOCIO);
    }

    public static boolean isLogado() {
        return getSocio() != null;
    }

    public static void logout() {
        VaadinSession.getCurrent().close();
    }

    public static boolean isAdmin() {
        var socio = getSocio();
        if (socio == null) return false;
        var id = socio.getId();
        return id != null && id.getId() == 1;
    }
}
