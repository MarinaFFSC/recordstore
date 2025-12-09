package recordstore.dominio.administracao;

import static org.apache.commons.lang3.Validate.notNull;

import recordstore.dominio.administracao.socio.SocioId;

public class Socio {

    private final SocioId id;
    private String nome;
    private Email emailContato;

    public Socio(String nome, Email emailContato) {
        this.id = null;
        setNome(nome);
        setEmailContato(emailContato);
    }

    public Socio(SocioId id, String nome, Email emailContato) {
        notNull(id, "O id não pode ser nulo");
        this.id = id;
        setNome(nome);
        setEmailContato(emailContato);
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
