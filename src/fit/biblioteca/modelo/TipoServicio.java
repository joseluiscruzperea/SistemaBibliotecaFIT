package fit.biblioteca.modelo;

/**
 * Tipos de servicio al registrarse un usuario en la recepción de la biblioteca.
 */
public enum TipoServicio {
    CONSULTA_SALA("Consulta en Sala / Lectura"),
    USO_COMPUTADORA("Uso de Computadora (Centro de Cómputo)");

    private final String descripcion;

    TipoServicio(String descripcion) {
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
