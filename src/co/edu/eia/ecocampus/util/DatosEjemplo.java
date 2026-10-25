package co.edu.eia.ecocampus.util;

import java.time.LocalDate;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.modelo.Actividad;
import co.edu.eia.ecocampus.modelo.Campana;
import co.edu.eia.ecocampus.modelo.MovimientoReciclaje;
import co.edu.eia.ecocampus.modelo.Operador;
import co.edu.eia.ecocampus.modelo.Parada;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.Reporte;
import co.edu.eia.ecocampus.modelo.RutaRecoleccion;
import co.edu.eia.ecocampus.modelo.UsuarioCampus;
import co.edu.eia.ecocampus.patron.CalculoPorMaterial;
import co.edu.eia.ecocampus.patron.FabricaReportes;
import co.edu.eia.ecocampus.patron.Observador;
import co.edu.eia.ecocampus.repositorio.RegistroCentral;

/** Carga un conjunto pequeño de datos para probar la aplicacion sin escribirlos a mano. */
public final class DatosEjemplo {

	private DatosEjemplo() {
	}

	public static void cargar(RegistroCentral registro, Observador observador) throws EcoCampusException {
		if (registro.getPersonas().existe("1001")) {
			throw new OperacionNoPermitidaException("Los datos de ejemplo ya fueron cargados.");
		}
		String hoy = LocalDate.now().toString();

		UsuarioCampus ana = new UsuarioCampus("1001", "Ana Gómez", "ana@eia.edu.co", "Estudiante");
		UsuarioCampus luis = new UsuarioCampus("1002", "Luis Pérez", "luis@eia.edu.co", "Docente");
		Operador carlos = new Operador("2001", "Carlos Ruiz", "carlos@eia.edu.co", "OP-01");
		carlos.habilitarResiduosEspeciales();
		Operador marta = new Operador("2002", "Marta Díaz", "marta@eia.edu.co", "OP-02");
		registro.getPersonas().registrar(ana);
		registro.getPersonas().registrar(luis);
		registro.getPersonas().registrar(carlos);
		registro.getPersonas().registrar(marta);

		PuntoEcologico biblioteca = new PuntoEcologico("PE-01", "Biblioteca", 80);
		biblioteca.agregarCategoria(Materiales.PLASTICO);
		biblioteca.agregarCategoria(Materiales.ORGANICO);
		PuntoEcologico cafeteria = new PuntoEcologico("PE-02", "Cafetería", 120);
		cafeteria.agregarCategoria(Materiales.PLASTICO);
		cafeteria.agregarCategoria(Materiales.ORGANICO);
		cafeteria.agregarCategoria(Materiales.ESPECIAL);
		PuntoEcologico bloque = new PuntoEcologico("PE-03", "Bloque 5", 60);
		bloque.agregarCategoria(Materiales.PLASTICO);
		bloque.desactivar();
		registro.getPuntos().registrar(biblioteca);
		registro.getPuntos().registrar(cafeteria);
		registro.getPuntos().registrar(bloque);

		Reporte r1 = FabricaReportes.crear(FabricaReportes.DESBORDAMIENTO, 1, ana, biblioteca,
				"El contenedor de plástico está desbordado", hoy);
		Reporte r2 = FabricaReportes.crear(FabricaReportes.CONTAMINACION, 2, luis, cafeteria,
				"Residuos orgánicos mezclados con plástico", hoy);
		Reporte r3 = FabricaReportes.crear(FabricaReportes.MATERIAL_ESPECIAL, 3, ana, cafeteria,
				"Baterías en el contenedor general", hoy);
		for (Reporte reporte : new Reporte[] { r1, r2, r3 }) {
			registro.getReportes().registrarSiEsValido(reporte);
			reporte.agregarObservador(observador);
		}

		RutaRecoleccion ruta = new RutaRecoleccion(1);
		ruta.agregarParada(new Parada(1, biblioteca, "Vaciar contenedor de plástico"));
		ruta.agregarParada(new Parada(2, cafeteria, "Revisar contenedores"));
		ruta.agregarObservador(observador);
		registro.getRutas().registrar(ruta);

		Campana campana = new Campana(1, "Semana del reciclaje", 3);
		campana.agregarActividad(new Actividad("Taller de separación", hoy, 30));
		campana.agregarObservador(observador);
		campana.inscribirParticipante(ana);
		registro.getCampanas().registrar(campana);

		CalculoPorMaterial calculo = new CalculoPorMaterial();
		ana.acumularEcoPuntos(new MovimientoReciclaje(calculo.calcular(Materiales.PLASTICO, 5),
				"Reciclaje de 5.0 kg de Plástico", hoy, Materiales.PLASTICO, 5));
		luis.acumularEcoPuntos(new MovimientoReciclaje(calculo.calcular(Materiales.ORGANICO, 3),
				"Reciclaje de 3.0 kg de Orgánico", hoy, Materiales.ORGANICO, 3));
	}
}
