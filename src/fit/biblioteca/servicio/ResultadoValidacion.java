package fit.biblioteca.servicio;

/**
 * Encapsula el resultado de validación de reglas de negocio para un préstamo.
 */
public class ResultadoValidacion {
    private final boolean aprobado;
    private final String mensaje;
    private final String codigoRegla;

    public ResultadoValidacion(boolean aprobado, String mensaje, String codigoRegla) {
        this.aprobado = aprobado;
        this.mensaje = mensaje;
        this.codigoRegla = codigoRegla;
    }

    public static ResultadoValidacion ok(String mensaje) {
        return new ResultadoValidacion(true, mensaje, "OK");
    }

    public static ResultadoValidacion fallo(String codigoRegla, String mensaje) {
        return new ResultadoValidacion(false, mensaje, codigoRegla);
    }

    public boolean isAprobado() {
        return aprobado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getCodigoRegla() {
        return codigoRegla;
    }
}
