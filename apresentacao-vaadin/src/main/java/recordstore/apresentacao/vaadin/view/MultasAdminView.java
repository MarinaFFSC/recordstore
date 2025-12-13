package recordstore.apresentacao.vaadin.view;

import java.time.LocalDate;
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
import recordstore.aplicacao.analise.MultaCalculadoraServico;
import recordstore.aplicacao.analise.MultaSolicitacaoServico;
import recordstore.aplicacao.administracao.socio.SocioResumo;
import recordstore.dominio.acervo.exemplar.EmprestimoServico;
import recordstore.dominio.acervo.exemplar.ExemplarId;

@Route(value = "admin/multas", layout = MainLayout.class)
public class MultasAdminView extends VerticalLayout implements BeforeEnterObserver {

    private final ExemplarServicoAplicacao exemplarServico;
    private final EmprestimoServico emprestimoServico;
    private final MultaCalculadoraServico multaServico;
    private final MultaSolicitacaoServico multaSolicitacaoServico;

    private final Grid<ExemplarResumoExpandido> grid =
            new Grid<>(ExemplarResumoExpandido.class, false);

    public MultasAdminView(ExemplarServicoAplicacao exemplarServico,
                           EmprestimoServico emprestimoServico,
                           MultaCalculadoraServico multaServico,
                           MultaSolicitacaoServico multaSolicitacaoServico) {
        this.exemplarServico = exemplarServico;
        this.emprestimoServico = emprestimoServico;
        this.multaServico = multaServico;
        this.multaSolicitacaoServico = multaSolicitacaoServico;

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

        H2 titulo = new H2("Multas pendentes (Admin)");
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
        if (!SessaoUsuario.isLogado() || !SessaoUsuario.isAdmin()) {
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

        grid.addColumn(ex -> {
                SocioResumo tomador = ex.getEmprestimo().getTomador();
                return tomador != null ? tomador.getNome() : "(desconhecido)";
            })
            .setHeader("Sócio")
            .setAutoWidth(true);

        grid.addColumn(ex -> ex.getMidia().getTitulo())
            .setHeader("Mídia")
            .setAutoWidth(true);

        grid.addColumn(ex -> ex.getEmprestimo().getPeriodo().getInicio())
            .setHeader("Início")
            .setAutoWidth(true);

        grid.addColumn(ex -> {
                var inicio = ex.getEmprestimo().getPeriodo().getInicio();
                return multaServico.calcularDiasComExemplar(inicio, LocalDate.now());
            })
            .setHeader("Dias com o exemplar")
            .setAutoWidth(true);

        grid.addColumn(ex -> {
                var fimPrevisto = ex.getEmprestimo().getPeriodo().getFim();
                return multaServico.calcularDiasAtraso(fimPrevisto, LocalDate.now());
            })
            .setHeader("Dias em atraso")
            .setAutoWidth(true);

        grid.addColumn(ex -> {
                var fimPrevisto = ex.getEmprestimo().getPeriodo().getFim();
                double valor = multaServico.calcularMultaPendente(fimPrevisto, LocalDate.now());
                return String.format("R$ %.2f", valor);
            })
            .setHeader("Multa")
            .setAutoWidth(true);

        grid.addComponentColumn(ex -> {
                Button confirmar = new Button("Confirmar pagamento & devolver",
                        e -> confirmarPagamentoEDevolver(ex));
                confirmar.getStyle()
                        .set("background-color", "#5CB85C")
                        .set("color", "#F7E9D7")
                        .set("font-weight", "600")
                        .set("border-radius", "999px")
                        .set("border", "none")
                        .set("padding", "0.25rem 0.9rem")
                        .set("font-size", "0.85rem")
                        .set("box-shadow", "0 3px 8px rgba(0,0,0,0.45)");
                return confirmar;
            })
            .setHeader("Ações")
            .setAutoWidth(true)
            .setFlexGrow(0);
    }

    private void carregarMultas() {
        // ✅ Iterator: pega a coleção iterável e (aqui) transforma em List só pra manter seu código igual
        List<ExemplarResumoExpandido> todosEmprestados =
                exemplarServico.pesquisarEmprestadosIterable().asList();

        var atrasadosESolicitados = todosEmprestados.stream()
            // apenas atrasados
            .filter(ex -> {
                var fimPrevisto = ex.getEmprestimo().getPeriodo().getFim();
                return multaServico.calcularDiasAtraso(fimPrevisto, LocalDate.now()) > 0;
            })
            // e APENAS os que tiveram solicitação de pagamento feita
            .filter(ex -> {
                try {
                    int exemplarId = Integer.parseInt(ex.getId());
                    return multaSolicitacaoServico.foiSolicitadaParaExemplar(exemplarId);
                } catch (NumberFormatException e) {
                    return false;
                }
            })
            .toList();

        grid.setItems(atrasadosESolicitados);
    }

    private void confirmarPagamentoEDevolver(ExemplarResumoExpandido ex) {
        try {
            String idStr = ex.getId();
            int idInt = Integer.parseInt(idStr);
            ExemplarId exemplarId = new ExemplarId(idInt);

            var fimPrevisto = ex.getEmprestimo().getPeriodo().getFim();
            double valorMulta = multaServico.calcularMultaPendente(fimPrevisto,LocalDate.now());

            // devolve direto no serviço de domínio (admin não passa pelo Proxy)
            emprestimoServico.devolver(exemplarId);

            // limpa solicitação desse exemplar
            multaSolicitacaoServico.limparSolicitacao(idInt);

            Notification.show(
                String.format("Multa de R$ %.2f registrada como paga. Exemplar devolvido.", valorMulta),
                5000, Position.MIDDLE
            );

            carregarMultas();
        } catch (Exception erro) {
            erro.printStackTrace();
            Notification.show("Erro ao confirmar pagamento/devolver: " + erro.getMessage(),
                    5000, Position.MIDDLE);
        }
    }

    public LocalDate hojeParaTeste() {
        return LocalDate.now().plusDays(10);  // simula 10 dias de atraso da multa
     // simula 10 dias de atraso da multa
    }

}
