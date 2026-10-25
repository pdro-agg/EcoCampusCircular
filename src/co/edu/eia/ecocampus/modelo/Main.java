package co.edu.eia.ecocampus.modelo;

import java.util.ArrayList;
import java.util.List;

import co.edu.eia.ecocampus.calculo.CalculadorEcoPuntos;
import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.notificacion.NotificadorEcoCampus;
import co.edu.eia.ecocampus.patron.CalculoPorCampana;
import co.edu.eia.ecocampus.patron.FabricaReportes;
import co.edu.eia.ecocampus.patron.CalculoPorMaterial;
import co.edu.eia.ecocampus.patron.Observador;
import co.edu.eia.ecocampus.repositorio.RegistroCentral;
import co.edu.eia.ecocampus.repositorio.RepositorioMemoria;
import co.edu.eia.ecocampus.repositorio.RepositorioReportes;
import co.edu.eia.ecocampus.servicio.ConsultaReportes;
import co.edu.eia.ecocampus.servicio.ProcesadorEcoPuntos;

public class Main {

	private static RepositorioMemoria<Persona> repoPersonas;
	private static RepositorioMemoria<PuntoEcologico> repoPuntos;
	private static RepositorioReportes repoReportes;
	private static RepositorioMemoria<RutaRecoleccion> repoRutas;
	private static RepositorioMemoria<Campana> repoCampanas;

	private static final Observador NOTIFICADOR = new NotificadorEcoCampus();
	private static final CalculadorEcoPuntos CALCULADOR = new CalculadorEcoPuntos(new CalculoPorMaterial());

	public static void main(String[] args) {
		// SINGLETON: todos los datos viven en UN SOLO registro central
		RegistroCentral registro = RegistroCentral.getInstancia();
		repoPersonas = registro.getPersonas();
		repoPuntos = registro.getPuntos();
		repoReportes = registro.getReportes();
		repoRutas = registro.getRutas();
		repoCampanas = registro.getCampanas();

		try {
			ejecutarDemostracion();
		} catch (EcoCampusException e) {
			// Un error NO esperado en la demo: se informa en vez de tumbar el programa
			System.out.println("Error inesperado en la demostracion: " + e.getMessage());
		} finally {
			// finally se ejecuta siempre: el resumen sale haya o no un error
			mostrarResumenGeneral();
		}
	}

	// throws EcoCampusException: los pasos "normales" propagan el error hacia main.
	// Los casos que DEBEN fallar se atrapan aqui mismo con su propio try/catch.
	private static void ejecutarDemostracion() throws EcoCampusException {
		System.out.println("\nEcoCampus Circular - demostracion Entrega 2");

		CategoriaMaterial plastico = new CategoriaMaterial("PL", "Plastico");
		CategoriaMaterial organico = new CategoriaMaterial("OR", "Organico");
		CategoriaMaterial especial = new CategoriaMaterial("ES", "Residuo especial");

		System.out.println("\nPaso 1: registro de puntos ecologicos");
		PuntoEcologico puntoec1 = new PuntoEcologico("PE1", "Bloque 20 - primer piso", 100);
		puntoec1.agregarCategoria(plastico);
		puntoec1.agregarCategoria(organico);
		repoPuntos.registrar(puntoec1);

		PuntoEcologico puntoec2 = new PuntoEcologico("PE2", "Bloque 30 - parqueadero", 50);
		puntoec2.agregarCategoria(especial);
		repoPuntos.registrar(puntoec2);
		puntoec2.desactivar(); // DESACTIVAMOS EL PUNTO ECOLOGICO 2
		System.out.println("Punto " + puntoec2.getCodigo() + " desactivado a proposito para probar validaciones.");

		System.out.println("\nPaso 2: registro de personas");
		UsuarioCampus usuario1 = new UsuarioCampus("1034991859", "Pablo Zuluaga", "pablo.zuluaga27@eia.edu.co",
				"Estudiante");
		UsuarioCampus usuario2 = new UsuarioCampus("1039276889", "Carlos Zea", "carlos@eia.edu.co", "Colaborador");
		Operador operador1 = new Operador("567897988", "Andres Toro", "andres@eia.edu.co", "OP1");
		ResponsableSostenibilidad responsable1 = new ResponsableSostenibilidad("1034993678", "Laura Ospina",
				"laura@eia.edu.co", 1, "Zona norte");
		repoPersonas.registrar(usuario1);
		repoPersonas.registrar(usuario2);
		repoPersonas.registrar(operador1);
		repoPersonas.registrar(responsable1);

		// CASO QUE DEBE FALLAR: MISMO ID
		try {
			repoPersonas.registrar(new UsuarioCampus("1034991859", "Copia", "copia@eia.edu.co", "Estudiante"));
			System.out.println("ERROR: el id duplicado debio ser rechazado");
		} catch (EcoCampusException e) {
			System.out.println("Rechazado como se esperaba: " + e.getMessage());
		}

		System.out.println("\nPaso 3: registro de reportes");
		// FACTORY: la fabrica decide la prioridad segun el tipo de reporte
		Reporte reporteValido = FabricaReportes.crear(FabricaReportes.DESBORDAMIENTO, 1, usuario1, puntoec1,
				"Caneca de plastico desbordada", "2026-08-20");
		reporteValido.agregarObservador(NOTIFICADOR); // OBSERVER: se suscribe al reporte
		repoReportes.registrarSiEsValido(reporteValido);
		System.out.println("Reporte 1 (punto activo) registrado");

		// CASO QUE DEBE FALLAR: PUNTO INACTIVO
		try {
			repoReportes.registrarSiEsValido(
					FabricaReportes.crear(FabricaReportes.MATERIAL_ESPECIAL, 2, usuario2, puntoec2,
							"Material especial abandonado", "2026-08-20"));
			System.out.println("ERROR: el reporte sobre punto inactivo debio ser rechazado");
		} catch (EcoCampusException e) {
			System.out.println("Reporte 2 (punto inactivo) rechazado: " + e.getMessage());
		}

		// CASO QUE DEBE FALLAR: SIN PUNTO ECOLOGICO
		try {
			repoReportes.registrarSiEsValido(
					FabricaReportes.crear(FabricaReportes.CONTAMINACION, 3, usuario2, null,
							"Reporte sin punto ecologico agregado", "2026-08-20"));
			System.out.println("ERROR: el reporte sin punto debio ser rechazado");
		} catch (EcoCampusException e) {
			System.out.println("Reporte 3 (sin punto ecologico) rechazado: " + e.getMessage());
		}

		// CASO QUE DEBE FALLAR: TRANSICION INVALIDA (CERRAR UN REPORTE PENDIENTE)
		try {
			reporteValido.cerrar(null);
			System.out.println("ERROR: no se debio poder cerrar un reporte pendiente");
		} catch (EcoCampusException e) {
			System.out.println("Cierre prematuro rechazado: " + e.getMessage());
		}

		System.out.println("\nPaso 4: asignacion de reporte");
		reporteValido.asignarOperador(operador1);
		operador1.ocupar();

		// CASO QUE DEBE FALLAR: ASIGNADO PERO SIN RECOLECCION
		try {
			reporteValido.cerrar(null);
			System.out.println("ERROR: no se debio cerrar sin recoleccion");
		} catch (EcoCampusException e) {
			System.out.println("Cierre sin recoleccion rechazado: " + e.getMessage());
		}

		System.out.println("\nPaso 5: ruta de recoleccion");
		RutaRecoleccion ruta1 = new RutaRecoleccion(1);
		ruta1.agregarObservador(NOTIFICADOR);
		ruta1.agregarParada(new Parada(1, puntoec1, "Recoger plastico y organico"));
		try {
			ruta1.agregarParada(new Parada(2, puntoec2, "Recoger residuo especial"));
			System.out.println("ERROR: la parada sobre punto inactivo debio ser rechazada");
		} catch (EcoCampusException e) {
			System.out.println("Parada rechazada: " + e.getMessage());
		}
		repoRutas.registrar(ruta1);

		System.out.println("\nPaso 6: recoleccion y cierre de reporte");
		Recoleccion recoleccion1 = new Recoleccion(1, "2026-08-21", operador1, "Recoleccion rutinaria bloque 20");
		recoleccion1.agregarMaterial(plastico, 4.5);
		recoleccion1.agregarMaterial(organico, 2.0);

		try {
			recoleccion1.agregarMaterial(especial, 3.0);
			System.out.println("ERROR: el residuo especial debio ser rechazado");
		} catch (EcoCampusException e) {
			System.out.println("Material especial rechazado: " + e.getMessage());
		}
		operador1.habilitarResiduosEspeciales();
		recoleccion1.agregarMaterial(especial, 3.0);
		System.out.println("Material especial con operador ya habilitado registrado");

		reporteValido.cerrar(recoleccion1);
		System.out.println("Reporte 1 cerrado con recoleccion asociada");
		// CASO QUE DEBE FALLAR: DE CERRADO A ASIGNADO SIN REAPERTURA EXPLICITA
		try {
			reporteValido.asignarOperador(operador1);
			System.out.println("ERROR: un reporte cerrado no puede volver a asignarse");
		} catch (EcoCampusException e) {
			System.out.println("Transicion rechazada: " + e.getMessage());
		}
		operador1.desocupar();
		int puntosCalculados = CALCULADOR.calcular(plastico, recoleccion1.getPesoTotal()); // STRATEGY
		MovimientoReciclaje movimiento1 = new MovimientoReciclaje(puntosCalculados, "Cierre del reporte 1",
				"2026-08-21", plastico, recoleccion1.getPesoTotal());
		usuario1.acumularEcoPuntos(movimiento1);
		ruta1.cerrarRuta();

		System.out.println("\nPaso 7: campana ambiental");
		Campana campana1 = new Campana(1, "Semana EcoCampus", 5);
		campana1.agregarObservador(NOTIFICADOR);
		campana1.agregarActividad(new Actividad("Reciclatón", "2026-09-10", 50));
		campana1.agregarActividad(new Actividad("Taller de compostaje", "2026-09-11", 30));
		campana1.inscribirParticipante(usuario1);
		campana1.inscribirParticipante(usuario2);
		try {
			campana1.inscribirParticipante(usuario1);
			System.out.println("ERROR: la inscripcion duplicada debio ser rechazada");
		} catch (EcoCampusException e) {
			System.out.println("Inscripcion rechazada: " + e.getMessage());
		}
		usuario2.acumularEcoPuntos(new MovimientoEcoPuntos(20, "Participacion en campaña 1", "2026-09-10"));
		repoCampanas.registrar(campana1);

		System.out.println("\nPaso 8: polimorfismo sobre Persona");
		for (Persona persona : repoPersonas.listar()) {
			System.out.println(persona);
		}

		System.out.println("\nPaso 9: eco-puntos, generics y wildcards");
		// CASO QUE DEBE FALLAR: EL MOVIMIENTO NEGATIVO
		try {
			usuario2.acumularEcoPuntos(new MovimientoEcoPuntos(-5, "Intento negativo", "2026-09-12"));
			System.out.println("ERROR: los eco-puntos negativos debieron ser rechazados");
		} catch (EcoCampusException e) {
			System.out.println("Movimiento rechazado: " + e.getMessage());
		}

		List<MovimientoReciclaje> reciclajes = new ArrayList<>();
		reciclajes.add(movimiento1);
		reciclajes.add(new MovimientoReciclaje(12, "Reporte 2", "2026-08-25", organico, 2.0));
		List<MovimientoEcoPuntos> historialGeneral = new ArrayList<>();

		// ? extends: el mismo metodo lee una lista de subclase y una de la clase base
		System.out.println("Puntos de List<MovimientoReciclaje>: " + ProcesadorEcoPuntos.sumarPuntos(reciclajes));
		System.out.println("Puntos de List<MovimientoEcoPuntos> de Pablo: "
				+ ProcesadorEcoPuntos.sumarPuntos(usuario1.getMovimientos()));
		// ? super: el destino puede ser una lista de la clase base
		ProcesadorEcoPuntos.copiarReciclajes(reciclajes, historialGeneral);
		System.out.println("Copiados al historial general: " + ProcesadorEcoPuntos.contarElementos(historialGeneral));
		// Metodo generico acotado: retorna MovimientoReciclaje sin casting
		MovimientoReciclaje mayor = ProcesadorEcoPuntos.mayorMovimiento(reciclajes);
		System.out.println("Mayor movimiento: " + mayor);

		System.out.println("\nPaso 10: strategy y observer");
		// STRATEGY: se cambia la politica de calculo sin tocar el calculador ni las entidades
		System.out.println("Por material (plastico, 9.5 kg): " + CALCULADOR.calcular(plastico, 9.5));
		CALCULADOR.setEstrategia(new CalculoPorCampana(8));
		System.out.println("Por campaña (8 puntos/kg, 9.5 kg): " + CALCULADOR.calcular(plastico, 9.5));
		CALCULADOR.setEstrategia(new CalculoPorMaterial());
		// OBSERVER: una campaña con cupo 2 avisa cuando se llena
		Campana campana2 = new Campana(2, "Jornada relampago", 2);
		campana2.agregarObservador(NOTIFICADOR);
		campana2.inscribirParticipante(usuario1);
		campana2.inscribirParticipante(usuario2);
		try {
			campana2.inscribirParticipante(new UsuarioCampus("2000", "Extra", "extra@eia.edu.co", "Estudiante"));
			System.out.println("ERROR: la campaña llena no debio admitir mas participantes");
		} catch (EcoCampusException e) {
			System.out.println("Inscripcion rechazada: " + e.getMessage());
		}
		repoCampanas.registrar(campana2);

		System.out.println("\nPaso 11: factory, singleton, clase interna y clase anonima");
		// FACTORY: la prioridad sale del tipo de reporte
		Reporte reporte4 = FabricaReportes.crear(FabricaReportes.CONTAMINACION, 4, usuario2, puntoec1,
				"Plastico mezclado con organico", "2026-08-26");
		Reporte reporte5 = FabricaReportes.crear(FabricaReportes.MATERIAL_ESPECIAL, 5, usuario1, puntoec1,
				"Pilas depositadas en la caneca", "2026-08-26");
		repoReportes.registrarSiEsValido(reporte4);
		repoReportes.registrarSiEsValido(reporte5);
		System.out.println("Prioridad por tipo -> " + reporte4.getTipo() + ": " + reporte4.getPrioridad() + " | "
				+ reporte5.getTipo() + ": " + reporte5.getPrioridad());
		try {
			FabricaReportes.crear("Ruido", 6, usuario1, puntoec1, "Tipo que no existe", "2026-08-26");
			System.out.println("ERROR: el tipo desconocido debio ser rechazado");
		} catch (EcoCampusException e) {
			System.out.println("Tipo rechazado: " + e.getMessage());
		}
		// SINGLETON: siempre se obtiene la misma instancia
		System.out.println("Misma instancia de RegistroCentral: "
				+ (RegistroCentral.getInstancia() == RegistroCentral.getInstancia()));
		// CLASE INTERNA: historial de cambios de estado del reporte 1
		System.out.println("Historial del reporte 1:");
		for (String cambio : reporteValido.getHistorial()) {
			System.out.println("- " + cambio);
		}
	}

	private static void mostrarResumenGeneral() {
		System.out.println("\nResumen general");

		System.out.println("\nReportes pendientes por prioridad:");
		// CLASE ANONIMA: la consulta ordena con un Comparator definido en el momento
		List<Reporte> pendientes = ConsultaReportes.pendientesPorPrioridad(repoReportes);
		for (Reporte reporte : pendientes) {
			System.out.println("- Reporte " + reporte.getNumero() + " (" + reporte.getPrioridad() + ", "
					+ reporte.getTipo() + ") en punto " + reporte.getPuntoEcologico().getCodigo());
		}
		if (pendientes.isEmpty()) {
			System.out.println("No hay reportes pendientes.");
		}

		System.out.println("\nRutas registradas:");
		for (RutaRecoleccion ruta : repoRutas.listar()) {
			System.out.println(ruta);
		}

		System.out.println("\nCampañas registradas:");
		for (Campana campana : repoCampanas.listar()) {
			System.out.println(campana);
		}

		System.out.println("\nRanking de eco-puntos:");
		for (Persona persona : repoPersonas.listar()) {
			if (persona instanceof UsuarioCampus) {
				UsuarioCampus u = (UsuarioCampus) persona;
				System.out.println("- " + u.getNombre() + ": " + u.getEcoPuntos() + " eco-puntos");
			}
		}

		System.out.println("\nResumen final de objetos registrados: Personas=" + repoPersonas.getTotal() + ", Puntos="
				+ repoPuntos.getTotal() + ", Reportes=" + repoReportes.getTotal() + ", Rutas=" + repoRutas.getTotal()
				+ ", Campañas=" + repoCampanas.getTotal());
	}
}
