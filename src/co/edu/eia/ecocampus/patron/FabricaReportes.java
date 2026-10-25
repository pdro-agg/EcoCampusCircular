package co.edu.eia.ecocampus.patron;

import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.Reporte;
import co.edu.eia.ecocampus.modelo.UsuarioCampus;

/**
 * FACTORY: centraliza la creacion de reportes segun su TIPO. Cada tipo tiene una prioridad
 * por defecto, asi quien crea el reporte no escribe la prioridad a mano ni olvida una regla.
 * Si aparece un tipo nuevo, se cambia aqui y no en cada lugar que hacia "new Reporte(...)".
 */
public class FabricaReportes {
	public static final String DESBORDAMIENTO = "Desbordamiento";
	public static final String CONTAMINACION = "Contaminacion";
	public static final String MATERIAL_ESPECIAL = "Material especial";

	public static Reporte crear(String tipo, int numero, UsuarioCampus autor, PuntoEcologico punto,
			String descripcion, String fecha) throws OperacionNoPermitidaException {
		String prioridad;
		if (DESBORDAMIENTO.equals(tipo)) {
			prioridad = "Alta";
		} else if (CONTAMINACION.equals(tipo)) {
			prioridad = "Media";
		} else if (MATERIAL_ESPECIAL.equals(tipo)) {
			prioridad = "Critica"; // los residuos especiales son los mas urgentes
		} else {
			throw new OperacionNoPermitidaException("Tipo de reporte desconocido: " + tipo);
		}
		return new Reporte(numero, tipo, autor, punto, descripcion, fecha, prioridad);
	}
}
