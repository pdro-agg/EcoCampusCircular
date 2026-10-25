package co.edu.eia.ecocampus.vista;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.Actividad;
import co.edu.eia.ecocampus.modelo.Campana;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.UsuarioCampus;
import co.edu.eia.ecocampus.persistencia.ConexionBD;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class PanelCampanas extends PanelBase {
	private final BitacoraEventos bitacora;

	private final TextField campoNombre = campo("Ej: Semana del reciclaje");
	private final TextField campoCupo = campo("Ej: 30");

	private final ComboBox<UsuarioCampus> comboUsuario = new ComboBox<>();
	private final TextField campoActividad = campo("Nombre de la actividad");
	private final DatePicker selectorFecha = new DatePicker(LocalDate.now());
	private final TextField campoCupoActividad = campo("Cupo de la actividad");

	private final TableView<Campana> tabla = crearTabla("Aún no hay campañas creadas.");
	private final ListView<String> listaParticipantes = new ListView<>();
	private final ListView<String> listaActividades = new ListView<>();

	public PanelCampanas(BitacoraEventos bitacora) {
		super("Campañas", "Campañas con cupo limitado: no se repiten inscripciones y una campaña cerrada no admite cambios.");
		this.bitacora = bitacora;

		mostrarComo(comboUsuario, u -> u.getNombre() + " (" + u.getId() + ")");

		Button botonCrear = botonPrimario("Crear campaña");
		botonCrear.setOnAction(evento -> crearCampana());
		Button botonInscribir = botonSecundario("Inscribir usuario");
		botonInscribir.setOnAction(evento -> inscribir());
		Button botonActividad = botonSecundario("Agregar actividad");
		botonActividad.setOnAction(evento -> agregarActividad());
		Button botonCerrar = botonPeligro("Cerrar campaña");
		botonCerrar.setOnAction(evento -> cerrarCampana());
		
		Button botonEliminar = botonPeligro("Eliminar (BD)");
		botonEliminar.setOnAction(evento -> eliminarCampanaBD());

		GridPane formularioNueva = formulario();
		agregarFila(formularioNueva, 0, "Nombre", campoNombre);
		agregarFila(formularioNueva, 1, "Cupo", campoCupo);

		GridPane formularioActividad = formulario();
		agregarFila(formularioActividad, 0, "Actividad", campoActividad);
		agregarFila(formularioActividad, 1, "Fecha", selectorFecha);
		agregarFila(formularioActividad, 2, "Cupo", campoCupoActividad);

		comboUsuario.setMaxWidth(Double.MAX_VALUE);
		VBox nueva = tarjeta("Nueva campaña", formularioNueva, botonCrear);
		VBox acciones = tarjeta("Sobre la campaña seleccionada", etiquetaSeccion("Inscripción"), comboUsuario,
				botonInscribir, etiquetaSeccion("Actividad"), formularioActividad, botonActividad,
				etiquetaSeccion("Estado"), new HBox(10, botonCerrar, botonEliminar));
		VBox izquierda = new VBox(18, nueva, acciones);
		izquierda.setPrefWidth(350);
		izquierda.setMinWidth(330);
		izquierda.setMaxHeight(Region.USE_PREF_SIZE);

		tabla.getColumns().add(columna("#", 40.0, (Campana c) -> String.valueOf(c.getNumero())));
		tabla.getColumns().add(columna("Nombre", 170.0, (Campana c) -> c.getNombre()));
		TableColumn<Campana, String> columnaEstado = columna("Estado", 80.0, (Campana c) -> c.getEstado());
		pintarEstados(columnaEstado);
		tabla.getColumns().add(columnaEstado);
		tabla.getColumns().add(columna("Participantes", 110.0, (Campana c) -> c.getTotalParticipantes() + " / " + c.getCupo()));
		tabla.getColumns().add(columna("Actividades", 90.0, (Campana c) -> String.valueOf(c.getActividades().size())));

		listaParticipantes.setPlaceholder(new javafx.scene.control.Label("Sin participantes."));
		listaActividades.setPlaceholder(new javafx.scene.control.Label("Sin actividades."));
		listaParticipantes.setPrefHeight(160);
		listaActividades.setPrefHeight(160);
		VBox participantes = new VBox(8, etiquetaSeccion("Participantes inscritos"), listaParticipantes);
		VBox actividades = new VBox(8, etiquetaSeccion("Actividades"), listaActividades);
		HBox.setHgrow(participantes, Priority.ALWAYS);
		HBox.setHgrow(actividades, Priority.ALWAYS);

		VBox derecha = new VBox(18, tarjeta("Campañas", tabla),
				tarjeta("Detalle de la campaña seleccionada", new HBox(18, participantes, actividades)));
		VBox.setVgrow(tabla, Priority.ALWAYS);

		HBox contenido = new HBox(18, izquierda, derecha);
		HBox.setHgrow(derecha, Priority.ALWAYS);
		getChildren().add(contenido);

		tabla.getSelectionModel().selectedItemProperty().addListener((observable, anterior, nuevo) -> mostrarDetalle());
		actualizar();
	}

	private void eliminarCampanaBD() {
		Campana campana = seleccionada();
		if (campana == null) return;

		Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, 
				"¿Desea eliminar la campaña '" + campana.getNombre() + "' de la BD?");
		confirmacion.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				String sql = "DELETE FROM Campanas WHERE numero = ?";
				try (Connection conn = ConexionBD.obtenerConexion();
					 PreparedStatement pstmt = conn.prepareStatement(sql)) {
					
					pstmt.setInt(1, Integer.parseInt(campana.getNumero()));
					pstmt.executeUpdate();
					
					tabla.getItems().remove(campana);
					ok("Campaña eliminada de la Base de Datos.");
					mostrarDetalle();
				} catch (SQLException ex) {
					error("Error al eliminar la campaña: " + ex.getMessage());
				}
			}
		});
	}

	private void crearCampana() {
		if (vacio(campoNombre, campoCupo)) return;
		int cupo;
		try { cupo = Integer.parseInt(campoCupo.getText().trim()); } 
		catch (NumberFormatException e) { error("El cupo debe ser un número entero."); return; }
		if (cupo <= 0) { error("El cupo debe ser mayor que cero."); return; }
		try {
			Campana campana = new Campana(siguienteNumero(registro.getCampanas().listar()), campoNombre.getText().trim(), cupo);
			registro.getCampanas().registrar(campana);
			campana.agregarObservador(bitacora);
			ok("Campaña " + campana.getNumero() + " creada: " + campana.getNombre() + ".");
			campoNombre.clear();
			campoCupo.clear();
			actualizar();
		} catch (EcoCampusException e) { error(e.getMessage()); }
	}

	private Campana seleccionada() {
		Campana campana = tabla.getSelectionModel().getSelectedItem();
		return falta(campana, "seleccionar una campaña de la tabla") ? null : campana;
	}

	private void inscribir() {
		Campana campana = seleccionada();
		if (campana == null) return;
		UsuarioCampus usuario = comboUsuario.getValue();
		if (falta(usuario, "elegir un usuario")) return;
		try {
			campana.inscribirParticipante(usuario);
			ok(usuario.getNombre() + " inscrito en la campaña " + campana.getNumero() + ".");
		} catch (EcoCampusException e) { error(e.getMessage()); }
		actualizar();
	}

	private void agregarActividad() {
	    Campana campana = seleccionada();
	    if (campana == null) return;
	    if (vacio(campoActividad, campoCupoActividad) || falta(selectorFecha.getValue(), "elegir la fecha")) return;
	    int cupo;
	    
	    try { 
	        cupo = Integer.parseInt(campoCupoActividad.getText().trim()); 
	    } catch (NumberFormatException e) { 
	        error("El cupo de la actividad debe ser un número entero."); 
	        return; 
	    }
	    
	    	    campana.agregarActividad(new Actividad(campoActividad.getText().trim(), selectorFecha.getValue().toString(), cupo));
	    ok("Actividad agregada a la campaña " + campana.getNumero() + ".");
	    campoActividad.clear();
	    campoCupoActividad.clear();
	    
	    actualizar();
	}

	private void cerrarCampana() {
	    Campana campana = seleccionada();
	    if (campana == null) return;
	    
	    try {
	        campana.cerrarCampana();
	        ok("Campaña " + campana.getNumero() + " cerrada.");
	    } catch (EcoCampusException e) {
	        error(e.getMessage());
	    }
	    actualizar();
	}

	private void mostrarDetalle() {
		Campana campana = tabla.getSelectionModel().getSelectedItem();
		listaParticipantes.getItems().clear();
		listaActividades.getItems().clear();
		if (campana == null) return;
		for (UsuarioCampus participante : campana.getParticipantes()) {
			listaParticipantes.getItems().add(participante.getNombre() + " (" + participante.getId() + ")");
		}
		for (Actividad actividad : campana.getActividades()) {
			listaActividades.getItems().add(actividad.getNombre() + " · " + actividad.getFecha() + " · cupo " + actividad.getCupo());
		}
	}

	@Override
	public void actualizar() {
		List<UsuarioCampus> usuarios = new ArrayList<>();
		for (Persona persona : registro.getPersonas().listar()) {
			if (persona instanceof UsuarioCampus) usuarios.add((UsuarioCampus) persona);
		}
		recargarCombo(comboUsuario, usuarios);
		recargar(tabla, registro.getCampanas().listar());
		mostrarDetalle();
	}
}