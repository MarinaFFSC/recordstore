package recordstore.apresentacao.vaadin.exemplar;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import recordstore.apresentacao.vaadin.layout.MainLayout;
import recordstore.aplicacao.acervo.exemplar.ExemplarServicoAplicacao;
import recordstore.aplicacao.acervo.exemplar.ExemplarResumo;

@Route(value = "admin/exemplares", layout = MainLayout.class)
@PageTitle("Administração de Exemplares | RecordStore")
public class ExemplarView extends VerticalLayout {

    private final ExemplarServicoAplicacao exemplarServico;

    private final Grid<ExemplarResumo> grid = new Grid<>(ExemplarResumo.class, false);

    private final TextField codigoMidiaField = new TextField("Código de barras da mídia");
    private final IntegerField quantidadeField = new IntegerField("Quantidade de exemplares");

    public ExemplarView(ExemplarServicoAplicacao exemplarServico) {
        this.exemplarServico = exemplarServico;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("Administração de Exemplares");
        titulo.getStyle()
                .set("color", "#F7E9D7")
                .set("margin-top", "0")
                .set("margin-bottom", "0.75rem")
                .set("letter-spacing", "0.08em")
                .set("text-transform", "uppercase");
        add(titulo);

        configurarFormulario();
        configurarGrid();
        carregarExemplares();

        add(
            new HorizontalLayout(codigoMidiaField, quantidadeField),
            criarBotoesAcao(),
            grid
        );
    }

    private void configurarFormulario() {
        codigoMidiaField.setWidth("250px");
        quantidadeField.setWidth("150px");
        quantidadeField.setMin(1);
        quantidadeField.setStep(1);
    }
    
    private void estilizarBotaoPrincipal(Button button) {
        button.getStyle()
                .set("background-color", "#E85D2A")   // orange
                .set("color", "#F7E9D7")              // cream
                .set("font-weight", "600")
                .set("border-radius", "8px")
                .set("padding", "0.45rem 1.2rem")
                .set("border", "none");
        button.getElement().getStyle().set("--lumo-primary-color", "#FF6F3C");
    }

    private HorizontalLayout criarBotoesAcao() {
        Button adicionar = new Button("Adicionar exemplares", e -> adicionarExemplares());
        Button removerSelecionados = new Button("Remover selecionados", e -> removerSelecionados());
        estilizarBotaoPrincipal(adicionar);
        estilizarBotaoPrincipal(removerSelecionados);
        return new HorizontalLayout(adicionar, removerSelecionados);
    }

    private void configurarGrid() {
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid.addColumn(ExemplarResumo::getId)
            .setHeader("ID Exemplar")
            .setAutoWidth(true);

        grid.addColumn(ex -> ex.getMidia().getId())
            .setHeader("Código de barras")
            .setAutoWidth(true);

        grid.addColumn(ex -> ex.getMidia().getTitulo())
            .setHeader("Título da mídia")
            .setAutoWidth(true);

        // ESTILO VISUAL: fundo claro + texto escuro
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

    private void carregarExemplares() {
        var exemplares = exemplarServico.pesquisarResumos();
        grid.setItems(exemplares);
    }

    private void adicionarExemplares() {
        if (codigoMidiaField.isEmpty() || quantidadeField.isEmpty()) {
            Notification.show("Informe o código da mídia e a quantidade.");
            return;
        }

        try {
            String codigo = codigoMidiaField.getValue().trim();
            int quantidade = quantidadeField.getValue();

            exemplarServico.criarExemplares(codigo, quantidade);

            Notification.show("Exemplares criados com sucesso.");
            carregarExemplares();

        } catch (Exception ex) {
            ex.printStackTrace();
            Notification.show("Erro ao criar exemplares: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private void removerSelecionados() {
        var selecionados = grid.getSelectedItems();
        if (selecionados == null || selecionados.isEmpty()) {
            Notification.show("Selecione ao menos um exemplar para remover.");
            return;
        }

        try {
            exemplarServico.removerPorIds(
                selecionados.stream()
                        .map(ExemplarResumo::getId)
                        .toList()
            );
            Notification.show("Exemplares removidos.");
            carregarExemplares();

        } catch (Exception ex) {
            ex.printStackTrace();
            Notification.show("Erro ao remover exemplares: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }
    
 // ========== ESTILO DOS INPUTS ==========

    private void estilizarCampoInput(HasStyle field) {
        field.getStyle()
     // fundo e bordas
        .set("--vaadin-input-field-background", "#FFFFFF")          // fundo do container
        .set("--vaadin-input-field-background-color", "#FFFFFF")    // fundo real do input
        .set("--vaadin-input-field-border-color", "#6A545E")
        .set("--vaadin-input-field-hover-border-color", "#3B2730")
        .set("--vaadin-input-field-focused-border-color", "#F7E9D7")

        // tira azul padrão
        .set("--vaadin-focus-ring-color", "transparent")
        .set("--lumo-primary-color", "#F7E9D7")
        .set("--lumo-primary-text-color", "#F7E9D7")
        .set("--lumo-primary-contrast-color", "#000000")
        .set("--vaadin-input-field-caret-color", "#3B2730")

        // texto
        .set("--vaadin-input-field-value-color", "#3B2730")
        .set("--vaadin-input-field-label-color", "#F7E9D7")
        .set("--vaadin-input-field-placeholder-color", "#C9B7A8")

        .set("--vaadin-input-field-padding", "0.4rem 0.75rem");
    }
}
    
