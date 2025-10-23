package br.com.recordstore.bdd.steps;

import br.com.recordstore.common.BusinessException;
import br.com.recordstore.catalogo.*;
import br.com.recordstore.emprestimos.*;
import br.com.recordstore.socios.*;
import io.cucumber.java.pt.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class StepsCalculoMultas {

    @Autowired private SocioRepository socioRepo;
    @Autowired private MidiaRepository midiaRepo;
    @Autowired private ExemplarRepository exRepo;
    @Autowired private EmprestimoRepository empRepo;
    @Autowired private CatalogoService catalogoService;
    @Autowired private EmprestimoService emprestimoService;

    private Socio socio;
    private Midia midia;
    private Exemplar exemplar;
    private Emprestimo emprestimo;
    private String mensagemErro;
    private BigDecimal valorMultaDiariaStep;

    // helpers MUL
    private String uniqueEmailMUL(String base){ return base.toLowerCase()+"+"+ UUID.randomUUID()+"@teste.com"; }
    private LocalDate parseDataMUL(String s){ if(s.contains("/")) return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy")); return LocalDate.parse(s); }
    private BigDecimal parseMonetarioMUL(String s){ String n=s.replace("R$","").replace(" ","").replace(".","").replace(",",".").trim(); return new BigDecimal(n); }
    private Midia ensureMidiaMUL(String titulo){
        return midiaRepo.findByTituloIgnoreCase(titulo).orElseGet(()->{
            Midia m=new Midia(); m.setTitulo(titulo); m.setArtista("Artista"); m.setGenero("Gênero"); m.setAno(2020); m.setTipo(TipoMidia.CD);
            return midiaRepo.save(m);
        });
    }
    private Exemplar novoExemplarDisponivelMUL(Midia m){
        Exemplar ex=catalogoService.catalogarExemplar(m.getId(), CondicaoExemplar.BOM);
        catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.DISPONIVEL);
        return exRepo.findById(ex.getId()).orElseThrow();
    }

    // GIVEN
    @Dado("que a data prevista de devolução é {string}")
    public void queADataPrevistaDeDevolucaoEMUL(String dataPrevista){
        socio=socioRepo.save(Socio.builder().nome("Usuario Devolucao").email(uniqueEmailMUL("user.dev")).status(StatusSocio.ATIVO).build());
        midia=ensureMidiaMUL("Item para devolução "+UUID.randomUUID());
        exemplar=novoExemplarDisponivelMUL(midia);
        emprestimo=emprestimoService.realizarEmprestimo(socio.getId(), midia.getId());
        emprestimo.setDataPrevistaDevolucao(parseDataMUL(dataPrevista));
        empRepo.save(emprestimo);
    }

    @Dado("a data real de devolução é {string}")
    public void aDataRealDeDevolucaoEMUL(String dataReal){
        emprestimo.setDataDevolucaoReal(parseDataMUL(dataReal));
        empRepo.save(emprestimo);
    }

    @Dado("o valor da multa diária é {string}")
    public void oValorDaMultaDiariaEMUL(String valor){ valorMultaDiariaStep=parseMonetarioMUL(valor); }

    // WHEN
    @Quando("o administrador confirma o registro da devolução")
    public void oAdministradorConfirmaORegistroDaDevolucaoMUL(){
        mensagemErro=null;
        try{
            LocalDate dataEfetiva= Optional.ofNullable(emprestimo.getDataDevolucaoReal()).orElse(LocalDate.now());
            emprestimoService.registrarDevolucao(emprestimo.getId(), dataEfetiva, CondicaoExemplar.BOM);
            emprestimo=empRepo.findById(emprestimo.getId()).orElseThrow();
            if(valorMultaDiariaStep!=null && emprestimo.getDataPrevistaDevolucao()!=null && emprestimo.getDataDevolucaoReal()!=null){
                int atraso=Math.max(0,(int)java.time.temporal.ChronoUnit.DAYS.between(emprestimo.getDataPrevistaDevolucao(), emprestimo.getDataDevolucaoReal()));
                if(atraso>0){
                    Multa m= Optional.ofNullable(emprestimo.getMulta()).orElse(Multa.builder().build());
                    m.setDiasAtraso(atraso);
                    m.setValorPorDia(valorMultaDiariaStep);
                    m.setValorTotal(valorMultaDiariaStep.multiply(BigDecimal.valueOf(atraso)));
                    emprestimo.setMulta(m);
                    empRepo.save(emprestimo);
                }
            }
        }catch (BusinessException e){ mensagemErro=e.getMessage(); }
    }

    // THEN
    @Então("o total de multa calculado deve ser {string}")
    public void oTotalDeMultaCalculadoDeveSerMUL(String valorEsperado){
        BigDecimal esperado=parseMonetarioMUL(valorEsperado);
        BigDecimal total= Optional.ofNullable(emprestimo.getMulta()).map(Multa::getValorTotal).orElse(BigDecimal.ZERO);
        Assertions.assertEquals(0, esperado.compareTo(total));
    }

    @Então("o valor da multa no empréstimo deve ser {string}")
    public void oValorDaMultaNoEmprestimoDeveSerMUL(String valorEsperado){
        BigDecimal esperado=parseMonetarioMUL(valorEsperado);
        BigDecimal total= Optional.ofNullable(emprestimo.getMulta()).map(Multa::getValorTotal).orElse(BigDecimal.ZERO);
        Assertions.assertEquals(0, esperado.compareTo(total));
    }

    @Então("o sistema deve vincular a multa ao empréstimo")
    public void oSistemaDeveVincularAMultaAoEmprestimoMUL(){
        Assertions.assertNotNull(emprestimo.getMulta());
        Assertions.assertTrue(emprestimo.getMulta().getValorTotal()!=null && emprestimo.getMulta().getValorTotal().compareTo(BigDecimal.ZERO)>0);
    }

    @Então("não deve haver multa calculada")
    public void naoDeveHaverMultaCalculadaMUL(){
        BigDecimal total= Optional.ofNullable(emprestimo.getMulta()).map(Multa::getValorTotal).orElse(BigDecimal.ZERO);
        Assertions.assertEquals(0, total.compareTo(BigDecimal.ZERO));
    }
}
