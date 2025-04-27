# GESTOR de Cuentas Bancarias 🏦

[![Java](https://img.shields.io/badge/Java-17-007396?logo=java&logoColor=white)](https://www.java.com/)  
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0.0-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)  
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)  
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.0.15-005F0F?logo=thymeleaf&logoColor=white)](https://www.thymeleaf.org/)  
[![Maven](https://img.shields.io/badge/Maven-3.9.0-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)  

Aplicación web desarrollada con **Spring Boot**, **MySQL** y **Thymeleaf** para gestionar clientes y cuentas bancarias.

## 📸 Capturas de pantalla

**Login:**

![Login](images/dfdd783a-a50d-474d-b740-32949c8c9444.png)

**Listado de cuentas:**

![Listado de cuentas](images/2cfe4ab1-5a1d-4c18-8c47-d60b86032ffb.png)

## 🚀 Tecnologías utilizadas

- Java 17
- Spring Boot
- Spring Security
- Thymeleaf
- MySQL
- Maven

## 🔗 Ruta de acceso

La aplicación está disponible en:  
[http://localhost:8080/](http://localhost:8080/)

## ⚙️ Configuración básica

Debes configurar tu archivo `application.properties` para la conexión a MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gestor_cuentas
spring.datasource.username=usuario
spring.datasource.password=contraseña
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=true
jwt.secret-key=SecretKey
```

> **Importante:** No subir datos sensibles (contraseñas reales).

## 📂 Clonar el proyecto

```bash
git clone https://github.com/JesusLuna2309/SpringBootCRUDLogin.git
```

## 🛠️ Funcionalidades

- Gestión completa (CRUD) de **clientes** y **cuentas bancarias**.  
- Relación **muchos a muchos** entre clientes y cuentas.  
- Cifrado de **IBAN** y **NIF** en las URLs.  
- Autenticación segura con **JWT** almacenado en **cookies HttpOnly**.  
- Ordenación de listados **insensible a tildes**.  
- Exportación de operaciones a **PDF**.  
- Manejo global de errores con vistas personalizadas.
- Exportación de datos .csv (próximamente).

## 🗂️ Estructura del Proyecto

src/
 └── main/
     ├── java/com/tarea6_luna_romero_jesus_psphlc/
     │    ├── config/          # Seguridad y JWT
     │    ├── controladores/   # Endpoints y vistas
     │    ├── entidades/       # Clases JPA
     │    ├── excepciones/     # Manejador global
     │    ├── jwt/             # Filtro y servicio JWT
     │    └── servicios/       # Lógica de negocio
     └── resources/
          ├── static/          # CSS, JS, imágenes
          ├── templates/       # Thymeleaf
          └── application.properties

## 📋 Estado del proyecto

✅ Proyecto funcional y en desarrollo activo.

## 📄 Licencia

Este proyecto está licenciado bajo la MIT License.

## ✉️ Contacto

Proyecto desarrollado por **Jesús Luna Romero**  
Repositorio: [https://github.com/JesusLuna2309/SpringBootCRUDLogin](https://github.com/JesusLuna2309/SpringBootCRUDLogin)


