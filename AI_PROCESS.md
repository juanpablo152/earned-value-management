# 

## Prompt usados

1. Quiero entender qué es EVM (Earned Value Management) en gestión de proyectos. Explícamelo paso a paso incluyendo:
- Definición general  
- Para qué sirve en proyectos  
- Conceptos clave: PV, EV, AC  
- Indicadores: CPI, SPI, CV, SV  
- Fórmulas y cómo interpretarlas  
- Ejemplo numérico sencillo  
- Cómo se usa en proyectos reales  
- Errores comunes al aplicarlo

Explícalo en lenguaje claro y con ejemplos prácticos.


2. Crea el archivo docker para levantar el backend junto con la bd postgres, si es necesario por el momento solo crea datos mock para que se pueda ejecutar el backend y no de error por falta de los datos de conexión.

3. Necesito que generes campos para un DTO y Request usando Java Record con validaciones Jakarta Validation.

Genera el código para los siguientes campos:

1. name
- Tipo: String
- Requerido
- No vacío
- Máximo 255 caracteres
- Mensajes:
  - "Activity name is required"
  - "Activity name must not exceed 255 characters"

2. budgetAtCompletion
- Tipo: BigDecimal
- Requerido
- Valor mínimo: 0.0
- Mensajes:
  - "Budget at completion BAC is required"
  - "Budget at completion must be zero or positive"

3. plannedProgress
- Tipo: BigDecimal
- Requerido
- Rango: 0.0 a 100.0
- Mensajes:
  - "Planned progress is required"
  - "Planned progress must be between 0 and 100"

4. actualProgress
- Tipo: BigDecimal
- Requerido
- Rango: 0.0 a 100.0
- Mensajes:
  - "Actual progress is required"
  - "Actual progress must be between 0 and 100"

5. actualCost
- Tipo: BigDecimal
- Requerido
- Valor mínimo: 0.0
- Mensajes:
  - "Actual cost (AC) is required"
  - "Actual cost must be zero or positive"


4. necesito que implementes los crud para los servicios @ActivityServiceImpl.java y @ProjectServiceImpl.java

5. Necesito que revises la lógica implementada y realices una implementación de manejo de errores global, de ser requerido utiliza enums y ajusta el manejo de las respuestas según las entidades y la lógica implementada.

6. Configura swagger api para testear todos mis endpoint.