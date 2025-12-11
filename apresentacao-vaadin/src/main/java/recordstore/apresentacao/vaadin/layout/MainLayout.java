package recordstore.apresentacao.vaadin.layout;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.router.RouterLink;

import recordstore.apresentacao.vaadin.SessaoUsuario;
import recordstore.apresentacao.vaadin.view.DashboardView;
import recordstore.apresentacao.vaadin.view.CatalogoView;
import recordstore.apresentacao.vaadin.view.MeusEmprestimosView;
import recordstore.apresentacao.vaadin.view.MinhasMultasView;
import recordstore.apresentacao.vaadin.socio.SocioView;
import recordstore.apresentacao.vaadin.midia.MidiaAdminView;
import recordstore.apresentacao.vaadin.exemplar.ExemplarView;
import recordstore.apresentacao.vaadin.view.MultasAdminView;

public class MainLayout extends AppLayout {

    public MainLayout() {
        // fundo geral com o mesmo gradiente usado nas outras telas
        getElement().getStyle()
                .set("background", "radial-gradient(circle at top, #3a1f2b 0, #12070b 55%, #050305 100%)");

        createHeader();
        createDrawer();
    }

    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().getStyle()
                .set("color", "#F7E9D7");

        H1 titulo = new H1("RecordStore");
        titulo.getStyle()
                .set("font-size", "1.6em")
                .set("margin", "0")
                .set("color", "#F7E9D7")
                .set("letter-spacing", "0.08em")
                .set("text-transform", "uppercase");

        Span usuarioSpan = new Span();
        usuarioSpan.getStyle()
                .set("color", "#F7E9D7")
                .set("font-size", "0.9em");

        if (SessaoUsuario.isLogado()) {
            var socio = SessaoUsuario.getSocio();
            usuarioSpan.setText("Logado como: " + socio.getNome());
        } else {
            usuarioSpan.setText("Não autenticado");
        }

        Button logout = new Button("Sair", e -> {
            SessaoUsuario.logout();
            UI.getCurrent().getPage().setLocation("/login");
        });
        logout.getStyle()
                .set("background-color", "#E85D2A")  // laranja queimado
                .set("color", "white")
                .set("border-radius", "999px")
                .set("font-weight", "600")
                .set("padding", "0.35rem 1.2rem")
                .set("border", "none")
                .set("box-shadow", "0 4px 10px rgba(0,0,0,0.45)");

        HorizontalLayout header = new HorizontalLayout(toggle, titulo, usuarioSpan, logout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(titulo);
        header.setWidthFull();
        header.getStyle()
                .set("padding", "0.5rem 1.5rem")
                .set("background-color", "#2B151C")   // vinho escuro
                .set("box-shadow", "0 2px 6px rgba(0,0,0,0.4)");

        addToNavbar(header);
    }

    private void createDrawer() {
        Nav nav = new Nav();
        nav.getStyle()
                .set("background-color", "#190C10")
                .set("height", "100%")
                .set("padding", "0");

        VerticalLayout menu = new VerticalLayout();
        menu.setPadding(false);
        menu.setSpacing(false);
        menu.setWidthFull();
        menu.getStyle()
                .set("padding-top", "0.75rem")
                .set("padding-bottom", "1.5rem");

        // ---------- Seção principal (usuário) ----------
        Span clienteTitle = new Span("Área do cliente");
        clienteTitle.getStyle()
                .set("color", "#F7E9D7")
                .set("font-weight", "600")
                .set("font-size", "0.85rem")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.1em")
                .set("padding", "0.25rem 1.25rem");

        RouterLink dashboard = new RouterLink("Início", DashboardView.class);
        RouterLink catalogo = new RouterLink("Catálogo de mídias", CatalogoView.class);
        RouterLink meusEmprestimos = new RouterLink("Meus empréstimos", MeusEmprestimosView.class);
        RouterLink minhasMultas = new RouterLink("Minhas multas", MinhasMultasView.class);

        styleMenuLink(dashboard);
        styleMenuLink(catalogo);
        styleMenuLink(meusEmprestimos);
        styleMenuLink(minhasMultas);

        menu.add(clienteTitle, dashboard, catalogo, meusEmprestimos, minhasMultas);

        // ---------- Seção admin ----------
        if (SessaoUsuario.isAdmin()) {
            Span adminTitle = new Span("Administração");
            adminTitle.getStyle()
                    .set("color", "#F7E9D7")
                    .set("font-weight", "600")
                    .set("font-size", "0.85rem")
                    .set("text-transform", "uppercase")
                    .set("letter-spacing", "0.1em")
                    .set("padding", "0.75rem 1.25rem 0.25rem 1.25rem");

            RouterLink sociosAdmin = new RouterLink("Gerenciar sócios", SocioView.class);
            RouterLink midiasAdmin = new RouterLink("Gerenciar mídias", MidiaAdminView.class);
            RouterLink exemplaresAdmin = new RouterLink("Gerenciar exemplares", ExemplarView.class);
            RouterLink multasAdmin = new RouterLink("Multas pendentes", MultasAdminView.class);

            styleMenuLink(sociosAdmin);
            styleMenuLink(midiasAdmin);
            styleMenuLink(exemplaresAdmin);
            styleMenuLink(multasAdmin);

            menu.add(adminTitle, sociosAdmin, midiasAdmin, exemplaresAdmin, multasAdmin);
        }

        nav.add(menu);
        addToDrawer(nav);
    }

    private void styleMenuLink(RouterLink link) {
        // Link aparece como um "item de lista" inteiro clicável
        link.getElement().getStyle()
                .set("display", "block")
                .set("padding", "0.5rem 1.5rem")
                .set("color", "#F7E9D7")
                .set("text-decoration", "none")
                .set("font-size", "0.95rem")
                .set("border-radius", "999px")
                .set("margin", "0.15rem 0.6rem")
                .set("transition", "background-color 0.15s ease, transform 0.1s ease");

        // hover com leve destaque
        link.getElement().addEventListener("mouseover",
                e -> {
                    link.getElement().getStyle()
                            .set("background-color", "#2B151C")
                            .set("transform", "translateX(2px)");
                });

        link.getElement().addEventListener("mouseout",
                e -> {
                    link.getElement().getStyle()
                            .set("background-color", "transparent")
                            .set("transform", "translateX(0)");
                });
    }
}
