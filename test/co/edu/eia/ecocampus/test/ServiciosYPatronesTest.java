package co.edu.eia.ecocampus.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.Reporte;
import co.edu.eia.ecocampus.modelo.UsuarioCampus;
import co.edu.eia.ecocampus.patron.FabricaReportes;
import co.edu.eia.ecocampus.repositorio.RegistroCentral;
import co.edu.eia.ecocampus.servicio.ConsultaReportes;

/** Consulta de pendientes: orden por prioridad y aviso cuando no hay resultados. */
public class ServiciosYPatronesTest {
	private RegistroCentral registro;
	private UsuarioCampus ana;
	private PuntoEcologico punto;

	@BeforeEach
	void preparar() {
		registro = RegistroCentral.getInstancia();
		registro.reiniciar();
		ana = new UsuarioCampus("U1", "Ana", "ana@eia.edu.co", "Estudiante");
		punto = new PuntoEcologico("PE-1", "Biblioteca", 50);
	}

	@Test
	void pendientesSalenDeMasAMenosUrgente() throws EcoCampusException {
		Reporte media = FabricaReportes.crear(FabricaReportes.CONTAMINACION, 1, ana, punto, "d", "2026-10-26");
		Reporte critica = FabricaReportes.crear(FabricaReportes.MATERIAL_ESPECIAL, 2, ana, punto, "d", "2026-10-26");
		Reporte alta = FabricaReportes.crear(FabricaReportes.DESBORDAMIENTO, 3, ana, punto, "d", "2026-10-26");
		registro.getReportes().registrarSiEsValido(media);
		registro.getReportes().registrarSiEsValido(critica);
		registro.getReportes().registrarSiEsValido(alta);

		List<Reporte> ordenados = ConsultaReportes.pendientesPorPrioridad(registro.getReportes());
		assertEquals(3, ordenados.size());
		assertSame(critica, ordenados.get(0));
		assertSame(alta, ordenados.get(1));
		assertSame(media, ordenados.get(2));
	}
}
