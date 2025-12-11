package recordstore.apresentacao.vaadin.login;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import recordstore.apresentacao.vaadin.SessaoUsuario;
import recordstore.apresentacao.vaadin.view.CadastroView;
import recordstore.dominio.administracao.Socio;
import recordstore.dominio.administracao.SocioRepositorio;
import recordstore.dominio.administracao.socio.SocioId;

@Route("login")
public class LoginView extends VerticalLayout {

    public LoginView(SocioRepositorio socioRepositorio) {

        // ======= Layout raiz (fundo da página) =======
        setSizeFull();
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        getStyle()
                .set("background", "radial-gradient(circle at top, #3a1f2b 0, #12070b 55%, #050305 100%)")
                .set("padding", "0")
                .set("margin", "0");

        // ======= Card central =======
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(Alignment.STRETCH);
        card.setWidth("360px");

        card.getStyle()
                .set("background-color", "#27131A")
                .set("border-radius", "18px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.6)")
                .set("padding", "24px 28px")
                .set("border", "1px solid rgba(255,255,255,0.06)");

        // ======= Título =======
        H1 titulo = new H1("RecordStore");
        titulo.getStyle()
                .set("margin", "0")
                .set("font-size", "1.8rem")
                .set("color", "#F7E9D7")
                .set("letter-spacing", "0.12em")
                .set("text-transform", "uppercase")
                .set("text-align", "center");

        Paragraph subtitulo = new Paragraph("Entre para curtir seu acervo de vinil favorito 🎧");
        subtitulo.getStyle()
                .set("margin-top", "0.4rem")
                .set("margin-bottom", "1.4rem")
                .set("font-size", "0.9rem")
                .set("color", "#C9B7A8")
                .set("text-align", "center");

        // ======= Campos =======
        IntegerField idField = new IntegerField("ID do Sócio");
        idField.setRequiredIndicatorVisible(true);
        idField.setWidthFull();
        idField.setPlaceholder("Ex: 1");
        estilizarCampoInput(idField);

        PasswordField senhaField = new PasswordField("Senha");
        senhaField.setRequiredIndicatorVisible(true);
        senhaField.setWidthFull();
        senhaField.setPlaceholder("Sua senha de acesso");
        estilizarCampoInput(senhaField);

        // ======= Botão Entrar =======
        Button entrar = new Button("Entrar", e -> {
            try {
                if (idField.isEmpty() || senhaField.isEmpty()) {
                    Notification.show("Preencha ID e senha.");
                    return;
                }

                var socioId = new SocioId(idField.getValue());
                Socio socio = socioRepositorio.obter(socioId);

                if (!socio.getSenha().getValor().equals(senhaField.getValue())) {
                    Notification.show("Senha incorreta.");
                    return;
                }

                SessaoUsuario.setSocio(socio);
                getUI().ifPresent(ui -> ui.navigate(""));
            } catch (Exception ex) {
                Notification.show("Sócio não encontrado");
            }
        });

        entrar.setWidthFull();
        entrar.getStyle()
                .set("margin-top", "0.8rem")
                .set("background-color", "#E85D2A")
                .set("color", "white")
                .set("font-weight", "600")
                .set("border-radius", "999px")
                .set("box-shadow", "0 4px 10px rgba(0,0,0,0.45)")
                .set("border", "none");

        // ======= Link de cadastro =======
        RouterLink linkCadastro = new RouterLink("Não tem conta? Cadastre-se", CadastroView.class);

        linkCadastro.getElement().getStyle()
                .set("margin-top", "0.6rem")
                .set("font-size", "0.85rem")
                .set("color", "#F7E9D7")
                .set("text-decoration", "underline")
                .set("text-underline-offset", "2px")
                .set("text-align", "center")
                .set("display", "block");

        // ======= Montagem =======
        card.add(titulo, subtitulo, idField, senhaField, entrar, linkCadastro);
        add(card);
    }

    // ==============================================================
    // CSS APLICADO A TODOS OS INPUTS (LABEL, TEXTO, PLACEHOLDER, ETC)
    // ==============================================================
    private void estilizarCampoInput(HasStyle field) {
        field.getStyle()
                .set("--vaadin-input-field-background", "#1A0D12") // Fundo escuro elegante
                .set("--vaadin-input-field-border-color", "rgba(247,233,215,0.28)")
                .set("--vaadin-input-field-hover-border-color", "#F7E9D7")
                .set("--vaadin-input-field-focus-ring-color", "#E85D2A") // Laranja queimado do tema
                .set("--vaadin-input-field-label-color", "#F7E9D7")
                .set("--vaadin-input-field-value-color", "#F7E9D7") // TEXTO DIGITADO
                .set("--vaadin-input-field-placeholder-color", "#C9B7A8"); // Placeholder
    }
}
