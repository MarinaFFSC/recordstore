package recordstore;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import recordstore.aplicacao.acervo.artista.ArtistaRepositorioAplicacao;
import recordstore.aplicacao.acervo.artista.ArtistaServicoAplicacao;
import recordstore.aplicacao.acervo.exemplar.ExemplarRepositorioAplicacao;
import recordstore.aplicacao.acervo.exemplar.ExemplarServicoAplicacao;
import recordstore.aplicacao.acervo.midia.MidiaRepositorioAplicacao;
import recordstore.aplicacao.acervo.midia.MidiaServicoAplicacao;
import recordstore.aplicacao.administracao.socio.SocioRepositorioAplicacao;
import recordstore.aplicacao.administracao.socio.SocioServicoAplicacao;
import recordstore.aplicacao.analise.EmprestimoRegistroRepositorioAplicacao;
import recordstore.aplicacao.analise.EmprestimoRegistroServicoAplicacao;

import recordstore.dominio.acervo.artista.ArtistaRepositorio;
import recordstore.dominio.acervo.artista.ArtistaServico;
import recordstore.dominio.acervo.exemplar.EmprestimoServico;
import recordstore.dominio.acervo.exemplar.ExemplarRepositorio;
import recordstore.dominio.acervo.exemplar.ExemplarServico;
import recordstore.dominio.acervo.midia.MidiaRepositorio;
import recordstore.dominio.acervo.midia.MidiaServico;
import recordstore.dominio.administracao.SocioRepositorio;
import recordstore.dominio.administracao.SocioServico;
import recordstore.dominio.analise.emprestimo.EmprestimoRegistroRepositorio;
import recordstore.dominio.evento.EventoBarramento;

@SpringBootApplication
public class VaadinAplicacao {

    // ---------- ARTISTAS ----------
    @Bean
    public ArtistaServico artistaServico(ArtistaRepositorio repositorio) {
        return new ArtistaServico(repositorio);
    }

    @Bean
    public ArtistaServicoAplicacao artistaServicoAplicacao(ArtistaRepositorioAplicacao repositorio) {
        return new ArtistaServicoAplicacao(repositorio);
    }

    // ---------- EXEMPLARES ----------
    @Bean
    public ExemplarServico exemplarServico(ExemplarRepositorio repositorio) {
        return new ExemplarServico(repositorio);
    }

    @Bean
    public ExemplarServicoAplicacao exemplarServicoAplicacao(ExemplarRepositorioAplicacao repositorio) {
        return new ExemplarServicoAplicacao(repositorio);
    }

    // ---------- EMPRÉSTIMO (domínio) ----------
    @Bean
    public EmprestimoServico emprestimoServico(ExemplarRepositorio exemplarRepositorio,
                                               EventoBarramento barramento) {
        return new EmprestimoServico(exemplarRepositorio, barramento);
    }

    // ---------- MIDIAS ----------
    @Bean
    public MidiaServico midiaServico(MidiaRepositorio repositorio) {
        return new MidiaServico(repositorio);
    }

    @Bean
    public MidiaServicoAplicacao midiaServicoAplicacao(MidiaRepositorioAplicacao repositorio) {
        return new MidiaServicoAplicacao(repositorio);
    }

    // ---------- EMPRÉSTIMOS (registros/análise) ----------
    @Bean
    public EmprestimoRegistroServicoAplicacao emprestimoRegistroServicoAplicacao(
            EmprestimoRegistroRepositorio repositorio,
            EmprestimoRegistroRepositorioAplicacao repositorioAplicacao,
            EventoBarramento servico) {
        return new EmprestimoRegistroServicoAplicacao(repositorio, repositorioAplicacao, servico);
    }

    // ---------- SÓCIOS ----------
    @Bean
    public SocioServico socioServico(SocioRepositorio repositorio) {
        return new SocioServico(repositorio);
    }

    @Bean
    public SocioServicoAplicacao socioServicoAplicacao(
            SocioRepositorioAplicacao repoAplicacao,
            SocioRepositorio repoDominio) {
        return new SocioServicoAplicacao(repoAplicacao, repoDominio);
    }

    public static void main(String[] args) {
        run(VaadinAplicacao.class, args);
    }
}
