package co.edu.eia.ecocampus.repositorio;

import java.util.List;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.excepcion.IdentificadorDuplicadoException;
import co.edu.eia.ecocampus.modelo.Campana;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.RutaRecoleccion;
import co.edu.eia.ecocampus.modelo.UsuarioCampus;
import co.edu.eia.ecocampus.persistencia.ConexionBD;
import co.edu.eia.ecocampus.persistencia.PersonaDAO;
import co.edu.eia.ecocampus.persistencia.PuntoEcologicoDAO;
import co.edu.eia.ecocampus.persistencia.PersistenciaException;

public class RegistroCentral {
	private static RegistroCentral instancia;

	private RepositorioMemoria<Persona> personas;
	private RepositorioMemoria<PuntoEcologico> puntos;
	private RepositorioReportes reportes;
	private RepositorioMemoria<RutaRecoleccion> rutas;
	private RepositorioMemoria<Campana> campanas;
	
	private PersonaDAO personaDAO = new PersonaDAO();
	private PuntoEcologicoDAO puntoDAO = new PuntoEcologicoDAO();

	private RegistroCentral() {
		inicializar();
	}

	public static RegistroCentral getInstancia() {
		if (instancia == null) {
			instancia = new RegistroCentral();
		}
		return instancia;
	}

	public void reiniciar() {
	    inicializar();
	    
	    // Limpiar las tablas en SQLite para que los tests no conserven basura
	    try (java.sql.Connection conn = ConexionBD.obtenerConexion();
	         java.sql.Statement stmt = conn.createStatement()) {
	        stmt.execute("DELETE FROM personas");
	        stmt.execute("DELETE FROM puntos_ecologicos");
	    } catch (java.sql.SQLException e) {
	        System.err.println("Error al reiniciar BD en test: " + e.getMessage());
	    }
	}

	private void inicializar() {
		this.personas = new RepositorioMemoria<>();
		this.puntos = new RepositorioMemoria<>();
		this.reportes = new RepositorioReportes();
		this.rutas = new RepositorioMemoria<>();
		this.campanas = new RepositorioMemoria<>();

		// 1. Inicializar tablas en SQLite
		ConexionBD.inicializarTablas();

		// 2. Cargar Personas desde la BD
		try {
			List<Persona> listaPersonas = personaDAO.cargarTodos();
			for (Persona p : listaPersonas) {
				if (!personas.existe(p.getId())) {
					try {
						this.personas.registrar(p);
					} catch (IdentificadorDuplicadoException e) {
						// Ignorar si ya está cargado
					}
				}
			}
		} catch (PersistenciaException e) {
			System.err.println("Error al cargar personas desde BD: " + e.getMessage());
		}

		// 3. Cargar Puntos Ecológicos desde la BD
		try {
			List<PuntoEcologico> listaPuntos = puntoDAO.cargarTodos();
			for (PuntoEcologico pt : listaPuntos) {
				if (!puntos.existe(pt.getId())) {
					try {
						this.puntos.registrar(pt);
					} catch (IdentificadorDuplicadoException e) {
						// Ignorar si ya está cargado
					}
				}
			}
		} catch (PersistenciaException e) {
			System.err.println("Error al cargar puntos ecológicos desde BD: " + e.getMessage());
		}
	}

	// Registrar usuario con Write-Through
	public void registrarUsuario(UsuarioCampus usuario) throws EcoCampusException {
		if (usuario == null || usuario.getId() == null || usuario.getId().trim().isEmpty()) {
			throw new EcoCampusException("El ID del usuario no es válido");
		}

		personas.registrar(usuario);

		try {
			personaDAO.guardar(usuario);
		} catch (PersistenciaException e) {
			throw e;
		}
	}

	// Registrar punto ecológico con Write-Through
	public void registrarPuntoEcologico(PuntoEcologico punto) throws EcoCampusException {
		if (punto == null || punto.getId() == null || punto.getId().trim().isEmpty()) {
			throw new EcoCampusException("El ID del punto ecológico no es válido");
		}

		puntos.registrar(punto);

		try {
			puntoDAO.guardar(punto);
		} catch (PersistenciaException e) {
			throw e;
		}
	}

	// --- REGLAS DE NEGOCIO: ELIMINACIÓN CON CONSISTENCIA ---

	public void eliminarPuntoEcologico(String idPunto) throws EcoCampusException {
		if (idPunto == null || idPunto.trim().isEmpty()) {
			throw new EcoCampusException("El ID del punto ecológico no es válido.");
		}

		PuntoEcologico punto = puntos.buscar(idPunto);
		if (punto == null) {
			throw new EcoCampusException("El punto ecológico con ID '" + idPunto + "' no existe.");
		}

		boolean tieneReportesAbiertos = reportes.listar().stream()
				.anyMatch(r -> r.getPuntoEcologico() != null 
						&& r.getPuntoEcologico().getId().equalsIgnoreCase(idPunto) 
						&& "Abierto".equalsIgnoreCase(r.getEstado()));

		if (tieneReportesAbiertos) {
			throw new EcoCampusException("No se puede eliminar el punto ecológico porque tiene reportes de llenado pendientes.");
		}

		puntos.eliminar(idPunto);
	}

	public void eliminarCampana(String idCampana) throws EcoCampusException {
		if (idCampana == null || idCampana.trim().isEmpty()) {
			throw new EcoCampusException("El ID de la campaña no es válido.");
		}

		Campana campana = campanas.buscar(idCampana);
		if (campana == null) {
			throw new EcoCampusException("La campaña con ID '" + idCampana + "' no existe.");
		}

		if (campana.getParticipantes() != null && !campana.getParticipantes().isEmpty()) {
			throw new EcoCampusException("No se puede eliminar la campaña porque tiene usuarios inscritos activos.");
		}

		campanas.eliminar(idCampana);
	}

	// --- GETTERS ---
	
	public RepositorioMemoria<Persona> getPersonas() {
		return personas;
	}

	public RepositorioMemoria<PuntoEcologico> getPuntos() {
		return puntos;
	}

	public RepositorioReportes getReportes() {
		return reportes;
	}

	public RepositorioMemoria<RutaRecoleccion> getRutas() {
		return rutas;
	}

	public RepositorioMemoria<Campana> getCampanas() {
		return campanas;
	}
}