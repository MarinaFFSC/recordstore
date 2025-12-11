package recordstore.apresentacao.vaadin.socio;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.Route;

import recordstore.aplicacao.administracao.socio.SocioServicoAplicacao;

@Route("novo-socio")
public class NovoSocioView extends VerticalLayout {

    public NovoSocioView(SocioServicoAplicacao service) {

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Fundo geral no mesmo padrão do app
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

        H2 titulo = new H2("Novo Sócio");
        titulo.getStyle()
                .set("margin", "0 0 1rem 0")
                .set("font-size", "1.5rem")
                .set("color", "#F7E9D7")
                .set("letter-spacing", "0.06em")
                .set("text-transform", "uppercase")
                .set("text-align", "center");

        IntegerField id = new IntegerField("ID");
        id.setMin(1);
        id.setStep(1);
        id.setWidthFull();
        estilizarCampoInput(id);

        TextField nome = new TextField("Nome");
        nome.setWidthFull();
        estilizarCampoInput(nome);

        EmailField email = new EmailField("Email");
        email.setWidthFull();
        estilizarCampoInput(email);

        PasswordField senha = new PasswordField("Senha");
        senha.setWidthFull();
        estilizarCampoInput(senha);

        Button salvar = new Button("Salvar", event -> {
            try {
                service.criar(id.getValue(), nome.getValue(), email.getValue(), senha.getValue());
                Notification.show("Sócio criado com sucesso!");
                getUI().ifPresent(ui -> ui.navigate(""));
            } catch (Exception e) {
                Notification.show("Erro: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });
        estilizarBotaoPrimario(salvar);

        FormLayout form = new FormLayout(id, nome, email, senha, salvar);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );
        form.setWidthFull();

        card.add(titulo, form);
        add(card);
    }

    // ========== Estilo dos campos ==========
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

    // ========== Estilo do botão primário ==========
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
