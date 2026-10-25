package co.edu.eia.ecocampus.vista;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.Campana;
import co.edu.eia.ecocampus.modelo.Operador;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.Reporte;
import co.edu.eia.ecocampus.modelo.UsuarioCampus;
import co.edu.eia.ecocampus.servicio.ConsultaReportes;
import co.edu.eia.ecocampus.util.DatosEjemplo;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Pantalla de inicio: indicadores generales y reportes pendientes por prioridad. */
public class PanelResumen extends PanelBase {
	private final BitacoraEventos bitacora;
	private final Label valorUsuarios = new Label();
	private final Label valorOperadores = new Label();
	private final Label valorPuntos = new Label();
	private final Label valorPendientes = new Label();
	private final Label valorCampanas = new Label();
	private final Label valorEcoPuntos = new Label();
	private final TableView<Reporte> tablaPendientes = crearTabla("No hay reportes pendientes.");

	public PanelResumen(BitacoraEventos bitacora) {
		super("Resumen", "Vista general del sistema EcoCampus Circular.");
		this.bitacora = bitacora;

		HBox indicadores = new HBox(14, kpi("Usuarios", valorUsuarios), kpi("Operadores", valorOperadores),
				kpi("Puntos activos", valorPuntos), kpi("Reportes pendientes", valorPendientes),
				kpi("Campañas abiertas", valorCampanas), kpi("Eco-puntos totales", valorEcoPuntos));
		for (javafx.scene.Node nodo : indicadores.getChildren()) {
			HBox.setHgrow(nodo, Priority.ALWAYS);
		}

		tablaPendientes.getColumns().add(columna("#", 50, (Reporte r) -> String.valueOf(r.getNumero())));
		tablaPendientes.getColumns().add(columna("Prioridad", 90, (Reporte r) -> r.getPrioridad()));
		tablaPendientes.getColumns().add(columna("Tipo", 140, (Reporte r) -> r.getTipo()));
		tablaPendientes.getColumns().add(columna("Punto", 160,
				(Reporte r) -> r.getPuntoEcologico().getCodigo() + " · " + r.getPuntoEcologico().getUbicacion()));
		tablaPendientes.getColumns().add(columna("Autor", 150, (Reporte r) -> r.getAutor().getNombre()));
		tablaPendientes.getColumns().add(columna("Fecha", 100, (Reporte r) -> r.getFecha()));

		Button botonDatos = botonSecundario("Cargar datos de ejemplo");
		botonDatos.setOnAction(evento -> cargarDatosEjemplo());
		Label pista = new Label("Crea usuarios, puntos, reportes, una ruta y una campaña para probar rápidamente.");
		pista.getStyleClass().add("panel-subtitulo");

		VBox tarjetaPendientes = tarjeta("Reportes pendientes por prioridad", tablaPendientes);
		VBox.setVgrow(tablaPendientes, Priority.ALWAYS);

		HBox filaDatos = new HBox(14, botonDatos, pista);
		filaDatos.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
		getChildren().addAll(indicadores, tarjetaPendientes, filaDatos);

		actualizar();
	}

	private static VBox kpi(String titulo, Label valor) {
		valor.getStyleClass().add("kpi-valor");
		Label etiqueta = new Label(titulo);
		etiqueta.getStyleClass().add("kpi-titulo");
		VBox tarjeta = new VBox(4, valor, etiqueta);
		tarjeta.getStyleClass().add("tarjeta");
		tarjeta.setMaxWidth(Double.MAX_VALUE);
		return tarjeta;
	}

	private void cargarDatosEjemplo() {
		try {
			DatosEjemplo.cargar(registro, bitacora);
			ok("Datos de ejemplo cargados.");
		} catch (EcoCampusException e) {
			error(e.getMessage());
		}
		actualizar();
	}

	@Override
	public void actualizar() {
		int usuarios = 0;
		int operadores = 0;
		int ecoPuntos = 0;
		for (Persona persona : registro.getPersonas().listar()) {
			if (persona instanceof UsuarioCampus) {
				usuarios++;
				ecoPuntos += ((UsuarioCampus) persona).getEcoPuntos();
			} else if (persona instanceof Operador) {
				operadores++;
			}
		}
		int puntosActivos = 0;
		for (PuntoEcologico punto : registro.getPuntos().listar()) {
			if ("Activo".equals(punto.getEstado())) {
				puntosActivos++;
			}
		}
		int campanasAbiertas = 0;
		for (Campana campana : registro.getCampanas().listar()) {
			if ("Abierta".equals(campana.getEstado())) {
				campanasAbiertas++;
			}
		}
		valorUsuarios.setText(String.valueOf(usuarios));
		valorOperadores.setText(String.valueOf(operadores));
		valorPuntos.setText(String.valueOf(puntosActivos));
		valorCampanas.setText(String.valueOf(campanasAbiertas));
		valorEcoPuntos.setText(String.valueOf(ecoPuntos));

		// ConsultaReportes ordena con un Comparator anonimo: Critica, Alta, Media...
		java.util.List<Reporte> pendientes = ConsultaReportes.pendientesPorPrioridad(registro.getReportes());
		valorPendientes.setText(String.valueOf(pendientes.size()));
		recargar(tablaPendientes, pendientes);
	}
}
