package co.edu.eia.ecocampus.modelo;

import java.util.ArrayList;
import java.util.List;
import co.edu.eia.ecocampus.core.Identificable;
import co.edu.eia.ecocampus.patron.Observador;
import co.edu.eia.ecocampus.excepcion.EcoCampusException;

public class Reporte implements Identificable {
    private String id;
    private String descripcion;
    private String fecha;
    private UsuarioCampus autor;
    private PuntoEcologico puntoEcologico;
    private String tipo;
    private String prioridad;
    private String estado;
    private Operador operadorAsignado;
    private List<String> historial;
    private List<Observador> observadores;
    private Operador operador;
    private RutaRecoleccion ruta;

    // 1. Constructor para la Base de Datos (DAO)
    public Reporte(String id, String descripcion, String fecha) {
        this.id = id;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.estado = "Pendiente";
        this.historial = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    // 2. Constructor complejo original para FabricaReportes
    public Reporte(int numero, String descripcion, UsuarioCampus autor, PuntoEcologico punto, String tipo, String prioridad, String estado) {
        this.id = String.valueOf(numero);
        this.descripcion = descripcion;
        this.autor = autor;
        this.puntoEcologico = punto;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fecha = "N/A"; 
        this.historial = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    // 3. Constructor para pruebas (int numero)
    public Reporte(int numero, UsuarioCampus autor, PuntoEcologico punto, String descripcion, String fecha) {
        this.id = String.valueOf(numero);
        this.autor = autor;
        this.puntoEcologico = punto;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.estado = "Pendiente";
        this.historial = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    // 4. NUEVO CONSTRUCTOR SOBRECARGADO: Acepta ID como String para EliminacionConsistenciaTest
    public Reporte(String id, UsuarioCampus autor, PuntoEcologico punto, String descripcion, String fecha) {
        this.id = id;
        this.autor = autor;
        this.puntoEcologico = punto;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.estado = "Pendiente";
        this.historial = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    @Override
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // Adaptador para el getNumero() que usaban la vista y la consola antiguas
    public int getNumero() {
        try {
            return Integer.parseInt(id.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public UsuarioCampus getAutor() { return autor; }
    public PuntoEcologico getPuntoEcologico() { return puntoEcologico; }
    public String getTipo() { return tipo; }
    public String getPrioridad() { return prioridad; }
    public String getEstado() { return estado; }
    public List<String> getHistorial() { return historial; }

    // Métodos de lógica de negocio que exigía el proyecto original
    public void asignarOperador(Operador operador) throws EcoCampusException {
        this.operadorAsignado = operador;
    }
    
    public void cerrar(Recoleccion recoleccion) throws EcoCampusException {
        this.estado = "Cerrado";
        if (recoleccion != null) {
            this.historial.add("Cerrado con recolección");
        }
    }

    public void agregarObservador(Observador observador) {
        this.observadores.add(observador);
    }

    @Override
    public String toString() {
        return "Reporte [" + id + "]: " + descripcion + " (" + fecha + ")";
    }
    public Operador getOperadorAsignado() {
        return this.operador; // Retorna tu variable de operador asignado
    }

    public void asignarRuta(RutaRecoleccion ruta) throws EcoCampusException {
        this.ruta = ruta; // Guarda la ruta en tu reporte
    }

    public void reabrir() throws EcoCampusException {
        this.estado = "Pendiente"; // O el estado con el que inicies los reportes
    }

    public static class HistorialCambios {
        private String descripcion;
        private long timestamp;

        public HistorialCambios(String descripcion) {
            this.descripcion = descripcion;
            this.timestamp = System.currentTimeMillis();
        }

        public String getDescripcion() {
            return descripcion;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}