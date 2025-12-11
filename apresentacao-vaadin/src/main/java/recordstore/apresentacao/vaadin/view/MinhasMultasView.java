package recordstore.apresentacao.vaadin.view;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import recordstore.apresentacao.vaadin.SessaoUsuario;
import recordstore.apresentacao.vaadin.layout.MainLayout;
import recordstore.apresentacao.vaadin.login.LoginView;
import recordstore.aplicacao.acervo.exemplar.ExemplarResumoExpandido;
import recordstore.aplicacao.acervo.exemplar.ExemplarServicoAplicacao;

@Route(value = "minhas-multas", layout = MainLayout.class)
public class MinhasMultasView extends VerticalLayout implements BeforeEnterObserver {

    private final ExemplarServicoAplicacao exemplarServico;

    private final Grid<ExemplarResumoExpandido> grid =
            new Grid<>(ExemplarResumoExpandido.class, false);

    public MinhasMultasView(ExemplarServicoAplicacao exemplarServico) {
        this.exemplarServico = exemplarServico;

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
        card.setMaxWidth("1100px");
        card.setAlignItems(Alignment.STRETCH);
        card.getStyle()
                .set("background-color", "#27131A")
                .set("border-radius", "18px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.6)")
                .set("padding", "24px 28px")
                .set("border", "1px solid rgba(255,255,255,0.06)")
                .set("margin-top", "24px")
                .set("margin-bottom", "24px");

        H2 titulo = new H2("Minhas multas pendentes");
        titulo.getStyle()
                .set("margin", "0 0 1rem 0")
                .set("color", "#F7E9D7")
                .set("font-weight", "600")
                .set("letter-spacing", "0.05em")
                .set("text-transform", "uppercase");

        configurarGrid();
        carregarMultas();

        card.add(titulo, grid);
        add(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SessaoUsuario.isLogado()) {
            event.forwardTo(LoginView.class);
        }
    }

    private void configurarGrid() {
        grid.setWidthFull();
        grid.setHeight("550px");

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

        grid.addColumn(ex -> ex.getMidia().getTitulo())
            .setHeader("Mídia")
            .setAutoWidth(true);

        grid.addColumn(ex -> ex.getMidia().getSubtitulo())
            .setHeader("Subtítulo")
            .setAutoWidth(true);

        grid.addColumn(ex -> ex.getEmprestimo().getPeriodo().getInicio())
            .setHeader("Início")
            .setAutoWidth(true);

        grid.addColumn(this::calcularDiasComExemplar)
            .setHeader("Dias com o exemplar")
            .setAutoWidth(true);

        grid.addColumn(this::calcularDiasAtraso)
            .setHeader("Dias em atraso")
            .setAutoWidth(true);

        grid.addColumn(ex -> String.format("R$ %.2f", calcularMulta(ex)))
            .setHeader("Multa")
            .setAutoWidth(true);

        grid.addComponentColumn(ex -> {
                Button solicitar = new Button("Solicitar pagamento");

                solicitar.addClickListener(e -> {
                    Notification.show(
                        "Solicitação de pagamento enviada. Aguarde o administrador registrar o pagamento.",
                        5000,
                        Position.MIDDLE
                    );
                    solicitar.setEnabled(false);
                });

                solicitar.getStyle()
                        .set("background-color", "#F0AD4E")
                        .set("color", "#1A0D12")
                        .set("font-weight", "600")
                        .set("border-radius", "999px")
                        .set("border", "none")
                        .set("padding", "0.25rem 0.9rem")
                        .set("font-size", "0.85rem")
                        .set("box-shadow", "0 3px 8px rgba(0,0,0,0.45)");
                return solicitar;
            })
            .setHeader("Ações")
            .setAutoWidth(true)
            .setFlexGrow(0);
    }

    private void carregarMultas() {
        if (!SessaoUsuario.isLogado()) {
            Notification.show("Você precisa estar logado para ver suas multas.");
            return;
        }

        var socio = SessaoUsuario.getSocio();
        int idSocio = socio.getId().getId();

        List<ExemplarResumoExpandido> todosEmprestados =
                exemplarServico.pesquisarEmprestados();

        var minhasMultas = todosEmprestados.stream()
            .filter(this::estaAtrasado)
            .filter(ex -> {
                var tomador = ex.getEmprestimo().getTomador();
                return tomador != null && tomador.getId() == idSocio;
            })
            .toList();

        grid.setItems(minhasMultas);
    }

    private long calcularDiasComExemplar(ExemplarResumoExpandido ex) {
        var inicio = ex.getEmprestimo().getPeriodo().getInicio();
        if (inicio == null) return 0;
        return ChronoUnit.DAYS.between(inicio, LocalDate.now());
    }

    private long calcularDiasAtraso(ExemplarResumoExpandido ex) {
        long dias = calcularDiasComExemplar(ex);
        return Math.max(0, dias - 7);
    }

    private boolean estaAtrasado(ExemplarResumoExpandido ex) {
        return calcularDiasComExemplar(ex) > 7;
    }

    private double calcularMulta(ExemplarResumoExpandido ex) {
        long diasAtraso = calcularDiasAtraso(ex);
        return diasAtraso * 2.50;
    }
}
