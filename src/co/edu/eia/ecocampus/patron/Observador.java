package co.edu.eia.ecocampus.patron;

/** OBSERVER: quien quiera enterarse de un cambio implementa esta interfaz. */
public interface Observador {
	void actualizar(String mensaje);
}
