# Sistema de Gestión de Citas Técnicas

## Descripción
Aplicación de escritorio desarrollada en Java con JavaFX y JDBC, conectada a PostgreSQL, para gestionar clientes y citas de mantenimiento y reparación de equipos informáticos de la empresa simulada TecnoAsistencia Nicaragua S.A.

El sistema permite administrar los registros de clientes y citas mediante operaciones CRUD (crear, consultar, modificar y eliminar), mantener la relación entre clientes y citas, buscar clientes, filtrar citas por fecha y estado, validar los datos ingresados y mostrar mensajes de confirmación y error. La información se almacena en PostgreSQL para garantizar su persistencia.

## Tecnologías
- Java 21
- JavaFX y FXML
- PostgreSQL
- JDBC
- Maven
- Git y GitHub
- Lombok

## Funcionalidades
- Gestión completa de clientes y citas técnicas.
- Asociación de citas con clientes existentes.
- Búsqueda de clientes por nombre, apellido o correo.
- Filtros de citas por fecha y estado.
- Validación de campos obligatorios y correos duplicados.
- Confirmación de eliminaciones y manejo de errores.
- Almacenamiento persistente en PostgreSQL.

## Requisitos
- JDK 21.
- PostgreSQL.
- IntelliJ IDEA.
- Git.

## Instalación y configuración
1. Clonar el repositorio y abrir el proyecto en IntelliJ IDEA.
2. Crear la base de datos `gestion_citas`.
3. Ejecutar el script `gestion_citas.sql` para crear las tablas y relaciones.
4. Configurar el usuario, la contraseña y la conexión a PostgreSQL en `DatabaseConnection.java`.
5. Cargar las dependencias de Maven y ejecutar `Launcher.java`.

**Importante:** No subir contraseñas reales al repositorio. Cada integrante debe configurar sus propias credenciales locales.

## Integrantes y responsabilidades

| Integrante | Usuario de GitHub | Responsabilidades |
|---|---|---|
| ANDREA SOFIA DE LA ROCA DELGADO | andreadelaroca | Administracion de base de datos |
| NAZARIO ESCOTO LARGAESPADA | Naza2006 | Documentador y tester de google |
| ALFREDO DAVID MONTALVAN OSEJO | wetalff | Desarrollador de videojuegos Indie |
| JORGE ADAN ORTEGA TORRES | Adan909 | Diseñador de la interfaz |




