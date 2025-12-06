package recordstore.apresentacao.acervo.midia;

import static java.util.Objects.nonNull;

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

		codigoBarraCampo = new TextField("Codigo de Barra");
		codigoBarraCampo.setWidthFull();
		codigoBarraCampo.setRequired(true);
		codigoBarraCampo.setReadOnly(edicao);
		add(codigoBarraCampo);

		tituloCampo = new TextField("Título");
		tituloCampo.setWidthFull();
		tituloCampo.setRequired(true);
		add(tituloCampo);

		subtituloCampo = new TextField("Subtítulo");
		subtituloCampo.setWidthFull();
		add(subtituloCampo);
	}

	public void ler(Midia midia) {
		nonNull(midia);

		var codigoBarra = midia.getId().toString();
		var titulo = midia.getTitulo();
		var subtitulo = midia.getSubTitulo();

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
		midai.setSubTitulo(valor(subtituloCampo));
	}

	private String valor(TextField campo) {
		var valor = campo.getValue();
		return valor.length() > 0 ? valor : null;
	}
}