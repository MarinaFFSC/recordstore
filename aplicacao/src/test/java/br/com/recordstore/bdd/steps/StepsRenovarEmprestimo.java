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
public class StepsRenovarEmprestimo {

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
    private LocalDate dataPrevistaAntes;

    // helpers REN
    private String uniqueEmailREN(String base){ return base.toLowerCase().replace(" ",".")+"+"+ UUID.randomUUID()+"@teste.com"; }
    private Midia ensureMidiaREN(String titulo){
        return midiaRepo.findByTituloIgnoreCase(titulo).orElseGet(()->{
            Midia m=new Midia(); m.setTitulo(titulo); m.setArtista("Artista"); m.setGenero("Gênero"); m.setAno(2020); m.setTipo(TipoMidia.CD);
            return midiaRepo.save(m);
        });
    }
    private Exemplar novoExemplarDisponivelREN(Midia m){
        Exemplar ex=catalogoService.catalogarExemplar(m.getId(), CondicaoExemplar.BOM);
        catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.DISPONIVEL);
        return exRepo.findById(ex.getId()).orElseThrow();
    }
    private void quitarMultasDoSocioREN(Socio s){
        empRepo.findBySocioId(s.getId()).forEach(e->{ if(e.getMulta()!=null){ e.getMulta().setPaga(true); e.getMulta().setValorTotal(BigDecimal.ZERO); empRepo.save(e);} });
    }
    private void injetarMultaPendenteREN(Emprestimo e){
        Multa m= Optional.ofNullable(e.getMulta()).orElse(Multa.builder().build());
        m.setDiasAtraso(1); m.setValorPorDia(new BigDecimal("2.00")); m.setValorTotal(new BigDecimal("2.00")); m.setPaga(false);
        e.setMulta(m); empRepo.save(e);
    }

    // GIVEN
    @Dado("que o empréstimo do exemplar {string} encontra-se dentro do prazo")
    public void emprestimoDentroDoPrazoREN(String tituloRotulo){
        String titulo=tituloRotulo.contains(" - ")?tituloRotulo.split(" - ",2)[1]:tituloRotulo;
        socio=socioRepo.save(Socio.builder().nome("Joao").email(uniqueEmailREN("joao.sem.multa")).status(StatusSocio.ATIVO).build());
        midia=ensureMidiaREN(titulo);
        exemplar=novoExemplarDisponivelREN(midia);
        emprestimo=emprestimoService.realizarEmprestimo(socio.getId(), midia.getId());
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().plusDays(3));
        empRepo.save(emprestimo);
    }

    @Dado("o sócio {string} não possui multas pendentes")
    public void socioNaoPossuiMultasPendentesREN(String nome){
        if(socio==null){
            socio=socioRepo.save(Socio.builder().nome(nome).email(uniqueEmailREN(nome)).status(StatusSocio.ATIVO).build());
        }
        quitarMultasDoSocioREN(socio);
    }

    @Dado("que o empréstimo do exemplar {string} está em atraso")
    public void emprestimoEmAtrasoREN(String tituloRotulo){
        String titulo=tituloRotulo.contains(" - ")?tituloRotulo.split(" - ",2)[1]:tituloRotulo;
        socio=socioRepo.save(Socio.builder().nome("Joao Multa").email(uniqueEmailREN("joao.com.multa")).status(StatusSocio.ATIVO).build());
        midia=ensureMidiaREN(titulo);
        exemplar=novoExemplarDisponivelREN(midia);
        emprestimo=emprestimoService.realizarEmprestimo(socio.getId(), midia.getId());
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().minusDays(1));
        empRepo.save(emprestimo);
    }

    @Dado("o sócio {string} possui multa pendente")
    public void socioPossuiMultaPendenteREN(String nome){
        if(socio==null){
            socio=socioRepo.save(Socio.builder().nome(nome).email(uniqueEmailREN(nome)).status(StatusSocio.ATIVO).build());
        }
        injetarMultaPendenteREN(emprestimo);
    }

    // WHEN
    @Quando("o administrador solicita a prorrogação do empréstimo")
    public void administradorSolicitaProrrogacaoDoEmprestimoREN(){
        mensagemErro=null;
        dataPrevistaAntes=emprestimo.getDataPrevistaDevolucao();
        try{
            LocalDate novaData=LocalDate.now().plusDays(7);
            emprestimoService.renovarEmprestimo(emprestimo.getId(), novaData);
            emprestimo=empRepo.findById(emprestimo.getId()).orElseThrow();
        }catch (BusinessException e){ mensagemErro=e.getMessage(); }
    }

    // THEN
    @Então("o sistema deve conceder a renovação")
    public void sistemaDeveConcederARenovacaoREN(){
        Assertions.assertNull(mensagemErro);
        Assertions.assertTrue(empRepo.findById(emprestimo.getId()).orElseThrow().getDataPrevistaDevolucao().isAfter(dataPrevistaAntes));
    }

    @Então("a nova data prevista de devolução deve ser posterior à atual")
    public void aNovaDataPrevistaDeveSerPosteriorAAAtualREN(){
        LocalDate depois=empRepo.findById(emprestimo.getId()).orElseThrow().getDataPrevistaDevolucao();
        Assertions.assertTrue(depois.isAfter(dataPrevistaAntes));
    }

    @Então("a renovação deve ser recusada")
    public void aRenovacaoDeveSerRecusadaREN(){ Assertions.assertNotNull(mensagemErro); }

    @Então("a mensagem de impedimento exibida deve ser {string}")
    public void aMensagemDeImpedimentoExibidaDeveSerREN(String msg){
        Assertions.assertNotNull(mensagemErro);
        Assertions.assertTrue(mensagemErro.contains(msg));
    }
}
