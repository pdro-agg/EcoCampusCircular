package co.edu.eia.ecocampus.servicio;

import java.util.List;

import co.edu.eia.ecocampus.modelo.MovimientoEcoPuntos;
import co.edu.eia.ecocampus.modelo.MovimientoReciclaje;

/**
 * Metodos genericos y con wildcards para procesar movimientos de eco-puntos.
 * No es una clase generica: solo lo son sus metodos (el <T> va antes del tipo de retorno).
 *
 * PECS: Producer Extends, Consumer Super.
 *  - La lista que PRODUCE datos que yo leo  -> ? extends
 *  - La lista que CONSUME datos que yo agrego -> ? super
 */
public class ProcesadorEcoPuntos {

	// LEE: acepta List<MovimientoEcoPuntos> y tambien List<MovimientoReciclaje>
	public static int sumarPuntos(List<? extends MovimientoEcoPuntos> movimientos) {
		int total = 0;
		for (MovimientoEcoPuntos movimiento : movimientos) {
			total += movimiento.getPuntos();
		}
		return total;
	}

	// METODO GENERICO ACOTADO: T es MovimientoEcoPuntos o una subclase, y retorna ese mismo T (sin casting)
	public static <T extends MovimientoEcoPuntos> T mayorMovimiento(List<T> movimientos) {
		if (movimientos.isEmpty()) {
			return null;
		}
		T mayor = movimientos.get(0);
		for (T movimiento : movimientos) {
			if (movimiento.getPuntos() > mayor.getPuntos()) {
				mayor = movimiento;
			}
		}
		return mayor;
	}

	// LEE del origen (extends) y ESCRIBE en el destino (super): acepta como destino
	// List<MovimientoReciclaje>, List<MovimientoEcoPuntos> o List<Object>
	public static void copiarReciclajes(List<? extends MovimientoReciclaje> origen,
			List<? super MovimientoReciclaje> destino) {
		for (MovimientoReciclaje movimiento : origen) {
			destino.add(movimiento);
		}
	}

	// ?: acepta cualquier lista; solo se pregunta el tamaño
	public static int contarElementos(List<?> elementos) {
		return elementos.size();
	}
}
