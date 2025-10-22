package br.com.recordstore.emprestimos;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.util.*;
import br.com.recordstore.catalogo.CondicaoExemplar;

@RestController @RequestMapping("/api/emprestimos")
public class EmprestimoController {
  private final EmprestimoService service;
  public EmprestimoController(EmprestimoService s){ this.service = s; }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Emprestimo realizar(@RequestParam Long socioId, @RequestParam Long midiaId){
    return service.realizarEmprestimo(socioId, midiaId);
  }

  @PostMapping("/{id}/renovar")
  public Emprestimo renovar(@PathVariable Long id, @RequestParam String novaData){
    return service.renovarEmprestimo(id, LocalDate.parse(novaData));
  }


	@PostMapping("/{id}/devolucao")
	public Emprestimo devolver(@PathVariable Long id,
	                           @RequestParam String data,
	                           @RequestParam CondicaoExemplar condicao) {
	    return service.registrarDevolucao(id, LocalDate.parse(data), condicao);
	}

  @GetMapping("/socio/{socioId}/ativos-atrasados")
  public List<Emprestimo> ativosEAtrasados(@PathVariable Long socioId){
    return service.listarAtivosEAtrasadosPorSocio(socioId);
  }

  @GetMapping("/socio/{socioId}/historico")
  public List<Emprestimo> historico(@PathVariable Long socioId){
    return service.historicoPorSocio(socioId);
  }
  
  @GetMapping("/exemplar/{exemplarId}/historico")
  public List<Emprestimo> historicoPorExemplar(@PathVariable Long exemplarId){
      return service.historicoPorExemplar(exemplarId);
  }

  @GetMapping("/midia/{midiaId}/historico")
  public List<Emprestimo> historicoPorMidia(@PathVariable Long midiaId){
      return service.historicoPorMidia(midiaId);
  }

}