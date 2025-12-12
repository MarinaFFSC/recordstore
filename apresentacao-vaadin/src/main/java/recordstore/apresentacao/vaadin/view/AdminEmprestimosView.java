package recordstore.apresentacao.vaadin.view;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import recordstore.aplicacao.acervo.exemplar.ExemplarResumoExpandido;
import recordstore.aplicacao.acervo.exemplar.ExemplarServicoAplicacao;
import recordstore.apresentacao.vaadin.SessaoUsuario;
import recordstore.apresentacao.vaadin.layout.MainLayout;
import recordstore.apresentacao.vaadin.login.LoginView;

@Route(value = "admin/emprestimos", layout = MainLayout.class)
@PageTitle("Empréstimos | RecordStore")
public class AdminEmprestimosView extends VerticalLayout implements BeforeEnterObserver {

    private final ExemplarServicoAplicacao exemplarServico;
    private final Grid<ExemplarResumoExpandido> grid =
            new Grid<>(ExemplarResumoExpandido.class, false);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AdminEmprestimosView(ExemplarServicoAplicacao exemplarServico) {
        this.exemplarServico = exemplarServico;

        setSizeFull();
        setPadding(true);
        setSpacing(false);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        // Fundo no mesmo padrão
        getStyle()
                .set("background", "radial-gradient(circle at top, #3a1f2b 0, #12070b 55%, #050305 100%)");

        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(true);
        card.setWidth("100%");
        card.setMaxWidth("1000px");
        card.setAlignItems(Alignment.STRETCH);
        card.getStyle()
                .set("background-color", "#27131A")
                .set("border-radius", "18px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.6)")
                .set("padding", "24px 28px")
                .set("border", "1px solid rgba(255,255,255,0.06)")
                .set("margin-top", "24px");

        H2 titulo = new H2("Empréstimos em andamento");
        titulo.getStyle()
                .set("margin", "0 0 1rem 0")
                .set("color", "#F7E9D7")
                .set("font-weight", "600")
                .set("letter-spacing", "0.05em")
                .set("text-transform", "uppercase");

        configurarGrid();
        carregarDados();

        card.add(titulo, grid);
        add(card);
    }

    private void configurarGrid() {
        grid.setWidthFull();
        grid.setHeight("450px");

        // ID do exemplar
        grid.addColumn(ExemplarResumoExpandido::getId)
                .setHeader("Exemplar")
                .setAutoWidth(true);

        // Título da mídia
        grid.addColumn(ex -> ex.getMidia().getTitulo())
                .setHeader("Mídia")
                .setFlexGrow(1);

        // Nome do sócio tomador
        grid.addColumn(ex -> ex.getEmprestimo() != null && ex.getEmprestimo().getTomador() != null
                        ? ex.getEmprestimo().getTomador().getNome()
                        : "(desconhecido)")
                .setHeader("Sócio")
                .setFlexGrow(1);

        // Início do empréstimo
        grid.addColumn(ex -> ex.getEmprestimo() != null && ex.getEmprestimo().getPeriodo() != null
                        && ex.getEmprestimo().getPeriodo().getInicio() != null
                        ? ex.getEmprestimo().getPeriodo().getInicio().format(formatter)
                        : "")
                .setHeader("Início")
                .setAutoWidth(true);

        // Fim previsto
        grid.addColumn(ex -> ex.getEmprestimo() != null && ex.getEmprestimo().getPeriodo() != null
                        && ex.getEmprestimo().getPeriodo().getFim() != null
                        ? ex.getEmprestimo().getPeriodo().getFim().format(formatter)
                        : "")
                .setHeader("Fim previsto")
                .setAutoWidth(true);

        // Status: aqui é sempre "Em andamento", porque só listamos emprestados
        grid.addColumn(ex -> "Em andamento")
                .setHeader("Status")
                .setAutoWidth(true);

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

    private void carregarDados() {
        // Usa o mesmo serviço que as outras telas usam para saber o que está emprestado AGORA
        List<ExemplarResumoExpandido> lista = exemplarServico.pesquisarEmprestados();
        grid.setItems(lista);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SessaoUsuario.isLogado()) {
            event.rerouteTo(LoginView.class);
            return;
        }
        if (!SessaoUsuario.isAdmin()) {
            event.rerouteTo(DashboardView.class);
        }
    }
}
