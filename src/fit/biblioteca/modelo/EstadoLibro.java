package fit.biblioteca.modelo;

/**
 * Estado físico y disponibilidad de un ejemplar bibliográfico.
 */
public enum EstadoLibro {
    DISPONIBLE("Disponible"),
    PRESTADO("Prestado"),
    EN_REPARACION("En Reparación"),
    CONSULTA_INTERNA("Consulta Interna / Reserva"),
    BAJA_DEFINITIVA("Baja Definitiva");

    private final String etiqueta;

    EstadoLibro(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
