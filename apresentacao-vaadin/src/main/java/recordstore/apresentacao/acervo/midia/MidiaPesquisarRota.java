package recordstore.apresentacao.acervo.midia;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import recordstore.aplicacao.acervo.midia.MidiaResumo;
import recordstore.aplicacao.acervo.midia.MidiaServicoAplicacao;
import jakarta.annotation.PostConstruct;

@Route("/midia/pesquisar")
public class MidiaPesquisarRota extends VerticalLayout implements AfterNavigationObserver {
	private static final long serialVersionUID = -9031401005322963865L;

	private @Autowired MidiaServicoAplicacao servico;

	private Grid<MidiaResumo> grade;

	@PostConstruct
	private void configurar() {
		var titulo = new H1("Midias");
		add(titulo);

		grade = new Grid<>(MidiaResumo.class, false);
		grade.addComponentColumn(this::link).setHeader("Codigo de Barra");
		grade.addColumn("titulo").setHeader("Título");
		add(grade);
	}

	private RouterLink link(MidiaResumo resumo) {
		var id = resumo.getId();
		return new RouterLink(id, MidiaEditarRota.class, id);
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		var midias = servico.pesquisarResumos();
		grade.setItems(midias);
	}
}