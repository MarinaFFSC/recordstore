package recordstore.aplicacao.administracao.socio;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

import recordstore.dominio.administracao.Email;
import recordstore.dominio.administracao.Password;
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

    public void criar(Integer id, String nome, String email, String senha) {
        if (repositorioAplicacao.existePorId(id)) {
            throw new IllegalArgumentException("Já existe um sócio cadastrado com esse ID.");
        }

        var socioId = new SocioId(id);
        var emailVo = new Email(email);
        var senhaVo = new Password(senha);
        var socio = new Socio(socioId, nome, emailVo, senhaVo);

        repositorioDominio.salvar(socio);
    }

    // ====== NOVO: ATUALIZAR SÓ NOME E EMAIL ======

    public void atualizar(Integer id, String nome, String email, String senha) {
        notNull(id, "O id não pode ser nulo");

        var socioId = new SocioId(id);
        var socio = repositorioDominio.obter(socioId);
        if (socio == null) {
            throw new IllegalArgumentException("Sócio não encontrado para o ID " + id);
        }

        socio.alterarNome(nome);
        socio.alterarEmailContato(new Email(email));

        // Só troca a senha se vier algo preenchido
        if (senha != null && !senha.isBlank()) {
            socio.alterarSenha(new Password(senha));
        }

        repositorioDominio.salvar(socio);
    }


    // ====== NOVO: EXCLUIR ======

    public void excluir(Integer id) {
        notNull(id, "O id não pode ser nulo");

        if (!repositorioAplicacao.existePorId(id)) {
            throw new IllegalArgumentException("Não existe sócio com o ID " + id);
        }

        var socioId = new SocioId(id);
        repositorioDominio.excluir(socioId);
    }
}
