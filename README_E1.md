# EcoCampus Circular - Entrega 1

EcoCampus Circular es un sistema integral de gestión de residuos, campañas ambientales y eco puntos. 
Gestiona personas, puntos ecologicos, reportes, rutas, campañas.

Esta es la primera entrega por lo que utiliza los conceptos de POO vistos hasta el momento
(encapsulamiento, herencia, polimorfismo, clases abstractas y interfaces).

## Integrantes
- Pablo Zuluaga Castrillón
- Isaac Buelvas Causil
- Pedro Aleman Girard
- Laura Jiménez Vásquez


## Importar el proyecto
- Paso 1: Guardar el zip del proyecto en nuestra carpeta de workspace
- Paso 2: En Eclipse buscamos file lo seleccionamos y deberiamos ver import
- Paso 3: Seleccionamos import, entramos a la carpeta general y le damos clic al boton de Existing Projects into Workspace, luego le damos Next
- Paso 4: Marcamos la opcion Select archive file, le damos browse para buscar y seleccionamos el zip que guardamos en la carpeta del Workspace
- Paso 5: Verificamos que el proyecto aparezca marcado con el check en la lista de proyectos detectados
- Paso 6: Le damos finish y ya queda lista la importacion del proyecto


## Ejecutar el proyecto
- Paso 1: Buscamos el paquete en el que se encuentra el main(co.edu.eia.ecocampus.modelo)
- Paso 2: Una vez estemos en el main le damos clic derecho Run As Java Application
- Paso 3: Para arrancar, la consola nos pedira la capacidad n de catalogos. 
- Paso 4: Para que el codigo sea probado de manera adecuada y pueda probar todos los casos solicitados el programa se ajustara a valores >= 10
- Paso 5: Si el usuario escribe algo que no es un numero, el programa usara 10 por defecto


## Como probar el proyecto
1. El primer caso crea dos puntos ecologicos y desactiva el punto ecologico 2 para probar validaciones
2. El segundo caso prueba el registro de personas, tiene un caso valido y otro que es rechazado por id duplicado
3. El tercer caso prueba los reportes, tiene un caso que es valido, otro que es rechazado por que usa un punto ecologico inactivo, uno rechazado por tener punto ecologico null
y por ultimo un cierre de reporte rechazado porque no hay recoleccion asociada
4. El cuarto caso prueba la Ruta de recoleccion, tiene un caso que rechaza la parada por usar un punto ecologico inactivo
5. El quinto caso prueba la recoleccion de materiales: Rechaza un residuo especial si el operador no esta habilitado, luego el operador es habilitado y el material aceptado.
Por ultimo el reporte es cerrado con exito, una vez existe la recoleccion con peso > 0. Al cerrar el reporte sucede el calculo de eco-puntos que se calculan a partir del 
peso y el material recolectado y ocurre el cierre de la ruta de recolección.
6. El ultimo caso prueba campaña, agrega actividades y participantes y rechaza una inscripcion duplicada.
7. Por ultimo el main tiene un resumen general que nos muestra: reportes pendientes por prioridad, rutas y campañas registradas, ranking de ecopuntos y un conteo total de objetos por catalogo.



