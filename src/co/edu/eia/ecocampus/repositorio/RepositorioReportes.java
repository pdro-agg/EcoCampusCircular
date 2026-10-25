package co.edu.eia.ecocampus.repositorio;

import co.edu.eia.ecocampus.excepcion.IdentificadorDuplicadoException;
import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.modelo.Reporte;

/**
 * Repositorio de reportes: hereda todo del repositorio generico y agrega
 * solo la regla propia (no registrar reportes invalidos).
 */
public class RepositorioReportes extends RepositorioMemoria<Reporte> {

	public void registrarSiEsValido(Reporte reporte) throws OperacionNoPermitidaException, IdentificadorDuplicadoException {
		if (reporte == null || "Invalido".equals(reporte.getEstado())) {
			throw new OperacionNoPermitidaException("No se puede registrar un reporte sobre un punto inactivo o inexistente");
		}
		registrar(reporte);
	}
}
