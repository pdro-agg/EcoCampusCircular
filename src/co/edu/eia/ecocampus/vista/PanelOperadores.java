package co.edu.eia.ecocampus.vista;

import java.util.ArrayList;
import java.util.List;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.Operador;
import co.edu.eia.ecocampus.modelo.Persona;
// --- IMPORTACIONES AÑADIDAS PARA ELIMINAR ---
import co.edu.eia.ecocampus.persistencia.EliminacionDAO;
import co.edu.eia.ecocampus.persistencia.PersistenciaException;
// --------------------------------------------
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

public class PanelOperadores extends PanelBase {
	private final TextField campoId = campo("Ej: 2001");
	private final TextField campoNombre = campo("Nombre completo");
	private final TextField campoCorreo = campo("correo@eia.edu.co");
	private final TextField campoCodigo = campo("Ej: OP-01");
	private final CheckBox cajaEspeciales = new CheckBox("Habilitado para residuos especiales");
	private final TableView<Operador> tabla = crearTabla("Aún no hay operadores registrados.");

	// --- DAO AÑADIDO ---
	private final EliminacionDAO eliminacionDAO = new EliminacionDAO();

	public PanelOperadores() {
		super("Operadores", "Personal que realiza las recolecciones. Solo un operador habilitado maneja residuos especiales.");

		Button botonRegistrar = botonPrimario("Registrar operador");
		botonRegistrar.setOnAction(evento -> registrarOperador());

		GridPane formulario = formulario();
		agregarFila(formulario, 0, "Identificación", campoId);
		agregarFila(formulario, 1, "Nombre", campoNombre);
		agregarFila(formulario, 2, "Correo", campoCorreo);
		agregarFila(formulario, 3, "Código", campoCodigo);

		VBox izquierda = tarjeta("Nuevo operador", formulario, cajaEspeciales, botonRegistrar);
		izquierda.setPrefWidth(350);
		izquierda.setMinWidth(330);
		izquierda.setMaxHeight(Region.USE_PREF_SIZE);

		tabla.getColumns().add(columna("Identificación", 100, (Operador o) -> o.getId()));
		tabla.getColumns().add(columna("Nombre", 150, (Operador o) -> o.getNombre()));
		tabla.getColumns().add(columna("Código", 80, (Operador o) -> o.getIdOperador()));
		TableColumn<Operador, String> columnaDisponible = columna("Estado", 90,
				(Operador o) -> o.getDisponible() ? "Disponible" : "Ocupado");
		pintarEstados(columnaDisponible);
		tabla.getColumns().add(columnaDisponible);
		tabla.getColumns().add(columna("Residuos especiales", 130,
				(Operador o) -> o.getHabilitadoResiduosEspeciales() ? "Habilitado" : "No habilitado"));

		Button botonDisponibilidad = botonSecundario("Cambiar disponibilidad");
		botonDisponibilidad.setOnAction(evento -> cambiarDisponibilidad());
		Button botonHabilitar = botonSecundario("Habilitar residuos especiales");
		botonHabilitar.setOnAction(evento -> habilitarEspeciales());

		// --- BOTÓN ELIMINAR AÑADIDO ---
		Button botonEliminar = botonPeligro("Eliminar operador");
		botonEliminar.setOnAction(evento -> eliminarOperadorSeleccionado());

		// --- SE AÑADIÓ botonEliminar AL HBox ---
		VBox derecha = tarjeta("Operadores registrados", tabla,
				new HBox(10, botonDisponibilidad, botonHabilitar, botonEliminar));
		VBox.setVgrow(tabla, Priority.ALWAYS);

		HBox contenido = new HBox(18, izquierda, derecha);
		HBox.setHgrow(derecha, Priority.ALWAYS);
		getChildren().add(contenido);

		actualizar();
	}

	private void registrarOperador() {
		if (vacio(campoId, campoNombre, campoCorreo, campoCodigo)) {
			return;
		}
		Operador operador = new Operador(campoId.getText().trim(), campoNombre.getText().trim(),
				campoCorreo.getText().trim(), campoCodigo.getText().trim());
		if (cajaEspeciales.isSelected()) {
			operador.habilitarResiduosEspeciales();
		}
		try {
			registro.getPersonas().registrar(operador);
			ok("Operador registrado: " + operador.getNombre());
			campoId.clear();
			campoNombre.clear();
			campoCorreo.clear();
			campoCodigo.clear();
			cajaEspeciales.setSelected(false);
			actualizar();
		} catch (EcoCampusException e) {
			error(e.getMessage());
		}
	}

	private void cambiarDisponibilidad() {
		Operador operador = tabla.getSelectionModel().getSelectedItem();
		if (falta(operador, "seleccionar un operador de la tabla")) {
			return;
		}
		if (operador.getDisponible()) {
			operador.ocupar();
			ok(operador.getNombre() + " ahora está ocupado.");
		} else {
			operador.desocupar();
			ok(operador.getNombre() + " ahora está disponible.");
		}
		actualizar();
	}

	private void habilitarEspeciales() {
		Operador operador = tabla.getSelectionModel().getSelectedItem();
		if (falta(operador, "seleccionar un operador de la tabla")) {
			return;
		}
		operador.habilitarResiduosEspeciales();
		ok(operador.getNombre() + " quedó habilitado para residuos especiales.");
		actualizar();
	}

	// --- MÉTODO AÑADIDO PARA LA ELIMINACIÓN ---
	private void eliminarOperadorSeleccionado() {
		Operador operador = tabla.getSelectionModel().getSelectedItem();
		if (falta(operador, "seleccionar un operador de la tabla para eliminar")) {
			return;
		}
		try {
			// Borra de la base de datos
			eliminacionDAO.eliminarOperador(operador.getId());
			
			// Intenta borrar de la memoria local para que desaparezca de la tabla inmediatamente
			try {
				registro.getPersonas().eliminar(operador.getId());
			} catch (Exception ignored) { }
			
			ok("Operador «" + operador.getNombre() + "» eliminado correctamente.");
			actualizar();
		} catch (PersistenciaException e) {
			error("Error al eliminar el operador de la BD: " + e.getMessage());
		}
	}

	@Override
	public void actualizar() {
		List<Operador> datos = new ArrayList<>();
		for (Persona persona : registro.getPersonas().listar()) {
			if (persona instanceof Operador) {
				datos.add((Operador) persona);
			}
		}
		recargar(tabla, datos);
	}
}