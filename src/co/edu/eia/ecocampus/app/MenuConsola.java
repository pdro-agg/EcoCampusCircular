package co.edu.eia.ecocampus.app;

import java.util.List;
import java.util.Scanner;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.Main;
import co.edu.eia.ecocampus.modelo.Reporte;
import co.edu.eia.ecocampus.modelo.UsuarioCampus;
import co.edu.eia.ecocampus.repositorio.RegistroCentral;
import co.edu.eia.ecocampus.servicio.ConsultaReportes;

/**
 * Menu de consola. Responsabilidad: hablar con el usuario (leer y mostrar).
 * Las reglas de negocio NO estan aqui: viven en el modelo y lanzan excepciones
 * que este menu atrapa para mostrar el mensaje.
 */
public class MenuConsola {
	private Scanner scanner;
	private RegistroCentral registro;

	public MenuConsola() {
		this.scanner = new Scanner(System.in);
		this.registro = RegistroCentral.getInstancia();
	}

	// ---------- 1. EL CICLO PRINCIPAL ----------
	public void ejecutar() {
		int opcion;
		do {
			mostrarMenu();
			opcion = leerEntero("Elige una opcion: ");
			procesarOpcion(opcion);
		} while (opcion != 0);
		System.out.println("Hasta pronto.");
	}

	private void mostrarMenu() {
		System.out.println("\n===== EcoCampus Circular =====");
		System.out.println("1. Registrar usuario del campus");
		System.out.println("2. Registrar operador");
		System.out.println("3. Registrar punto ecologico");
		System.out.println("4. Crear reporte");
		System.out.println("5. Asignar reporte a un operador");
		System.out.println("6. Registrar recoleccion y cerrar reporte");
		System.out.println("7. Crear campaña e inscribir participante");
		System.out.println("8. Ver reportes pendientes por prioridad");
		System.out.println("9. Ver ranking de eco-puntos");
		System.out.println("10. Cargar datos de demostracion");
		System.out.println("0. Salir");
	}

	// ---------- 2. DECIDIR QUE HACER SEGUN LA OPCION ----------
	private void procesarOpcion(int opcion) {
		switch (opcion) {
		case 1:
			registrarUsuario();
			break;
		case 2:
			enConstruccion("Registrar operador");
			break;
		case 3:
			enConstruccion("Registrar punto ecologico");
			break;
		case 4:
			enConstruccion("Crear reporte");
			break;
		case 5:
			enConstruccion("Asignar reporte a un operador");
			break;
		case 6:
			enConstruccion("Registrar recoleccion y cerrar reporte");
			break;
		case 7:
			enConstruccion("Crear campaña e inscribir participante");
			break;
		case 8:
			verReportesPendientes();
			break;
		case 9:
			enConstruccion("Ver ranking de eco-puntos");
			break;
		case 10:
			Main.main(new String[0]);
			break;
		case 0:
			break;
		default:
			System.out.println("Opcion no valida. Elige un numero del menu.");
		}
	}

	// ---------- 3. UNA OPCION COMPLETA: EL MODELO A SEGUIR ----------
	private void registrarUsuario() {
		System.out.println("\n-- Registrar usuario del campus --");
		String id = leerTexto("Identificacion: ");
		String nombre = leerTexto("Nombre: ");
		String correo = leerTexto("Correo: ");
		String tipo = leerTexto("Tipo de vinculacion (Estudiante, Colaborador...): ");

		UsuarioCampus usuario = new UsuarioCampus(id, nombre, correo, tipo);
		try {
			registro.getPersonas().registrar(usuario);
			System.out.println("Usuario registrado: " + nombre);
		} catch (EcoCampusException e) {
			// la regla (id unico) la valida el repositorio; el menu solo muestra el mensaje
			System.out.println("No se pudo registrar: " + e.getMessage());
		}
	}

	private void verReportesPendientes() {
		System.out.println("\n-- Reportes pendientes por prioridad --");
		List<Reporte> pendientes = ConsultaReportes.pendientesPorPrioridad(registro.getReportes());
		if (pendientes.isEmpty()) {
			System.out.println("No hay reportes pendientes."); // las busquedas deben avisar si no hay resultados
		}
		for (Reporte reporte : pendientes) {
			System.out.println("- Reporte " + reporte.getNumero() + " (" + reporte.getPrioridad() + ", "
					+ reporte.getTipo() + ") en punto " + reporte.getPuntoEcologico().getCodigo());
		}
	}

	private void enConstruccion(String nombreOpcion) {
		System.out.println("\"" + nombreOpcion + "\" todavia no esta implementada.");
	}

	// ---------- 4. AYUDANTES PARA LEER DATOS ----------
	private String leerTexto(String mensaje) {
		System.out.print(mensaje);
		return scanner.nextLine().trim();
	}

	// Repite la pregunta hasta que el usuario escriba un numero entero valido
	private int leerEntero(String mensaje) {
		while (true) {
			String texto = leerTexto(mensaje);
			try {
				return Integer.parseInt(texto);
			} catch (NumberFormatException e) {
				System.out.println("Debes escribir un numero entero.");
			}
		}
	}
}
