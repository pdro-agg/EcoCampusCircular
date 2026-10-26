package co.edu.eia.ecocampus.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.modelo.Operador;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.Recoleccion;
import co.edu.eia.ecocampus.modelo.Reporte;
import co.edu.eia.ecocampus.modelo.UsuarioCampus;
import co.edu.eia.ecocampus.patron.FabricaReportes;
import co.edu.eia.ecocampus.repositorio.RegistroCentral;
import co.edu.eia.ecocampus.util.Materiales;

/** Flujo de estados del reporte: Pendiente -> Asignado -> Cerrado. */
public class ReporteTest {
	private Operador operador;
	private Reporte reporte;

	@BeforeEach
	void preparar() throws EcoCampusException {
		RegistroCentral.getInstancia().reiniciar();
		UsuarioCampus autor = new UsuarioCampus("U1", "Ana", "ana@eia.edu.co", "Estudiante");
		PuntoEcologico punto = new PuntoEcologico("PE-1", "Biblioteca", 50);
		operador = new Operador("O1", "Carlos", "carlos@eia.edu.co", "OP-1");
		reporte = FabricaReportes.crear(FabricaReportes.DESBORDAMIENTO, 1, autor, punto, "Contenedor lleno",
				"2026-10-26");
	}

	@Test
	void cerrarSinPesoLanzaExcepcionYElReporteSigueAsignado() throws EcoCampusException {
		reporte.asignarOperador(operador);
		Recoleccion vacia = new Recoleccion(1, "2026-10-26", operador);
		assertThrows(OperacionNoPermitidaException.class, () -> reporte.cerrar(vacia));
		assertEquals("Asignado", reporte.getEstado());
	}

	@Test
	void historialRegistraCadaTransicionEnOrden() throws EcoCampusException {
		reporte.asignarOperador(operador);
		Recoleccion recoleccion = new Recoleccion(1, "2026-10-26", operador);
		recoleccion.agregarMaterial(Materiales.PLASTICO, 4);
		reporte.cerrar(recoleccion);

		List<String> historial = reporte.getHistorial();
		assertEquals(2, historial.size());
		assertEquals("Reporte 1: Pendiente -> Asignado", historial.get(0));
		assertEquals("Reporte 1: Asignado -> Cerrado", historial.get(1));
	}
}
