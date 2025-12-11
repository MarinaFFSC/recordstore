package recordstore.apresentacao.vaadin.midia;

import static com.vaadin.flow.component.UI.getCurrent;
import static com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY;
import static com.vaadin.flow.component.notification.Notification.Position.TOP_CENTER;
import static com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS;
import static com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import recordstore.dominio.acervo.midia.CodigoBarra;
import recordstore.dominio.acervo.midia.CodigoBarraFabrica;
import recordstore.dominio.acervo.midia.MidiaServico;
import jakarta.annotation.PostConstruct;

@Route("midia/alterar")
public class MidiaEditarRota extends VerticalLayout implements HasUrlParameter<String> {
    private static final long serialVersionUID = -405348699654790564L;

    private @Autowired MidiaServico servico;

    private MidiaFormulario formulario;
    private CodigoBarra codigoBarra;

    @PostConstruct
    private void configurar() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Fundo geral
        getStyle()
                .set("background", "radial-gradient(circle at top, #3a1f2b 0, #12070b 55%, #050305 100%)");

        // Card central
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(true);
        card.setWidth("420px");
        card.setAlignItems(Alignment.STRETCH);
        card.getStyle()
                .set("background-color", "#27131A")
                .set("border-radius", "18px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.6)")
                .set("padding", "24px 28px")
                .set("border", "1px solid rgba(255,255,255,0.06)");

        var titulo = new H1("Edição de mídia");
        titulo.getStyle()
                .set("margin", "0 0 1rem 0")
                .set("font-size", "1.6rem")
                .set("color", "#F7E9D7")
                .set("letter-spacing", "0.06em")
                .set("text-transform", "uppercase")
                .set("text-align", "center");

        formulario = new MidiaFormulario(true);

        var salvar = new Button("Salvar");
        salvar.addThemeVariants(LUMO_PRIMARY);
        salvar.addClickListener(this::salvar);
        salvar.getStyle()
                .set("background-color", "#E85D2A")
                .set("color", "white")
                .set("font-weight", "600")
                .set("border-radius", "999px")
                .set("border", "none")
                .set("box-shadow", "0 4px 10px rgba(0,0,0,0.45)")
                .set("margin-top", "0.75rem")
                .set("width", "100%");

        card.add(titulo, formulario, salvar);
        add(card);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        var fabrica = new CodigoBarraFabrica();
        codigoBarra = fabrica.construir(parameter);

        var midia = servico.obter(codigoBarra);
        formulario.ler(midia);
    }

    private void salvar(ClickEvent<Button> evento) {
        var midia = servico.obter(codigoBarra);
        formulario.escrever(midia);
        servico.salvar(midia);

        var notificacao = new Notification("Mídia salva com sucesso", 5000, TOP_CENTER);
        notificacao.addThemeVariants(LUMO_SUCCESS);
        notificacao.open();

        var ui = getCurrent();
        var pagina = ui.getPage();
        var historico = pagina.getHistory();
        historico.back();
    }
}
