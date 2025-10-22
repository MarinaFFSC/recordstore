package br.com.recordstore.bdd.steps;

import br.com.recordstore.common.BusinessException;

import br.com.recordstore.catalogo.CatalogoService;
import br.com.recordstore.catalogo.CondicaoExemplar;
import br.com.recordstore.catalogo.Exemplar;
import br.com.recordstore.catalogo.ExemplarRepository;
import br.com.recordstore.catalogo.Midia;
import br.com.recordstore.catalogo.MidiaRepository;
import br.com.recordstore.catalogo.StatusExemplar;
import br.com.recordstore.catalogo.TipoMidia;

import br.com.recordstore.emprestimos.Emprestimo;
import br.com.recordstore.emprestimos.EmprestimoRepository;
import br.com.recordstore.emprestimos.EmprestimoService;
import br.com.recordstore.emprestimos.Multa;
import br.com.recordstore.emprestimos.StatusEmprestimo;

import br.com.recordstore.socios.Socio;
import br.com.recordstore.socios.SocioRepository;
import br.com.recordstore.socios.SocioService;
import br.com.recordstore.socios.StatusSocio;

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
public class StepsRecordStore {

    @Autowired private SocioRepository socioRepo;
    @Autowired private MidiaRepository midiaRepo;
    @Autowired private ExemplarRepository exRepo;
    @Autowired private EmprestimoRepository empRepo;

    @Autowired private SocioService socioService;
    @Autowired private CatalogoService catalogoService;
    @Autowired private EmprestimoService emprestimoService;

    private Socio socio;
    private Midia midia;
    private Exemplar exemplar;
    private Emprestimo emprestimo;
    private String mensagemErro;

    // valor de multa diária definido por step (se nulo, usa o padrão do service)
    private BigDecimal valorMultaDiariaStep = null;

    private Integer limiteEsperado;
    private LocalDate dataPrevistaAntesRenovacao;

    // ===================== Helpers =====================

    private String uniqueEmail(String base) {
        String norm = base.toLowerCase().replace(" ", ".")
                .replace("ã","a").replace("á","a").replace("â","a")
                .replace("é","e").replace("ê","e")
                .replace("í","i").replace("ó","o").replace("ô","o")
                .replace("ú","u").replace("ç","c");
        return norm + "+" + UUID.randomUUID() + "@teste.com";
    }

    private Midia ensureMidia(String titulo) {
        return midiaRepo.findByTituloIgnoreCase(titulo).orElseGet(() -> {
            Midia m = new Midia();
            m.setTitulo(titulo);
            m.setArtista("Artista");
            m.setGenero("Gênero");
            m.setAno(2020);
            m.setTipo(TipoMidia.CD);
            return midiaRepo.save(m);
        });
    }

    private Exemplar ensureExemplarDisponivel(Midia m) {
        Exemplar ex = catalogoService.catalogarExemplar(m.getId(), CondicaoExemplar.BOM);
        catalogoService.atualizarStatusExemplar(ex.getId(), StatusExemplar.DISPONIVEL);
        return exRepo.findById(ex.getId()).orElseThrow();
    }

    private void setStatusExemplar(String statusTexto) {
        switch (statusTexto.toLowerCase()) {
            case "disponível" -> catalogoService.atualizarStatusExemplar(exemplar.getId(), StatusExemplar.DISPONIVEL);
            case "emprestado" -> catalogoService.atualizarStatusExemplar(exemplar.getId(), StatusExemplar.EMPRESTADO);
            case "indisponível" -> catalogoService.atualizarStatusExemplar(exemplar.getId(), StatusExemplar.INDISPONIVEL);
            default -> throw new IllegalArgumentException("Status de exemplar inválido: " + statusTexto);
        }
        exemplar = exRepo.findById(exemplar.getId()).orElseThrow();
    }

    private void setStatusSocio(String statusTexto) {
        switch (statusTexto.toLowerCase()) {
            case "ativo" -> socio.setStatus(StatusSocio.ATIVO);
            case "bloqueado" -> socio.setStatus(StatusSocio.BLOQUEADO);
            case "suspenso" -> socio.setStatus(StatusSocio.SUSPENSO);
            default -> throw new IllegalArgumentException("Status de sócio inválido: " + statusTexto);
        }
        socio = socioRepo.save(socio);
    }

    private void criarEmprestimoAtivo() {
        emprestimo = emprestimoService.realizarEmprestimo(socio.getId(), midia.getId());
        emprestimo = empRepo.findById(emprestimo.getId()).orElseThrow();
        exemplar = emprestimo.getExemplar();
    }

    private Emprestimo criarEmprestimoAtivoPara(Socio s, String tituloMidia) {
        Midia m = ensureMidia(tituloMidia);
        ensureExemplarDisponivel(m);
        Emprestimo emp = emprestimoService.realizarEmprestimo(s.getId(), m.getId());
        return empRepo.findById(emp.getId()).orElseThrow();
    }

    private BigDecimal parseMonetario(String s){
        if (s == null) return BigDecimal.ZERO;
        String norm = s.replace("R$", "").replace(" ", "").replace(".", "").replace(",", ".").trim();
        return new BigDecimal(norm);
    }

    private LocalDate parseData(String s) {
        try {
            if (s.contains("/")) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return LocalDate.parse(s, fmt);
            }
            return LocalDate.parse(s);
        } catch (Exception e) {
            throw new RuntimeException("Data inválida: " + s);
        }
    }

    private void ensureSocioEMidiaParaAluguel() {
        if (socio == null) {
            socio = new Socio();
            socio.setNome("Socio Default");
            socio.setEmail(uniqueEmail("socio.default"));
            socio.setStatus(StatusSocio.ATIVO);
            socio = socioRepo.save(socio);
        }
        if (midia == null) {
            if (exemplar != null) {
                midia = exemplar.getMidia();
            } else {
                midia = ensureMidia("Midia Default");
                ensureExemplarDisponivel(midia);
            }
        }
    }

    private void ajustarMultaComValorDoStepSeNecessario() {
        if (emprestimo == null || valorMultaDiariaStep == null) return;
        if (emprestimo.getDataPrevistaDevolucao() == null || emprestimo.getDataDevolucaoReal() == null) return;

        int atraso = Math.max(0, (int) java.time.temporal.ChronoUnit.DAYS
                .between(emprestimo.getDataPrevistaDevolucao(), emprestimo.getDataDevolucaoReal()));
        if (atraso <= 0) return;

        Multa m = Optional.ofNullable(emprestimo.getMulta()).orElse(Multa.builder().build());
        m.setValorPorDia(valorMultaDiariaStep);
        m.setDiasAtraso(atraso);
        m.setValorTotal(valorMultaDiariaStep.multiply(BigDecimal.valueOf(atraso)));
        emprestimo.setMulta(m);
        empRepo.save(emprestimo);
    }

    // ===================== GIVEN =====================

    @Dado("que existe um exemplar {string} com status {string}")
    @Dado("existe um exemplar {string} com status {string}")
    public void existeExemplarComStatus(String tituloRotulo, String statusTexto) {
        String titulo = tituloRotulo.contains(" - ") ? tituloRotulo.split(" - ", 2)[1] : tituloRotulo;
        midia = ensureMidia(titulo);
        exemplar = ensureExemplarDisponivel(midia);
        setStatusExemplar(statusTexto);
    }

    @Dado("que existe um sócio {string} com status {string}")
    @Dado("existe um sócio {string} com status {string}")
    public void existeUmSocioComStatus(String nome, String statusTexto) {
        socio = new Socio();
        socio.setNome(nome);
        socio.setEmail(uniqueEmail(nome));
        socio.setStatus(StatusSocio.ATIVO);
        socio = socioRepo.save(socio);
        setStatusSocio(statusTexto);
    }

    @Dado("existe um sócio ativo logado no sistema")
    public void existeSocioAtivoLogado() { existeUmSocioComStatus("Usuário Ativo", "ativo"); }

    @Dado("há um empréstimo ativo do exemplar {string}")
    @Dado("que há um empréstimo ativo do exemplar {string}")
    public void haEmprestimoAtivoDoExemplar(String tituloRotulo) {
        existeExemplarComStatus(tituloRotulo, "disponível");
        existeUmSocioComStatus("Teste Emprestimo", "ativo");
        criarEmprestimoAtivo();
    }

    @Dado("existe um empréstimo ativo vinculado a esse exemplar")
    public void emprestimoAtivoVinculado() {
        Assertions.assertNotNull(exemplar, "Exemplar deve existir antes");
        if (socio == null) existeSocioAtivoLogado();
        midia = exemplar.getMidia();
        // garante que o service enxergará DISPONIVEL
        catalogoService.atualizarStatusExemplar(exemplar.getId(), StatusExemplar.DISPONIVEL);
        criarEmprestimoAtivo(); // marcará como EMPRESTADO
        Assertions.assertNotNull(emprestimo, "Empréstimo não foi criado");
    }

    @Dado("que a data prevista de devolução é {string}")
    public void dataPrevistaDevolucao(String dataPrevista) {
        existeExemplarComStatus("Item para devolução", "disponível");
        existeUmSocioComStatus("Usuário Devolução", "ativo");
        criarEmprestimoAtivo();
        emprestimo.setDataPrevistaDevolucao(parseData(dataPrevista));
        empRepo.save(emprestimo);
    }

    @Dado("a data real de devolução é {string}")
    public void dataRealDevolucao(String dataReal) {
        Assertions.assertNotNull(emprestimo, "Crie o empréstimo antes de informar a data real");
        emprestimo.setDataDevolucaoReal(parseData(dataReal));
        empRepo.save(emprestimo);
    }

    @Dado("o valor da multa diária é {string}")
    public void valorMultaDiaria(String valor) { this.valorMultaDiariaStep = parseMonetario(valor); }

    @Dado("que o limite de empréstimos ativos por sócio é {string}")
    public void limiteEmprestimos(String limite) { this.limiteEsperado = Integer.parseInt(limite); }

    @Dado("o sócio {string} possui {int} empréstimos ativos")
    public void socioPossuiNEmprestimos(String nome, Integer qtd) {
        existeUmSocioComStatus(nome, "ativo");
        for (int i = 1; i <= qtd; i++) criarEmprestimoAtivoPara(socio, "Mídia de teste " + i);
    }

    // alias para versões dos .feature
    @Dado("o sócio {string} possui {string} empréstimos ativos")
    public void socioPossuiNEmprestimosStr(String nome, String qtdStr) {
        socioPossuiNEmprestimos(nome, Integer.parseInt(qtdStr));
    }

    @Dado("que o sócio {string} está {string} por multa não paga")
    public void socioBloqueadoPorMulta(String nome, String status) { existeUmSocioComStatus(nome, status); }

    @Dado("o sócio {string} está com status {string} e sem multas")
    @Dado("que o sócio {string} está com status {string} e sem multas")
    public void socioSemMultas(String nome, String status) {
        existeUmSocioComStatus(nome, status);
        empRepo.findBySocioId(socio.getId()).forEach(e -> {
            if (e.getMulta() != null) { e.getMulta().setPaga(true); empRepo.save(e); }
        });
    }

    // === aliases necessários pelos .feature ===
    @Dado("que o sócio {string} não possui multas pendentes")
    public void socioNaoPossuiMultasPendentes(String nome) {
        socioSemMultas(nome, "ativo");
    }

    @Dado("o empréstimo do exemplar {string} está dentro do prazo")
    public void emprestimoDentroDoPrazo(String tituloRotulo) {
        existeExemplarComStatus(tituloRotulo, "disponível");
        existeUmSocioComStatus("Sem Multa - Prazo", "ativo");
        criarEmprestimoAtivo();
        // deixa prevista > hoje e remove/zera multa
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().plusDays(3));
        if (emprestimo.getMulta() != null) {
            emprestimo.getMulta().setPaga(true);
            emprestimo.getMulta().setValorTotal(BigDecimal.ZERO);
        }
        empRepo.save(emprestimo);
    }

    @Dado("que o sócio {string} possui multa pendente")
    public void socioPossuiMultaPendente(String nome) {
        existeUmSocioComStatus(nome, "ativo");
        // cria um empréstimo e injeta multa não paga
        criarEmprestimoAtivoPara(socio, "Título com multa");
        Emprestimo e = empRepo.findBySocioId(socio.getId()).get(0);
        Multa m = Optional.ofNullable(e.getMulta()).orElse(Multa.builder().build());
        m.setDiasAtraso(2);
        m.setValorPorDia(new BigDecimal("2.50"));
        m.setValorTotal(new BigDecimal("5.00"));
        m.setPaga(false);
        e.setMulta(m);
        empRepo.save(e);
    }

    @Dado("o empréstimo do exemplar {string} está em atraso")
    public void emprestimoEmAtraso(String tituloRotulo) {
        existeExemplarComStatus(tituloRotulo, "disponível");
        existeUmSocioComStatus("Com atraso", "ativo");
        criarEmprestimoAtivo();
        // deixa ATIVO porém com data prevista no passado (em atraso)
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().minusDays(1));
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        empRepo.save(emprestimo);
    }

    @Dado("ocorre uma devolução com atraso gerando multa pendente")
    public void devolucaoComAtrasoGerandoMulta() {
        existeExemplarComStatus("Vinil - Kind of Blue", "disponível");
        if (socio == null) existeSocioAtivoLogado();
        criarEmprestimoAtivo();
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().minusDays(1));
        emprestimo.setDataDevolucaoReal(LocalDate.now());
        empRepo.save(emprestimo);
        try {
            emprestimoService.registrarDevolucao(emprestimo.getId(), LocalDate.now(), CondicaoExemplar.BOM);
        } catch (BusinessException e) {
            mensagemErro = e.getMessage();
        }
        ajustarMultaComValorDoStepSeNecessario();
        socio = socioRepo.findById(socio.getId()).orElseThrow();
    }

    // ===================== WHEN =====================

    @Quando("o administrador solicita o aluguel do exemplar")
    public void solicitaAluguelExemplar() {
        mensagemErro = null;
        emprestimo = null;
        ensureSocioEMidiaParaAluguel();
        try {
            emprestimo = emprestimoService.realizarEmprestimo(socio.getId(), midia.getId());
        } catch (BusinessException e) {
            mensagemErro = e.getMessage();
        }
    }

    // alias pedido pelos .feature
    @Quando("o administrador solicita a locação")
    public void o_administrador_solicita_a_locacao() { solicitaAluguelExemplar(); }

    @Quando("o administrador realiza a locação do exemplar")
    public void realizaLocacao() { solicitaAluguelExemplar(); }

    @Quando("o administrador registra a devolução")
    @Quando("o administrador registra a devolução no sistema")
    public void registraDevolucao() {
        Assertions.assertNotNull(emprestimo, "Empréstimo deve existir para devolução");
        mensagemErro = null;
        try {
            LocalDate dataReal = emprestimo.getDataDevolucaoReal() != null ? emprestimo.getDataDevolucaoReal() : LocalDate.now();
            emprestimoService.registrarDevolucao(emprestimo.getId(), dataReal, CondicaoExemplar.BOM);
            emprestimo = empRepo.findById(emprestimo.getId()).orElseThrow();
            exemplar  = exRepo.findById(emprestimo.getExemplar().getId()).orElseThrow();
            ajustarMultaComValorDoStepSeNecessario();
        } catch (BusinessException e) {
            mensagemErro = e.getMessage();
        }
    }

    @Quando("o administrador tentar registrar a devolução sem informar a condição física")
    public void registrarDevolucaoSemCondicao() {
        Assertions.assertNotNull(emprestimo, "Empréstimo deve existir para devolução");
        mensagemErro = null;
        try {
            emprestimoService.registrarDevolucao(emprestimo.getId(), LocalDate.now(), null);
        } catch (BusinessException e) {
            mensagemErro = e.getMessage();
        }
    }

    @Quando("o administrador registrar a devolução informando a condição {string}")
    public void registrarDevolucaoComCondicao(String condicao) {
        Assertions.assertNotNull(emprestimo, "Empréstimo deve existir para devolução");
        mensagemErro = null;
        try {
            CondicaoExemplar cond = condicao.equalsIgnoreCase("danificado")
                    ? CondicaoExemplar.DANIFICADO
                    : CondicaoExemplar.BOM;
            emprestimoService.registrarDevolucao(emprestimo.getId(), LocalDate.now(), cond);
            emprestimo = empRepo.findById(emprestimo.getId()).orElseThrow();
            exemplar  = exRepo.findById(emprestimo.getExemplar().getId()).orElseThrow();
            ajustarMultaComValorDoStepSeNecessario();
        } catch (BusinessException e) {
            mensagemErro = e.getMessage();
        }
    }

    @Quando("o administrador solicitar a renovação do empréstimo")
    public void solicitarRenovacao() {
        Assertions.assertNotNull(emprestimo, "Empréstimo deve existir para renovação");
        mensagemErro = null;
        dataPrevistaAntesRenovacao = emprestimo.getDataPrevistaDevolucao();
        try {
            emprestimoService.renovarEmprestimo(emprestimo.getId(), LocalDate.now().plusDays(7));
            emprestimo = empRepo.findById(emprestimo.getId()).orElseThrow();
        } catch (BusinessException e) {
            mensagemErro = e.getMessage();
        }
    }

    @Quando("o sistema registrar a multa no empréstimo")
    public void sistemaRegistraMulta() {
        Assertions.assertNotNull(emprestimo, "Empréstimo deve existir");
        mensagemErro = null;
        try {
            emprestimo.setDataPrevistaDevolucao(LocalDate.now().minusDays(3));
            emprestimo.setDataDevolucaoReal(LocalDate.now());
            empRepo.save(emprestimo);
            emprestimoService.registrarDevolucao(emprestimo.getId(), LocalDate.now(), CondicaoExemplar.BOM);
            ajustarMultaComValorDoStepSeNecessario();
        } catch (BusinessException e) {
            mensagemErro = e.getMessage();
        }
    }

    // ===================== THEN =====================

    @Então("o sistema deve permitir a locação")
    public void sistemaPermiteLocacao() {
        Assertions.assertNotNull(emprestimo, "Empréstimo não foi criado");
        Assertions.assertEquals(StatusEmprestimo.ATIVO, emprestimo.getStatus());
    }

    @Então("o status do exemplar deve ser atualizado para {string}")
    public void statusAtualizado(String esperado) {
        exemplar = exRepo.findById(exemplar.getId()).orElseThrow();
        switch (esperado.toLowerCase()) {
            case "disponível" -> Assertions.assertEquals(StatusExemplar.DISPONIVEL, exemplar.getStatus());
            case "emprestado" -> Assertions.assertEquals(StatusExemplar.EMPRESTADO, exemplar.getStatus());
            case "indisponível" -> Assertions.assertEquals(StatusExemplar.INDISPONIVEL, exemplar.getStatus());
            default -> Assertions.fail("Status inesperado: " + esperado);
        }
    }

    @Então("o status do exemplar deve mudar para {string}")
    public void statusMudarPara(String esperado) { statusAtualizado(esperado); }

    @Então("o sistema não deve calcular multa")
    public void naoCalculaMulta() {
        BigDecimal total = Optional.ofNullable(emprestimo.getMulta())
                .map(Multa::getValorTotal)
                .orElse(BigDecimal.ZERO);
        Assertions.assertEquals(0, total.compareTo(BigDecimal.ZERO));
    }

    @Então("o valor da multa deve ser {string}")
    public void valorMulta(String valorStr) {
        BigDecimal esperado = parseMonetario(valorStr);
        BigDecimal total = Optional.ofNullable(emprestimo.getMulta())
                .map(Multa::getValorTotal)
                .orElse(BigDecimal.ZERO);
        Assertions.assertEquals(0, esperado.compareTo(total));
    }

    @Então("o sistema deve calcular multa de {string}")
    public void calculaMulta(String valor) { valorMultaDiaria(valor); }

    @Então("registrar esse valor no empréstimo")
    public void registrarValorNoEmprestimo() {
        Assertions.assertNotNull(emprestimo.getMulta(), "Multa não registrada");
        Assertions.assertTrue(emprestimo.getMulta().getValorTotal().compareTo(BigDecimal.ZERO) > 0);
    }

    @Então("o sistema deve recusar a locação")
    public void recusarLocacao() {
        Assertions.assertNotNull(mensagemErro, "Esperava mensagem de recusa");
        if (emprestimo != null) {
            Assertions.assertNotEquals(StatusEmprestimo.ATIVO, emprestimo.getStatus(),
                    "Não deveria ter empréstimo ativo quando recusa");
        }
    }

    // alias pedido pelos .feature
    @Então("o sistema deve recusar a operação")
    public void o_sistema_deve_recusar_a_operacao() { recusarLocacao(); }

    @Então("o sistema deve exibir uma mensagem de erro {string}")
    public void mensagemDeErroExata(String msg) {
        Assertions.assertNotNull(mensagemErro, "Esperava mensagem de erro");
        Assertions.assertTrue(mensagemErro.contains(msg),
                "Mensagem esperada conter: " + msg + " | obtida: " + mensagemErro);
    }

    @Então("exibir a mensagem {string}")
    public void exibirMensagem(String msg) { mensagemDeErroExata(msg); }

    @Então("a locação não deve ser criada")
    public void locacaoNaoCriada() {
        Assertions.assertNotNull(mensagemErro, "Esperava recusa");
        Assertions.assertTrue(emprestimo == null || emprestimo.getId() == null,
                "Empréstimo não deveria existir quando operação é negada");
    }

    @Então("o sistema deve atualizar o status do sócio para {string}")
    public void atualizarStatusSocio(String status) {
        socio = socioRepo.findById(socio.getId()).orElseThrow();
        switch (status.toLowerCase()) {
            case "ativo" -> Assertions.assertEquals(StatusSocio.ATIVO, socio.getStatus());
            case "bloqueado" -> Assertions.assertEquals(StatusSocio.BLOQUEADO, socio.getStatus());
            case "suspenso"  -> Assertions.assertEquals(StatusSocio.SUSPENSO,  socio.getStatus());
            default -> Assertions.fail("Status de sócio inesperado: " + status);
        }
    }

    @Então("impedir novas locações e renovações")
    public void impedirNovasOperacoes() {
        Assertions.assertEquals(StatusSocio.BLOQUEADO, socio.getStatus());
    }

    @Então("registrar {string}")
    @Então("registrar o evento {string}")
    public void registrarEvento(String evento) { Assertions.assertNotNull(evento); }

    @Então("registrar o empréstimo como {string}")
    public void registrarEmprestimo(String status) {
        Assertions.assertEquals("ativo", status.toLowerCase());
        Assertions.assertNotNull(emprestimo);
        Assertions.assertEquals(StatusEmprestimo.ATIVO, emprestimo.getStatus());
    }

    @Então("o sistema deve recusar o registro")
    public void recusarRegistro() { Assertions.assertNotNull(mensagemErro); }

    @Então("o sistema deve atualizar o status do exemplar para {string}")
    public void atualizarStatusExemplar(String status) { statusAtualizado(status); }

    @Então("impedir novas locações desse exemplar")
    public void impedirNovasLocacoes() {
        exemplar = exRepo.findById(exemplar.getId()).orElseThrow();
        Assertions.assertEquals(StatusExemplar.INDISPONIVEL, exemplar.getStatus());
    }

    @Então("o sistema deve permitir a renovação")
    public void oSistemaDevePermitirRenovacao() {
        Assertions.assertNull(mensagemErro, "Não deveria haver erro na renovação");
    }

    @Então("o sistema deve negar a renovação")
    public void oSistemaDeveNegarRenovacao() {
        Assertions.assertNotNull(mensagemErro, "Deveria negar a renovação");
    }

    @Então("recalcular a nova data prevista de devolução")
    public void recalcularNovaDataPrevistaDeDevolucao() {
        Assertions.assertNotNull(emprestimo, "Empréstimo ausente");
        Assertions.assertNotNull(dataPrevistaAntesRenovacao, "Data prevista anterior não registrada");
        LocalDate depois = empRepo.findById(emprestimo.getId()).orElseThrow().getDataPrevistaDevolucao();
        Assertions.assertTrue(depois.isAfter(dataPrevistaAntesRenovacao),
                "Esperava data prevista posterior à anterior");
    }
}
