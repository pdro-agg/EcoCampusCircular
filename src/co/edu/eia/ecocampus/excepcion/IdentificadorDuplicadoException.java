package co.edu.eia.ecocampus.excepcion;

/** Se lanza cuando se intenta registrar algo con un identificador que ya existe. */
public class IdentificadorDuplicadoException extends EcoCampusException {

	public IdentificadorDuplicadoException(String mensaje) {
		super(mensaje);
	}
}
