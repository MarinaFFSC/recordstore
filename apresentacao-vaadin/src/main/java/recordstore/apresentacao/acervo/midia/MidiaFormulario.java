package recordstore.apresentacao.acervo.midia;

import static java.util.Objects.nonNull;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import recordstore.dominio.acervo.midia.Midia;

public class MidiaFormulario extends VerticalLayout {
	private static final long serialVersionUID = -4762635896445399303L;

	private TextField CodigoBarraCampo;
	private TextField tituloCampo;
	private TextField subtituloCampo;

	public MidiaFormulario(boolean edicao) {
		setPadding(false);

		CodigoBarraCampo = new TextField("Codigo de Barra");
		CodigoBarraCampo.setWidthFull();
		CodigoBarraCampo.setRequired(true);
		CodigoBarraCampo.setReadOnly(edicao);
		add(CodigoBarraCampo);

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

		var CodigoBarra = midia.getId().toString();
		var titulo = midia.getTitulo();
		var subtitulo = midia.getSubTitulo();

		setValue(CodigoBarraCampo, CodigoBarra);
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