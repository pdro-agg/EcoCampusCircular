package co.edu.eia.ecocampus.excepcion;

/** Se lanza cuando una regla de negocio impide la operacion (punto inactivo, sin cupo, sin recoleccion...). */
public class OperacionNoPermitidaException extends EcoCampusException {

	public OperacionNoPermitidaException(String mensaje) {
		super(mensaje);
	}
}
