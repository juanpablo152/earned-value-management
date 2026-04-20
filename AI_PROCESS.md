# AI_PROCESS.md

herramientas y prompt usados durante mi desarollo.

## Herramientas usadas

- CursorAI: la principal porque me proporciona mejores autocompletados, contextos y mejor entendimiento del código que estoy implementando de igual manera su modo agente es superior a mi parecer que el de copilot.
- Github Copilot: La use durante la fase de desarrollo porque contiene varios modelos que son eficientes para desarrollar y también estoy familiarizado ya con su uso en el IDE para optimizar tiempos.
- Gemini pro: Lo use para el entendimiento de los conceptos de EVM, use Gemini porque a mi parecer me da explicaciones centradas, me proporciona gráficos de ejemplo interactivos donde fue mucho más rápido entender todo.

## Todos los prompt usados

### 1. Quiero entender qué es EVM (Earned Value Management) en gestión de proyectos. Explícamelo paso a paso incluyendo:
- Definición general  
- Para qué sirve en proyectos  
- Conceptos clave: PV, EV, AC  
- Indicadores: CPI, SPI, CV, SV  
- Fórmulas y cómo interpretarlas  
- Ejemplo numérico sencillo  
- Cómo se usa en proyectos reales  
- Errores comunes al aplicarlo

Explícalo en lenguaje claro y con ejemplos prácticos.


### 2. Crea el archivo docker para levantar el backend junto con la bd postgres, si es necesario por el momento solo crea datos mock para que se pueda ejecutar el backend y no de error por falta de los datos de conexión.

### 3. Necesito que generes campos para un DTO y Request usando Java Record con validaciones Jakarta Validation.

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


### 4. necesito que implementes los crud para los servicios @backend/evm/src/main/java/com/earnedvaluemanagement/evm/service/impl/ActivityServiceImpl.java y @backend/evm/src/main/java/com/earnedvaluemanagement/evm/service/impl/ProjectServiceImpl.java 

### 5. Necesito que revises la lógica implementada y realices una implementación de manejo de errores global, de ser requerido utiliza enums y ajusta el manejo de las respuestas según las entidades y la lógica implementada.

### 6. Configura swagger api para testear todos mis endpoint.

### 7. Configura los permisos de CORS para poder realizar peticiones desde un front.

### 8. Revisalos test actuales, luego de ser revisados realiza lo siguiente:

1. vas a corregir los test que necesiten para que queden correctamente implementados.
2. añade más test que validen la implementación de los servicios completos.

### 9. Implementa los servicios teniendo en cuenta las interfaces de la carpeta @frontend/earned-value-management-frontend/src/app/core/models/.

### 10. Realiza la configuración de un archivo proxy.conf ya que estoy teniendo problemas de CORS con mis peticiones desde front.

### 11. Agrega una navbar sencilla en el top fija solo para redirigir a proyectos con un logo sencillo a la izquierda y el redireccionamiento a la derecha.

### 12. implementa para la visualización del dashboard gráficos para las actividades de los proyectos siguiendo el uso de la librería chart.js.

### 13. vas a realizar los siguienets ajustes visuales usando tailwind:

1. Vas a aplicar una paleta de pocos colores simple, visualmente agradable sin animaciones para ajustar todos los estilos de los componentes.
2. añadir iconos para los elementos donde visualmente sea necesario entender que realiza una acción.
3. agrega colores y ajustes visuales para los gráficos de chart.js.
3. Ajustar estos estilos con responsive.

### 14. necesito crear un service que maneje los mensajes de errores para los input de los componentes form @frontend/earned-value-management-frontend/src/app/features/dashboard/activity-form/activity-form.component.ts y @frontend/earned-value-management-frontend/src/app/features/projects/project-form/project-form.component.ts 

## Cómo aprendiste EVM: qué le preguntaste a la IA, cómo validaste que entendiste las fórmulas antes de implementarlas.  

Para aprender EVM use el siguiente prompt con Gemini pro:

Quiero entender qué es EVM (Earned Value Management) en gestión de proyectos. Explícamelo paso a paso incluyendo:
- Definición general  
- Para qué sirve en proyectos  
- Conceptos clave: PV, EV, AC  
- Indicadores: CPI, SPI, CV, SV  
- Fórmulas y cómo interpretarlas  
- Ejemplo numérico sencillo  
- Cómo se usa en proyectos reales  
- Errores comunes al aplicarlo

El cúal me proporcionó la infromación importante sobre el tema y un ejemplo con un gráfico sobre su funcionamiento, luego en el ejemplo númerico que me dio simplemente valide los cálculos a la par que iba siguiente el ejemplo para poder comprender como tal funcionamiento de la metodología.

## Dos decisiones donde no seguiste lo que la IA te sugirió, explicando qué propuso y por qué tomaste un camino diferente. Cómo verificaste que los cálculos son correctos — no solo que el código funciona, sino que los números tienen sentido.

1. Al realizar la implementación de la lógica de negocio en los servicios planteba manejar errores personalizados en cada función donde pues preferí que hubiera un manejador de erorres globales para más facilidad y entendimiento del código. 
2. Cuando pedi implementar los servicios en el front una vez defini los modelos de interfaces me falto explicar que los implementará con LastValueFrom en vez de Observable ya que no me voy a suscribir a peticiones sencillas de un CRUD que no necesito estar observando, simplemente requiero que se ejecuten me den la data y ya, entonces tuve que ajustarla manualmente a LastValueFrom.  

## Una decisión de arquitectura que tomaste de forma independiente.  

Antes de iniciar el proyecto ya iba enfocado por decisión propia a en el backend usar una arquitectura limpia por capas para mantener delegada las responsabilidades y separada mi lógica de negocio del acceso a datos y del controlador y para el frontend iba decidido a usar una arquitectura orientada a features que es lo que más he usado con Angular para realizar proyectos escalables y de igual manera también el orden en el que ejecute no inicie front hasta que tenia completado el backend.

## Una reflexión honesta sobre qué harías diferente si repitieras el ejercicio. 

Si repitiera el ejercicio lo que haría sería añadir una seed que ya tenga datos cargados de pronto para optimizar más rápido la validación del ejercicio en ejecución, tal vez plantearme otra arquitectura que sean más optimas para el ejercicio y pude realizar una mayor separación de componentes donde puedo extraer layouts para lo que es dashboard y proyectos, también separar el componente input para reutilizarlo en ambos form.