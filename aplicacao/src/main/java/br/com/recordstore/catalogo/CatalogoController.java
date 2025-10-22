package br.com.recordstore.catalogo;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Map;

@RestController @RequestMapping("/api/catalogo")
public class CatalogoController {
  private final CatalogoService service; private final MidiaRepository midiaRepo; private final ExemplarRepository exRepo;
  public CatalogoController(CatalogoService s, MidiaRepository m, ExemplarRepository e){ this.service=s; this.midiaRepo=m; this.exRepo=e; }

  @PostMapping("/midias")
  @ResponseStatus(HttpStatus.CREATED)
  public Midia catalogarMidia(@RequestBody Midia midia){ return service.catalogarMidia(midia); }

  @PostMapping("/midias/{id}/exemplares")
  @ResponseStatus(HttpStatus.CREATED)
  public Exemplar novoExemplar(@PathVariable Long id, @RequestParam CondicaoExemplar condicao){
    return service.catalogarExemplar(id, condicao);
  }

  @PatchMapping("/exemplares/{id}/status")
  public Exemplar atualizarStatus(@PathVariable Long id, @RequestParam StatusExemplar status){
    return service.atualizarStatusExemplar(id, status);
  }

  @DeleteMapping("/exemplares/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removerExemplar(@PathVariable Long id){ service.removerExemplar(id); }

  @GetMapping("/midias")
  public List<Midia> listar(){ return service.listarAcervo(); }
  
  @DeleteMapping("/midias/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removerMidia(@PathVariable Long id){
      service.removerMidia(id);
  }

}