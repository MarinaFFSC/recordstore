package recordstore.apresentacao.vaadin.midia;

import static java.util.Objects.nonNull;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import recordstore.dominio.acervo.midia.Midia;

public class MidiaFormulario extends VerticalLayout {
    private static final long serialVersionUID = -4762635896445399303L;

    private TextField codigoBarraCampo;
    private TextField tituloCampo;
    private TextField subtituloCampo;

    public MidiaFormulario(boolean edicao) {
        setPadding(false);
        setSpacing(true);
        setMargin(false);
        getStyle().set("gap", "0.75rem");

        codigoBarraCampo = new TextField("Código de barras");
        codigoBarraCampo.setWidthFull();
        codigoBarraCampo.setRequired(true);
        codigoBarraCampo.setReadOnly(edicao);
        estilizarCampoInput(codigoBarraCampo);
        add(codigoBarraCampo);

        tituloCampo = new TextField("Título");
        tituloCampo.setWidthFull();
        tituloCampo.setRequired(true);
        estilizarCampoInput(tituloCampo);
        add(tituloCampo);

        subtituloCampo = new TextField("Subtítulo");
        subtituloCampo.setWidthFull();
        estilizarCampoInput(subtituloCampo);
        add(subtituloCampo);
    }

    public void ler(Midia midia) {
        nonNull(midia);

        var codigoBarra = midia.getId().toString();
        var titulo = midia.getTitulo();
        var subtitulo = midia.getSubtitulo();

        setValue(codigoBarraCampo, codigoBarra);
        setValue(tituloCampo, titulo);
        setValue(subtituloCampo, subtitulo);
    }

    private void setValue(TextField campo, String valor) {
        if (valor != null) {
            campo.setValue(valor);
        } else {
            campo.setValue("");
        }
    }

    public void escrever(Midia midia) {
        nonNull(midia);

        midia.setTitulo(valor(tituloCampo));
        midia.setSubtitulo(valor(subtituloCampo));
    }

    private String valor(TextField campo) {
        var valor = campo.getValue();
        return valor.length() > 0 ? valor : null;
    }

    // ================== ESTILO DOS INPUTS ==================

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
}
