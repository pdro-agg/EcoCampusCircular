package co.edu.eia.ecocampus.servicio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.edu.eia.ecocampus.modelo.Reporte;
import co.edu.eia.ecocampus.repositorio.RepositorioReportes;

/** Consultas sobre reportes. */
public class ConsultaReportes {

	// Mayor numero = mas urgente
	private static int urgencia(String prioridad) {
		if ("Critica".equals(prioridad)) {
			return 4;
		} else if ("Alta".equals(prioridad)) {
			return 3;
		} else if ("Media".equals(prioridad)) {
			return 2;
		}
		return 1;
	}

	public static List<Reporte> pendientesPorPrioridad(RepositorioReportes repositorio) {
		List<Reporte> pendientes = new ArrayList<>();
		for (Reporte reporte : repositorio.listar()) {
			if ("Pendiente".equals(reporte.getEstado())) {
				pendientes.add(reporte);
			}
		}
		// IMPLEMENTACION ANONIMA: el criterio de orden se usa solo en esta consulta, asi que
		// no justifica una clase con nombre propio.
		Collections.sort(pendientes, new Comparator<Reporte>() {
			@Override
			public int compare(Reporte a, Reporte b) {
				return urgencia(b.getPrioridad()) - urgencia(a.getPrioridad()); // de mas a menos urgente
			}
		});
		return pendientes;
	}
}
