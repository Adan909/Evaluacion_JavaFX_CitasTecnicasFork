# Sistema de Gestión de Citas Técnicas

## Tecnologías
- ANDREA SOFIA DE LA ROCA DELGADO
- NAZARIO ESCOTO LARGAESPADA
- ALFREDO DAVID MONTALVAN OSEJO
- JORGE ADAN ORTEGA TORRES


## Descripción
Sistema de escritorio desarrollado en Java con JavaFX y PostgreSQL para la administración de clientes y citas técnicas de la empresa simulada TecnoAsistencia Nicaragua S.A. La aplicación permite registrar y consultar información de los clientes, programar citas para servicios técnicos, modificar datos, eliminar registros y actualizar el estado de las citas. Cuenta con una interfaz gráfica que facilita la gestión de la información y una base de datos relacional que permite almacenarla de forma organizada.

## Tecnologías
- Java 21
- JavaFX
- PostgreSQL
- JDBC
- Maven
- Lombok

## Funcionalidades
- Registro, consulta, edición y eliminación de clientes.
- Gestión de citas técnicas asociadas a clientes.
- Actualización del estado de las citas.
- Búsqueda y filtrado de registros.
- Validación de datos.

## Instalación y ejecución
1. Instalar JDK 21, PostgreSQL e IntelliJ IDEA.
2. Crear la base de datos `gestion_citas` y ejecutar el script SQL incluido en el proyecto.
3. Configurar las credenciales de PostgreSQL en `DatabaseConnection.java`.
4. Abrir el proyecto en IntelliJ IDEA, cargar las dependencias de Maven y ejecutar `Launcher.java`.
