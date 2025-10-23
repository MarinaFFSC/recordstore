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
public class StepsLocacaoExemplar {

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

    // helpers LEX
    private String uniqueEmailLEX(String base){ return base.toLowerCase().replace(" ",".")+"+"+ UUID.randomUUID()+"@teste.com"; }
    private String normalizeLEX(String s){
        if(s==null) return "";
        String n= Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+","");
        return n.toLowerCase();
    }
    private Midia ensureMidiaLEX(String titulo){
        return midiaRepo.findByTituloIgnoreCase(titulo).orElseGet(() -> {
            Midia m=new Midia(); m.setTitulo(titulo); m.setArtista("Artista"); m.setGenero("Gênero"); m.setAno(2020); m.setTipo(TipoMidia.CD);
            return midiaRepo.save(m);
        });
    }
    private Exemplar novoExemplarDisponivelLEX(Midia m){
        Exemplar ex=catalogoService.catalogarExemplar(m.getId(), CondicaoExemplar.BOM);
        catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.DISPONIVEL);
        return exRepo.findById(ex.getId()).orElseThrow();
    }
    private void aplicarStatusExemplarLEX(Exemplar ex,String statusTexto){
        String st=normalizeLEX(statusTexto);
        if(st.equals("disponivel")) catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.DISPONIVEL);
        else if(st.equals("emprestado")||st.equals("alugada")||st.equals("alugado")) catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.EMPRESTADO);
        else if(st.equals("indisponivel")) catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.INDISPONIVEL);
        else throw new IllegalArgumentException("Status de exemplar inválido: "+statusTexto);
    }

    // GIVENs
    @Dado("existe um exemplar {string} com status {string}")
    public void existeUmExemplarComStatusLEX(String titulo,String status){
        midia=ensureMidiaLEX(titulo.contains(" - ")?titulo.split(" - ",2)[1]:titulo);
        exemplar=novoExemplarDisponivelLEX(midia);
        aplicarStatusExemplarLEX(exemplar,status);
        exemplar=exRepo.findById(exemplar.getId()).orElseThrow();
    }

    @Dado("há um empréstimo ativo do exemplar {string}")
    public void haEmprestimoAtivoDoExemplarLEX(String tituloRotulo){
        String titulo=tituloRotulo.contains(" - ")?tituloRotulo.split(" - ",2)[1]:tituloRotulo;
        midia=ensureMidiaLEX(titulo);
        exemplar=novoExemplarDisponivelLEX(midia);
        socio=socioRepo.save(Socio.builder().nome("Socio Emprestimo").email(uniqueEmailLEX("socio.emprestimo")).status(StatusSocio.ATIVO).build());
        Emprestimo emp=emprestimoService.realizarEmprestimo(socio.getId(), midia.getId());
        emprestimo=empRepo.findById(emp.getId()).orElseThrow();
        exemplar=exRepo.findById(emprestimo.getExemplar().getId()).orElseThrow();
        Assertions.assertEquals(StatusExemplar.EMPRESTADO, exemplar.getStatus());
    }

    @Dado("existe um sócio ativo autenticado no sistema")
    public void existeSocioAtivoAutenticadoLEX(){
        socio=socioRepo.save(Socio.builder().nome("Usuario Ativo").email(uniqueEmailLEX("usuario.ativo")).status(StatusSocio.ATIVO).build());
    }

    // WHENs
    @Quando("o administrador requisita o aluguel do exemplar")
    public void administradorRequisitaAluguelExemplarLEX(){
        mensagemErro=null; emprestimo=null;
        try{
            emprestimo=emprestimoService.realizarEmprestimo(socio.getId(), midia.getId());
            emprestimo=empRepo.findById(emprestimo.getId()).orElseThrow();
            exemplar=exRepo.findById(emprestimo.getExemplar().getId()).orElseThrow();
        }catch (BusinessException e){ mensagemErro=e.getMessage(); }
    }

    @Quando("o administrador tenta efetuar a locação desse exemplar")
    public void administradorTentaEfetuarLocacaoDesseExemplarLEX(){
        mensagemErro=null; emprestimo=null;
        try{ emprestimo=emprestimoService.realizarEmprestimo(socio.getId(), midia.getId()); }
        catch (BusinessException e){ mensagemErro=e.getMessage(); }
    }

    // THENs
    @Então("o sistema deve autorizar a locação")
    public void sistemaDeveAutorizarLocacaoLEX(){
        Assertions.assertNotNull(emprestimo);
        Assertions.assertEquals(StatusEmprestimo.ATIVO, emprestimo.getStatus());
        exemplar=exRepo.findById(emprestimo.getExemplar().getId()).orElseThrow();
        Assertions.assertEquals(StatusExemplar.EMPRESTADO, exemplar.getStatus());
    }

    @Então("o empréstimo deve ser registrado como {string}")
    public void emprestimoDeveSerRegistradoComoLEX(String esperado){
        Assertions.assertNotNull(emprestimo);
        Assertions.assertEquals("ATIVO", esperado.toUpperCase());
        Assertions.assertEquals(StatusEmprestimo.ATIVO, emprestimo.getStatus());
    }

    @Então("a operação de locação deve ser recusada")
    public void operacaoDeLocacaoDeveSerRecusadaLEX(){
        Assertions.assertNotNull(mensagemErro);
        if(emprestimo!=null){ Assertions.assertNotEquals(StatusEmprestimo.ATIVO, emprestimo.getStatus()); }
    }

    @Então("nenhuma nova locação deve ser criada")
    public void nenhumaNovaLocacaoDeveSerCriadaLEX(){
        Assertions.assertTrue(emprestimo==null || emprestimo.getId()==null);
    }
}
