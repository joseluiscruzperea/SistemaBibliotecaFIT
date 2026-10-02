# DOCUMENTO DE ESPECIFICACIÓN DE REQUERIMIENTOS DE SOFTWARE (SRS)
## SISTEMA INTEGRAL DE BIBLIOTECA - FACULTAD DE INGENIERÍA (FIT)
### UNIVERSIDAD AUTÓNOMA DE TAMAULIPAS (UAT)
#### Basado en la Metodología Abbott y Análisis Orientado a Objetos (Capítulo 3)
**Versión 2.0 (Actualizada con las Observaciones del Comité Revisor)**

---

## 1. INTRODUCCIÓN Y REQUERIMIENTOS ACTUALIZADOS

El presente documento formaliza los requerimientos del **Sistema Integral de Biblioteca** para la **Facultad de Ingeniería (FIT - UAT)**, incorporando las modificaciones normativas y técnicas aprobadas en la revisión de proyecto:

1. **Clasificación Dewey y Tarjetas Físicas con Folio Único Consecutivo:** El acervo se organiza bajo el Sistema de Clasificación Decimal Dewey. Cada copia física tiene asociada una Tarjeta de Préstamo con un consecutivo numérico único e irrepetible (`TARJ-0001`, `TARJ-0002`...).
2. **Plazo Estricto de 2 Días Hábiles:** El préstamo a domicilio es por 2 días hábiles (fines de semana no cuentan). Si el libro se solicita en viernes o fin de semana, se devuelve el martes.
3. **Módulo de Prórrogas / Renovaciones (Solo Matrícula):** El estudiante puede renovar su préstamo acudiendo a ventanilla **únicamente con su matrícula** (no necesita llevar el libro físicamente). Cada prórroga otorga **2 días hábiles** adicionales. Se permite un **máximo estricto de 2 prórrogas** por préstamo; concluida la segunda, el sistema bloquea cualquier extensión y exige la devolución física.
4. **Regla de Enfriamiento (Cooldown de 1 Día Hábil):** Una vez que un alumno devuelve un libro, debe transcurrir al menos 1 día hábil para que ese mismo alumno pueda volver a solicitar el mismo título (evitando acaparamientos).
5. **Tasa de Multa Actualizada a $20.00 Pesos Diarios:** La sanción económica por retraso aumenta a razón de **$20.00 pesos por cada día hábil de mora**.
6. **Módulo de Registro de Entrada / Acceso a Biblioteca:** Los usuarios se registran en recepción únicamente con su matrícula y seleccionan:
   * **Consulta en Sala / Cubículo de Lectura**.
   * **Uso de Computadora en Centro de Cómputo**, donde se les asigna equipo (`PC-01` a `PC-15`) y tiempo en horas (1, 2 o 3 horas) con hora límite de entrega.
7. **Catálogo con Lector Actual y Recomendador de Temas:** El catálogo visualiza la ficha del libro, ISBN, estante, clasificación Dewey y el **Lector Actual** (si está prestado, indica quién lo tiene y cuándo lo regresa). Incorpora un motor de búsqueda por **palabras clave** y **recomendaciones temáticas automáticas**.

---

## 2. APLICACIÓN DE LA METODOLOGÍA ABBOTT

### 2.1. Análisis Gramatical de Oraciones (Sujeto + Verbo + Objeto)

| # | Oración de Requisito del Sistema | Sujeto (Actor) | Acción (Verbo) | Objeto (Entidad / Dominio) |
|---|---|---|---|---|
| 1 | El Alumno solicita acceso a biblioteca con su matrícula. | Alumno | solicita | Acceso con Matrícula |
| 2 | El sistema asigna computadora y horas autorizadas de uso. | Sistema | asigna | Computadora y Horas |
| 3 | El Auxiliar registra préstamo asociando matrícula y tarjeta Dewey. | Auxiliar | registra | Préstamo con Tarjeta Única |
| 4 | El sistema calcula la fecha de retorno en 2 días hábiles. | Sistema | calcula | Plazo (2 Días Hábiles) |
| 5 | El Alumno solicita prórroga de libro únicamente con su matrícula. | Alumno | solicita | Prórroga solo con Matrícula |
| 6 | El sistema valida que el préstamo no exceda 2 prórrogas. | Sistema | valida | Límite de 2 Prórrogas |
| 7 | El sistema suma 2 días hábiles a la fecha límite. | Sistema | suma | 2 Días Hábiles Adicionales |
| 8 | El sistema impide préstamo inmediato del mismo libro (1 día cooldown).| Sistema | restringe | Préstamo Reincidente (1 día) |
| 9 | El sistema calcula la multa a razón de $20 pesos por día hábil. | Sistema | calcula | Multa de $20/día hábil |
| 10 | El Alumno busca libros mediante palabras clave o temas recomendados. | Alumno | busca | Palabras Clave y Temas |
| 11 | El sistema muestra el lector actual que tiene en posesión la copia. | Sistema | muestra | Lector Actual del Ejemplar |
| 12 | El Auxiliar cobra la multa en caja y desbloquea al alumno. | Auxiliar | cobra | Multa en Efectivo |
| 13 | El sistema bloquea la constancia de no adeudo si hay sanciones. | Sistema | bloquea | Carta de No Adeudo |

---

## 3. ESPECIFICACIÓN DETALLADA DE CASOS DE USO (SRS)

### CU-01 Registrar Préstamo con Tarjeta Dewey (2 Días Hábiles)
* **Actor Principal:** Auxiliar de Mostrador.
* **Objetivo:** Registrar la salida de un libro vinculando la matrícula del lector y el folio de tarjeta Dewey.
* **Precondiciones:** Lector activo, sin multas y dentro de su cuota. Ejemplar disponible y no sujeto a cooldown.
* **Regla de Plazo:** Se asignan **2 días hábiles**. Fines de semana no cuentan:
  * Si sale viernes $\rightarrow$ entrega el martes.
  * Si sale sábado o domingo $\rightarrow$ entrega el martes.
  * Si sale lunes $\rightarrow$ entrega el miércoles.
* **Postcondiciones:** La tarjeta y el ejemplar pasan a estado `PRESTADO`, registrando la matrícula del lector actual.

### CU-02 Solicitar Prórroga de Préstamo (Solo con Matrícula)
* **Actor Principal:** Alumno / Auxiliar de Mostrador.
* **Objetivo:** Extender la fecha de devolución sin necesidad de presentar el libro físico en ventanilla.
* **Descripción:** El alumno acude al mostrador únicamente con su credencial/matrícula. El sistema despliega sus préstamos activos y el contador de prórrogas (`numeroProrrogas`).
* **Reglas:**
  * Máximo **2 prórrogas permitidas**.
  * Cada prórroga concede **2 días hábiles adicionales**.
  * Si `numeroProrrogas == 2`, el sistema rechaza la solicitud y notifica que debe devolver el libro físicamente.
  * Si el libro ya se encuentra vencido, no se autoriza prórroga (debe pagar multa).

### CU-03 Registrar Devolución y Cooldown de 1 Día
* **Actor Principal:** Auxiliar de Mostrador.
* **Objetivo:** Recibir el libro, calcular mora si aplica y activar regla de enfriamiento.
* **Cálculo de Multa:** Si fecha de retorno > fecha límite, `multa = diasHabilesRetraso * $20.00 pesos`.
* **Regla de Cooldown:** El sistema registra la fecha de devolución para esa combinación de `Matrícula + ISBN`. Si ese mismo lector intenta pedir el mismo título antes de que pase 1 día hábil, el sistema bloquea el préstamo.

### CU-04 Registro de Entrada y Asignación de Computadoras
* **Actor Principal:** Visitante (Alumno/Docente) / Recepcionista.
* **Objetivo:** Registrar la asistencia a la biblioteca y el control de equipos de cómputo.
* **Descripción:** Se captura la matrícula del usuario. Si solicita **Consulta en Sala**, se asienta el acceso. Si solicita **Uso de Computadora**, se selecciona el equipo (`PC-01` a `PC-15`) y la cantidad de horas autorizadas (1 a 4 horas), calculando la hora límite de sesión.

### CU-05 Búsqueda Temática y Recomendador de Catálogo
* **Actor Principal:** Lector / Coordinador de Carrera.
* **Objetivo:** Localizar libros por palabras clave, áreas de especialidad y consultar el lector actual.
* **Funcionalidad:** El sistema provee un selector con los temas recomendados de la facultad (Geotecnia, Estructuras, Investigación de Operaciones, Calidad, Incoterms, Finanzas, Cálculo) y expone la clasificación Dewey y quién tiene prestada cada copia.

---

## 4. REGLAS DE NEGOCIO ACTUALIZADAS (RN)

| ID Regla | Enunciado Normativo |
|:---|:---|
| **RN-01** | **Cuota Máxima:** Hasta 3 libros para alumnos y hasta 5 para docentes. |
| **RN-02** | **Plazo de 2 Días Hábiles:** Todo préstamo ordinario tiene vigencia de 2 días hábiles. Si se adquiere el viernes, debe entregarse el martes. |
| **RN-03** | **Consulta Interna / Dewey Sala:** Obras de reserva y normas técnicas no salen a domicilio bajo ninguna circunstancia. |
| **RN-04** | **Tarifa de Multa de $20/Día:** Por cada día hábil de retraso en la entrega de un libro se genera una sanción acumulativa de **$20.00 pesos mexicanos**. |
| **RN-05** | **Bloqueo Inmediato por Adeudo:** Usuarios con cualquier importe de multa pendiente no pueden solicitar nuevos libros. |
| **RN-06** | **Prórrogas Limitadas (Máx 2):** Se autorizan hasta 2 prórrogas de 2 días hábiles cada una, solicitadas únicamente con la matrícula. La tercera prórroga está prohibida. |
| **RN-07** | **Regla de Cooldown (1 Día Hábil):** Al devolver un libro, debe transcurrir al menos 1 día hábil para que el mismo usuario vuelva a solicitar el mismo título. |
| **RN-08** | **Folio Único Consecutivo de Tarjeta Dewey:** Cada ejemplar físico se identifica con un folio consecutivo irrepetible (`TARJ-XXXX`). |
| **RN-09** | **Asignación Controlada de Cómputo:** Las computadoras de biblioteca se asignan por matrícula con tiempo en horas delimitado. |
| **RN-10** | **Carta de No Adeudo para Titulación:** Requisito indispensable que exige solvencia al 100% en biblioteca. |
