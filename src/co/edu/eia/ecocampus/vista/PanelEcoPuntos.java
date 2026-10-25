package co.edu.eia.ecocampus.vista;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.edu.eia.ecocampus.calculo.CalculadorEcoPuntos;
import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.CategoriaMaterial;
import co.edu.eia.ecocampus.modelo.MovimientoEcoPuntos;
import co.edu.eia.ecocampus.modelo.MovimientoReciclaje;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.UsuarioCampus;
import co.edu.eia.ecocampus.patron.CalculoPorCampana;
import co.edu.eia.ecocampus.patron.CalculoPorMaterial;
import co.edu.eia.ecocampus.patron.EstrategiaCalculoEcoPuntos;
import co.edu.eia.ecocampus.persistencia.ConexionBD;
import co.edu.eia.ecocampus.servicio.ProcesadorEcoPuntos;
import co.edu.eia.ecocampus.util.Materiales;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class PanelEcoPuntos extends PanelBase {
	private static final String POR_MATERIAL = "Por material (tabla de puntos por kilo)";
	private static final String POR_CAMPANA = "Por campaña (puntos fijos por kilo)";

	private final ComboBox<UsuarioCampus> comboUsuario = new ComboBox<>();
	private final ComboBox<CategoriaMaterial> comboMaterial = new ComboBox<>();
	private final TextField campoPeso = campo("Peso en kg (ej: 4.5)");
	private final ComboBox<String> comboEstrategia = new ComboBox<>();
	private final TextField campoPuntosPorKilo = campo("Puntos por kilo (ej: 12)");

	private final TableView<UsuarioCampus> tablaRanking = crearTabla("Aún no hay usuarios registrados.");
	private final ListView<String> listaMovimientos = new ListView<>();
	private final Label resumenUsuario = new Label("Selecciona un usuario del ranking para ver sus movimientos.");
	
	// MAPA PARA LOS PUNTOS REALES DE LA BD
	private Map<String, Integer> puntosEnBD = new HashMap<>();

	public PanelEcoPuntos() {
		super("Eco-puntos", "Cada reciclaje suma puntos al usuario. La forma de calcularlos se elige con una estrategia.");

		mostrarComo(comboUsuario, u -> u.getNombre() + " (" + u.getId() + ")");
		mostrarComo(comboMaterial, c -> c.getNombre());
		comboMaterial.getItems().addAll(Materiales.todos());
		comboMaterial.setValue(Materiales.PLASTICO);
		comboEstrategia.getItems().addAll(POR_MATERIAL, POR_CAMPANA);
		comboEstrategia.setValue(POR_MATERIAL);
		campoPuntosPorKilo.setDisable(true);
		comboEstrategia.valueProperty().addListener(
				(observable, anterior, nuevo) -> campoPuntosPorKilo.setDisable(!POR_CAMPANA.equals(nuevo)));

		Button botonRegistrar = botonPrimario("Calcular, guardar en BD y acumular");
		botonRegistrar.setOnAction(evento -> registrarReciclaje());

		GridPane formulario = formulario();
		agregarFila(formulario, 0, "Usuario", comboUsuario);
		agregarFila(formulario, 1, "Material", comboMaterial);
		agregarFila(formulario, 2, "Peso (kg)", campoPeso);
		agregarFila(formulario, 3, "Estrategia", comboEstrategia);
		agregarFila(formulario, 4, "Puntos/kilo", campoPuntosPorKilo);

		VBox izquierda = tarjeta("Registrar reciclaje", formulario, botonRegistrar);
		izquierda.setPrefWidth(380);
		izquierda.setMinWidth(350);
		izquierda.setMaxHeight(Region.USE_PREF_SIZE);

		tablaRanking.getColumns().add(columna("Pos.", 50, (UsuarioCampus u) -> String.valueOf(tablaRanking.getItems().indexOf(u) + 1)));
		tablaRanking.getColumns().add(columna("Usuario", 170, (UsuarioCampus u) -> u.getNombre()));
		
		// SOLUCIÓN AL BUG: Esta columna leerá el total calculado desde la Base de Datos, ignorando los inactivos de la memoria
		tablaRanking.getColumns().add(columna("Eco-puntos (BD)", 110, (UsuarioCampus u) -> {
			int puntosReales = puntosEnBD.getOrDefault(u.getId(), u.getEcoPuntos());
			return String.valueOf(puntosReales);
		}));
		
		tablaRanking.getColumns().add(columna("Movimientos (Mem)", 110,
				(UsuarioCampus u) -> String.valueOf(ProcesadorEcoPuntos.contarElementos(u.getMovimientos()))));
		tablaRanking.getColumns().add(columna("Mayor (Mem)", 140, (UsuarioCampus u) -> {
			MovimientoEcoPuntos mayor = ProcesadorEcoPuntos.mayorMovimiento(u.getMovimientos());
			return mayor == null ? "—" : mayor.getPuntos() + " pts";
		}));
		tablaRanking.setMinHeight(200);

		listaMovimientos.setPlaceholder(new Label("Sin movimientos."));
		listaMovimientos.setPrefHeight(150);

		VBox derecha = new VBox(18, tarjeta("Ranking de eco-puntos", tablaRanking),
				tarjeta("Movimientos del usuario seleccionado", resumenUsuario, listaMovimientos));
		VBox.setVgrow(tablaRanking, Priority.ALWAYS);

		HBox contenido = new HBox(18, izquierda, derecha);
		HBox.setHgrow(derecha, Priority.ALWAYS);
		getChildren().add(contenido);

		tablaRanking.getSelectionModel().selectedItemProperty().addListener((observable, anterior, nuevo) -> mostrarMovimientos());
		actualizar();
	}

	private void registrarReciclaje() {
		UsuarioCampus usuario = comboUsuario.getValue();
		CategoriaMaterial material = comboMaterial.getValue();
		if (falta(usuario, "elegir un usuario") || falta(material, "elegir un material")) return;
		double peso;
		try { peso = Double.parseDouble(campoPeso.getText().trim().replace(',', '.')); } 
		catch (NumberFormatException e) { error("El peso debe ser un número (ej: 4.5)."); return; }
		
		EstrategiaCalculoEcoPuntos estrategia;
		if (POR_MATERIAL.equals(comboEstrategia.getValue())) {
			estrategia = new CalculoPorMaterial();
		} else {
			try { estrategia = new CalculoPorCampana(Integer.parseInt(campoPuntosPorKilo.getText().trim())); } 
			catch (NumberFormatException e) { error("Los puntos por kilo deben ser un número entero."); return; }
		}
		
		try {
			CalculadorEcoPuntos calculador = new CalculadorEcoPuntos(estrategia);
			int puntos = calculador.calcular(material, peso);
			String concepto = "Reciclaje de " + peso + " kg de " + material.getNombre();
			String fechaStr = LocalDate.now().toString();
			
			// 1. Acumular en memoria
			MovimientoReciclaje mov = new MovimientoReciclaje(puntos, concepto, fechaStr, material, peso);
			usuario.acumularEcoPuntos(mov);
			
			// 2. SOLUCIÓN BUG: Guardar inmediatamente en SQL Server para que otros paneles lo vean
			// AJUSTA tu nombre de tabla y columnas
			String sql = "INSERT INTO MovimientoEcoPuntos (usuarioId, puntos, concepto, fecha) VALUES (?, ?, ?, ?)";
			try (Connection conn = ConexionBD.obtenerConexion();
			     PreparedStatement pstmt = conn.prepareStatement(sql)) {
			    pstmt.setString(1, usuario.getId());
			    pstmt.setInt(2, puntos);
			    pstmt.setString(3, concepto);
			    pstmt.setString(4, fechaStr);
			    pstmt.executeUpdate();
			} catch (Exception ex) {
			    System.err.println("No se pudo guardar el punto en la base de datos: " + ex.getMessage());
			}
			ok(usuario.getNombre() + " acumuló " + puntos + " puntos en memoria y BD.");
			campoPeso.clear();
			actualizar();
		} catch (EcoCampusException e) {
			error(e.getMessage());
		}
	}

	private void mostrarMovimientos() {
		UsuarioCampus usuario = tablaRanking.getSelectionModel().getSelectedItem();
		listaMovimientos.getItems().clear();
		if (usuario == null) {
			resumenUsuario.setText("Selecciona un usuario del ranking para ver sus movimientos.");
			return;
		}
		
		// Preferencia al total de BD
		int totalBD = puntosEnBD.getOrDefault(usuario.getId(), ProcesadorEcoPuntos.sumarPuntos(usuario.getMovimientos()));
		resumenUsuario.setText(usuario.getNombre() + " · total acumulado (BD): " + totalBD + " puntos");
		
		for (MovimientoEcoPuntos movimiento : usuario.getMovimientos()) {
			listaMovimientos.getItems().add(movimiento.getFecha() + " · +" + movimiento.getPuntos() + " pts · " + movimiento.getConcepto());
		}
	}
	@Override
	public void actualizar() {
	    puntosEnBD.clear();
	    
	    // Consulta actualizada con 'usuarioId'
	    String sql = "SELECT usuarioId, SUM(puntos) AS total FROM MovimientoEcoPuntos GROUP BY usuarioId";
	    
	    try (Connection conn = ConexionBD.obtenerConexion();
	         PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {
	        while(rs.next()) {
	            puntosEnBD.put(rs.getString("usuarioId"), rs.getInt("total"));
	        }
	    } catch (Exception e) {
	        System.out.println("Aviso: la lectura a BD en PanelEcoPuntos falló. " + e.getMessage());
	    }

	    List<UsuarioCampus> usuarios = new ArrayList<>();
	    for (Persona persona : registro.getPersonas().listar()) {
	        if (persona instanceof UsuarioCampus) {
	            usuarios.add((UsuarioCampus) persona);
	        }
	    }
	    recargarCombo(comboUsuario, usuarios);
	    
	    List<UsuarioCampus> ranking = new ArrayList<>(usuarios);
	    ranking.sort((u1, u2) -> {
	        int p1 = puntosEnBD.getOrDefault(u1.getId(), u1.getEcoPuntos());
	        int p2 = puntosEnBD.getOrDefault(u2.getId(), u2.getEcoPuntos());
	        return Integer.compare(p2, p1);
	    });
	    
	    recargar(tablaRanking, ranking);
	    mostrarMovimientos();
	}
}