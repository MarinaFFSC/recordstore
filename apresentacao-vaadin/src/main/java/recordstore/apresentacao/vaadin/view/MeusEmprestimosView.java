package recordstore.apresentacao.vaadin.view;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.vaadin.flow.component.UI;
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

import org.springframework.beans.factory.annotation.Qualifier;

import recordstore.apresentacao.vaadin.SessaoUsuario;
import recordstore.apresentacao.vaadin.layout.MainLayout;
import recordstore.apresentacao.vaadin.login.LoginView;
import recordstore.aplicacao.acervo.exemplar.ExemplarResumoExpandido;
import recordstore.aplicacao.acervo.exemplar.ExemplarServicoAplicacao;
import recordstore.aplicacao.analise.MultaCalculadoraServico;
import recordstore.dominio.acervo.exemplar.EmprestimoOperacoes;
import recordstore.dominio.acervo.exemplar.ExemplarId;

@Route(value = "meus-emprestimos", layout = MainLayout.class)
public class MeusEmprestimosView extends VerticalLayout implements BeforeEnterObserver {

    private final ExemplarServicoAplicacao exemplarServico;
    // AGORA usamos a interface EmprestimoOperacoes (vai cair no PROXY)
    private final EmprestimoOperacoes emprestimoServico;
    // Serviço oficial de cálculo de multa
    private final MultaCalculadoraServico multaServico;

    private final Grid<ExemplarResumoExpandido> grid =
            new Grid<>(ExemplarResumoExpandido.class, false);

    public MeusEmprestimosView(ExemplarServicoAplicacao exemplarServico,
                               @Qualifier("emprestimoOperacoes") EmprestimoOperacoes emprestimoServico,
                               MultaCalculadoraServico multaServico) {
        this.exemplarServico = exemplarServico;
        this.emprestimoServico = emprestimoServico;
        this.multaServico = multaServico;

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

        H2 titulo = new H2("Meus empréstimos");
        titulo.getStyle()
                .set("margin", "0 0 1rem 0")
                .set("color", "#F7E9D7")
                .set("font-weight", "600")
                .set("letter-spacing", "0.05em")
                .set("text-transform", "uppercase");

        configurarGrid();
        carregarMeusEmprestimos();

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

        // Estilo da tabela
        grid.getStyle()
                .set("background-color", "#FFFFFF")
                .set("border-radius", "12px")
                .set("border", "1px solid rgba(0,0,0,0.08)")
                .set("font-size", "0.9rem");

        grid.getElement().getStyle()
                .set("--lumo-body-text-color", "#2B151C")
                .set("--lumo-header-text-color", "#2B151C");

        // Mídia
        grid.addColumn(ex -> ex.getMidia().getTitulo())
            .setHeader("Mídia")
            .setAutoWidth(true);

        grid.addColumn(ex -> ex.getMidia().getSubtitulo())
            .setHeader("Subtítulo")
            .setAutoWidth(true);

        // Datas
        grid.addColumn(ex -> ex.getEmprestimo().getPeriodo().getInicio())
            .setHeader("Início")
            .setAutoWidth(true);

        grid.addColumn(ex -> ex.getEmprestimo().getPeriodo().getFim())
            .setHeader("Fim previsto")
            .setAutoWidth(true);

        // Dias com o exemplar
        grid.addColumn(this::calcularDiasComExemplar)
            .setHeader("Dias com o exemplar")
            .setAutoWidth(true);

        // Agora "Em atraso" usa a MESMA regra da multa
        grid.addColumn(ex -> estaAtrasado(ex) ? "Sim" : "Não")
            .setHeader("Em atraso")
            .setAutoWidth(true);

        // Multa usando MultaCalculadoraServico
        grid.addColumn(ex -> {
                double multa = calcularMulta(ex);
                if (multa > 0.0) {
                    return String.format("R$ %.2f", multa);
                }
                return "-";
            })
            .setHeader("Multa")
            .setAutoWidth(true);

        // Ações
        grid.addComponentColumn(ex -> {
                if (estaAtrasado(ex)) {
                    Button verMulta = new Button("Pagar multa",
                            e -> UI.getCurrent().navigate(MinhasMultasView.class));
                    estilizarBotaoPerigo(verMulta);
                    return verMulta;
                } else {
                    Button devolver = new Button("Devolver",
                            e -> devolverExemplar(ex));
                    estilizarBotaoPrimario(devolver);
                    return devolver;
                }
            })
            .setHeader("Ações")
            .setAutoWidth(true)
            .setFlexGrow(0);
    }

    private void carregarMeusEmprestimos() {
        if (!SessaoUsuario.isLogado()) {
            Notification.show("Você precisa estar logado para ver seus empréstimos.");
            return;
        }

        var socio = SessaoUsuario.getSocio();
        int idSocioLogado = socio.getId().getId();

        // ✅ Iterator: em vez de pegar List direto, pega a coleção iterável
        List<ExemplarResumoExpandido> todosEmprestados =
                exemplarServico.pesquisarEmprestadosIterable().asList();

        var meus = todosEmprestados.stream()
                .filter(ex -> {
                    var tomador = ex.getEmprestimo().getTomador();
                    return tomador != null && tomador.getId() == idSocioLogado;
                })
                .toList();

        grid.setItems(meus);
    }

    private long calcularDiasComExemplar(ExemplarResumoExpandido ex) {
        var inicio = ex.getEmprestimo().getPeriodo().getInicio();
        if (inicio == null) return 0;
        return ChronoUnit.DAYS.between(inicio, LocalDate.now());
    }

    // "atrasado" = multa > 0 segundo a MESMA regra usada no resto do sistema
    private boolean estaAtrasado(ExemplarResumoExpandido ex) {
        var fimPrevisto = ex.getEmprestimo().getPeriodo().getFim();
        if (fimPrevisto == null) return false;

        return multaServico.calcularMultaPendente(fimPrevisto, LocalDate.now()) > 0.0;
    }

    private double calcularMulta(ExemplarResumoExpandido ex) {
        var fimPrevisto = ex.getEmprestimo().getPeriodo().getFim();
        if (fimPrevisto == null) return 0.0;

        return multaServico.calcularMultaPendente(fimPrevisto, LocalDate.now());
    }

    private void devolverExemplar(ExemplarResumoExpandido ex) {
        // Proteção visual da view (primeiro filtro)
        if (estaAtrasado(ex)) {
            Notification.show("Este empréstimo possui multa pendente. Use a tela 'Minhas multas' para pagar.");
            return;
        }

        try {
            String idStr = ex.getId();
            int idInt = Integer.parseInt(idStr);
            ExemplarId exemplarId = new ExemplarId(idInt);

            // Vai cair no PROXY (EmprestimoOperacoes)
            emprestimoServico.devolver(exemplarId);

            Notification.show("Exemplar devolvido com sucesso!", 3000, Position.TOP_CENTER);
            carregarMeusEmprestimos();
        } catch (IllegalStateException e) {
            // Erros de regra de negócio vindos do Proxy
            Notification.show(e.getMessage(), 5000, Position.MIDDLE);
        } catch (Exception erro) {
            erro.printStackTrace();
            Notification.show("Erro ao devolver exemplar: " + erro.getMessage(), 5000,
                    Position.MIDDLE);
        }
    }

    // ===== estilos de botões =====
    private void estilizarBotaoPrimario(Button button) {
        button.getStyle()
                .set("background-color", "#E85D2A")
                .set("color", "white")
                .set("font-weight", "600")
                .set("border-radius", "999px")
                .set("border", "none")
                .set("padding", "0.25rem 0.9rem")
                .set("font-size", "0.85rem")
                .set("box-shadow", "0 3px 8px rgba(0,0,0,0.45)");
    }

    private void estilizarBotaoPerigo(Button button) {
        button.getStyle()
                .set("background-color", "#9A1F2A")
                .set("color", "#F7E9D7")
                .set("font-weight", "500")
                .set("border-radius", "999px")
                .set("border", "1px solid rgba(255,255,255,0.18)")
                .set("padding", "0.25rem 0.9rem")
                .set("font-size", "0.85rem")
                .set("box-shadow", "0 3px 8px rgba(0,0,0,0.45)");
    }

    private LocalDate hojeParaTestar() {
        // MODO TESTE: simular que hoje é 10 dias no futuro
        return LocalDate.now().plusDays(10);
    }

}
