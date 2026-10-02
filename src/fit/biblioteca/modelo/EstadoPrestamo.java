package fit.biblioteca.modelo;

/**
 * Estado del ciclo de vida de un préstamo.
 */
public enum EstadoPrestamo {
    ACTIVO("Activo"),
    DEVUELTO_A_TIEMPO("Devuelto a Tiempo"),
    DEVUELTO_CON_RETRASO("Devuelto con Retraso"),
    CANCELADO("Cancelado (Deshecho)");

    private final String descripcion;

    EstadoPrestamo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
