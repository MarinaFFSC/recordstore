package recordstore.apresentacao.vaadin.socio;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEnterEvent;

import recordstore.aplicacao.administracao.socio.SocioResumo;
import recordstore.aplicacao.administracao.socio.SocioServicoAplicacao;
import recordstore.apresentacao.vaadin.SessaoUsuario;
import recordstore.apresentacao.vaadin.layout.MainLayout;

@Route(value = "admin/socios", layout = MainLayout.class)
public class SocioView extends VerticalLayout implements BeforeEnterObserver {

    private final SocioServicoAplicacao service;
    private final Grid<SocioResumo> grid = new Grid<>(SocioResumo.class, false);

    public SocioView(SocioServicoAplicacao service) {
        this.service = service;

        setSizeFull();
        setPadding(true);
        setSpacing(false);
        setMargin(false);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.START);

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

        H2 titulo = new H2("Gerenciar sócios");
        titulo.getStyle()
                .set("margin", "0 0 1rem 0")
                .set("color", "#F7E9D7")
                .set("font-weight", "600")
                .set("letter-spacing", "0.05em")
                .set("text-transform", "uppercase");

        Button novo = new Button("Novo Sócio", e -> abrirFormulario());
        novo.getStyle()
                .set("background-color", "#E85D2A")
                .set("color", "white")
                .set("font-weight", "600")
                .set("border-radius", "999px")
                .set("border", "none")
                .set("padding", "0.4rem 1.2rem")
                .set("box-shadow", "0 4px 10px rgba(0,0,0,0.45)");

        configurarGrid();

        card.add(titulo, novo, grid);
        add(card);
    }

    private void configurarGrid() {
        grid.setItems(service.pesquisarResumos());
        grid.addColumn(SocioResumo::getId).setHeader("ID");
        grid.addColumn(SocioResumo::getNome).setHeader("Nome");

        grid.setWidthFull();
        grid.setHeight("400px");
        grid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_NO_BORDER
        );
        grid.getStyle()
                .set("background-color", "#FFFFFF")
                .set("border-radius", "8px")
                .set("border", "1px solid rgba(0,0,0,0.06)")
                .set("font-size", "0.9rem");
        grid.getElement().getStyle()
                .set("--lumo-body-text-color", "#2B151C")
                .set("--lumo-header-text-color", "#2B151C");
    }

    private void abrirFormulario() {
        getUI().ifPresent(ui -> ui.navigate("novo-socio"));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SessaoUsuario.isLogado()) {
            event.forwardTo("login");
            return;
        }
        if (!SessaoUsuario.isAdmin()) {
            event.forwardTo(""); 
        }
    }
}
