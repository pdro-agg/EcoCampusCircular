package co.edu.eia.ecocampus.app;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane; // O el contenedor principal que uses para tu vista
import javafx.stage.Screen;
import javafx.stage.Stage;

public class PruebaJavaFX extends Application {

	@Override
	public void start(Stage ventana) {
		try {
			// Si tu vista principal es un panel o una clase que extiende de Pane/BorderPane:
			// Por ejemplo, instanciando tu panel principal o ventana:
			// Parent root = new VentanaPrincipal(); // O tu contenedor raíz en Java
			
			// Ejemplo genérico con un contenedor raíz:
			BorderPane root = new BorderPane(); 
			// Aquí agregarías tus paneles de la vista (ej: root.setCenter(new PanelResumen());)
			
			Scene scene = new Scene(root);
			ventana.setTitle("EcoCampus Circular");
			ventana.setScene(scene);
			
			// Obtenemos el espacio exacto disponible (excluyendo la barra de tareas)
			Rectangle2D limitesVisuales = Screen.getPrimary().getVisualBounds();
			
			ventana.setX(limitesVisuales.getMinX());
			ventana.setY(limitesVisuales.getMinY());
			ventana.setWidth(limitesVisuales.getWidth());
			ventana.setHeight(limitesVisuales.getHeight());
			
			ventana.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}