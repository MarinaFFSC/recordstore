package recordstore.apresentacao.administracao.socio;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import recordstore.aplicacao.administracao.socio.SocioServicoAplicacao;
import recordstore.aplicacao.administracao.socio.SocioResumo;

@RestController
public class SocioControlador {

    private final SocioServicoAplicacao servico;

    public SocioControlador(SocioServicoAplicacao servico) {
        this.servico = servico;
    }

    @GetMapping("/socios")
    public List<SocioResumo> listar() {
        return servico.pesquisarResumos();
    }
    
    @PostMapping("/socios")
    public void criar(@RequestBody SocioFormulario form) {
        servico.criar(form.id, form.nome, form.email);
    }

}
