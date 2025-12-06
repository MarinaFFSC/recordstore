package recordstore.apresentacao.acervo.midia;

import static com.vaadin.flow.component.UI.getCurrent;
import static com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY;
import static com.vaadin.flow.component.notification.Notification.Position.TOP_CENTER;
import static com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS;
import static com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import recordstore.dominio.acervo.midia.CodigoBarra;
import recordstore.dominio.acervo.midia.CodigoBarra;
import recordstore.dominio.acervo.midia.MidiaServico;
import jakarta.annotation.PostConstruct;

@Route("midia/alterar")
public class MidiaEditarRota extends VerticalLayout implements HasUrlParameter<String> {
	private static final long serialVersionUID = -405348699654790564L;

	private @Autowired MidiaServico servico;

	private MidiaFormulario formulario;
	private CodigoBarra CodigoBarra;

	@PostConstruct
	private void configurar() {
		var titulo = new H1("Edição de midia");
		add(titulo);

		formulario = new MidiaFormulario(true);
		add(formulario);

		var salvar = new Button("Salvar");
		salvar.addThemeVariants(LUMO_PRIMARY);
		salvar.addClickListener(this::salvar);
		add(salvar);
		setHorizontalComponentAlignment(END, salvar);
	}

	@Override
	public void setParameter(BeforeEvent event, String parameter) {
		var fabrica = new CodigoBarra();
		CodigoBarra = fabrica.construir(parameter);

		var midia = servico.obter(CodigoBarra);
		formulario.ler(midia);
	}

	private void salvar(ClickEvent<Button> evento) {
		var midia = servico.obter(CodigoBarra);
		formulario.escrever(midia);
		servico.salvar(midia);

		var notificacao = new Notification("Midia salvo com sucesso", 10000, TOP_CENTER);
		notificacao.addThemeVariants(LUMO_SUCCESS);
		notificacao.open();

		var ui = getCurrent();
		var pagina = ui.getPage();
		var historico = pagina.getHistory();
		historico.back();
	}
}