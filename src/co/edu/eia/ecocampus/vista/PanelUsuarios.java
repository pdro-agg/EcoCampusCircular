package co.edu.eia.ecocampus.vista;

import java.util.ArrayList;
import java.util.List;

import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.UsuarioCampus;
import co.edu.eia.ecocampus.persistencia.EliminacionDAO;
import co.edu.eia.ecocampus.persistencia.PersistenciaException;
import co.edu.eia.ecocampus.persistencia.PersonaDAO;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PanelUsuarios extends PanelBase {
	private final TextField campoId = campo("Ej: 1001");
	private final TextField campoNombre = campo("Nombre completo");
	private final TextField campoCorreo = campo("correo@eia.edu.co");
	private final ComboBox<String> comboVinculacion = new ComboBox<>();
	private final TextField campoBusqueda = campo("Buscar por identificación o nombre...");
	private final TableView<UsuarioCampus> tabla = crearTabla("Aún no hay usuarios registrados.");
	
	private final PersonaDAO personaDAO = new PersonaDAO();
	private final EliminacionDAO eliminacionDAO = new EliminacionDAO();

	public PanelUsuarios() {
		super("Usuarios del campus", "Personas que reportan, reciclan y se inscriben en las campañas.");

		comboVinculacion.getItems().addAll("Estudiante", "Docente", "Colaborador", "Visitante", "Operador");
		comboVinculacion.setValue("Estudiante");

		Button botonRegistrar = botonPrimario("Registrar usuario");
		botonRegistrar.setOnAction(evento -> registrarUsuario());
		campoBusqueda.textProperty().addListener((observable, anterior, nuevo) -> actualizar());

		GridPane formulario = formulario();
		agregarFila(formulario, 0, "Identificación", campoId);
		agregarFila(formulario, 1, "Nombre", campoNombre);
		agregarFila(formulario, 2, "Correo", campoCorreo);
		agregarFila(formulario, 3, "Vinculación", comboVinculacion);

		VBox izquierda = tarjeta("Nuevo usuario", formulario, botonRegistrar);
		izquierda.setPrefWidth(350);
		izquierda.setMinWidth(330);
		izquierda.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);

		tabla.getColumns().add(columna("Identificación", 110, (UsuarioCampus u) -> u.getId()));
		tabla.getColumns().add(columna("Nombre", 170, (UsuarioCampus u) -> u.getNombre()));
		tabla.getColumns().add(columna("Correo", 190, (UsuarioCampus u) -> u.getCorreo()));
		tabla.getColumns().add(columna("Vinculación", 110, (UsuarioCampus u) -> u.getTipoVinculacion()));
		tabla.getColumns().add(columna("Eco-puntos", 90, (UsuarioCampus u) -> String.valueOf(u.getEcoPuntos())));

		// Botón de eliminación configurado e integrado en la interfaz
		Button botonEliminar = botonPeligro("Eliminar usuario seleccionado");
		botonEliminar.setOnAction(evento -> {
			UsuarioCampus seleccionado = tabla.getSelectionModel().getSelectedItem();
			if (seleccionado == null) {
				error("Selecciona un usuario u operador de la tabla para eliminar.");
				return;
			}

			try {
				eliminacionDAO.eliminarPorId("personas", seleccionado.getId());
				ok("Usuario " + seleccionado.getNombre() + " eliminado de la base de datos.");
				actualizar();
			} catch (PersistenciaException e) {
				error("Error al eliminar de la BD: " + e.getMessage());
			}
		});

		// Agregamos el botón dentro de la tarjeta derecha debajo de la tabla
		VBox derecha = tarjeta("Usuarios registrados", campoBusqueda, tabla, botonEliminar);
		VBox.setVgrow(tabla, Priority.ALWAYS);

		HBox contenido = new HBox(18, izquierda, derecha);
		HBox.setHgrow(derecha, Priority.ALWAYS);
		getChildren().add(contenido);

		actualizar();
	}

	private void registrarUsuario() {
		if (vacio(campoId, campoNombre, campoCorreo)) {
			return;
		}
		UsuarioCampus usuario = new UsuarioCampus(campoId.getText().trim(), campoNombre.getText().trim(),
				campoCorreo.getText().trim(), comboVinculacion.getValue());
		try {
			personaDAO.guardar(usuario);
			ok("Usuario registrado en SQL Server: " + usuario.getNombre());
			campoId.clear();
			campoNombre.clear();
			campoCorreo.clear();
			actualizar();
		} catch (PersistenciaException e) {
			error("Error en la base de datos: " + e.getMessage());
		}
	}

	@Override
	public void actualizar() {
		List<Persona> personasBD = new ArrayList<>();
		try {
			personasBD = personaDAO.cargarTodos();
		} catch (PersistenciaException e) {
			System.err.println("Error al cargar desde SQL Server: " + e.getMessage());
		}

		String filtro = campoBusqueda.getText() == null ? "" : campoBusqueda.getText().trim().toLowerCase();
		List<UsuarioCampus> datos = new ArrayList<>();
		
		for (Persona persona : personasBD) {
			if (persona instanceof UsuarioCampus) {
				UsuarioCampus usuario = (UsuarioCampus) persona;
				if (filtro.isEmpty() || usuario.getId().toLowerCase().contains(filtro)
						|| usuario.getNombre().toLowerCase().contains(filtro)) {
					datos.add(usuario);
				}
			}
		}

		tabla.setPlaceholder(new Label(filtro.isEmpty() ? "Aún no hay usuarios en la base de datos."
				: "No se encontraron usuarios para «" + campoBusqueda.getText().trim() + "»."));
		recargar(tabla, datos);
	}
}