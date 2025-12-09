package recordstore.aplicacao.administracao.socio;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

import recordstore.dominio.administracao.Email;
import recordstore.dominio.administracao.Socio;
import recordstore.dominio.administracao.SocioRepositorio;
import recordstore.dominio.administracao.socio.SocioId;

public class SocioServicoAplicacao {

    private final SocioRepositorioAplicacao repositorioAplicacao;
    private final SocioRepositorio repositorioDominio;

    public SocioServicoAplicacao(SocioRepositorioAplicacao repositorioAplicacao,
                                 SocioRepositorio repositorioDominio) {
        notNull(repositorioAplicacao, "O repositório de aplicação não pode ser nulo");
        notNull(repositorioDominio, "O repositório de domínio não pode ser nulo");
        this.repositorioAplicacao = repositorioAplicacao;
        this.repositorioDominio = repositorioDominio;
    }

    public List<SocioResumo> pesquisarResumos() {
        return repositorioAplicacao.pesquisarResumos();
    }

    public void criar(Integer id, String nome, String email) {
        var socioId = new SocioId(id);
        var emailVo = new Email(email);       // usa o Value Object de domínio
        var socio = new Socio(socioId, nome, emailVo);
        repositorioDominio.salvar(socio);
    }
}
