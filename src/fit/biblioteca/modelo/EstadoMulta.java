package fit.biblioteca.modelo;

/**
 * Estado administrativo de una sanción económica por retraso.
 */
public enum EstadoMulta {
    PENDIENTE("Pendiente de Pago"),
    PAGADA("Pagada"),
    CONDONADA("Condonada por Jefatura");

    private final String descripcion;

    EstadoMulta(String descripcion) {
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
