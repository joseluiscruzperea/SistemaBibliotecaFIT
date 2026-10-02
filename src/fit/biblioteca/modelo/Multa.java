package fit.biblioteca.modelo;

import java.time.LocalDate;

/**
 * Entidad de sanción económica generada automáticamente cuando una transacción excede el plazo.
 * Cumple con RN-04: $10 pesos por cada día hábil de retraso.
 */
public class Multa {
    private String idMulta;
    private Prestamo prestamo;
    private Usuario usuario;
    private double monto;
    private LocalDate fechaGeneracion;
    private LocalDate fechaPago;
    private EstadoMulta estado;
    private String motivo;
    private String cobradoPor;

    public Multa(String idMulta, Prestamo prestamo, Usuario usuario, double monto, 
                 LocalDate fechaGeneracion, String motivo) {
        this.idMulta = idMulta;
        this.prestamo = prestamo;
        this.usuario = usuario;
        this.monto = monto;
        this.fechaGeneracion = fechaGeneracion;
        this.estado = EstadoMulta.PENDIENTE;
        this.motivo = motivo;
    }

    public void registrarPago(String cajero, LocalDate fecha) {
        this.estado = EstadoMulta.PAGADA;
        this.cobradoPor = cajero;
        this.fechaPago = fecha;
    }

    public void condonar(String motivoCondonacion, String autorizador) {
        this.estado = EstadoMulta.CONDONADA;
        this.motivo += " [Condonada por " + autorizador + ": " + motivoCondonacion + "]";
        this.fechaPago = LocalDate.now();
        this.cobradoPor = autorizador;
    }

    // Getters y Setters
    public String getIdMulta() { return idMulta; }
    public void setIdMulta(String idMulta) { this.idMulta = idMulta; }

    public Prestamo getPrestamo() { return prestamo; }
    public void setPrestamo(Prestamo prestamo) { this.prestamo = prestamo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public LocalDate getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDate fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public EstadoMulta getEstado() { return estado; }
    public void setEstado(EstadoMulta estado) { this.estado = estado; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getCobradoPor() { return cobradoPor; }
    public void setCobradoPor(String cobradoPor) { this.cobradoPor = cobradoPor; }

    @Override
    public String toString() {
        return idMulta + " | " + usuario.getMatricula() + " | $" + String.format("%.2f", monto) + " [" + estado + "]";
    }
}
