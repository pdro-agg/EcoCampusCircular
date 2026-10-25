package co.edu.eia.ecocampus.modelo;

import java.util.ArrayList;
import java.util.List;
import co.edu.eia.ecocampus.core.Identificable;
import co.edu.eia.ecocampus.patron.Observador;
import co.edu.eia.ecocampus.excepcion.EcoCampusException;

public class Campana implements Identificable {
    private String id;
    private String nombre;
    private int metaPuntos;
    private String estado;
    private List<UsuarioCampus> participantes;
    private List<Actividad> actividades;
    private List<Observador> observadores;

    // 1. Constructor para la Base de Datos (DAO)
    public Campana(String id, String nombre, int metaPuntos) {
        this.id = id;
        this.nombre = nombre;
        this.metaPuntos = metaPuntos;
        this.estado = "Activa";
        this.participantes = new ArrayList<>();
        this.actividades = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    // 2. Constructor antiguo para DatosEjemplo y Main (que usaban un int como ID)
    public Campana(int idViejo, String nombre, int metaPuntos) {
        this(String.valueOf(idViejo), nombre, metaPuntos);
    }

    @Override
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getMetaPuntos() { return metaPuntos; }
    public void setMetaPuntos(int metaPuntos) { this.metaPuntos = metaPuntos; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<UsuarioCampus> getParticipantes() { return participantes; }

    // Métodos de lógica de negocio que exigía el proyecto original
    public void inscribirParticipante(UsuarioCampus participante) throws EcoCampusException {
        this.participantes.add(participante);
    }

    public void agregarActividad(Actividad actividad) {
        this.actividades.add(actividad);
    }
    public String getNumero() {
        return getId(); // O retorna la variable donde guardes el ID/Número
    }

    public int getCupo() {
        return this.metaPuntos; // O la variable que represente el cupo máximo de participantes
    }

    public int getTotalParticipantes() {
        return (this.participantes != null) ? this.participantes.size() : 0;
    }

    public List<Actividad> getActividades() {
        return this.actividades;
    }

    public void cerrarCampana() throws EcoCampusException {
        this.estado = "Cerrada"; // O el estado que manejes
    }

    public void agregarObservador(Observador observador) {
        this.observadores.add(observador);
    }

    @Override
    public String toString() {
        return "Campaña: " + nombre + " (Meta: " + metaPuntos + " pts)";
    }
}