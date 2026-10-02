package fit.biblioteca.datos;

import fit.biblioteca.modelo.*;
import fit.biblioteca.servicio.BibliotecaService;
import java.time.LocalDate;

/**
 * Carga de datos iniciales del catálogo y comunidad académica de la FIT.
 * Incluye acervo para Civil, Industrial y Negocios, Clasificación Dewey,
 * Folios Únicos de Tarjeta (TARJ-XXXX), palabras clave y casos de prueba.
 */
public class DatosIniciales {

    public static void cargarDatos(BibliotecaService service) {
        // ==========================================
        // 1. USUARIOS (ALUMNOS Y DOCENTES)
        // ==========================================
        Usuario u1 = new Usuario("2021001", "Juan", "Pérez Ramos", "juan.perez@uat.edu.mx", "8331002030", 
                                 TipoUsuario.ALUMNO, Carrera.INGENIERIA_CIVIL, 7);
        Usuario u2 = new Usuario("2021002", "Carlos", "Garza López", "carlos.garza@uat.edu.mx", "8332114455", 
                                 TipoUsuario.ALUMNO, Carrera.INGENIERIA_CIVIL, 8);
        Usuario u3 = new Usuario("2022001", "Mariana", "Tovar Méndez", "mariana.tovar@uat.edu.mx", "8339887766", 
                                 TipoUsuario.ALUMNO, Carrera.INGENIERIA_INDUSTRIAL, 5);
        Usuario u4 = new Usuario("2022002", "Luis", "García Morán", "luis.garcia@uat.edu.mx", "8334445566", 
                                 TipoUsuario.ALUMNO, Carrera.INGENIERIA_INDUSTRIAL, 6);
        Usuario u5 = new Usuario("2023001", "Sofía", "Mendoza Ríos", "sofia.mendoza@uat.edu.mx", "8335556677", 
                                 TipoUsuario.ALUMNO, Carrera.NEGOCIOS_INTERNACIONALES, 3);
        Usuario u6 = new Usuario("2023002", "Alejandro", "Castillo Wong", "alejandro.castillo@uat.edu.mx", "8337778899", 
                                 TipoUsuario.ALUMNO, Carrera.NEGOCIOS_INTERNACIONALES, 4);

        // Docentes
        Usuario d1 = new Usuario("DOC-101", "Roberto Carlos", "Morales Ruiz", "roberto.morales@uat.edu.mx", "8331112233", 
                                 TipoUsuario.DOCENTE, Carrera.INGENIERIA_CIVIL, 0);
        Usuario d2 = new Usuario("DOC-102", "Claudia Patricia", "Garza Salinas", "claudia.garza@uat.edu.mx", "8332223344", 
                                 TipoUsuario.DOCENTE, Carrera.INGENIERIA_INDUSTRIAL, 0);
        Usuario d3 = new Usuario("DOC-103", "Fernando", "Treviño Peña", "fernando.trevino@uat.edu.mx", "8333334455", 
                                 TipoUsuario.DOCENTE, Carrera.NEGOCIOS_INTERNACIONALES, 0);

        service.getUsuarios().add(u1);
        service.getUsuarios().add(u2);
        service.getUsuarios().add(u3);
        service.getUsuarios().add(u4);
        service.getUsuarios().add(u5);
        service.getUsuarios().add(u6);
        service.getUsuarios().add(d1);
        service.getUsuarios().add(d2);
        service.getUsuarios().add(d3);

        // ==========================================
        // 2. CATÁLOGO CON CLASIFICACIÓN DEWEY Y TARJETAS FÍSICAS
        // ==========================================

        // --- INGENIERÍA CIVIL ---
        Libro lCiv1 = new Libro("978-607-481-888-8", "Mecánica de Suelos y Cimentaciones", "Braja M. Das", "Cengage", 2021, 
                                "Geotecnia", Carrera.INGENIERIA_CIVIL, "624.151 DAS",
                                "suelos, cimentaciones, geotecnia, terzaghi, resistencia, empuje", 
                                false, "Estante CIV-A1");
        Ejemplar ejCiv1_1 = new Ejemplar("CIV-101", service.generarSiguienteFolioTarjeta(), lCiv1, 1, EstadoLibro.DISPONIBLE, "Excelente");
        Ejemplar ejCiv1_2 = new Ejemplar("CIV-102", service.generarSiguienteFolioTarjeta(), lCiv1, 2, EstadoLibro.DISPONIBLE, "Bueno");
        lCiv1.agregarEjemplar(ejCiv1_1);
        lCiv1.agregarEjemplar(ejCiv1_2);

        Libro lCiv2 = new Libro("978-968-18-6228-2", "Diseño de Estructuras de Concreto Reforzado", "Jack C. McCormac", "Limusa Wiley", 2020, 
                                "Estructuras", Carrera.INGENIERIA_CIVIL, "624.183 MAC",
                                "estructuras, concreto, vigas, columnas, losas, aci, sismo", 
                                false, "Estante CIV-A2");
        Ejemplar ejCiv2_1 = new Ejemplar("CIV-201", service.generarSiguienteFolioTarjeta(), lCiv2, 1, EstadoLibro.DISPONIBLE, "Excelente");
        Ejemplar ejCiv2_2 = new Ejemplar("CIV-202", service.generarSiguienteFolioTarjeta(), lCiv2, 2, EstadoLibro.DISPONIBLE, "Pasta desgastada");
        lCiv2.agregarEjemplar(ejCiv2_1);
        lCiv2.agregarEjemplar(ejCiv2_2);

        Libro lCivRes = new Libro("978-000-000-001-1", "Normas Técnicas Complementarias de Construcción", "Colegio de Ing. Civiles", "FIT Ediciones", 2023, 
                                  "Normativa", Carrera.INGENIERIA_CIVIL, "624.021 NOR",
                                  "normas, construccion, reglamento, sct, sala, reserva", 
                                  true, "Estante RESERVA-01");
        Ejemplar ejCivRes_1 = new Ejemplar("CIV-RES-01", service.generarSiguienteFolioTarjeta(), lCivRes, 1, EstadoLibro.CONSULTA_INTERNA, "Nuevo - Solo Sala");
        lCivRes.agregarEjemplar(ejCivRes_1);

        // --- INGENIERÍA INDUSTRIAL ---
        Libro lInd1 = new Libro("978-607-32-4115-1", "Investigación de Operaciones", "Hamdy A. Taha", "Pearson", 2021, 
                                "Optimización", Carrera.INGENIERIA_INDUSTRIAL, "658.403 TAH",
                                "investigacion de operaciones, programacion lineal, simplex, redes, transporte, optimizacion", 
                                false, "Estante IND-B1");
        Ejemplar ejInd1_1 = new Ejemplar("IND-101", service.generarSiguienteFolioTarjeta(), lInd1, 1, EstadoLibro.DISPONIBLE, "Excelente");
        Ejemplar ejInd1_2 = new Ejemplar("IND-102", service.generarSiguienteFolioTarjeta(), lInd1, 2, EstadoLibro.DISPONIBLE, "Excelente");
        Ejemplar ejInd1_3 = new Ejemplar("IND-103", service.generarSiguienteFolioTarjeta(), lInd1, 3, EstadoLibro.DISPONIBLE, "Bueno");
        lInd1.agregarEjemplar(ejInd1_1);
        lInd1.agregarEjemplar(ejInd1_2);
        lInd1.agregarEjemplar(ejInd1_3);

        Libro lInd2 = new Libro("978-607-15-0985-7", "Control Estadístico de la Calidad y Seis Sigma", "H. Gutiérrez Pulido", "McGraw-Hill", 2022, 
                                "Calidad", Carrera.INGENIERIA_INDUSTRIAL, "658.562 GUT",
                                "calidad, seis sigma, graficos de control, spc, muestreo, procesos", 
                                false, "Estante IND-B2");
        Ejemplar ejInd2_1 = new Ejemplar("IND-201", service.generarSiguienteFolioTarjeta(), lInd2, 1, EstadoLibro.DISPONIBLE, "Excelente");
        Ejemplar ejInd2_2 = new Ejemplar("IND-202", service.generarSiguienteFolioTarjeta(), lInd2, 2, EstadoLibro.DISPONIBLE, "Bueno");
        lInd2.agregarEjemplar(ejInd2_1);
        lInd2.agregarEjemplar(ejInd2_2);

        Libro lIndRes = new Libro("978-000-000-002-2", "Manual de Seguridad e Higiene Industrial y Normas STPS", "Comité de Seguridad FIT", "UAT", 2024, 
                                  "Seguridad", Carrera.INGENIERIA_INDUSTRIAL, "658.382 MAN",
                                  "seguridad, higiene, stps, ergonomia, proteccion, reserva", 
                                  true, "Estante RESERVA-02");
        Ejemplar ejIndRes_1 = new Ejemplar("IND-RES-01", service.generarSiguienteFolioTarjeta(), lIndRes, 1, EstadoLibro.CONSULTA_INTERNA, "Uso Exclusivo Laboratorio");
        lIndRes.agregarEjemplar(ejIndRes_1);

        // --- NEGOCIOS Y TECNOLOGÍAS ---
        Libro lNeg1 = new Libro("978-607-744-890-7", "Comercio Internacional e Incoterms 2020", "Carlos Morales", "Trillas", 2022, 
                                "Aduanas", Carrera.NEGOCIOS_INTERNACIONALES, "382.01 MOR",
                                "comercio internacional, incoterms, aduanas, exportacion, logistica, flete", 
                                false, "Estante NEG-C1");
        Ejemplar ejNeg1_1 = new Ejemplar("NEG-101", service.generarSiguienteFolioTarjeta(), lNeg1, 1, EstadoLibro.DISPONIBLE, "Excelente");
        Ejemplar ejNeg1_2 = new Ejemplar("NEG-102", service.generarSiguienteFolioTarjeta(), lNeg1, 2, EstadoLibro.DISPONIBLE, "Excelente");
        lNeg1.agregarEjemplar(ejNeg1_1);
        lNeg1.agregarEjemplar(ejNeg1_2);

        Libro lNeg2 = new Libro("978-607-15-0810-2", "Administración Estratégica y Política de Negocios", "Thomas L. Wheelen", "Pearson", 2020, 
                                "Estrategia", Carrera.NEGOCIOS_INTERNACIONALES, "658.401 WHE",
                                "administracion, estrategia, foda, negocios, liderazgo, competitividad", 
                                false, "Estante NEG-C2");
        Ejemplar ejNeg2_1 = new Ejemplar("NEG-201", service.generarSiguienteFolioTarjeta(), lNeg2, 1, EstadoLibro.DISPONIBLE, "Excelente");
        lNeg2.agregarEjemplar(ejNeg2_1);

        // --- TRONCO COMÚN ---
        Libro lTc1 = new Libro("978-607-481-777-5", "Cálculo de Una Variable: Trascendentes Tempranas", "James Stewart", "Cengage", 2022, 
                               "Matemáticas", Carrera.TRONCO_COMUN, "515.1 STE",
                               "calculo, derivadas, integrales, funciones, limites, matematicas", 
                               false, "Estante BAS-D1");
        Ejemplar ejTc1_1 = new Ejemplar("BAS-101", service.generarSiguienteFolioTarjeta(), lTc1, 1, EstadoLibro.DISPONIBLE, "Bueno");
        Ejemplar ejTc1_2 = new Ejemplar("BAS-102", service.generarSiguienteFolioTarjeta(), lTc1, 2, EstadoLibro.DISPONIBLE, "Bueno");
        Ejemplar ejTc1_3 = new Ejemplar("BAS-103", service.generarSiguienteFolioTarjeta(), lTc1, 3, EstadoLibro.DISPONIBLE, "Bueno");
        lTc1.agregarEjemplar(ejTc1_1);
        lTc1.agregarEjemplar(ejTc1_2);
        lTc1.agregarEjemplar(ejTc1_3);

        // Registrar libros
        service.getLibros().add(lCiv1);
        service.getLibros().add(lCiv2);
        service.getLibros().add(lCivRes);
        service.getLibros().add(lInd1);
        service.getLibros().add(lInd2);
        service.getLibros().add(lIndRes);
        service.getLibros().add(lNeg1);
        service.getLibros().add(lNeg2);
        service.getLibros().add(lTc1);

        // Registrar todos los ejemplares
        for (Libro l : service.getLibros()) {
            service.getEjemplares().addAll(l.getEjemplares());
        }

        // ==========================================
        // 3. PRÉSTAMOS PREVIOS Y CASOS DE PRUEBA
        // ==========================================

        // Caso A: Préstamo activo normal (Juan Pérez se llevó Mecánica de Suelos)
        service.registrarPrestamo(u1, ejCiv1_1, "Mariana Rojas", LocalDate.now().minusDays(1));

        // Caso B: Multa previa ($40 pesos: 2 días hábiles de retraso a $20/día)
        Prestamo pVencido = new Prestamo("PR-2026-HIST01", u2, ejCiv2_1, 
                                         LocalDate.now().minusDays(8), 
                                         LocalDate.now().minusDays(4), "Mariana Rojas");
        pVencido.setEstado(EstadoPrestamo.DEVUELTO_CON_RETRASO);
        pVencido.setFechaDevolucionReal(LocalDate.now().minusDays(2));
        pVencido.setDiasHabilesRetraso(2);
        
        Multa m1 = new Multa("MUL-0001", pVencido, u2, 40.00, LocalDate.now().minusDays(2), 
                             "Retraso de 2 días hábiles en entrega de Diseño de Estructuras ($20/día)");
        pVencido.setMultaAsociada(m1);
        service.getPrestamos().add(pVencido);
        service.getMultas().add(m1);
        u2.getHistorialMultas().add(m1);

        // Caso C: Alumno Luis García (2022002) al tope de 3 libros
        service.registrarPrestamo(u4, ejInd1_1, "Jesus Vázquez", LocalDate.now().minusDays(1));
        service.registrarPrestamo(u4, ejInd1_2, "Jesus Vázquez", LocalDate.now().minusDays(1));
        service.registrarPrestamo(u4, ejInd2_1, "Jesus Vázquez", LocalDate.now().minusDays(1));

        // ==========================================
        // 4. REGISTROS DE ACCESO INICIALES (RECEPCIÓN Y CÓMPUTO)
        // ==========================================
        service.registrarAccesoBiblioteca(u1, TipoServicio.USO_COMPUTADORA, "PC-04", 2);
        service.registrarAccesoBiblioteca(u3, TipoServicio.CONSULTA_SALA, null, 0);
        service.registrarAccesoBiblioteca(u5, TipoServicio.USO_COMPUTADORA, "PC-07", 1);
    }
}
