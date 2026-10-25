package co.edu.eia.ecocampus.notificacion;

import co.edu.eia.ecocampus.patron.Observador;

/** Observador concreto: muestra por consola cada cambio relevante. */
public class NotificadorEcoCampus implements Observador {
	@Override
	public void actualizar(String mensaje) {
		System.out.println("[Notificacion] " + mensaje);
	}
}
