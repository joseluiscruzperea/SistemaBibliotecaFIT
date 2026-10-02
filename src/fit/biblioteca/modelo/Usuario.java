package fit.biblioteca.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa a un usuario de la biblioteca (Alumno o Docente) de la FIT.
 */
public class Usuario {
    private String matricula;
    private String nombre;
    private String apellido;
    private String correoInstitucional;
    private String telefono;
    private TipoUsuario tipo;
    private Carrera carrera;
    private int semestre;
    private boolean activo;
    private List<Prestamo> prestamosActivos;
    private List<Multa> historialMultas;

    public Usuario(String matricula, String nombre, String apellido, String correoInstitucional, 
                   String telefono, TipoUsuario tipo, Carrera carrera, int semestre) {
        this.matricula = matricula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correoInstitucional = correoInstitucional;
        this.telefono = telefono;
        this.tipo = tipo;
        this.carrera = carrera;
        this.semestre = semestre;
        this.activo = true;
        this.prestamosActivos = new ArrayList<>();
        this.historialMultas = new ArrayList<>();
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public boolean tieneAdeudo() {
        return getSaldoPendiente() > 0.0;
    }

    public double getSaldoPendiente() {
        double total = 0.0;
        for (Multa m : historialMultas) {
            if (m.getEstado() == EstadoMulta.PENDIENTE) {
                total += m.getMonto();
            }
        }
        return total;
    }

    public int getCantidadPrestamosActivos() {
        return prestamosActivos.size();
    }

    public boolean alcanzaLimiteLibros() {
        return getCantidadPrestamosActivos() >= tipo.getLimiteLibros();
    }

    public String getEstadoSemaforo() {
        if (!activo) return "INACTIVO";
        if (tieneAdeudo()) return "ROJO_ADEUDO";
        if (alcanzaLimiteLibros()) return "AMARILLO_LIMITE";
        return "VERDE_OK";
    }

    // Getters y Setters
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreoInstitucional() { return correoInstitucional; }
    public void setCorreoInstitucional(String correoInstitucional) { this.correoInstitucional = correoInstitucional; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }

    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }

    public int getSemestre() { return semestre; }
    public void setSemestre(int semestre) { this.semestre = semestre; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public List<Prestamo> getPrestamosActivos() { return prestamosActivos; }
    public List<Multa> getHistorialMultas() { return historialMultas; }

    @Override
    public String toString() {
        return matricula + " - " + getNombreCompleto() + " (" + tipo + " - " + carrera.getAbreviatura() + ")";
    }
}
