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
public class StepsValidacaoSocioLocacao {

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

    // helpers VSL
    private String uniqueEmailVSL(String base){ return base.toLowerCase().replace(" ",".")+"+"+ UUID.randomUUID()+"@teste.com"; }
    private String normalizeVSL(String s){
        if(s==null) return "";
        String n= Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+","");
        return n.toLowerCase();
    }
    private Midia ensureMidiaVSL(String titulo){
        return midiaRepo.findByTituloIgnoreCase(titulo).orElseGet(()->{
            Midia m=new Midia(); m.setTitulo(titulo); m.setArtista("Artista"); m.setGenero("Gênero"); m.setAno(2020); m.setTipo(TipoMidia.CD);
            return midiaRepo.save(m);
        });
    }
    private Exemplar novoExemplarDisponivelVSL(Midia m){
        Exemplar ex=catalogoService.catalogarExemplar(m.getId(), CondicaoExemplar.BOM);
        catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.DISPONIVEL);
        return exRepo.findById(ex.getId()).orElseThrow();
    }
    private void aplicarStatusExemplarVSL(Exemplar ex,String status){
        String st=normalizeVSL(status);
        if(st.equals("disponivel")) catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.DISPONIVEL);
        else if(st.equals("emprestado")||st.equals("alugado")||st.equals("alugada")) catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.EMPRESTADO);
        else if(st.equals("indisponivel")) catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.INDISPONIVEL);
        else throw new IllegalArgumentException("Status de exemplar inválido: "+status);
    }
    private void aplicarStatusSocioVSL(Socio s,String statusTexto){
        String st=normalizeVSL(statusTexto);
        if(st.equals("ativo")) s.setStatus(StatusSocio.ATIVO);
        else if(st.equals("bloqueado")) s.setStatus(StatusSocio.BLOQUEADO);
        else if(st.equals("suspenso")) s.setStatus(StatusSocio.SUSPENSO);
        else throw new IllegalArgumentException("Status de sócio inválido: "+statusTexto);
        socio=socioRepo.save(s);
    }

    // GIVEN
    @Dado("que o cadastro do sócio {string} está {string} e sem multas registradas")
    public void cadastroSocioAtivoSemMultasVSL(String nome,String status){
        socio=socioRepo.save(Socio.builder().nome(nome).email(uniqueEmailVSL(nome)).status(StatusSocio.ATIVO).build());
        aplicarStatusSocioVSL(socio,status);
        empRepo.findBySocioId(socio.getId()).forEach(e->{ if(e.getMulta()!=null){ e.getMulta().setPaga(true); e.getMulta().setValorTotal(java.math.BigDecimal.ZERO); empRepo.save(e);} });
    }

    @Dado("que o cadastro do sócio {string} está {string} por multa não paga")
    public void cadastroSocioBloqueadoPorMultaVSL(String nome,String status){
        socio=socioRepo.save(Socio.builder().nome(nome).email(uniqueEmailVSL(nome)).status(StatusSocio.ATIVO).build());
        aplicarStatusSocioVSL(socio,status);
    }

    @Dado("há no acervo um exemplar {string} marcado como {string}")
    public void haNoAcervoUmExemplarMarcadoComoVSL(String tituloRotulo,String statusExemplar){
        String titulo=tituloRotulo.contains(" - ")?tituloRotulo.split(" - ",2)[1]:tituloRotulo;
        midia=ensureMidiaVSL(titulo);
        exemplar=novoExemplarDisponivelVSL(midia);
        aplicarStatusExemplarVSL(exemplar,statusExemplar);
        exemplar=exRepo.findById(exemplar.getId()).orElseThrow();
    }

    // WHEN
    @Quando("o administrador envia a solicitação de locação")
    public void oAdministradorEnviaASolicitacaoDeLocacaoVSL(){
        mensagemErro=null; emprestimo=null;
        try{ emprestimo=emprestimoService.realizarEmprestimo(socio.getId(), midia.getId()); emprestimo=empRepo.findById(emprestimo.getId()).orElseThrow(); }
        catch (BusinessException e){ mensagemErro=e.getMessage(); }
    }

    // THEN
    @Então("a solicitação de locação deve ser aprovada")
    public void aSolicitacaoDeLocacaoDeveSerAprovadaVSL(){
        Assertions.assertNull(mensagemErro);
        Assertions.assertNotNull(emprestimo);
        Assertions.assertEquals(StatusEmprestimo.ATIVO, emprestimo.getStatus());
    }

    @Então("a solicitação de locação deve ser impedida")
    public void aSolicitacaoDeLocacaoDeveSerImpedidaVSL(){
        Assertions.assertNotNull(mensagemErro);
        if(emprestimo!=null){ Assertions.assertNotEquals(StatusEmprestimo.ATIVO, emprestimo.getStatus()); }
    }

    @Então("a mensagem apresentada ao administrador deve ser {string}")
    public void aMensagemApresentadaAoAdministradorDeveSerVSL(String msg){
        Assertions.assertNotNull(mensagemErro);
        Assertions.assertTrue(mensagemErro.contains(msg));
    }
}
