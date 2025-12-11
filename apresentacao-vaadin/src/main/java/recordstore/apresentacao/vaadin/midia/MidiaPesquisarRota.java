package recordstore.apresentacao.vaadin.midia;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import recordstore.aplicacao.acervo.midia.MidiaResumo;
import recordstore.aplicacao.acervo.midia.MidiaServicoAplicacao;
import jakarta.annotation.PostConstruct;

@Route("/midia/pesquisar")
public class MidiaPesquisarRota extends VerticalLayout implements AfterNavigationObserver {
    private static final long serialVersionUID = -9031401005322963865L;

    private @Autowired MidiaServicoAplicacao servico;

    private Grid<MidiaResumo> grade;

    @PostConstruct
    private void configurar() {
        setSizeFull();
        setPadding(true);
        setSpacing(false);
        setMargin(false);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        // Fundo
        getStyle()
                .set("background", "radial-gradient(circle at top, #3a1f2b 0, #12070b 55%, #050305 100%)");

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

        var titulo = new H1("Mídias");
        titulo.getStyle()
                .set("margin", "0 0 1rem 0")
                .set("font-size", "1.6rem")
                .set("color", "#F7E9D7")
                .set("letter-spacing", "0.06em")
                .set("text-transform", "uppercase");

        grade = new Grid<>(MidiaResumo.class, false);
        grade.setWidthFull();
        grade.setHeight("450px");
        grade.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_NO_BORDER
        );
        grade.getStyle()
                .set("background-color", "#1A0D12")
                .set("border-radius", "12px")
                .set("border", "1px solid rgba(255,255,255,0.08)")
                .set("color", "#F7E9D7")
                .set("font-size", "0.9rem");

        grade.addComponentColumn(this::link).setHeader("Código de barras");
        grade.addColumn("titulo").setHeader("Título");

        card.add(titulo, grade);
        add(card);
    }

    private RouterLink link(MidiaResumo resumo) {
        var id = resumo.getId();
        RouterLink link = new RouterLink(id, MidiaEditarRota.class, id);
        link.getElement().getStyle()
                .set("color", "#F7E9D7")
                .set("text-decoration", "underline")
                .set("text-underline-offset", "2px");
        return link;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        var midias = servico.pesquisarResumos();
        grade.setItems(midias);
    }
}
