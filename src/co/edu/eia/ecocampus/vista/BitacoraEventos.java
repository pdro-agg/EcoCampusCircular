package co.edu.eia.ecocampus.vista;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import co.edu.eia.ecocampus.patron.Observador;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * OBSERVER en la interfaz: se registra como observador de reportes, rutas y campañas, y
 * cada aviso que reciben aparece en la lista de eventos (el ultimo arriba).
 */
public class BitacoraEventos implements Observador {
	private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");
	private final ObservableList<String> eventos = FXCollections.observableArrayList();

	@Override
	public void actualizar(String mensaje) {
		eventos.add(0, LocalTime.now().format(HORA) + "   " + mensaje.replace("\n", " · "));
	}

	public ObservableList<String> getEventos() {
		return eventos;
	}
}
