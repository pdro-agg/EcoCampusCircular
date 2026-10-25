package co.edu.eia.ecocampus.patron;

import java.util.ArrayList;
import java.util.List;

/**
 * OBSERVER: el "sujeto" mantiene la lista de observadores y los avisa cuando algo cambia.
 * Reporte, RutaRecoleccion y Campana lo extienden: ya no conocen a ningun notificador
 * concreto, solo avisan. Asi se reduce el acoplamiento.
 */
public abstract class Sujeto {
	private List<Observador> observadores;

	protected Sujeto() {
		this.observadores = new ArrayList<>();
	}

	public void agregarObservador(Observador observador) {
		if (observador != null && !observadores.contains(observador)) {
			observadores.add(observador);
		}
	}

	public void quitarObservador(Observador observador) {
		observadores.remove(observador);
	}

	protected void notificarObservadores(String mensaje) {
		for (Observador observador : observadores) {
			observador.actualizar(mensaje);
		}
	}
}
