package co.edu.eia.ecocampus.vista;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.CategoriaMaterial;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.util.Materiales;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class PanelPuntos extends PanelBase {
	private final TextField campoCodigo = campo("Ej: PE-01");
	private final TextField campoUbicacion = campo("Ej: Biblioteca");
	private final TextField campoCapacidad = campo("Ej: 80");
	// Map: cada categoria con su casilla, para recorrerlas sin repetir codigo
	private final Map<CategoriaMaterial, CheckBox> casillas = new LinkedHashMap<>();
	private final TableView<PuntoEcologico> tabla = crearTabla("Aún no hay puntos ecológicos registrados.");

	public PanelPuntos() {
		super("Puntos ecológicos", "Contenedores del campus. Un punto inactivo no recibe reportes ni rutas.");

		Button botonRegistrar = botonPrimario("Registrar punto");
		botonRegistrar.setOnAction(evento -> registrarPunto());

		GridPane formulario = formulario();
		agregarFila(formulario, 0, "Código", campoCodigo);
		agregarFila(formulario, 1, "Ubicación", campoUbicacion);
		agregarFila(formulario, 2, "Capacidad", campoCapacidad);

		VBox izquierda = tarjeta("Nuevo punto", formulario, etiquetaSeccion("Categorías que admite"));
		for (CategoriaMaterial categoria : Materiales.todos()) {
			CheckBox casilla = new CheckBox(categoria.getNombre());
			casillas.put(categoria, casilla);
			izquierda.getChildren().add(casilla);
		}
		izquierda.getChildren().add(botonRegistrar);
		izquierda.setPrefWidth(350);
		izquierda.setMinWidth(330);
		izquierda.setMaxHeight(Region.USE_PREF_SIZE);

		tabla.getColumns().add(columna("Código", 80, (PuntoEcologico p) -> p.getCodigo()));
		tabla.getColumns().add(columna("Ubicación", 140, (PuntoEcologico p) -> p.getUbicacion()));
		tabla.getColumns().add(columna("Capacidad", 80, (PuntoEcologico p) -> String.valueOf(p.getCapacidad())));
		TableColumn<PuntoEcologico, String> columnaEstado = columna("Estado", 80,
				(PuntoEcologico p) -> p.getEstado());
		pintarEstados(columnaEstado);
		tabla.getColumns().add(columnaEstado);
		tabla.getColumns().add(columna("Categorías", 220, (PuntoEcologico p) -> categoriasComoTexto(p)));

		Button botonActivar = botonSecundario("Activar");
		botonActivar.setOnAction(evento -> cambiarEstado(true));
		Button botonDesactivar = botonPeligro("Desactivar");
		botonDesactivar.setOnAction(evento -> cambiarEstado(false));

		VBox derecha = tarjeta("Puntos registrados", tabla, new HBox(10, botonActivar, botonDesactivar));
		VBox.setVgrow(tabla, Priority.ALWAYS);

		HBox contenido = new HBox(18, izquierda, derecha);
		HBox.setHgrow(derecha, Priority.ALWAYS);
		getChildren().add(contenido);

		actualizar();
	}

	private static String categoriasComoTexto(PuntoEcologico punto) {
		List<String> nombres = new ArrayList<>();
		for (CategoriaMaterial categoria : punto.getCategorias()) {
			nombres.add(categoria.getNombre());
		}
		java.util.Collections.sort(nombres);
		return nombres.isEmpty() ? "—" : String.join(", ", nombres);
	}

	private void registrarPunto() {
		if (vacio(campoCodigo, campoUbicacion, campoCapacidad)) {
			return;
		}
		int capacidad;
		try {
			capacidad = Integer.parseInt(campoCapacidad.getText().trim());
		} catch (NumberFormatException e) {
			error("La capacidad debe ser un número entero.");
			return;
		}
		if (capacidad <= 0) {
			error("La capacidad debe ser mayor que cero.");
			return;
		}
		PuntoEcologico punto = new PuntoEcologico(campoCodigo.getText().trim(), campoUbicacion.getText().trim(),
				capacidad);
		for (Map.Entry<CategoriaMaterial, CheckBox> entrada : casillas.entrySet()) {
			if (entrada.getValue().isSelected()) {
				punto.agregarCategoria(entrada.getKey());
			}
		}
		try {
			registro.getPuntos().registrar(punto);
			ok("Punto registrado: " + punto.getCodigo());
			campoCodigo.clear();
			campoUbicacion.clear();
			campoCapacidad.clear();
			for (CheckBox casilla : casillas.values()) {
				casilla.setSelected(false);
			}
			actualizar();
		} catch (EcoCampusException e) {
			error(e.getMessage());
		}
	}

	private void cambiarEstado(boolean activar) {
		PuntoEcologico punto = tabla.getSelectionModel().getSelectedItem();
		if (falta(punto, "seleccionar un punto de la tabla")) {
			return;
		}
		if (activar) {
			punto.activar();
			ok("Punto " + punto.getCodigo() + " activado.");
		} else {
			punto.desactivar();
			ok("Punto " + punto.getCodigo() + " desactivado.");
		}
		actualizar();
	}

	@Override
	public void actualizar() {
		recargar(tabla, registro.getPuntos().listar());
	}
}
