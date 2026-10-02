# ARQUITECTURA MODELO - VISTA - CONTROLADOR (MVC) CON JAVAFX Y FXML
## Sistema Integral de Biblioteca - Facultad de Ingeniería (FIT · UAT)

El proyecto ha sido estructurado estrictamente bajo el patrón de diseño de software **Modelo - Vista - Controlador (MVC)** utilizando **JavaFX con FXML** y hojas de estilo **CSS**, optimizado para **Eclipse IDE**.

---

### 1. MAPA DE LA ARQUITECTURA MVC

```
fit.biblioteca
│
├── modelo/                      <--- MODELO (M)
│   ├── Usuario.java             (Entidad Lector: Alumno / Docente con cuotas y semáforo)
│   ├── Libro.java               (Ficha bibliográfica, ISBN, carrera y estante)
│   ├── Ejemplar.java            (Copia física unitaria con código de barras)
│   ├── Prestamo.java            (Transacción, fecha límite, cálculo de días hábiles)
│   ├── Multa.java               (Sanción de $10/día hábil, cobro en caja y condonación)
│   ├── CorteCaja.java           (Arqueo de turno con folio y ticket imprimible)
│   ├── Carrera.java             (Ingeniería Civil, Industrial, Negocios, Tronco Común)
│   ├── TipoUsuario.java         (Cuotas reglamentarias: 3 libros / 5 libros)
│   └── EstadoLibro.java / EstadoPrestamo.java / EstadoMulta.java
│
├── servicio/                    <--- CAPA DE NEGOCIO Y REGLAS (Services)
│   ├── BibliotecaService.java   (Validación RN-01 a RN-05, reversión de error, corte y reportes)
│   └── ResultadoValidacion.java (Dictamen de aprobación o bloqueo)
│
├── controlador/                 <--- CONTROLADOR (C)
│   └── MainController.java      (Maneja eventos de usuario, inyección FXML y binding de tablas)
│
├── vista/                       <--- VISTA (V)
│   ├── MainView.fxml            (Definición declarativa de la interfaz gráfica XML)
│   ├── styles.css               (Estilos visuales, colores de semáforo verde/rojo y tarjetas)
│   ├── MainApp.java             (Lanzador de la aplicación JavaFX con FXMLLoader)
│   └── BibliotecaConsolaApp.java(Versión consola para ejecución inmediata de prueba)
│
└── datos/
    └── DatosIniciales.java      (Carga de acervo de Civil, Industrial, Negocios y casos de prueba)
```

---

### 2. CÓMO IMPORTAR Y EJECUTAR EN ECLIPSE IDE

1. **Importar el proyecto:**
   * Abre Eclipse $\rightarrow$ Menú **File** $\rightarrow$ **Import...**
   * Selecciona: **General** $\rightarrow$ **Existing Projects into Workspace** $\rightarrow$ clic en **Next**.
   * En **Select root directory**, presiona **Browse...** y selecciona la ruta:
     ```
     C:\Users\jlcru\.gemini\antigravity\scratch\SistemaBibliotecaFIT
     ```
   * Clic en **Finish**.

2. **Ejecutar la Vista FXML (`MainApp.java`):**
   * En el **Package Explorer**, navega a:
     `src` $\rightarrow$ `fit.biblioteca.vista` $\rightarrow$ **`MainApp.java`**.
   * Clic derecho $\rightarrow$ **Run As** $\rightarrow$ **Run Configurations...**
   * Pestaña **Arguments** $\rightarrow$ en el campo **VM arguments**, añade la ruta al SDK de JavaFX de tu equipo:
     ```text
     --module-path "C:\Users\jlcru\Downloads\openjfx-26.0.2_windows-x64_bin-sdk (1)\javafx-sdk-26.0.2\lib" --add-modules javafx.controls,javafx.fxml
     ```
   * Clic en **Run**.

3. **Ejecución alternativa en consola:**
   * Si deseas validar la lógica de negocio sin configurar parámetros VM:
   * Clic derecho en `fit.biblioteca.vista.BibliotecaConsolaApp.java` $\rightarrow$ **Run As** $\rightarrow$ **Java Application**.

---

### 3. FLUJO OPERATIVO Y PRUEBAS EN EL FXML

| Regla / Criterio | Acción en la Pantalla | Resultado Observado en el FXML |
| :--- | :--- | :--- |
| **Préstamo Normal (Verde)** | Matrícula: `2021001` + Código: `CIV-101` | Semáforo verde, fecha calculada brincando fines de semana, comprobante generado. |
| **Bloqueo por Multa (Rojo - RN-04)** | Matrícula: `2021002` | Semáforo rojo instantáneo: *"¡ALERTA! El usuario tiene MULTAS PENDIENTES por $30.00"*. |
| **Límite Alcanzado (Amarillo - RN-01)** | Matrícula: `2022002` | Semáforo amarillo: *"Límite de 3 libros alcanzado"*. Préstamo denegado. |
| **Consulta Interna (RN-03)** | Libro: `CIV-RES-01` | Alerta: *"Los libros de Reserva / Consulta Interna no pueden salir a domicilio"*. |
| **Deshacer Error (CA-03)** | Botón *"↩ Deshacer Último Préstamo"* | Cancela la última transacción, devuelve el libro a Disponible y libera la cuota. |
| **Devolución con Mora ($10/día)** | Pestaña Devoluciones $\rightarrow$ fecha retorno posterior | Alerta con cálculo automático de mora ($10 por día hábil) y alta en caja. |
| **Constancia de No Adeudo (RN-05)** | Pestaña Lectores $\rightarrow$ Seleccionar usuario sin deudas | Emisión de constancia oficial válida para titulación. Si debe, se bloquea. |
| **Corte de Turno** | Pestaña Reportes $\rightarrow$ *"Corte de Caja"* | Genera el ticket de arqueo con multas cobradas en efectivo y exporta a `.txt`. |
