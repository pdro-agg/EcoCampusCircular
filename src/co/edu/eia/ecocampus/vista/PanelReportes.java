package co.edu.eia.ecocampus.vista;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.CategoriaMaterial;
import co.edu.eia.ecocampus.modelo.Operador;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.Recoleccion;
import co.edu.eia.ecocampus.modelo.Reporte;
import co.edu.eia.ecocampus.modelo.RutaRecoleccion;
import co.edu.eia.ecocampus.modelo.UsuarioCampus;
import co.edu.eia.ecocampus.patron.FabricaReportes;
import co.edu.eia.ecocampus.servicio.ConsultaReportes;
import co.edu.eia.ecocampus.util.Materiales;
import co.edu.eia.ecocampus.persistencia.ConexionBD;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class PanelReportes extends PanelBase {
	private final BitacoraEventos bitacora;

	private final ComboBox<UsuarioCampus> comboAutor = new ComboBox<>();
	private final ComboBox<PuntoEcologico> comboPunto = new ComboBox<>();
	private final ComboBox<String> comboTipo = new ComboBox<>();
	private final TextArea areaDescripcion = new TextArea();
	private final DatePicker selectorFecha = new DatePicker(LocalDate.now());

	private final TableView<Reporte> tabla = crearTabla("Aún no hay reportes registrados.");
	private final CheckBox soloPendientes = new CheckBox("Solo pendientes, ordenados por prioridad");

	private final Label etiquetaSeleccion = new Label();
	private final ComboBox<Operador> comboOperador = new ComboBox<>();
	private final ComboBox<RutaRecoleccion> comboRuta = new ComboBox<>();
	private final ComboBox<CategoriaMaterial> comboMaterial = new ComboBox<>();
	private final TextField campoPeso = campo("Peso en kg (ej: 12.5)");
	private final TextArea areaHistorial = new TextArea();

	public PanelReportes(BitacoraEventos bitacora) {
		super("Reportes", "Incidencias en los puntos ecológicos: se crean, se asignan y se cierran con una recolección.");
		this.bitacora = bitacora;

		mostrarComo(comboAutor, u -> u.getNombre() + " (" + u.getId() + ")");
		mostrarComo(comboPunto, p -> p.getCodigo() + " · " + p.getUbicacion()
				+ ("Activo".equals(p.getEstado()) ? "" : "  [Inactivo]"));
		mostrarComo(comboOperador, o -> o.getNombre() + " (" + o.getIdOperador() + ")"
				+ (o.getDisponible() ? "" : "  [Ocupado]"));
		mostrarComo(comboRuta, r -> "Ruta " + r.getNumero() + " (" + r.getEstado() + ")");
		mostrarComo(comboMaterial, c -> c.getNombre());
		comboTipo.getItems().addAll(FabricaReportes.DESBORDAMIENTO, FabricaReportes.CONTAMINACION,
				FabricaReportes.MATERIAL_ESPECIAL);
		comboTipo.setValue(FabricaReportes.DESBORDAMIENTO);
		comboMaterial.getItems().addAll(Materiales.todos());
		comboMaterial.setValue(Materiales.PLASTICO);

		areaDescripcion.setPrefRowCount(3);
		areaDescripcion.setWrapText(true);
		areaDescripcion.setPromptText("¿Qué está pasando en el punto?");
		areaHistorial.setEditable(false);
		areaHistorial.setPrefRowCount(5);
		areaHistorial.setWrapText(true);

		getChildren().add(new HBox(18, crearTarjetaFormulario(), crearTarjetaTabla()));
		getChildren().add(crearTarjetaAcciones());

		tabla.getSelectionModel().selectedItemProperty().addListener((observable, anterior, nuevo) -> mostrarSeleccion());
		soloPendientes.selectedProperty().addListener((observable, anterior, nuevo) -> actualizar());

		actualizar();
	}

	private VBox crearTarjetaFormulario() {
		GridPane formulario = formulario();
		agregarFila(formulario, 0, "Autor", comboAutor);
		agregarFila(formulario, 1, "Punto", comboPunto);
		agregarFila(formulario, 2, "Tipo", comboTipo);
		agregarFila(formulario, 3, "Fecha", selectorFecha);
		agregarFila(formulario, 4, "Descripción", areaDescripcion);

		Button botonCrear = botonPrimario("Crear reporte");
		botonCrear.setOnAction(evento -> crearReporte());

		VBox tarjeta = tarjeta("Nuevo reporte", formulario, botonCrear);
		tarjeta.setPrefWidth(360);
		tarjeta.setMinWidth(340);
		tarjeta.setMaxHeight(Region.USE_PREF_SIZE);
		return tarjeta;
	}

	private VBox crearTarjetaTabla() {
		tabla.getColumns().add(columna("#", 40.0, (Reporte r) -> String.valueOf(r.getNumero())));
		tabla.getColumns().add(columna("Tipo", 120.0, (Reporte r) -> r.getTipo()));
		tabla.getColumns().add(columna("Prioridad", 80.0, (Reporte r) -> r.getPrioridad()));
		TableColumn<Reporte, String> columnaEstado = columna("Estado", 90.0, (Reporte r) -> r.getEstado());
		pintarEstados(columnaEstado);
		tabla.getColumns().add(columnaEstado);
		tabla.getColumns().add(columna("Punto", 70.0, (Reporte r) -> r.getPuntoEcologico().getCodigo()));
		tabla.getColumns().add(columna("Autor", 120.0, (Reporte r) -> r.getAutor().getNombre()));
		tabla.getColumns().add(columna("Operador", 120.0,
		        (Reporte r) -> r.getOperadorAsignado() == null ? "—" : r.getOperadorAsignado().getNombre()));

		VBox tarjeta = tarjeta("Reportes registrados", soloPendientes, tabla);
		VBox.setVgrow(tabla, Priority.ALWAYS);
		HBox.setHgrow(tarjeta, Priority.ALWAYS);
		return tarjeta;
	}

	private VBox crearTarjetaAcciones() {
		etiquetaSeleccion.getStyleClass().add("etiqueta-seccion");

		Button botonOperador = botonSecundario("Asignar operador");
		botonOperador.setOnAction(evento -> asignarOperador());
		Button botonRuta = botonSecundario("Asignar ruta");
		botonRuta.setOnAction(evento -> asignarRuta());
		Button botonCerrar = botonPrimario("Cerrar con recolección");
		botonCerrar.setOnAction(evento -> cerrarReporte());
		Button botonReabrir = botonSecundario("Reabrir reporte");
		botonReabrir.setOnAction(evento -> reabrirReporte());

		Button botonEliminar = botonPeligro("Eliminar Reporte (BD)");
		botonEliminar.setOnAction(evento -> eliminarReporteBD());

		VBox asignacion = new VBox(8, etiquetaSeccion("1. Asignación"), comboOperador, botonOperador, comboRuta, botonRuta);
		VBox cierre = new VBox(8, etiquetaSeccion("2. Cierre"), new Label("Operador: el elegido en la asignación"),
				comboMaterial, campoPeso, botonCerrar, botonReabrir);
		VBox historial = new VBox(8, etiquetaSeccion("Historial de estados"), areaHistorial, botonEliminar);
		
		for (VBox columna : new VBox[] { asignacion, cierre, historial }) {
			HBox.setHgrow(columna, Priority.ALWAYS);
			columna.setMinWidth(220);
		}
		comboOperador.setMaxWidth(Double.MAX_VALUE);
		comboRuta.setMaxWidth(Double.MAX_VALUE);
		comboMaterial.setMaxWidth(Double.MAX_VALUE);
		botonEliminar.setMaxWidth(Double.MAX_VALUE);
		VBox.setVgrow(areaHistorial, Priority.ALWAYS);

		return tarjeta("Acciones sobre el reporte seleccionado", etiquetaSeleccion,
				new HBox(24, asignacion, cierre, historial));
	}

	private void eliminarReporteBD() {
		Reporte reporte = seleccionado();
		if (reporte == null) return;

		Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, 
				"¿Desea eliminar de la base de datos el reporte #" + reporte.getNumero() + "?");
		confirmacion.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				String sql = "DELETE FROM Reportes WHERE numero = ?";
				try (Connection conn = ConexionBD.obtenerConexion();
					 PreparedStatement pstmt = conn.prepareStatement(sql)) {
					
					pstmt.setInt(1, reporte.getNumero());
					pstmt.executeUpdate();
					
					tabla.getItems().remove(reporte);
					ok("Reporte eliminado correctamente de la Base de Datos.");
					mostrarSeleccion();
				} catch (SQLException ex) {
					error("No se pudo eliminar de la BD: " + ex.getMessage());
				}
			}
		});
	}

	private void crearReporte() {
		UsuarioCampus autor = comboAutor.getValue();
		PuntoEcologico punto = comboPunto.getValue();
		if (falta(autor, "elegir quién reporta") || falta(punto, "elegir el punto ecológico")) return;
		if (selectorFecha.getValue() == null || areaDescripcion.getText().trim().isEmpty()) {
			error("Escribe una descripción y elige la fecha."); return;
		}
		try {
			int numero = siguienteNumero(registro.getReportes().listar());
			Reporte reporte = FabricaReportes.crear(comboTipo.getValue(), numero, autor, punto,
					areaDescripcion.getText().trim(), selectorFecha.getValue().toString());
			registro.getReportes().registrarSiEsValido(reporte);
			reporte.agregarObservador(bitacora);
			ok("Reporte #" + numero + " creado con prioridad " + reporte.getPrioridad() + ".");
			areaDescripcion.clear();
			actualizar();
		} catch (EcoCampusException e) {
			error(e.getMessage());
		}
	}

	private Reporte seleccionado() {
		Reporte reporte = tabla.getSelectionModel().getSelectedItem();
		return falta(reporte, "seleccionar un reporte de la tabla") ? null : reporte;
	}

	private void asignarOperador() {
		Reporte reporte = seleccionado();
		if (reporte == null) return;
		Operador operador = comboOperador.getValue();
		if (falta(operador, "elegir un operador")) return;
		try {
			reporte.asignarOperador(operador);
			ok("Reporte #" + reporte.getNumero() + " asignado a " + operador.getNombre() + ".");
		} catch (EcoCampusException e) { error(e.getMessage()); }
		actualizar();
	}

	private void asignarRuta() {
		Reporte reporte = seleccionado();
		if (reporte == null) return;
		RutaRecoleccion ruta = comboRuta.getValue();
		if (falta(ruta, "elegir una ruta")) return;
		try {
			reporte.asignarRuta(ruta);
			ok("Reporte #" + reporte.getNumero() + " asignado a la ruta " + ruta.getNumero() + ".");
		} catch (EcoCampusException e) { error(e.getMessage()); }
		actualizar();
	}

	private void cerrarReporte() {
		Reporte reporte = seleccionado();
		if (reporte == null) return;
		Operador operador = comboOperador.getValue();
		CategoriaMaterial material = comboMaterial.getValue();
		if (falta(operador, "elegir el operador que hizo la recolección") || falta(material, "elegir el material recolectado")) return;
		double peso;
		try { peso = Double.parseDouble(campoPeso.getText().trim().replace(',', '.')); } 
		catch (NumberFormatException e) { error("El peso debe ser un número (ej: 12.5)."); return; }
		try {
			Recoleccion recoleccion = new Recoleccion(reporte.getNumero(), LocalDate.now().toString(), operador);
			recoleccion.agregarMaterial(material, peso);
			reporte.cerrar(recoleccion);
			ok("Reporte #" + reporte.getNumero() + " cerrado con " + peso + " kg de " + material.getNombre() + ".");
			campoPeso.clear();
		} catch (EcoCampusException e) { error(e.getMessage()); }
		actualizar();
	}

	private void reabrirReporte() {
		Reporte reporte = seleccionado();
		if (reporte == null) return;
		try {
			reporte.reabrir();
			ok("Reporte #" + reporte.getNumero() + " reabierto.");
		} catch (EcoCampusException e) { error(e.getMessage()); }
		actualizar();
	}

	private void mostrarSeleccion() {
		Reporte reporte = tabla.getSelectionModel().getSelectedItem();
		if (reporte == null) {
			etiquetaSeleccion.setText("Selecciona un reporte de la tabla para operar sobre él.");
			areaHistorial.clear();
			return;
		}
		etiquetaSeleccion.setText("Reporte #" + reporte.getNumero() + " · " + reporte.getTipo() + " · " + reporte.getEstado() + " — " + reporte.getDescripcion());
		List<String> registros = reporte.getHistorial();
		areaHistorial.setText(registros.isEmpty() ? "Sin cambios de estado todavía." : String.join("\n", registros));
	}

	@Override
	public void actualizar() {
		List<UsuarioCampus> usuarios = new ArrayList<>();
		List<Operador> operadores = new ArrayList<>();
		for (Persona persona : registro.getPersonas().listar()) {
			if (persona instanceof UsuarioCampus) usuarios.add((UsuarioCampus) persona);
			else if (persona instanceof Operador) operadores.add((Operador) persona);
		}
		recargarCombo(comboAutor, usuarios);
		recargarCombo(comboPunto, registro.getPuntos().listar());
		recargarCombo(comboOperador, operadores);
		recargarCombo(comboRuta, registro.getRutas().listar());

		List<Reporte> datos = soloPendientes.isSelected()
				? ConsultaReportes.pendientesPorPrioridad(registro.getReportes())
				: registro.getReportes().listar();
		tabla.setPlaceholder(new Label(soloPendientes.isSelected() ? "No hay reportes pendientes." : "Aún no hay reportes registrados."));
		recargar(tabla, datos);
		mostrarSeleccion();
	}
}