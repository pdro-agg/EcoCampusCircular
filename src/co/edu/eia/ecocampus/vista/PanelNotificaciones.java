package co.edu.eia.ecocampus.vista;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Muestra los avisos que envian los observados (reportes, rutas y campañas) al cambiar de estado. */
public class PanelNotificaciones extends PanelBase {

	public PanelNotificaciones(BitacoraEventos bitacora) {
		super("Notificaciones", "Cada cambio de estado de un reporte, una ruta o una campaña queda registrado aquí.");

		ListView<String> lista = new ListView<>(bitacora.getEventos());
		lista.setPlaceholder(new Label("Aún no hay eventos. Asigna un reporte o cierra una campaña para ver avisos."));
		lista.setMinHeight(380);

		Button botonLimpiar = botonSecundario("Limpiar notificaciones");
		botonLimpiar.setOnAction(evento -> bitacora.getEventos().clear());

		VBox tarjeta = tarjeta("Eventos recientes", lista, botonLimpiar);
		VBox.setVgrow(lista, Priority.ALWAYS);
		getChildren().add(tarjeta);
	}

	@Override
	public void actualizar() {
		// la lista esta enlazada a la bitacora: se actualiza sola
	}
}
