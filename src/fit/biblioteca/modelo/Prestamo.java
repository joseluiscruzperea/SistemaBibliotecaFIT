package fit.biblioteca.modelo;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Entidad transaccional que registra la salida y retorno de un ejemplar asociado a un usuario.
 * Cumple con las nuevas políticas:
 * - Plazo base: 2 días hábiles (si sale viernes, se regresa martes; fines de semana no cuentan).
 * - Hasta 2 prórrogas permitidas (+2 días hábiles cada una), solicitadas solo con matrícula.
 * - Multa por mora: $20.00 pesos por cada día hábil de retraso.
 */
public class Prestamo {
    public static final int MAX_PRORROGAS = 2;
    public static final double TARIFA_MULTA_DIARIA = 20.0; // $20 pesos diarios

    private String idPrestamo;
    private Usuario usuario;
    private Ejemplar ejemplar;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEsperada;
    private LocalDate fechaDevolucionReal;
    private EstadoPrestamo estado;
    private int numeroProrrogas; // Contador de prórrogas aplicadas (máximo 2)
    private int diasHabilesRetraso;
    private Multa multaAsociada;
    private String atendidoPor;

    public Prestamo(String idPrestamo, Usuario usuario, Ejemplar ejemplar, 
                    LocalDate fechaPrestamo, LocalDate fechaDevolucionEsperada, String atendidoPor) {
        this.idPrestamo = idPrestamo;
        this.usuario = usuario;
        this.ejemplar = ejemplar;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.estado = EstadoPrestamo.ACTIVO;
        this.numeroProrrogas = 0;
        this.diasHabilesRetraso = 0;
        this.atendidoPor = atendidoPor;
    }

    public boolean puedeSolicitarProrroga() {
        return estado == EstadoPrestamo.ACTIVO && numeroProrrogas < MAX_PRORROGAS;
    }

    public void aplicarProrroga(LocalDate nuevaFechaVencimiento) {
        if (!puedeSolicitarProrroga()) {
            throw new IllegalStateException("No se pueden otorgar más prórrogas. Límite máximo de 2 alcanzado.");
        }
        this.numeroProrrogas++;
        this.fechaDevolucionEsperada = nuevaFechaVencimiento;
    }

    public boolean estaVencido(LocalDate fechaActual) {
        if (estado != EstadoPrestamo.ACTIVO) return false;
        return fechaActual.isAfter(fechaDevolucionEsperada);
    }

    public int calcularDiasHabilesRetraso(LocalDate fechaRetorno) {
        if (!fechaRetorno.isAfter(fechaDevolucionEsperada)) {
            return 0;
        }
        int diasHabiles = 0;
        LocalDate cur = fechaDevolucionEsperada.plusDays(1);
        while (!cur.isAfter(fechaRetorno)) {
            DayOfWeek dow = cur.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                diasHabiles++;
            }
            cur = cur.plusDays(1);
        }
        return diasHabiles;
    }

    // Getters y Setters
    public String getIdPrestamo() { return idPrestamo; }
    public void setIdPrestamo(String idPrestamo) { this.idPrestamo = idPrestamo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Ejemplar getEjemplar() { return ejemplar; }
    public void setEjemplar(Ejemplar ejemplar) { this.ejemplar = ejemplar; }

    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public LocalDate getFechaDevolucionEsperada() { return fechaDevolucionEsperada; }
    public void setFechaDevolucionEsperada(LocalDate fechaDevolucionEsperada) { this.fechaDevolucionEsperada = fechaDevolucionEsperada; }

    public LocalDate getFechaDevolucionReal() { return fechaDevolucionReal; }
    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) { this.fechaDevolucionReal = fechaDevolucionReal; }

    public EstadoPrestamo getEstado() { return estado; }
    public void setEstado(EstadoPrestamo estado) { this.estado = estado; }

    public int getNumeroProrrogas() { return numeroProrrogas; }
    public void setNumeroProrrogas(int numeroProrrogas) { this.numeroProrrogas = numeroProrrogas; }

    public int getDiasHabilesRetraso() { return diasHabilesRetraso; }
    public void setDiasHabilesRetraso(int diasHabilesRetraso) { this.diasHabilesRetraso = diasHabilesRetraso; }

    public Multa getMultaAsociada() { return multaAsociada; }
    public void setMultaAsociada(Multa multaAsociada) { this.multaAsociada = multaAsociada; }

    public String getAtendidoPor() { return atendidoPor; }
    public void setAtendidoPor(String atendidoPor) { this.atendidoPor = atendidoPor; }

    @Override
    public String toString() {
        return idPrestamo + " | " + usuario.getMatricula() + " - " + ejemplar.getLibro().getTitulo() + 
               " | Límite: " + fechaDevolucionEsperada + " (Prórrogas: " + numeroProrrogas + "/2)";
    }
}
