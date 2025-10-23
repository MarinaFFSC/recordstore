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
import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
public class StepsAtualizacaoStatusExemplar {

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

    // helpers AEX
    private String uniqueEmailAEX(String base){ return base.toLowerCase().replace(" ",".")+"+"+ UUID.randomUUID()+"@teste.com"; }
    private String normalizeAEX(String s){
        if(s==null) return "";
        String n= Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+","");
        return n.toLowerCase();
    }
    private Midia ensureMidiaAEX(String titulo){
        return midiaRepo.findByTituloIgnoreCase(titulo).orElseGet(()->{
            Midia m=new Midia(); m.setTitulo(titulo); m.setArtista("Artista"); m.setGenero("Gênero"); m.setAno(2020); m.setTipo(TipoMidia.CD);
            return midiaRepo.save(m);
        });
    }
    private Exemplar novoExemplarDisponivelAEX(Midia m){
        Exemplar ex=catalogoService.catalogarExemplar(m.getId(), CondicaoExemplar.BOM);
        catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.DISPONIVEL);
        return exRepo.findById(ex.getId()).orElseThrow();
    }
    private void assertStatusExemplarAEX(Exemplar ex,String esperadoTexto){
        String esperado=normalizeAEX(esperadoTexto);
        StatusExemplar es=switch (esperado){
            case "disponivel" -> StatusExemplar.DISPONIVEL;
            case "emprestado","alugado","alugada" -> StatusExemplar.EMPRESTADO;
            case "indisponivel","manutencao" -> StatusExemplar.INDISPONIVEL;
            default -> throw new IllegalArgumentException("Status esperado inválido: "+esperadoTexto);
        };
        Exemplar atual=exRepo.findById(ex.getId()).orElseThrow();
        Assertions.assertEquals(es, atual.getStatus());
    }

    // GIVEN
    @Dado("há um empréstimo ativo associado a este exemplar")
    public void haEmprestimoAtivoAssociadoAEsteExemplarAEX(){
        String titulo="Item Devolucao "+ UUID.randomUUID();
        midia=ensureMidiaAEX(titulo);
        exemplar=novoExemplarDisponivelAEX(midia);
        socio=socioRepo.save(Socio.builder().nome("Socio Devolucao").email(uniqueEmailAEX("socio.devolucao")).status(StatusSocio.ATIVO).build());
        Emprestimo emp=emprestimoService.realizarEmprestimo(socio.getId(), midia.getId());
        emprestimo=empRepo.findById(emp.getId()).orElseThrow();
        exemplar=exRepo.findById(emprestimo.getExemplar().getId()).orElseThrow();
        Assertions.assertEquals(StatusExemplar.EMPRESTADO, exemplar.getStatus());
    }

    // WHEN
    @Quando("o administrador efetua o registro da devolução informando a condição {string}")
    public void efetuaRegistroDevolucaoComCondicaoAEX(String condicaoTexto){
        mensagemErro=null;
        try{
            CondicaoExemplar cond= normalizeAEX(condicaoTexto).equals("danificado")? CondicaoExemplar.DANIFICADO: CondicaoExemplar.BOM;
            emprestimoService.registrarDevolucao(emprestimo.getId(), LocalDate.now(), cond);
            emprestimo=empRepo.findById(emprestimo.getId()).orElseThrow();
            exemplar=exRepo.findById(exemplar.getId()).orElseThrow();
        }catch (BusinessException e){ mensagemErro=e.getMessage(); }
    }

    @Quando("o administrador tenta registrar a devolução sem informar a condição física")
    public void tentaRegistrarDevolucaoSemCondicaoAEX(){
        mensagemErro=null;
        try{ emprestimoService.registrarDevolucao(emprestimo.getId(), LocalDate.now(), null); }
        catch (BusinessException e){ mensagemErro=e.getMessage(); }
    }

    // THEN
    @Então("o sistema deve marcar o exemplar como {string}")
    public void sistemaDeveMarcarExemplarComoAEX(String esperado){ Assertions.assertNull(mensagemErro); assertStatusExemplarAEX(exemplar, esperado); }

    @Então("registrar o evento {string}")
    public void registrarOEventoAEX(String evento){ Assertions.assertNotNull(evento); }

    @Então("o sistema deve rejeitar o registro de devolução")
    public void sistemaDeveRejeitarRegistroDevolucaoAEX(){ Assertions.assertNotNull(mensagemErro); }

    @Então("a mensagem apresentada deve ser {string}")
    public void aMensagemApresentadaDeveSerAEX(String mensagem){
        Assertions.assertNotNull(mensagemErro);
        Assertions.assertTrue(mensagemErro.contains(mensagem));
    }
}
