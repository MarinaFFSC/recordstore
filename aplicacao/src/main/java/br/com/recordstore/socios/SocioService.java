package br.com.recordstore.socios;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.recordstore.common.BusinessException;
import br.com.recordstore.emprestimos.EmprestimoRepository;

@Service
public class SocioService {
  private final SocioRepository repo;
  private final EmprestimoRepository empRepo;
  public SocioService(SocioRepository repo, EmprestimoRepository empRepo){
    this.repo = repo; this.empRepo = empRepo;
  }

  @Transactional
  public Socio cadastrar(String nome, String email){
    repo.findByEmail(email).ifPresent(s -> { throw new BusinessException("Já existe sócio com este e-mail"); });
    Socio socio = Socio.builder().nome(nome).email(email).status(StatusSocio.ATIVO).build();
    return repo.save(socio);
  }

  @Transactional
  public Socio alterarStatus(Long id, StatusSocio novo){
    Socio s = repo.findById(id).orElseThrow(() -> new BusinessException("Sócio não encontrado"));
    s.setStatus(novo);
    return repo.save(s);
  }

  public boolean possuiMultasNaoPagas(Long socioId){
    return empRepo.existsBySocioIdAndMultaIsNotNull(socioId);
  }
}
