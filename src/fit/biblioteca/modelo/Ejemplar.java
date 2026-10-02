package fit.biblioteca.modelo;

/**
 * Entidad física individual de un libro.
 * Cada ejemplar tiene asociada una Tarjeta de Préstamo con un Folio Único Consecutivo
 * que no se repite para ningún libro físico dentro del acervo.
 */
public class Ejemplar {
    private String codigoBarras;
    private String folioTarjeta; // Folio único consecutivo de tarjeta Dewey (ej. TARJ-0001)
    private Libro libro;
    private int numeroCopia;
    private EstadoLibro estado;
    private String notasEstado;
    private Prestamo prestamoActivo; // Referencia al préstamo activo para saber quién lo tiene

    public Ejemplar(String codigoBarras, String folioTarjeta, Libro libro, int numeroCopia, 
                    EstadoLibro estado, String notasEstado) {
        this.codigoBarras = codigoBarras;
        this.folioTarjeta = folioTarjeta;
        this.libro = libro;
        this.numeroCopia = numeroCopia;
        this.estado = estado;
        this.notasEstado = notasEstado;
        this.prestamoActivo = null;
    }

    public boolean estaDisponibleParaPrestamo() {
        return estado == EstadoLibro.DISPONIBLE && !libro.isEsConsultaInterna();
    }

    public String getLectorActualInfo() {
        if (estado == EstadoLibro.PRESTADO && prestamoActivo != null) {
            Usuario u = prestamoActivo.getUsuario();
            return u.getMatricula() + " - " + u.getNombreCompleto() + " (Vence: " + prestamoActivo.getFechaDevolucionEsperada() + ")";
        } else if (libro.isEsConsultaInterna()) {
            return "Reserva / Solo Sala";
        } else if (estado == EstadoLibro.EN_REPARACION) {
            return "En Taller de Reparación";
        }
        return "Disponible en Estantería";
    }

    // Getters y Setters
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getFolioTarjeta() { return folioTarjeta; }
    public void setFolioTarjeta(String folioTarjeta) { this.folioTarjeta = folioTarjeta; }

    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }

    public int getNumeroCopia() { return numeroCopia; }
    public void setNumeroCopia(int numeroCopia) { this.numeroCopia = numeroCopia; }

    public EstadoLibro getEstado() { return estado; }
    public void setEstado(EstadoLibro estado) { this.estado = estado; }

    public String getNotasEstado() { return notasEstado; }
    public void setNotasEstado(String notasEstado) { this.notasEstado = notasEstado; }

    public Prestamo getPrestamoActivo() { return prestamoActivo; }
    public void setPrestamoActivo(Prestamo prestamoActivo) { this.prestamoActivo = prestamoActivo; }

    @Override
    public String toString() {
        return folioTarjeta + " (" + codigoBarras + ") - " + (libro != null ? libro.getTitulo() : "") + " [" + estado + "]";
    }
}
