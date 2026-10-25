package co.edu.eia.ecocampus.vista;

import java.util.List;
import java.util.function.Function;

import co.edu.eia.ecocampus.core.Identificable;
import co.edu.eia.ecocampus.repositorio.RegistroCentral;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * Clase base de todas las secciones de la ventana. Aqui vive lo que se repite:
 * titulo, mensaje de exito/error y utilidades para armar formularios y tablas.
 * Cada subclase implementa actualizar(), que vuelve a leer los datos del repositorio.
 */
public abstract class PanelBase extends VBox {
	protected final RegistroCentral registro = RegistroCentral.getInstancia();
	protected final Label mensaje = new Label();

	protected PanelBase(String titulo, String subtitulo) {
		super(18);
		getStyleClass().add("panel");

		Label etiquetaTitulo = new Label(titulo);
		etiquetaTitulo.getStyleClass().add("panel-titulo");
		Label etiquetaSubtitulo = new Label(subtitulo);
		etiquetaSubtitulo.getStyleClass().add("panel-subtitulo");

		mensaje.setWrapText(true);
		mensaje.setMaxWidth(Double.MAX_VALUE);
		// el mensaje solo ocupa espacio cuando tiene texto
		mensaje.visibleProperty().bind(mensaje.textProperty().isNotEmpty());
		mensaje.managedProperty().bind(mensaje.textProperty().isNotEmpty());

		getChildren().addAll(new VBox(2, etiquetaTitulo, etiquetaSubtitulo), mensaje);
	}

	/** Vuelve a leer los datos y refresca tablas y listas. */
	public abstract void actualizar();

	// ---------- mensajes ----------
	protected void ok(String texto) {
		mensaje.getStyleClass().setAll("mensaje", "mensaje-ok");
		mensaje.setText("✓  " + texto);
	}

	protected void error(String texto) {
		mensaje.getStyleClass().setAll("mensaje", "mensaje-error");
		mensaje.setText("✗  " + texto);
	}

	// ---------- validaciones de entrada ----------
	protected boolean vacio(TextField... campos) {
		for (TextField campo : campos) {
			if (campo.getText() == null || campo.getText().trim().isEmpty()) {
				error("Completa todos los campos.");
				return true;
			}
		}
		return false;
	}

	protected boolean falta(Object valor, String accion) {
		if (valor == null) {
			error("Debes " + accion + ".");
			return true;
		}
		return false;
	}

	// ---------- controles ----------
	protected static TextField campo(String sugerencia) {
		TextField campo = new TextField();
		campo.setPromptText(sugerencia);
		return campo;
	}

	private static Button boton(String texto, String estilo) {
		Button boton = new Button(texto);
		boton.getStyleClass().add(estilo);
		return boton;
	}

	protected static Button botonPrimario(String texto) {
		return boton(texto, "boton-primario");
	}

	protected static Button botonSecundario(String texto) {
		return boton(texto, "boton-secundario");
	}

	protected static Button botonPeligro(String texto) {
		return boton(texto, "boton-peligro");
	}

	protected static VBox tarjeta(String titulo, Node... contenido) {
		VBox tarjeta = new VBox(12);
		tarjeta.getStyleClass().add("tarjeta");
		if (titulo != null) {
			Label etiqueta = new Label(titulo);
			etiqueta.getStyleClass().add("tarjeta-titulo");
			tarjeta.getChildren().add(etiqueta);
		}
		tarjeta.getChildren().addAll(contenido);
		return tarjeta;
	}

	protected static GridPane formulario() {
		GridPane grilla = new GridPane();
		grilla.setHgap(12);
		grilla.setVgap(10);
		ColumnConstraints etiquetas = new ColumnConstraints();
		etiquetas.setMinWidth(95);
		ColumnConstraints controles = new ColumnConstraints();
		controles.setHgrow(Priority.ALWAYS);
		grilla.getColumnConstraints().add(etiquetas);
		grilla.getColumnConstraints().add(controles);
		return grilla;
	}

	protected static void agregarFila(GridPane grilla, int numeroFila, String etiqueta, Region control) {
		Label texto = new Label(etiqueta);
		texto.getStyleClass().add("etiqueta");
		control.setMaxWidth(Double.MAX_VALUE);
		grilla.add(texto, 0, numeroFila);
		grilla.add(control, 1, numeroFila);
	}

	protected static Label etiquetaSeccion(String texto) {
		Label etiqueta = new Label(texto);
		etiqueta.getStyleClass().add("etiqueta-seccion");
		return etiqueta;
	}

	// ---------- tablas ----------
	protected static <T> TableView<T> crearTabla(String textoVacio) {
		TableView<T> tabla = new TableView<>();
		tabla.setPlaceholder(new Label(textoVacio));
		tabla.setMinHeight(230);
		return tabla;
	}

	protected static <T> TableColumn<T, String> columna(String titulo, double ancho, Function<T, String> valor) {
		TableColumn<T, String> columna = new TableColumn<>(titulo);
		columna.setCellValueFactory(datos -> new ReadOnlyStringWrapper(valor.apply(datos.getValue())));
		columna.setPrefWidth(ancho);
		return columna;
	}

	/** Colorea el texto de una columna de estado segun su valor (Pendiente, Cerrado, Activo...). */
	protected static <T> void pintarEstados(TableColumn<T, String> columna) {
		columna.setCellFactory(c -> new TableCell<T, String>() {
			@Override
			protected void updateItem(String texto, boolean vacia) {
				super.updateItem(texto, vacia);
				getStyleClass().removeIf(estilo -> estilo.startsWith("estado-"));
				if (vacia || texto == null) {
					setText(null);
				} else {
					setText(texto);
					getStyleClass().add("estado-" + texto.toLowerCase().replace(' ', '-'));
				}
			}
		});
	}

	/** Reemplaza los datos de la tabla y conserva la fila seleccionada si sigue existiendo. */
	protected static <T> void recargar(TableView<T> tabla, List<T> datos) {
		T seleccionado = tabla.getSelectionModel().getSelectedItem();
		tabla.getItems().setAll(datos);
		if (seleccionado != null && datos.contains(seleccionado)) {
			tabla.getSelectionModel().select(seleccionado);
		}
		tabla.refresh();
	}

	// ---------- listas desplegables ----------
	/** Define como se muestra cada elemento del ComboBox (por defecto saldria su toString()). */
	protected static <T> void mostrarComo(ComboBox<T> combo, Function<T, String> texto) {
		combo.setConverter(new StringConverter<T>() {
			@Override
			public String toString(T elemento) {
				return elemento == null ? "" : texto.apply(elemento);
			}

			@Override
			public T fromString(String cadena) {
				return null;
			}
		});
	}

	protected static <T> void recargarCombo(ComboBox<T> combo, List<T> datos) {
		T previo = combo.getValue();
		combo.getItems().setAll(datos);
		combo.setValue(previo != null && datos.contains(previo) ? previo : null);
	}

	// ---------- numeracion ----------
	/** Siguiente numero libre para entidades cuyo id es numerico (reportes, rutas, campañas). */
	protected static int siguienteNumero(List<? extends Identificable> elementos) {
		int mayor = 0;
		for (Identificable elemento : elementos) {
			mayor = Math.max(mayor, Integer.parseInt(elemento.getId()));
		}
		return mayor + 1;
	}
}
