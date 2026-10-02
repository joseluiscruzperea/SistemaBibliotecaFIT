package fit.biblioteca.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Registro de entrada/acceso a la biblioteca por matrícula.
 * Controla si el usuario solicita consulta en sala o uso de computadora con tiempo asignado en horas.
 */
public class RegistroAcceso {
    private String idAcceso;
    private Usuario usuario;
    private LocalDateTime fechaHoraEntrada;
    private TipoServicio tipoServicio;
    private String numeroComputadora; // Ej. PC-01, PC-02
    private int horasAsignadas;       // Ej. 1, 2 o 3 horas
    private LocalDateTime horaLimiteUso;
    private boolean activo;

    public RegistroAcceso(String idAcceso, Usuario usuario, TipoServicio tipoServicio, 
                          String numeroComputadora, int horasAsignadas) {
        this.idAcceso = idAcceso;
        this.usuario = usuario;
        this.tipoServicio = tipoServicio;
        this.fechaHoraEntrada = LocalDateTime.now();
        this.numeroComputadora = numeroComputadora;
        this.horasAsignadas = horasAsignadas;
        this.activo = true;
        if (tipoServicio == TipoServicio.USO_COMPUTADORA && horasAsignadas > 0) {
            this.horaLimiteUso = this.fechaHoraEntrada.plusHours(horasAsignadas);
        } else {
            this.horaLimiteUso = null;
        }
    }

    public void finalizarSesion() {
        this.activo = false;
    }

    // Getters y Setters
    public String getIdAcceso() { return idAcceso; }
    public Usuario getUsuario() { return usuario; }
    public LocalDateTime getFechaHoraEntrada() { return fechaHoraEntrada; }
    public TipoServicio getTipoServicio() { return tipoServicio; }
    public String getNumeroComputadora() { return numeroComputadora != null ? numeroComputadora : "N/A"; }
    public int getHorasAsignadas() { return horasAsignadas; }
    public LocalDateTime getHoraLimiteUso() { return horaLimiteUso; }
    public boolean isActivo() { return activo; }

    public String getHoraEntradaFormateada() {
        return fechaHoraEntrada.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String getHoraLimiteFormateada() {
        if (horaLimiteUso == null) return "N/A (Sala)";
        return horaLimiteUso.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    @Override
    public String toString() {
        return idAcceso + " | " + usuario.getMatricula() + " - " + tipoServicio + 
               (tipoServicio == TipoServicio.USO_COMPUTADORA ? " [" + numeroComputadora + " por " + horasAsignadas + "h]" : "");
    }
}
