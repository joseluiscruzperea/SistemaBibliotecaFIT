package fit.biblioteca.modelo;

/**
 * Carreras académicas pertenecientes a la Facultad de Ingeniería (FIT).
 */
public enum Carrera {
    INGENIERIA_CIVIL("Ingeniería Civil", "IC"),
    INGENIERIA_INDUSTRIAL("Ingeniería Industrial", "II"),
    NEGOCIOS_INTERNACIONALES("Lic. en Negocios y Tecnologías", "LNT"),
    TRONCO_COMUN("Ciencias Básicas / Tronco Común", "CB");

    private final String nombreCompleto;
    private final String abreviatura;

    Carrera(String nombreCompleto, String abreviatura) {
        this.nombreCompleto = nombreCompleto;
        this.abreviatura = abreviatura;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    @Override
    public String toString() {
        return nombreCompleto;
    }
}
