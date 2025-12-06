package recordstore.dominio.evento;

public interface EventoObservador<E> {
	void observarEvento(E evento);
}