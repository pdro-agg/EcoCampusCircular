package co.edu.eia.ecocampus.app;

import java.net.URL;

import co.edu.eia.ecocampus.persistencia.ConexionBD;
import co.edu.eia.ecocampus.vista.BitacoraEventos;
import co.edu.eia.ecocampus.vista.PanelBase;
import co.edu.eia.ecocampus.vista.PanelCampanas;
import co.edu.eia.ecocampus.vista.PanelEcoPuntos;
import co.edu.eia.ecocampus.vista.PanelNotificaciones;
import co.edu.eia.ecocampus.vista.PanelOperadores;
import co.edu.eia.ecocampus.vista.PanelPuntos;
import co.edu.eia.ecocampus.vista.PanelReportes;
import co.edu.eia.ecocampus.vista.PanelResumen;
import co.edu.eia.ecocampus.vista.PanelRutas;
import co.edu.eia.ecocampus.vista.PanelUsuarios;
import javafx.application.Application;
import javafx.geometry.Rectangle2D; // <--- Importante para los límites visuales
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen; // <--- Importante para obtener la pantalla
import javafx.stage.Stage;

/**
 * Punto de entrada de la interfaz grafica. Arma una barra lateral de navegacion y muestra
 * en el centro la seccion elegida. La logica de negocio no esta aqui: esta en el modelo.
 */
public class VentanaPrincipal extends Application {
	private final BitacoraEventos bitacora = new BitacoraEventos();
	private final ScrollPane zonaCentral = new ScrollPane();

	@Override
	public void start(Stage ventana) {
		// Inicializa la base de datos local (SQLite/SQL Server) y sus tablas si no existen
		ConexionBD.inicializarTablas();

		zonaCentral.setFitToWidth(true);

		VBox barraLateral = new VBox(6);
		barraLateral.getStyleClass().add("barra-lateral");
		barraLateral.setPrefWidth(230);
		barraLateral.setMinWidth(230);

		Label marca = new Label("EcoCampus Circular");
		marca.getStyleClass().add("marca-titulo");
		VBox cabecera = new VBox(marca);
		cabecera.setStyle("-fx-padding: 0 0 18 6;");
		barraLateral.getChildren().add(cabecera);

		ToggleGroup grupo = new ToggleGroup();
		// una seccion por flujo del sistema
		agregarSeccion(barraLateral, grupo, "Resumen", new PanelResumen(bitacora));
		agregarSeccion(barraLateral, grupo, "Usuarios", new PanelUsuarios());
		agregarSeccion(barraLateral, grupo, "Operadores", new PanelOperadores());
		agregarSeccion(barraLateral, grupo, "Puntos ecológicos", new PanelPuntos());
		agregarSeccion(barraLateral, grupo, "Reportes", new PanelReportes(bitacora));
		agregarSeccion(barraLateral, grupo, "Rutas", new PanelRutas(bitacora));
		agregarSeccion(barraLateral, grupo, "Campañas", new PanelCampanas(bitacora));
		agregarSeccion(barraLateral, grupo, "Eco-puntos", new PanelEcoPuntos());
		agregarSeccion(barraLateral, grupo, "Notificaciones", new PanelNotificaciones(bitacora));

		// el grupo siempre debe tener una seccion elegida
		grupo.selectedToggleProperty().addListener((observable, anterior, actual) -> {
			if (actual == null) {
				anterior.setSelected(true);
			} else {
				PanelBase panel = (PanelBase) actual.getUserData();
				panel.actualizar();
				zonaCentral.setContent(panel);
				zonaCentral.setVvalue(0);
			}
		});
		grupo.getToggles().get(0).setSelected(true);

		BorderPane raiz = new BorderPane();
		raiz.setLeft(barraLateral);
		raiz.setCenter(zonaCentral);

		// Creamos la escena sin medidas fijas estrictas para que se adapte al contenedor
		Scene escena = new Scene(raiz);
		URL estilos = getClass().getResource("/co/edu/eia/ecocampus/vista/estilos.css");
		if (estilos != null) {
			escena.getStylesheets().add(estilos.toExternalForm());
		}

		ventana.setTitle("EcoCampus Circular");
		ventana.setMinWidth(1050);
		ventana.setMinHeight(680);
		ventana.setScene(escena);

		// =========================================================================
		// AJUSTE DE PANTALLA COMPLETA RESPETANDO LA BARRA DE TAREAS DE WINDOWS
		// =========================================================================
		Rectangle2D limitesVisuales = Screen.getPrimary().getVisualBounds();
		ventana.setX(limitesVisuales.getMinX());
		ventana.setY(limitesVisuales.getMinY());
		ventana.setWidth(limitesVisuales.getWidth());
		ventana.setHeight(limitesVisuales.getHeight());
		// =========================================================================

		ventana.show();
	}

	private void agregarSeccion(VBox barra, ToggleGroup grupo, String titulo, PanelBase panel) {
		ToggleButton boton = new ToggleButton(titulo);
		boton.getStyleClass().add("nav-boton");
		boton.setMaxWidth(Double.MAX_VALUE);
		boton.setToggleGroup(grupo);
		boton.setUserData(panel);
		barra.getChildren().add(boton);
	}

	public static void main(String[] args) {
		launch(args);
	}
}