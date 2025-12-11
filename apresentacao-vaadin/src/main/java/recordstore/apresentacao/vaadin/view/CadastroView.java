package recordstore.apresentacao.vaadin.view;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import recordstore.aplicacao.administracao.socio.SocioServicoAplicacao;

@Route("cadastro")
@PageTitle("Cadastro de usuário | RecordStore")
public class CadastroView extends VerticalLayout {

    private final SocioServicoAplicacao socioServico;

    public CadastroView(SocioServicoAplicacao socioServico) {
        this.socioServico = socioServico;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Fundo no padrão RecordStore
        getStyle()
                .set("background", "radial-gradient(circle at top, #3a1f2b 0, #12070b 55%, #050305 100%)");

        // Card central
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(Alignment.STRETCH);
        card.setWidth("420px");
        card.getStyle()
                .set("background-color", "#27131A")
                .set("border-radius", "18px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.6)")
                .set("padding", "24px 28px")
                .set("border", "1px solid rgba(255,255,255,0.06)");

        H2 titulo = new H2("Cadastro de usuário");
        titulo.getStyle()
                .set("margin", "0")
                .set("font-size", "1.6rem")
                .set("color", "#F7E9D7")
                .set("letter-spacing", "0.06em")
                .set("text-transform", "uppercase")
                .set("text-align", "center");

        Paragraph subtitulo = new Paragraph(
                "Crie sua conta de sócio para acessar o catálogo e fazer empréstimos."
        );
        subtitulo.getStyle()
                .set("margin-top", "0.5rem")
                .set("margin-bottom", "1.25rem")
                .set("color", "#C9B7A8")
                .set("font-size", "0.95rem")
                .set("text-align", "center");

        IntegerField idField = new IntegerField("ID do sócio");
        idField.setRequiredIndicatorVisible(true);
        idField.setMin(1);
        idField.setWidthFull();
        estilizarCampoInput(idField);

        TextField nomeField = new TextField("Nome completo");
        nomeField.setRequiredIndicatorVisible(true);
        nomeField.setWidthFull();
        estilizarCampoInput(nomeField);

        EmailField emailField = new EmailField("E-mail");
        emailField.setRequiredIndicatorVisible(true);
        emailField.setClearButtonVisible(true);
        emailField.setWidthFull();
        estilizarCampoInput(emailField);

        PasswordField senhaField = new PasswordField("Senha");
        senhaField.setRequiredIndicatorVisible(true);
        senhaField.setWidthFull();
        estilizarCampoInput(senhaField);

        Button cadastrar = new Button("Cadastrar", e -> {
            if (!validar(idField, nomeField, emailField, senhaField)) {
                return;
            }

            try {
                Integer id = idField.getValue();
                String nome = nomeField.getValue().trim();
                String email = emailField.getValue().trim();
                String senha = senhaField.getValue();

                socioServico.criar(id, nome, email, senha);

                Notification.show("Cadastro realizado com sucesso!", 3000, Notification.Position.MIDDLE);

                getUI().ifPresent(ui -> ui.navigate("login"));

            } catch (IllegalArgumentException ex) {
                Notification.show(ex.getMessage(), 4000, Notification.Position.MIDDLE);
            } catch (Exception ex) {
                ex.printStackTrace();
                Notification.show("Erro inesperado ao cadastrar.", 5000, Notification.Position.MIDDLE);
            }
        });
        estilizarBotaoPrimario(cadastrar);

        FormLayout form = new FormLayout(idField, nomeField, emailField, senhaField, cadastrar);
        form.setWidthFull();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        card.add(titulo, subtitulo, form);
        add(card);
    }

    private boolean validar(IntegerField idField, TextField nomeField,
                            EmailField emailField, PasswordField senhaField) {

        if (idField.getValue() == null || idField.getValue() <= 0) {
            Notification.show("Informe um ID válido (inteiro maior que zero).",
                    3000, Notification.Position.MIDDLE);
            return false;
        }

        if (nomeField.getValue() == null || nomeField.getValue().trim().isEmpty()) {
            Notification.show("Informe o nome.",
                    3000, Notification.Position.MIDDLE);
            return false;
        }

        if (emailField.getValue() == null || emailField.getValue().trim().isEmpty()) {
            Notification.show("Informe o e-mail.",
                    3000, Notification.Position.MIDDLE);
            return false;
        }

        // aqui você pode adicionar validação da senha se quiser
        return true;
    }

    // ===== estilos =====

    private void estilizarCampoInput(HasStyle field) {
        field.getStyle()
                .set("--vaadin-input-field-background", "#1A0D12")
                .set("--vaadin-input-field-border-color", "rgba(247,233,215,0.28)")
                .set("--vaadin-input-field-hover-border-color", "#F7E9D7")
                .set("--vaadin-input-field-focus-ring-color", "#E85D2A")
                .set("--vaadin-input-field-label-color", "#F7E9D7")
                .set("--vaadin-input-field-value-color", "#F7E9D7")
                .set("--vaadin-input-field-placeholder-color", "#C9B7A8");
    }

    private void estilizarBotaoPrimario(Button button) {
        button.setWidthFull();
        button.getStyle()
                .set("background-color", "#E85D2A")
                .set("color", "white")
                .set("font-weight", "600")
                .set("border-radius", "999px")
                .set("margin-top", "0.75rem")
                .set("border", "none")
                .set("box-shadow", "0 4px 10px rgba(0,0,0,0.45)");
    }
}
