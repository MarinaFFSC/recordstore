package recordstore.apresentacao.vaadin.exemplar;

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

        add(new H2("Administração de Exemplares"));

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

    private HorizontalLayout criarBotoesAcao() {
        Button adicionar = new Button("Adicionar exemplares", e -> adicionarExemplares());
        Button removerSelecionados = new Button("Remover selecionados", e -> removerSelecionados());

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
}
