package br.com.recordstore.socios;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import jakarta.validation.constraints.*;
import java.util.Map;

@RestController @RequestMapping("/api/socios")
public class SocioController {
  private final SocioService service; private final SocioRepository repo;
  public SocioController(SocioService s, SocioRepository r){ this.service=s; this.repo=r; }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Socio criar(@RequestBody Map<String,String> body){
    return service.cadastrar(body.get("nome"), body.get("email"));
  }

  @PatchMapping("/{id}/status")
  public Socio atualizarStatus(@PathVariable Long id, @RequestParam StatusSocio status){
    return service.alterarStatus(id, status);
  }

  @GetMapping("/{id}")
  public Socio obter(@PathVariable Long id){ return repo.findById(id).orElseThrow(); }
}