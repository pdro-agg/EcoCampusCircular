package co.edu.eia.ecocampus.vista;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.Parada;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.RutaRecoleccion;
import co.edu.eia.ecocampus.persistencia.ConexionBD;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class PanelRutas extends PanelBase {
	private final BitacoraEventos bitacora;
	private final ComboBox<PuntoEcologico> comboPunto = new ComboBox<>();
	private final TextField campoAccion = campo("Ej: Vaciar contenedor");
	private final TableView<RutaRecoleccion> tablaRutas = crearTabla("Aún no hay rutas creadas.");
	private final TableView<Parada> tablaParadas = crearTabla("Selecciona una ruta para ver sus paradas.");

	public PanelRutas(BitacoraEventos bitacora) {
		super("Rutas de recolección", "Una ruta agrupa paradas en puntos ecológicos activos. Una ruta cerrada no admite paradas.");
		this.bitacora = bitacora;

		mostrarComo(comboPunto, p -> p.getCodigo() + " · " + p.getUbicacion()
				+ ("Activo".equals(p.getEstado()) ? "" : "  [Inactivo]"));
		comboPunto.setMaxWidth(Double.MAX_VALUE);

		Button botonCrear = botonPrimario("Crear nueva ruta");
		botonCrear.setOnAction(evento -> crearRuta());
		Button botonParada = botonSecundario("Agregar parada a la ruta seleccionada");
		botonParada.setOnAction(evento -> agregarParada());
		Button botonCerrar = botonPeligro("Cerrar ruta");
		botonCerrar.setOnAction(evento -> cerrarRuta());
		Button botonAbrir = botonSecundario("Reabrir ruta");
		botonAbrir.setOnAction(evento -> abrirRuta());

		Button botonEliminar = botonPeligro("Eliminar (BD)");
		botonEliminar.setOnAction(evento -> eliminarRutaBD());

		VBox izquierda = tarjeta("Gestión", botonCrear, etiquetaSeccion("Nueva parada"), comboPunto, campoAccion,
				botonParada, etiquetaSeccion("Estado de la ruta"), new HBox(10, botonCerrar, botonAbrir, botonEliminar));
		izquierda.setPrefWidth(350);
		izquierda.setMinWidth(330);
		izquierda.setMaxHeight(Region.USE_PREF_SIZE);

		tablaRutas.getColumns().add(columna("#", 50, (RutaRecoleccion r) -> String.valueOf(r.getNumero())));
		TableColumn<RutaRecoleccion, String> columnaEstado = columna("Estado", 100, (RutaRecoleccion r) -> r.getEstado());
		pintarEstados(columnaEstado);
		tablaRutas.getColumns().add(columnaEstado);
		tablaRutas.getColumns().add(columna("Paradas", 80, (RutaRecoleccion r) -> String.valueOf(r.getTotalParadas())));
		tablaRutas.setMinHeight(150);
		tablaRutas.setPrefHeight(180);

		tablaParadas.getColumns().add(columna("Orden", 60, (Parada p) -> String.valueOf(p.getOrden())));
		tablaParadas.getColumns().add(columna("Punto", 90, (Parada p) -> p.getPuntoEcologico().getCodigo()));
		tablaParadas.getColumns().add(columna("Ubicación", 150, (Parada p) -> p.getPuntoEcologico().getUbicacion()));
		tablaParadas.getColumns().add(columna("Acción esperada", 220, (Parada p) -> p.getAccionEsperada()));
		tablaParadas.setMinHeight(150);

		VBox derecha = new VBox(18, tarjeta("Rutas", tablaRutas), tarjeta("Paradas de la ruta seleccionada", tablaParadas));
		VBox.setVgrow(tablaParadas, Priority.ALWAYS);

		HBox contenido = new HBox(18, izquierda, derecha);
		HBox.setHgrow(derecha, Priority.ALWAYS);
		getChildren().add(contenido);

		tablaRutas.getSelectionModel().selectedItemProperty().addListener((observable, anterior, nuevo) -> mostrarParadas());
		actualizar();
	}

	private void eliminarRutaBD() {
		RutaRecoleccion ruta = seleccionada();
		if (ruta == null) return;

		Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, 
				"¿Desea eliminar la ruta #" + ruta.getNumero() + " de la base de datos?");
		confirmacion.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				String sql = "DELETE FROM Rutas WHERE numero = ?";
				try (Connection conn = ConexionBD.obtenerConexion();
					 PreparedStatement pstmt = conn.prepareStatement(sql)) {
					
					pstmt.setInt(1, ruta.getNumero());
					pstmt.executeUpdate();
					
					tablaRutas.getItems().remove(ruta);
					ok("Ruta eliminada de la Base de Datos.");
					mostrarParadas();
				} catch (SQLException ex) {
					error("Error al eliminar la ruta de la BD: " + ex.getMessage());
				}
			}
		});
	}

	private void crearRuta() {
		try {
			RutaRecoleccion ruta = new RutaRecoleccion(siguienteNumero(registro.getRutas().listar()));
			registro.getRutas().registrar(ruta);
			ruta.agregarObservador(bitacora);
			ok("Ruta " + ruta.getNumero() + " creada.");
			actualizar();
		} catch (EcoCampusException e) { error(e.getMessage()); }
	}

	private RutaRecoleccion seleccionada() {
		RutaRecoleccion ruta = tablaRutas.getSelectionModel().getSelectedItem();
		return falta(ruta, "seleccionar una ruta de la tabla") ? null : ruta;
	}

	private void agregarParada() {
		RutaRecoleccion ruta = seleccionada();
		if (ruta == null) return;
		PuntoEcologico punto = comboPunto.getValue();
		if (falta(punto, "elegir un punto ecológico") || vacio(campoAccion)) return;
		try {
			ruta.agregarParada(new Parada(ruta.getTotalParadas() + 1, punto, campoAccion.getText().trim()));
			ok("Parada agregada a la ruta " + ruta.getNumero() + ".");
			campoAccion.clear();
		} catch (EcoCampusException e) { error(e.getMessage()); }
		actualizar();
	}

	private void cerrarRuta() {
		RutaRecoleccion ruta = seleccionada();
		if (ruta == null) return;
		try {
			ruta.cerrarRuta();
			ok("Ruta " + ruta.getNumero() + " cerrada.");
		} catch (EcoCampusException e) { error(e.getMessage()); }
		actualizar();
	}

	private void abrirRuta() {
		RutaRecoleccion ruta = seleccionada();
		if (ruta == null) return;
		try {
			ruta.abrirRuta();
			ok("Ruta " + ruta.getNumero() + " reabierta.");
		} catch (EcoCampusException e) { error(e.getMessage()); }
		actualizar();
	}

	private void mostrarParadas() {
		RutaRecoleccion ruta = tablaRutas.getSelectionModel().getSelectedItem();
		List<Parada> paradas = ruta == null ? new ArrayList<Parada>() : ruta.getParadas();
		tablaParadas.setPlaceholder(new Label(ruta == null ? "Selecciona una ruta para ver sus paradas." : "Esta ruta todavía no tiene paradas."));
		tablaParadas.getItems().setAll(paradas);
	}

	@Override
	public void actualizar() {
		recargarCombo(comboPunto, registro.getPuntos().listar());
		recargar(tablaRutas, registro.getRutas().listar());
		mostrarParadas();
	}
}