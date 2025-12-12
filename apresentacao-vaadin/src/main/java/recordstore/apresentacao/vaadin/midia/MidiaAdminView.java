package recordstore.apresentacao.vaadin.midia;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import recordstore.apresentacao.vaadin.SessaoUsuario;
import recordstore.apresentacao.vaadin.layout.MainLayout;
import recordstore.apresentacao.vaadin.login.LoginView;

import recordstore.aplicacao.acervo.artista.ArtistaResumo;
import recordstore.aplicacao.acervo.artista.ArtistaServicoAplicacao;
import recordstore.aplicacao.acervo.midia.MidiaResumo;
import recordstore.aplicacao.acervo.midia.MidiaServicoAplicacao;

import recordstore.dominio.acervo.artista.Artista;
import recordstore.dominio.acervo.artista.ArtistaId;
import recordstore.dominio.acervo.artista.ArtistaServico;
import recordstore.dominio.acervo.exemplar.ExemplarRepositorio;
import recordstore.dominio.acervo.midia.CodigoBarra;
import recordstore.dominio.acervo.midia.CodigoBarraFabrica;
import recordstore.dominio.acervo.midia.Midia;
import recordstore.dominio.acervo.midia.MidiaRepositorio;

@Route(value = "admin/midias", layout = MainLayout.class)
@PageTitle("Administração | RecordStore")
public class MidiaAdminView extends VerticalLayout implements BeforeEnterObserver {

    private final MidiaRepositorio midiaRepositorio;

    private final MidiaServicoAplicacao midiaServicoAplicacao;
    private final ArtistaServico artistaServico;
    private final ArtistaServicoAplicacao artistaServicoAplicacao;
    
    private MultiSelectComboBox<ArtistaResumo> artistasCombo;

    private final CodigoBarraFabrica codigoBarraFabrica = new CodigoBarraFabrica();

    private final Grid<MidiaResumo> midiasGrid = new Grid<>(MidiaResumo.class, false);
    private final Grid<ArtistaResumo> artistasGrid = new Grid<>(ArtistaResumo.class, false);

    public MidiaAdminView(MidiaRepositorio midiaRepositorio,
                          ExemplarRepositorio exemplarRepositorio,
                          MidiaServicoAplicacao midiaServicoAplicacao,
                          ArtistaServico artistaServico,
                          ArtistaServicoAplicacao artistaServicoAplicacao) {

        this.midiaRepositorio = midiaRepositorio;
        this.midiaServicoAplicacao = midiaServicoAplicacao;
        this.artistaServico = artistaServico;
        this.artistaServicoAplicacao = artistaServicoAplicacao;

        setSizeFull();
        setPadding(true);
        setSpacing(false);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        getStyle()
                .set("background", "linear-gradient(180deg, #1b1013 0%, #11060a 45%, #050306 100%)");

        // TÍTULO
        H2 titulo = new H2("Administração do acervo");
        titulo.getStyle()
                .set("color", "#F7E9D7")
                .set("margin-top", "0")
                .set("margin-bottom", "0.75rem")
                .set("letter-spacing", "0.08em")
                .set("text-transform", "uppercase");
        add(titulo);

        // SEÇÃO ARTISTAS
        VerticalLayout secArtistas = new VerticalLayout();
        secArtistas.setPadding(true);
        secArtistas.setSpacing(true);
        secArtistas.setWidthFull();
        secArtistas.getStyle()
                .set("background-color", "#28151C")
                .set("border-radius", "16px")
                .set("box-shadow", "0 6px 18px rgba(0,0,0,0.55)")
                .set("border", "1px solid rgba(255,255,255,0.04)")
                .set("margin-bottom", "1rem");

        H3 tituloArtistas = new H3("Artistas");
        tituloArtistas.getStyle()
                .set("color", "#F7E9D7")
                .set("margin-top", "0")
                .set("margin-bottom", "0.5rem");

        secArtistas.add(tituloArtistas, criarFormularioArtista());
        configurarGridArtistas();
        secArtistas.add(artistasGrid);

        // SEÇÃO MÍDIAS
        VerticalLayout secMidias = new VerticalLayout();
        secMidias.setPadding(true);
        secMidias.setSpacing(true);
        secMidias.setWidthFull();
        secMidias.getStyle()
                .set("background-color", "#28151C")
                .set("border-radius", "16px")
                .set("box-shadow", "0 6px 18px rgba(0,0,0,0.55)")
                .set("border", "1px solid rgba(255,255,255,0.04)")
                .set("margin-bottom", "1rem");

        H3 tituloMidias = new H3("Mídias");
        tituloMidias.getStyle()
                .set("color", "#F7E9D7")
                .set("margin-top", "0")
                .set("margin-bottom", "0.5rem");

        secMidias.add(tituloMidias, criarFormularioMidia());
        configurarGridMidias();
        secMidias.add(midiasGrid);

        add(secArtistas, secMidias);

        carregarGridArtistas();
        carregarGridMidias();
        atualizarArtistasCombo();
    }

    // GUARD ADMIN
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SessaoUsuario.isLogado() || !SessaoUsuario.isAdmin()) {
            event.rerouteTo(LoginView.class);
        }
    }

    // ========== ARTISTAS ==========

    private FormLayout criarFormularioArtista() {
        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 3)
        );

        IntegerField idField = new IntegerField("ID do artista");
        idField.setMin(1);
        idField.setStep(1);
        idField.setWidthFull();
        estilizarCampoInput(idField);

        TextField nomeField = new TextField("Nome do artista");
        nomeField.setWidthFull();
        estilizarCampoInput(nomeField);

        Button salvar = new Button("Cadastrar artista", e -> {
            try {
                if (idField.isEmpty() || nomeField.isEmpty()) {
                    Notification.show("Informe ID e nome do artista.");
                    return;
                }

                int id = idField.getValue();
                String nome = nomeField.getValue().trim();
                if (nome.isBlank()) {
                    Notification.show("Nome não pode estar em branco.");
                    return;
                }

                Artista artista = new Artista(new ArtistaId(id), nome);
                artistaServico.salvar(artista);

                Notification.show("Artista cadastrado com sucesso!");
                idField.clear();
                nomeField.clear();
                carregarGridArtistas();
                atualizarArtistasCombo(); 
            } catch (Exception ex) {
                ex.printStackTrace();
                Notification.show("Erro ao salvar artista: " + ex.getMessage(),
                        5000, Notification.Position.MIDDLE);
            }
        });

        estilizarBotaoPrincipal(salvar);

        form.add(idField, nomeField, salvar);
        form.setColspan(nomeField, 1);
        form.setColspan(salvar, 1);

        return form;
    }

    private void configurarGridArtistas() {
        artistasGrid.addColumn(ArtistaResumo::getId)
                .setHeader("ID")
                .setAutoWidth(true);
        artistasGrid.addColumn(ArtistaResumo::getNome)
                .setHeader("Nome")
                .setAutoWidth(true);

        // NOVO: coluna de ações
        artistasGrid.addComponentColumn(this::criarColunaAcoesArtista)
                .setHeader("Ações")
                .setAutoWidth(true)
                .setFlexGrow(0);

        artistasGrid.setHeight("220px");

        artistasGrid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_NO_BORDER
        );
        artistasGrid.getStyle()
                .set("background-color", "#FFFFFF")
                .set("border-radius", "8px")
                .set("border", "1px solid rgba(0,0,0,0.06)")
                .set("font-size", "0.9rem");
        artistasGrid.getElement().getStyle()
                .set("--lumo-body-text-color", "#2B151C")
                .set("--lumo-header-text-color", "#2B151C");
    }


    private void carregarGridArtistas() {
        List<ArtistaResumo> artistas = artistaServicoAplicacao.pesquisarResumos();
        artistasGrid.setItems(artistas);
    }
    
    private HorizontalLayout criarColunaAcoesArtista(ArtistaResumo resumo) {
        Button editar = new Button("Editar", e -> abrirDialogEdicaoArtista(resumo));
        editar.getStyle()
                .set("background-color", "#3B2730")
                .set("color", "#F7E9D7")
                .set("border-radius", "16px")
                .set("font-size", "0.75rem")
                .set("padding", "0.2rem 0.6rem")
                .set("border", "none");

        Button excluir = new Button("Excluir", e -> confirmarExclusaoArtista(resumo));
        excluir.getStyle()
                .set("background-color", "#C0392B")
                .set("color", "white")
                .set("border-radius", "16px")
                .set("font-size", "0.75rem")
                .set("padding", "0.2rem 0.6rem")
                .set("border", "none");

        HorizontalLayout layout = new HorizontalLayout(editar, excluir);
        layout.setSpacing(true);
        return layout;
    }

    private void abrirDialogEdicaoArtista(ArtistaResumo resumo) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Editar artista #" + resumo.getId());

        TextField nomeField = new TextField("Nome");
        nomeField.setWidthFull();
        nomeField.setValue(resumo.getNome() != null ? resumo.getNome() : "");
        estilizarCampoInput(nomeField);

        VerticalLayout content = new VerticalLayout(nomeField);
        content.setPadding(false);
        content.setSpacing(true);
        dialog.add(content);

        Button cancelar = new Button("Cancelar", e -> dialog.close());
        cancelar.getStyle()
                .set("background-color", "transparent")
                .set("color", "#3B2730");

        Button salvar = new Button("Salvar", e -> {
            try {
                String novoNome = nomeField.getValue();
                if (novoNome == null || novoNome.isBlank()) {
                    Notification.show("O nome não pode estar em branco.");
                    return;
                }

                var artistaId = new ArtistaId(resumo.getId());
                var artista = artistaServico.obter(artistaId);
                artista.setNome(novoNome);
                artistaServico.salvar(artista);

                Notification.show("Artista atualizado com sucesso!");
                dialog.close();
                carregarGridArtistas();
                atualizarArtistasCombo(); // recarrega combo de artistas da seção de mídias
            } catch (Exception ex) {
                Notification.show("Erro ao atualizar artista: " + ex.getMessage(),
                        5000, Notification.Position.MIDDLE);
            }
        });

        salvar.getStyle()
                .set("background-color", "#E85D2A")
                .set("color", "white")
                .set("border-radius", "999px")
                .set("border", "none")
                .set("padding", "0.3rem 0.9rem");

        dialog.getFooter().add(cancelar, salvar);
        add(dialog);
        dialog.open();
    }

    private void confirmarExclusaoArtista(ArtistaResumo resumo) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Excluir artista");

        Paragraph texto = new Paragraph(
                "Tem certeza que deseja excluir o artista \"" +
                resumo.getNome() + "\" (ID " + resumo.getId() + ")?");
        dialog.add(texto);

        Button cancelar = new Button("Cancelar", e -> dialog.close());
        cancelar.getStyle()
                .set("background-color", "transparent")
                .set("color", "#3B2730");

        Button excluir = new Button("Excluir", e -> {
            try {
                var artistaId = new ArtistaId(resumo.getId());
                artistaServico.excluir(artistaId);

                Notification.show("Artista excluído com sucesso!");
                dialog.close();
                carregarGridArtistas();
                atualizarArtistasCombo();
            } catch (Exception ex) {
                Notification.show("Erro ao excluir artista: " + ex.getMessage(),
                        5000, Notification.Position.MIDDLE);
            }
        });
        excluir.getStyle()
                .set("background-color", "#C0392B")
                .set("color", "white")
                .set("border-radius", "999px")
                .set("border", "none")
                .set("padding", "0.3rem 0.9rem");

        dialog.getFooter().add(cancelar, excluir);

        add(dialog);
        dialog.open();
    }


    // ========== MÍDIAS ==========

    private FormLayout criarFormularioMidia() {
        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("700px", 2)
        );

        TextField codigoField    = new TextField("Código de barras (10 ou 13 dígitos)");
        TextField tituloField    = new TextField("Título");
        TextField subtituloField = new TextField("Subtítulo");
        TextArea  descricaoField = new TextArea("Descrição");
        codigoField.setWidthFull();
        tituloField.setWidthFull();
        subtituloField.setWidthFull();
        descricaoField.setWidthFull();
        descricaoField.setHeight("90px");

        estilizarCampoInput(codigoField);
        estilizarCampoInput(tituloField);
        estilizarCampoInput(subtituloField);
        estilizarCampoInput(descricaoField);

        artistasCombo = new MultiSelectComboBox<>("Artistas");
        artistasCombo.setItemLabelGenerator(a -> a.getId() + " - " + a.getNome());
        artistasCombo.setPlaceholder("Selecione um ou mais artistas");
        artistasCombo.setWidthFull();
        estilizarCampoInput(artistasCombo);

        Button salvarMidia = new Button("Cadastrar mídia", e -> {
            try {
                String codigo    = codigoField.getValue();
                String titulo    = tituloField.getValue();
                String subtitulo = subtituloField.getValue();
                String descricao = descricaoField.getValue();

                if (codigo.isBlank() || titulo.isBlank()) {
                    Notification.show("Código e título são obrigatórios.");
                    return;
                }

                var selecionados = artistasCombo.getValue();
                if (selecionados == null || selecionados.isEmpty()) {
                    Notification.show("Selecione pelo menos um artista.");
                    return;
                }

                List<ArtistaId> artistas = selecionados.stream()
                        .map(a -> new ArtistaId(a.getId()))
                        .collect(Collectors.toList());

                CodigoBarra codigoBarra = codigoBarraFabrica.construir(codigo);

                Midia midia = new Midia(
                        codigoBarra,
                        titulo,
                        subtitulo,
                        descricao,
                        artistas
                );

                midiaRepositorio.salvar(midia);

                Notification.show("Mídia cadastrada com sucesso!");
                limparCamposMidia(codigoField, tituloField, subtituloField, descricaoField);
                artistasCombo.clear();
                carregarGridMidias();
            } catch (Exception ex) {
                ex.printStackTrace();
                Notification.show("Erro ao cadastrar mídia: " + ex.getMessage(),
                        5000, Notification.Position.MIDDLE);
            }
        });

        estilizarBotaoPrincipal(salvarMidia);

        form.add(codigoField, tituloField, subtituloField, descricaoField, artistasCombo, salvarMidia);
        form.setColspan(descricaoField, 2);
        form.setColspan(artistasCombo, 2);
        form.setColspan(salvarMidia, 2);

        return form;
    }

    private void limparCamposMidia(TextField codigo, TextField titulo,
                                   TextField subtitulo, TextArea descricao) {
        codigo.clear();
        titulo.clear();
        subtitulo.clear();
        descricao.clear();
    }

    private void configurarGridMidias() {
        midiasGrid.addColumn(MidiaResumo::getId).setHeader("Código");
        midiasGrid.addColumn(MidiaResumo::getTitulo).setHeader("Título");
        midiasGrid.addColumn(MidiaResumo::getSubtitulo).setHeader("Subtítulo");
        midiasGrid.addColumn(MidiaResumo::getDescricao).setHeader("Descrição");
        midiasGrid.addComponentColumn(this::criarColunaAcoes)
                .setHeader("Ações")
                .setAutoWidth(true)
                .setFlexGrow(0);

        midiasGrid.setHeight("260px");

        midiasGrid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_NO_BORDER
        );
        midiasGrid.getStyle()
                .set("background-color", "#FFFFFF")
                .set("border-radius", "8px")
                .set("border", "1px solid rgba(0,0,0,0.06)")
                .set("font-size", "0.9rem");
        midiasGrid.getElement().getStyle()
                .set("--lumo-body-text-color", "#2B151C")
                .set("--lumo-header-text-color", "#2B151C");
    }

    private void carregarGridMidias() {
        var lista = midiaServicoAplicacao.pesquisarResumos();
        midiasGrid.setItems(lista);
    }

    private HorizontalLayout criarColunaAcoes(MidiaResumo resumo) {
        Button editar = new Button("Editar", e ->
                getUI().ifPresent(ui -> ui.navigate("midia/alterar/" + resumo.getId()))
        );
        editar.getStyle()
                .set("background-color", "#3B2730")
                .set("color", "#F7E9D7")
                .set("border-radius", "16px")
                .set("font-size", "0.75rem")
                .set("padding", "0.2rem 0.6rem")
                .set("border", "none");

        Button excluir = new Button("Excluir", e -> confirmarExclusaoMidia(resumo));
        excluir.getStyle()
                .set("background-color", "#C0392B")
                .set("color", "white")
                .set("border-radius", "16px")
                .set("font-size", "0.75rem")
                .set("padding", "0.2rem 0.6rem")
                .set("border", "none");

        HorizontalLayout layout = new HorizontalLayout(editar, excluir);
        layout.setSpacing(true);
        return layout;
    }

    private void confirmarExclusaoMidia(MidiaResumo resumo) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Excluir mídia");

        Paragraph texto = new Paragraph(
                "Tem certeza que deseja excluir a mídia \"" +
                resumo.getTitulo() + "\" (código " + resumo.getId() + ")?");
        dialog.add(texto);

        Button cancelar = new Button("Cancelar", e -> dialog.close());
        cancelar.getStyle()
                .set("background-color", "transparent")
                .set("color", "#3B2730");

        Button excluir = new Button("Excluir", e -> {
            try {
                CodigoBarra codigo = codigoBarraFabrica.construir(resumo.getId());
                midiaRepositorio.excluir(codigo);

                Notification.show("Mídia excluída com sucesso!");
                dialog.close();
                carregarGridMidias();
            } catch (Exception ex) {
                Notification.show("Erro ao excluir: " + ex.getMessage(),
                        5000, Notification.Position.MIDDLE);
            }
        });
        excluir.getStyle()
                .set("background-color", "#C0392B")
                .set("color", "white")
                .set("border-radius", "999px")
                .set("border", "none")
                .set("padding", "0.3rem 0.9rem");

        dialog.getFooter().add(cancelar, excluir);

        add(dialog);
        dialog.open();
    }

    private void atualizarArtistasCombo() {
        if (artistasCombo != null) {
            var artistas = artistaServicoAplicacao.pesquisarResumos();
            artistasCombo.setItems(artistas);
        }
    }

    // ========== BOTÕES ==========

    private void estilizarBotaoPrincipal(Button button) {
        button.setWidthFull();
        button.getStyle()
                .set("background-color", "#E85D2A")
                .set("color", "white")
                .set("font-weight", "600")
                .set("border-radius", "999px")
                .set("margin-top", "0.5rem")
                .set("box-shadow", "0 4px 10px rgba(0,0,0,0.45)");
    }

    // ========== ESTILO DOS INPUTS ==========

    private void estilizarCampoInput(HasStyle field) {
        field.getStyle()
            // fundo e bordas
            .set("--vaadin-input-field-background", "#23171C")
            .set("--vaadin-input-field-border-color", "#6A545E")
            .set("--vaadin-input-field-hover-border-color", "#3B2730")
            .set("--vaadin-input-field-focused-border-color", "#F7E9D7")

            // tira azul padrão
            .set("--vaadin-focus-ring-color", "transparent")
            .set("--lumo-primary-color", "#F7E9D7")
            .set("--lumo-primary-text-color", "#F7E9D7")
            .set("--lumo-primary-contrast-color", "#000000")
            .set("--vaadin-input-field-caret-color", "#F7E9D7")
    
            .set("--vaadin-input-field-background", "#FFFFFF") // container
            .set("--vaadin-input-field-background-color", "#FFFFFF")// fundo REAL do input
            
            // texto
            .set("--vaadin-input-field-value-color", "#3B2730")
            .set("--vaadin-input-field-label-color", "#F7E9D7")
            .set("--vaadin-input-field-placeholder-color", "#C9B7A8")

            .set("--vaadin-input-field-padding", "0.4rem 0.75rem");
    }
}
