package recordstore.dominio.administracao;

import static org.apache.commons.lang3.Validate.notNull;

import recordstore.dominio.administracao.socio.SocioId;

public class Socio {

    private final SocioId id;
    private String nome;
    private Email emailContato;
    private Password senha;

    public Socio(String nome, Email emailContato, Password senha) {
        this.id = null;
        setNome(nome);
        setEmailContato(emailContato);
        setSenha(senha);
    }

    public Socio(SocioId id, String nome, Email emailContato, Password senha) {
        notNull(id, "O id não pode ser nulo");
        this.id = id;
        setNome(nome);
        setEmailContato(emailContato);
        setSenha(senha);
    }

    public SocioId getId() {
        return id;
    }

    private void setNome(String nome) {
        notNull(nome, "O nome não pode ser nulo");
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
    
    private void setSenha(Password senha) {
    	notNull(senha, "A senha não pode ser nula");
        this.senha = senha;
    }
    
    public Password getSenha() {
        return senha;
    }

    private void setEmailContato(Email emailContato) {
        notNull(emailContato, "O email de contato não pode ser nulo");
        this.emailContato = emailContato;
    }

    public Email getEmailContato() {
        return emailContato;
    }

    @Override
    public String toString() {
        return nome;
    }
}
