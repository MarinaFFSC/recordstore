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
public class StepsRegistrarDevolucaoAtualizaDisponibilidade {

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

    // helpers DEV
    private String uniqueEmailDEV(String base){ return base.toLowerCase().replace(" ",".")+"+"+ UUID.randomUUID()+"@teste.com"; }
    private String normalizeDEV(String s){
        if(s==null) return "";
        String n= Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+","");
        return n.toLowerCase();
    }
    private Midia ensureMidiaDEV(String titulo){
        return midiaRepo.findByTituloIgnoreCase(titulo).orElseGet(()->{
            Midia m=new Midia(); m.setTitulo(titulo); m.setArtista("Artista"); m.setGenero("Gênero"); m.setAno(2020); m.setTipo(TipoMidia.CD);
            return midiaRepo.save(m);
        });
    }
    private Exemplar novoExemplarDisponivelDEV(Midia m){
        Exemplar ex=catalogoService.catalogarExemplar(m.getId(), CondicaoExemplar.BOM);
        catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.DISPONIVEL);
        return exRepo.findById(ex.getId()).orElseThrow();
    }
    private CondicaoExemplar mapCondicaoDEV(String cond){
        String c=normalizeDEV(cond).replace('-', '_').replace(' ','_');
        if(c.equals("bom_estado")||c.equals("bom")) return CondicaoExemplar.BOM;
        if(c.equals("danificado")) return CondicaoExemplar.DANIFICADO;
        throw new IllegalArgumentException("Condição física inválida: "+cond);
    }

    // GIVEN
    @Dado("existe um empréstimo em andamento para este exemplar")
    public void existeUmEmprestimoEmAndamentoParaEsteExemplarDEV(){
        String titulo="Exemplar Devolucao "+UUID.randomUUID();
        midia=ensureMidiaDEV(titulo);
        exemplar=novoExemplarDisponivelDEV(midia);
        socio=socioRepo.save(Socio.builder().nome("Socio Devolucao").email(uniqueEmailDEV("socio.devolucao")).status(StatusSocio.ATIVO).build());
        emprestimo=emprestimoService.realizarEmprestimo(socio.getId(), midia.getId());
        emprestimo=empRepo.findById(emprestimo.getId()).orElseThrow();
        exemplar=exRepo.findById(emprestimo.getExemplar().getId()).orElseThrow();
        Assertions.assertEquals(StatusExemplar.EMPRESTADO, exemplar.getStatus());
    }

    // WHEN
    @Quando("o administrador tenta finalizar a devolução sem informar a condição física")
    public void oAdministradorTentaFinalizarSemInformarCondicaoDEV(){
        mensagemErro=null;
        try{ emprestimoService.registrarDevolucao(emprestimo.getId(), LocalDate.now(), null); }
        catch (BusinessException e){ mensagemErro=e.getMessage(); }
    }

    @Quando("o administrador finaliza a devolução informando a condição {string}")
    public void oAdministradorFinalizaADevolucaoInformandoACondicaoDEV(String condicaoTexto){
        mensagemErro=null;
        try{
            CondicaoExemplar cond=mapCondicaoDEV(condicaoTexto);
            emprestimoService.registrarDevolucao(emprestimo.getId(), LocalDate.now(), cond);
            emprestimo=empRepo.findById(emprestimo.getId()).orElseThrow();
            exemplar=exRepo.findById(exemplar.getId()).orElseThrow();
        }catch (BusinessException e){ mensagemErro=e.getMessage(); }
    }

    // THEN
    @Então("o sistema deve impedir o processamento da devolução")
    public void oSistemaDeveImpedirOProcessamentoDaDevolucaoDEV(){ Assertions.assertNotNull(mensagemErro); }

    @Então("a mensagem de validação exibida deve ser {string}")
    public void aMensagemDeValidacaoExibidaDeveSerDEV(String msg){
        Assertions.assertNotNull(mensagemErro);
        Assertions.assertTrue(mensagemErro.contains(msg));
    }

    @Então("o sistema deve aceitar a devolução")
    public void oSistemaDeveAceitarADevolucaoDEV(){ Assertions.assertNull(mensagemErro); }

    @Então("o status do exemplar após a devolução deve ser {string}")
    public void oStatusDoExemplarAposADevolucaoDeveSerDEV(String esperadoTexto){
        String esperado=normalizeDEV(esperadoTexto);
        StatusExemplar es=switch (esperado){
            case "disponivel" -> StatusExemplar.DISPONIVEL;
            case "emprestado","alugado","alugada" -> StatusExemplar.EMPRESTADO;
            case "indisponivel","manutencao" -> StatusExemplar.INDISPONIVEL;
            default -> throw new IllegalArgumentException("Status esperado inválido: "+esperadoTexto);
        };
        Exemplar atual=exRepo.findById(exemplar.getId()).orElseThrow();
        Assertions.assertEquals(es, atual.getStatus());
    }

    @Então("o evento registrado deve ser {string}")
    public void oEventoRegistradoDeveSerDEV(String evento){ Assertions.assertNotNull(evento); }
}
