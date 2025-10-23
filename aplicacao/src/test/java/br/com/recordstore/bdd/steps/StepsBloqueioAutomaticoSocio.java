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
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class StepsBloqueioAutomaticoSocio {

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

    // helpers BLO
    private String uniqueEmailBLO(String base){ return base.toLowerCase().replace(" ",".")+"+"+ UUID.randomUUID()+"@teste.com"; }
    private Midia ensureMidiaBLO(String titulo){
        return midiaRepo.findByTituloIgnoreCase(titulo).orElseGet(()->{
            Midia m=new Midia(); m.setTitulo(titulo); m.setArtista("Artista"); m.setGenero("Gênero"); m.setAno(2020); m.setTipo(TipoMidia.CD);
            return midiaRepo.save(m);
        });
    }
    private Exemplar novoExemplarDisponivelBLO(Midia m){
        Exemplar ex=catalogoService.catalogarExemplar(m.getId(), CondicaoExemplar.BOM);
        catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.DISPONIVEL);
        return exRepo.findById(ex.getId()).orElseThrow();
    }
    private void quitarMultasDoSocioBLO(Socio s){
        empRepo.findBySocioId(s.getId()).forEach(e->{ if(e.getMulta()!=null){ e.getMulta().setPaga(true); e.getMulta().setValorTotal(BigDecimal.ZERO); empRepo.save(e);} });
    }

    // GIVEN
    @Dado("há um sócio ativo sem multas registradas")
    public void haUmSocioAtivoSemMultasRegistradasBLO(){
        socio=socioRepo.save(Socio.builder().nome("Socio Ativo").email(uniqueEmailBLO("socio.ativo")).status(StatusSocio.ATIVO).build());
        quitarMultasDoSocioBLO(socio);
    }

    @Dado("existe um empréstimo com devolução em atraso que gera multa pendente")
    public void existeEmprestimoComDevolucaoEmAtrasoQueGeraMultaPendenteBLO(){
        midia=ensureMidiaBLO("Vinil - Kind of Blue");
        exemplar=novoExemplarDisponivelBLO(midia);
        emprestimo=emprestimoService.realizarEmprestimo(socio.getId(), midia.getId());
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().minusDays(3));
        emprestimo.setDataDevolucaoReal(LocalDate.now());
        empRepo.save(emprestimo);
    }

    @Dado("que o cadastro do sócio {string} encontra-se {string} por multa não paga")
    public void cadastroSocioEncontraSePorMultaNaoPagaBLO(String nome,String statusTexto){
        socio=socioRepo.save(Socio.builder().nome(nome).email(uniqueEmailBLO(nome)).status(StatusSocio.ATIVO).build());
        String st=statusTexto.trim().toLowerCase();
        if(st.equals("bloqueado")) socio.setStatus(StatusSocio.BLOQUEADO);
        else if(st.equals("ativo")) socio.setStatus(StatusSocio.ATIVO);
        else if(st.equals("suspenso")) socio.setStatus(StatusSocio.SUSPENSO);
        else throw new IllegalArgumentException("Status de sócio inválido: "+statusTexto);
        socio=socioRepo.save(socio);
    }

    @Dado("existe um exemplar {string} no acervo com status {string}")
    public void existeUmExemplarNoAcervoComStatusBLO(String tituloRotulo,String statusExemplar){
        String titulo=tituloRotulo.contains(" - ")?tituloRotulo.split(" - ",2)[1]:tituloRotulo;
        midia=ensureMidiaBLO(titulo);
        exemplar=novoExemplarDisponivelBLO(midia);
        catalogoService.atualizarStatusExemplar(exemplar.getId(), StatusExemplar.DISPONIVEL);
        exemplar=exRepo.findById(exemplar.getId()).orElseThrow();
    }

    // WHEN
    @Quando("o sistema processa a multa do empréstimo para avaliação de bloqueio")
    public void oSistemaProcessaAMultaDoEmprestimoParaAvaliacaoDeBloqueioBLO(){
        mensagemErro=null;
        try{
            emprestimoService.registrarDevolucao(emprestimo.getId(),
                    Optional.ofNullable(emprestimo.getDataDevolucaoReal()).orElse(LocalDate.now()),
                    CondicaoExemplar.BOM);
            socio=socioRepo.findById(socio.getId()).orElseThrow();
        }catch (BusinessException e){ mensagemErro=e.getMessage(); }
    }

    @Quando("o administrador tenta iniciar uma locação para esse sócio")
    public void oAdministradorTentaIniciarUmaLocacaoParaEsseSocioBLO(){
        mensagemErro=null;
        try{ emprestimo=emprestimoService.realizarEmprestimo(socio.getId(), midia.getId()); }
        catch (BusinessException e){ mensagemErro=e.getMessage(); }
    }

    // THEN
    @Então("o status do sócio deve ser ajustado para {string}")
    public void oStatusDoSocioDeveSerAjustadoParaBLO(String esperado){
        socio=socioRepo.findById(socio.getId()).orElseThrow();
        String exp=esperado.trim().toLowerCase();
        if(exp.equals("bloqueado")) Assertions.assertEquals(StatusSocio.BLOQUEADO, socio.getStatus());
        else if(exp.equals("ativo")) Assertions.assertEquals(StatusSocio.ATIVO, socio.getStatus());
        else if(exp.equals("suspenso")) Assertions.assertEquals(StatusSocio.SUSPENSO, socio.getStatus());
        else Assertions.fail("Status esperado inválido: "+esperado);
    }

    @Então("novas locações e renovações devem ser impedidas")
    public void novasLocacoesERenovacoesDevemSerImpedidasBLO(){ Assertions.assertEquals(StatusSocio.BLOQUEADO, socio.getStatus()); }

    @Então("a operação deve ser negada por bloqueio do sócio")
    public void aOperacaoDeveSerNegadaPorBloqueioDoSocioBLO(){ Assertions.assertNotNull(mensagemErro); }

    @Então("deve ser exibida a mensagem {string}")
    public void deveSerExibidaAMensagemBLO(String esperado){
        Assertions.assertNotNull(mensagemErro);
        Assertions.assertTrue(mensagemErro.contains(esperado));
    }
}
