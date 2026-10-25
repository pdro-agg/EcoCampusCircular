package co.edu.eia.ecocampus.excepcion;

/**
 * Excepcion base del dominio (checked, hereda de Exception). Las tres excepciones
 * concretas heredan de ella, asi que se puede atrapar solo EcoCampusException
 * cuando no importa distinguir el tipo de error.
 */
public class EcoCampusException extends Exception {

	public EcoCampusException(String mensaje) {
		super(mensaje);
	}
}
