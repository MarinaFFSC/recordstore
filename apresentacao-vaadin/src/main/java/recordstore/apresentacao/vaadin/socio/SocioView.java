package recordstore.apresentacao.vaadin.socio;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.textfield.PasswordField;


import recordstore.aplicacao.administracao.socio.SocioResumo;
import recordstore.aplicacao.administracao.socio.SocioServicoAplicacao;
import recordstore.apresentacao.vaadin.SessaoUsuario;
import recordstore.apresentacao.vaadin.layout.MainLayout;

@Route(value = "admin/socios", layout = MainLayout.class)
public class SocioView extends VerticalLayout implements BeforeEnterObserver {

    private final SocioServicoAplicacao service;
    private final Grid<SocioResumo> grid = new Grid<>(SocioResumo.class, false);

    public SocioView(SocioServicoAplicacao service) {
        this.service = service;

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
        card.setMaxWidth("900px");
        card.setAlignItems(Alignment.STRETCH);
        card.getStyle()
                .set("background-color", "#27131A")
                .set("border-radius", "18px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.6)")
                .set("padding", "24px 28px")
                .set("border", "1px solid rgba(255,255,255,0.06)")
                .set("margin-top", "24px");

        H2 titulo = new H2("Gerenciar sócios");
        titulo.getStyle()
                .set("margin", "0 0 1rem 0")
                .set("color", "#F7E9D7")
                .set("font-weight", "600")
                .set("letter-spacing", "0.05em")
                .set("text-transform", "uppercase");

        Button novo = new Button("Novo Sócio", e -> abrirFormulario());
        novo.getStyle()
                .set("background-color", "#E85D2A")
                .set("color", "white")
                .set("font-weight", "600")
                .set("border-radius", "999px")
                .set("border", "none")
                .set("padding", "0.4rem 1.2rem")
                .set("box-shadow", "0 4px 10px rgba(0,0,0,0.45)");

        configurarGrid();

        card.add(titulo, novo, grid);
        add(card);
    }

    private void configurarGrid() {
        grid.setWidthFull();
        grid.setHeight("400px");
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

        grid.addColumn(SocioResumo::getId)
                .setHeader("ID")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(SocioResumo::getNome)
                .setHeader("Nome")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.addColumn(SocioResumo::getEmail)
                .setHeader("Email")
                .setAutoWidth(true)
                .setFlexGrow(1);

        // Coluna de ações
        grid.addComponentColumn(socio -> {
            Button editar = new Button("Editar", e -> abrirDialogEdicao(socio));
            editar.getStyle()
                    .set("background-color", "#3B2730")
                    .set("color", "#F7E9D7")
                    .set("border-radius", "16px")
                    .set("font-size", "0.75rem")
                    .set("padding", "0.2rem 0.6rem")
                    .set("border", "none");

            Button excluir = new Button("Excluir", e -> confirmarExclusao(socio));
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
        }).setHeader("Ações").setAutoWidth(true).setFlexGrow(0);

        atualizarGrid();
    }

    private void atualizarGrid() {
        grid.setItems(service.pesquisarResumos());
    }

    private void abrirFormulario() {
        getUI().ifPresent(ui -> ui.navigate("novo-socio"));
    }

    // ====== DIÁLOGO DE EDIÇÃO ======
    private void abrirDialogEdicao(SocioResumo socioResumo) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Editar sócio #" + socioResumo.getId());

        TextField nomeField = new TextField("Nome");
        nomeField.setWidthFull();
        nomeField.setValue(socioResumo.getNome() != null ? socioResumo.getNome() : "");

        EmailField emailField = new EmailField("Email");
        emailField.setWidthFull();
        emailField.setValue(socioResumo.getEmail() != null ? socioResumo.getEmail() : "");

        PasswordField senhaField = new PasswordField("Nova senha");
        senhaField.setWidthFull();
        senhaField.setPlaceholder("Deixe em branco para não alterar");

        VerticalLayout content = new VerticalLayout(nomeField, emailField, senhaField);
        content.setPadding(false);
        content.setSpacing(true);
        dialog.add(content);

        Button cancelar = new Button("Cancelar", e -> dialog.close());

        Button salvar = new Button("Salvar", e -> {
            try {
                service.atualizar(
                    socioResumo.getId(),
                    nomeField.getValue(),
                    emailField.getValue(),
                    senhaField.getValue() // pode vir vazio
                );
                Notification.show("Sócio atualizado com sucesso!");
                dialog.close();
                atualizarGrid();
            } catch (Exception ex) {
                Notification.show("Erro ao atualizar: " + ex.getMessage(),
                        5000, Notification.Position.MIDDLE);
            }
        });

        dialog.getFooter().add(cancelar, salvar);
        add(dialog);
        dialog.open();
    }


    // ====== CONFIRMAÇÃO DE EXCLUSÃO ======
    private void confirmarExclusao(SocioResumo socioResumo) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Excluir sócio");

        Paragraph texto = new Paragraph(
                "Tem certeza que deseja excluir o sócio \"" +
                socioResumo.getNome() + "\" (ID " + socioResumo.getId() + ")?");
        dialog.add(texto);

        Button cancelar = new Button("Cancelar", e -> dialog.close());
        cancelar.getStyle()
                .set("background-color", "transparent")
                .set("color", "#3B2730");

        Button excluir = new Button("Excluir", e -> {
            try {
                service.excluir(socioResumo.getId());
                Notification.show("Sócio excluído com sucesso!");
                dialog.close();
                atualizarGrid();
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

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SessaoUsuario.isLogado()) {
            event.forwardTo("login");
            return;
        }
        if (!SessaoUsuario.isAdmin()) {
            event.forwardTo("");
        }
    }
}
