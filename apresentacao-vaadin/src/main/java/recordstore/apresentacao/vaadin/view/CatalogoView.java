package recordstore.apresentacao.vaadin.view;

import java.time.LocalDate;

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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.springframework.beans.factory.annotation.Qualifier;

import recordstore.apresentacao.vaadin.SessaoUsuario;
import recordstore.apresentacao.vaadin.layout.MainLayout;
import recordstore.apresentacao.vaadin.login.LoginView;

import recordstore.aplicacao.acervo.midia.MidiaResumoExpandido;
import recordstore.aplicacao.acervo.midia.MidiaServicoAplicacao;

import recordstore.aplicacao.acervo.exemplar.ExemplarServicoAplicacao;
import recordstore.aplicacao.acervo.exemplar.ExemplarResumoExpandido;

import recordstore.aplicacao.analise.MultaCalculadoraServico;

import recordstore.dominio.administracao.Socio;
import recordstore.dominio.acervo.midia.CodigoBarraFabrica;
import recordstore.dominio.acervo.exemplar.EmprestimoOperacoes;

@Route(value = "catalogo", layout = MainLayout.class)
@PageTitle("Catálogo de mídias | RecordStore")
public class CatalogoView extends VerticalLayout implements BeforeEnterObserver {

    private final MidiaServicoAplicacao midiaServico;
    private final ExemplarServicoAplicacao exemplarServico;
    // AGORA: usamos a interface que é implementada pelo PROXY
    private final EmprestimoOperacoes emprestimoServico;
    // Para usar a mesma regra de multa do backend
    private final MultaCalculadoraServico multaServico;

    private final Grid<MidiaResumoExpandido> grid = new Grid<>(MidiaResumoExpandido.class, false);

    private final CodigoBarraFabrica codigoBarraFabrica = new CodigoBarraFabrica();

    public CatalogoView(MidiaServicoAplicacao midiaServico,
                        ExemplarServicoAplicacao exemplarServico,
                        @Qualifier("emprestimoOperacoes") EmprestimoOperacoes emprestimoServico,
                        MultaCalculadoraServico multaServico) {
        this.midiaServico = midiaServico;
        this.exemplarServico = exemplarServico;
        this.emprestimoServico = emprestimoServico;
        this.multaServico = multaServico;

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

        H2 titulo = new H2("Catálogo de mídias");
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

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SessaoUsuario.isLogado()) {
            Notification.show("Faça login para acessar o catálogo.");
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

        grid.addColumn(r -> r.getMidia().getTitulo())
            .setHeader("Título")
            .setAutoWidth(true)
            .setSortable(true);

        grid.addColumn(r -> r.getMidia().getSubtitulo())
            .setHeader("Subtítulo")
            .setAutoWidth(true);

        grid.addColumn(r -> r.getMidia().getDescricao())
            .setHeader("Descrição")
            .setAutoWidth(true);

        grid.addColumn(r -> r.getArtista().getNome())
            .setHeader("Artista principal")
            .setAutoWidth(true);

        grid.addColumn(MidiaResumoExpandido::getExemplaresDisponiveis)
            .setHeader("Disponíveis")
            .setAutoWidth(true);

        grid.addColumn(MidiaResumoExpandido::getTotalExemplares)
            .setHeader("Total")
            .setAutoWidth(true);

        grid.addComponentColumn(r -> {
            Button btn = new Button("Emprestar", click -> {
                if (!SessaoUsuario.isLogado()) {
                    Notification.show("Faça login para realizar empréstimos.");
                    return;
                }

                var socio = SessaoUsuario.getSocio();

                // Usa a mesma regra de multa do backend (MultaCalculadoraServico)
                if (socioTemMulta(socio)) {
                    Notification.show(
                        "Você possui multa/atraso em empréstimos. Regularize suas multas antes de fazer um novo empréstimo.",
                        5000,
                        Position.MIDDLE
                    );
                    return;
                }

                if (r.getExemplaresDisponiveis() <= 0) {
                    Notification.show("Não há exemplares disponíveis desta mídia.");
                    return;
                }

                try {
                    String codigoMidia = r.getMidia().getId();
                    var codigoBarra = codigoBarraFabrica.construir(codigoMidia);
                    var socioId = socio.getId();

                    // Vai cair no PROXY (EmprestimoServicoProxy)
                    emprestimoServico.realizarEmprestimo(codigoBarra, socioId);

                    Notification.show("Empréstimo realizado com sucesso!", 3000, Position.TOP_CENTER);
                    carregarDados();

                } catch (IllegalStateException ex) {
                    // Mensagem do Proxy (ex.: multa pendente, etc.)
                    Notification.show(ex.getMessage(), 5000, Position.MIDDLE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Notification.show("Erro ao realizar empréstimo: " + ex.getMessage(),
                            5000, Position.MIDDLE);
                }
            });

            btn.setEnabled(r.getExemplaresDisponiveis() > 0);
            btn.getStyle()
                    .set("background-color", "#E85D2A")
                    .set("color", "white")
                    .set("font-weight", "600")
                    .set("border-radius", "999px")
                    .set("border", "none")
                    .set("padding", "0.25rem 0.9rem")
                    .set("font-size", "0.85rem")
                    .set("box-shadow", "0 3px 8px rgba(0,0,0,0.45)");

            return btn;
        }).setHeader("Ações");
    }

    private void carregarDados() {
        var itens = midiaServico.pesquisarResumosExpandidos();
        grid.setItems(itens);
    }

    /**
     * Verifica se o sócio tem qualquer empréstimo com multa (> 0) usando a mesma
     * regra do serviço de multa (Strategy).
     */
    private boolean socioTemMulta(Socio socio) {
        if (socio == null || socio.getId() == null) {
            return false;
        }

        int idSocio = socio.getId().getId();
        LocalDate hoje = LocalDate.now();

        // ✅ Iterator aplicado aqui (coleção iterável de emprestados)
        return exemplarServico.pesquisarEmprestadosIterable().stream()
            // só empréstimos do sócio
            .filter(ex -> {
                var emprestimo = ex.getEmprestimo();
                if (emprestimo == null || emprestimo.getTomador() == null) {
                    return false;
                }
                return emprestimo.getTomador().getId() == idSocio;
            })
            // verifica se para esse empréstimo a multa é > 0 segundo a Strategy
            .anyMatch(ex -> {
                var fimPrevisto = ex.getEmprestimo().getPeriodo().getFim();
                if (fimPrevisto == null) return false;
                double multa = multaServico.calcularMultaPendente(fimPrevisto, hojeParaTeste());
                return multa > 0.0;
            });
    }

    public LocalDate hojeParaTeste() {
        return LocalDate.now().plusDays(10);  // simula 10 dias de atraso
    }

}
