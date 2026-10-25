package co.edu.eia.ecocampus.persistencia;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;

public class PersistenciaException extends EcoCampusException {

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje + ": " + causa.getMessage());
    }

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }
}