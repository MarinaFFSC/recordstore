package recordstore.apresentacao.vaadin.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import recordstore.apresentacao.vaadin.SessaoUsuario;
import recordstore.apresentacao.vaadin.layout.MainLayout;

@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout implements BeforeEnterObserver {

    public DashboardView() {
        setSizeFull();
        setPadding(true);
        setSpacing(false);
        setMargin(false);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        // Card central
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(true);
        card.setWidth("100%");
        card.setMaxWidth("900px");
        card.setAlignItems(Alignment.STRETCH);
        card.getStyle()
                .set("background-color", "#27131A")
                .set("border-radius", "18px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.6)")
                .set("padding", "24px 28px")
                .set("border", "1px solid rgba(255,255,255,0.06)")
                .set("margin-top", "24px");

        H2 titulo = new H2("Bem-vindo à RecordStore");
        titulo.getStyle()
                .set("margin", "0 0 0.75rem 0")
                .set("color", "#F7E9D7")
                .set("font-weight", "600")
                .set("letter-spacing", "0.05em")
                .set("text-transform", "uppercase");

        Paragraph subtitulo = new Paragraph(
                "Aqui você pode navegar pelo catálogo de mídias, realizar empréstimos e, se for admin, gerenciar o acervo e os sócios."
        );
        subtitulo.getStyle()
                .set("margin", "0")
                .set("color", "#C9B7A8")
                .set("font-size", "0.98rem")
                .set("line-height", "1.6");

        card.add(titulo, subtitulo);
        add(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SessaoUsuario.isLogado()) {
            event.forwardTo("login");
        }
    }
}
