package br.com.recordstore.catalogo;
import org.springframework.stereotype.Service;
import br.com.recordstore.emprestimos.StatusEmprestimo;
import org.springframework.transaction.annotation.Transactional;
import br.com.recordstore.common.BusinessException;
import java.util.*;

@Service
public class CatalogoService {
  private final MidiaRepository midiaRepo;
  private final ExemplarRepository exRepo;
  private final br.com.recordstore.emprestimos.EmprestimoRepository empRepo;

  public CatalogoService(MidiaRepository m, ExemplarRepository e, br.com.recordstore.emprestimos.EmprestimoRepository er){ this.midiaRepo=m; this.exRepo=e; this.empRepo=er; }

  @Transactional
  public Midia catalogarMidia(Midia midia){ return midiaRepo.save(midia); }

  @Transactional
  public Exemplar catalogarExemplar(Long midiaId, CondicaoExemplar condicao){
    Midia midia = midiaRepo.findById(midiaId).orElseThrow(() -> new BusinessException("Mídia não encontrada"));
    int numero = (int)(exRepo.countByMidiaId(midiaId) + 1);
    if (exRepo.existsByMidiaIdAndNumero(midiaId, numero))
      throw new BusinessException("Exemplar duplicado para esta mídia");
    Exemplar ex = Exemplar.builder().midia(midia).numero(numero)
      .status(StatusExemplar.DISPONIVEL).condicao(condicao).build();
    return exRepo.save(ex);
  }

  @Transactional
  public Exemplar atualizarStatusExemplar(Long exemplarId, StatusExemplar status){
    Exemplar ex = exRepo.findById(exemplarId).orElseThrow(() -> new BusinessException("Exemplar não encontrado"));
    ex.setStatus(status);
    return exRepo.save(ex);
  }

  @Transactional
  public void removerExemplar(Long exemplarId){
    if (Boolean.TRUE.equals(emprestado(exemplarId)))
      throw new BusinessException("Não é possível remover exemplar emprestado");
    exRepo.deleteById(exemplarId);
  }

  public List<Midia> listarAcervo(){ return midiaRepo.findAll(); }

  private Boolean emprestado(Long exemplarId){
    return empRepo.existsByExemplarIdAndStatus(exemplarId, br.com.recordstore.emprestimos.StatusEmprestimo.ATIVO);
  }
  
  @Transactional
  public void removerMidia(Long midiaId){
      Midia midia = midiaRepo.findById(midiaId)
          .orElseThrow(() -> new BusinessException("Mídia não encontrada"));

      for (Exemplar ex : midia.getExemplares()){
          if (empRepo.existsByExemplarIdAndStatus(ex.getId(), StatusEmprestimo.ATIVO)){
              throw new BusinessException("Não é possível remover a mídia: há exemplares emprestados");
          }
      }
      midiaRepo.deleteById(midiaId);
  }

}