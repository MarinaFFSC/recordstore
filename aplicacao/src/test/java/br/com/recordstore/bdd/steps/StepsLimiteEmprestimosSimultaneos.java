package br.com.recordstore.bdd.steps;

import br.com.recordstore.common.BusinessException;
import br.com.recordstore.catalogo.*;
import br.com.recordstore.emprestimos.*;
import br.com.recordstore.socios.*;
import io.cucumber.java.pt.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.Normalizer;
import java.util.UUID;

@SpringBootTest
public class StepsLimiteEmprestimosSimultaneos {

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

    private int limiteMaximo;

    // helpers LIM
    private String uniqueEmailLIM(String base){ return base.toLowerCase().replace(" ",".")+"+"+ UUID.randomUUID()+"@teste.com"; }
    private String normalizeLIM(String s){
        if(s==null) return "";
        String n= Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+","");
        return n.toLowerCase();
    }
    private Midia ensureMidiaLIM(String titulo){
        return midiaRepo.findByTituloIgnoreCase(titulo).orElseGet(()->{
            Midia m=new Midia(); m.setTitulo(titulo); m.setArtista("Artista"); m.setGenero("Gênero"); m.setAno(2020); m.setTipo(TipoMidia.CD);
            return midiaRepo.save(m);
        });
    }
    private Exemplar novoExemplarDisponivelLIM(Midia m){
        Exemplar ex=catalogoService.catalogarExemplar(m.getId(), CondicaoExemplar.BOM);
        catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.DISPONIVEL);
        return exRepo.findById(ex.getId()).orElseThrow();
    }
    private void aplicarStatusExemplarLIM(Exemplar ex,String status){
        String st=normalizeLIM(status);
        if(st.equals("disponivel")) catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.DISPONIVEL);
        else if(st.equals("emprestado")||st.equals("alugado")||st.equals("alugada")) catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.EMPRESTADO);
        else if(st.equals("indisponivel")) catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.INDISPONIVEL);
        else throw new IllegalArgumentException("Status de exemplar inválido: "+status);
    }
    private Emprestimo criarEmprestimoAtivoLIM(Socio s,String titulo){
        Midia m=ensureMidiaLIM(titulo);
        Exemplar ex=novoExemplarDisponivelLIM(m);
        Emprestimo e=emprestimoService.realizarEmprestimo(s.getId(), m.getId());
        return empRepo.findById(e.getId()).orElseThrow();
    }

    // GIVEN
    @Dado("que o limite máximo de empréstimos por sócio é {string}")
    public void queOLimiteMaximoDeEmprestimosPorSocioELIM(String limite){ this.limiteMaximo=Integer.parseInt(limite); }

    @Dado("o sócio {string} possui {int} empréstimos em andamento")
    public void oSocioPossuiNEmprestimosEmAndamentoLIM(String nome,Integer qtd){
        socio=socioRepo.save(Socio.builder().nome(nome).email(uniqueEmailLIM(nome)).status(StatusSocio.ATIVO).build());
        for(int i=1;i<=qtd;i++){ criarEmprestimoAtivoLIM(socio,"Titulo Limite "+i+" "+UUID.randomUUID()); }
        Assertions.assertTrue(empRepo.findBySocioId(socio.getId()).size()>=qtd);
    }

    @Dado("existe no catálogo um exemplar {string} com status {string}")
    public void existeNoCatalogoUmExemplarComStatusLIM(String tituloRotulo,String statusTexto){
        String titulo=tituloRotulo.contains(" - ")?tituloRotulo.split(" - ",2)[1]:tituloRotulo;
        midia=ensureMidiaLIM(titulo);
        exemplar=novoExemplarDisponivelLIM(midia);
        aplicarStatusExemplarLIM(exemplar,statusTexto);
        exemplar=exRepo.findById(exemplar.getId()).orElseThrow();
    }

    // WHEN
    @Quando("o administrador submete o pedido de locação respeitando o limite")
    public void oAdministradorSubmeteOPedidoDeLocacaoRespeitandoOLimiteLIM(){
        mensagemErro=null; emprestimo=null;
        try{ emprestimo=emprestimoService.realizarEmprestimo(socio.getId(), midia.getId()); emprestimo=empRepo.findById(emprestimo.getId()).orElseThrow(); }
        catch (BusinessException e){ mensagemErro=e.getMessage(); }
    }

    // THEN
    @Então("a locação deve ser autorizada por estar abaixo do limite")
    public void aLocacaoDeveSerAutorizadaPorEstarAbaixoDoLimiteLIM(){
        Assertions.assertNull(mensagemErro);
        Assertions.assertNotNull(emprestimo);
        Assertions.assertEquals(StatusEmprestimo.ATIVO, emprestimo.getStatus());
    }

    @Então("a locação deve ser recusada por limite atingido")
    public void aLocacaoDeveSerRecusadaPorLimiteAtingidoLIM(){
        Assertions.assertNotNull(mensagemErro);
        if(emprestimo!=null){ Assertions.assertNotEquals(StatusEmprestimo.ATIVO, emprestimo.getStatus()); }
    }

    @Então("a mensagem de limite retornada deve ser {string}")
    public void aMensagemDeLimiteRetornadaDeveSerLIM(String esperado){
        Assertions.assertNotNull(mensagemErro);
        Assertions.assertTrue(mensagemErro.contains(esperado));
    }
}
