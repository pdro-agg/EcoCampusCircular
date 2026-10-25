package co.edu.eia.ecocampus.excepcion;

/** Se lanza cuando se pide un cambio de estado que el flujo no permite (ej. cerrar un reporte pendiente). */
public class TransicionInvalidaException extends EcoCampusException {

	public TransicionInvalidaException(String mensaje) {
		super(mensaje);
	}
}
