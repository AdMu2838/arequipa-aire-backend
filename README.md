# Sistema de Monitoreo de Calidad del Aire - Arequipa Backend

## 🌟 Descripción

Backend completo del **Sistema Inteligente de Monitoreo y Predicción de Calidad del Aire Urbano** para la ciudad de Arequipa, desarrollado con Spring Boot 3.2+ y Java 17.

Este sistema proporciona APIs REST completas para el monitoreo de calidad del aire, con integración a múltiples fuentes de datos, sistema de alertas inteligente y predicciones usando machine learning.

## 🚀 Características Principales

### ✅ Implementado

- **🏗️ Arquitectura Spring Boot 3.2+** con Java 17
- **🗄️ Modelo de datos JPA completo** con entidades optimizadas
- **🔐 Autenticación JWT** con Spring Security y roles de usuario
- **📊 APIs REST documentadas** con OpenAPI/Swagger
- **📈 Cálculo de AQI** según estándar EPA
- **🚨 Sistema de alertas personalizado** por tipo de sensibilidad
- **🗃️ Cache inteligente** con Caffeine
- **🛡️ Manejo global de errores** y validaciones
- **📍 6 estaciones de monitoreo** con datos de ejemplo
- **📱 Endpoints públicos y autenticados**

### 🔧 Tecnologías

- **Spring Boot 3.2.0** - Framework principal
- **Java 17** - Lenguaje de programación
- **Spring Security** - Autenticación y autorización JWT
- **Spring Data JPA** - Persistencia de datos
- **H2 Database** - Base de datos en memoria (desarrollo)
- **PostgreSQL** - Base de datos para producción
- **Caffeine** - Sistema de cache
- **OpenAPI 3** - Documentación de APIs
- **Maven** - Gestión de dependencias

## 📁 Estructura del Proyecto

```
src/main/java/com/arequipa/aire/backend/
├── ArequipaAireBackendApplication.java     # Aplicación principal
├── config/                                 # Configuraciones
│   ├── SecurityConfig.java                # Seguridad JWT
│   ├── OpenApiConfig.java                 # Documentación Swagger
│   └── CacheConfig.java                   # Configuración cache
├── controller/                             # Controladores REST
│   ├── AuthController.java                # Autenticación
│   └── CalidadAireController.java          # APIs calidad aire
├── service/                                # Lógica de negocio
│   ├── AuthService.java                   # Servicio autenticación
│   ├── CalidadAireService.java             # Servicio principal
│   ├── AlertasService.java                 # Gestión alertas
│   ├── EstacionService.java                # Gestión estaciones
│   └── UserDetailsServiceImpl.java         # Spring Security
├── repository/                             # Acceso a datos
│   ├── EstacionRepository.java             # Consultas estaciones
│   ├── MedicionRepository.java             # Consultas mediciones
│   ├── UsuarioRepository.java              # Gestión usuarios
│   ├── AlertaRepository.java               # Gestión alertas
│   └── PrediccionRepository.java           # Predicciones
├── entity/                                 # Entidades JPA
│   ├── Estacion.java                       # Estaciones de monitoreo
│   ├── Medicion.java                       # Mediciones de calidad
│   ├── Usuario.java                        # Usuarios del sistema
│   ├── Alerta.java                         # Alertas personalizadas
│   └── Prediccion.java                     # Predicciones ML
├── dto/                                    # Objetos de transferencia
│   ├── CalidadAireDTO.java                 # DTO calidad aire
│   ├── UsuarioDTO.java                     # DTO usuarios
│   ├── AuthResponseDTO.java                # DTO autenticación
│   └── ...                                 # Otros DTOs
├── util/                                   # Utilidades
│   ├── AQICalculator.java                  # Calculadora AQI EPA
│   └── JwtUtils.java                       # Utilidades JWT
├── security/                               # Seguridad
│   ├── JwtAuthenticationFilter.java        # Filtro JWT
│   └── JwtAuthenticationEntryPoint.java    # Entry point auth
└── exception/                              # Manejo de errores
    └── GlobalExceptionHandler.java         # Manejo global errores
```

## 🌐 APIs Disponibles

### 🔓 Endpoints Públicos

- `GET /api/calidad-aire/actual` - Datos actuales de todas las estaciones
- `GET /api/calidad-aire/mapa` - Datos optimizados para mapas
- `GET /api/calidad-aire/estacion/{id}` - Datos de estación específica
- `GET /api/estaciones` - Lista de estaciones activas
- `POST /api/auth/login` - Autenticación
- `POST /api/auth/registro` - Registro de usuarios

### 🔐 Endpoints Autenticados

- `GET /api/auth/me` - Perfil del usuario actual
- `PUT /api/auth/perfil` - Actualizar perfil
- `GET /api/calidad-aire/historico` - Datos históricos con paginación
- `GET /api/calidad-aire/recientes` - Mediciones recientes
- `GET /api/alertas/usuario/{id}` - Alertas por usuario

### 👮‍♂️ Endpoints para Autoridades

- `GET /api/calidad-aire/estadisticas/distritos` - Estadísticas por distrito
- `GET /api/calidad-aire/aqi-alto` - Mediciones con AQI alto

## 🏗️ Instalación y Configuración

### 📋 Prerrequisitos

- Java 17+
- Maven 3.8+
- (Opcional) PostgreSQL para producción

### 🚀 Inicio Rápido

1. **Clonar el repositorio**
```bash
git clone https://github.com/AdMu2838/arequipa-aire-backend.git
cd arequipa-aire-backend
```

2. **Compilar el proyecto**
```bash
mvn clean compile
```

3. **Ejecutar la aplicación**
```bash
mvn spring-boot:run
```

4. **Acceder a la documentación**
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- H2 Console: http://localhost:8080/api/h2-console

## 🔑 Usuarios de Prueba

### Administrador
- **Usuario:** `admin`
- **Password:** `admin123`
- **Rol:** ADMIN

### Autoridad Municipal
- **Usuario:** `autoridad_arequipa`
- **Password:** `admin123`
- **Rol:** AUTORIDAD

### Ciudadano
- **Usuario:** `juan_perez`
- **Password:** `admin123`
- **Rol:** CIUDADANO

## 📊 Estaciones de Monitoreo

El sistema incluye 6 estaciones distribuidas en Arequipa:

1. **Estación Centro** - Cercado (-16.4090, -71.5375)
2. **Estación Miraflores** - Miraflores (-16.3973, -71.5300)
3. **Estación Cayma** - Cayma (-16.3833, -71.5500)
4. **Estación Paucarpata** - Paucarpata (-16.4290, -71.5080)
5. **Estación Characato** - Characato (-16.4667, -71.4833)
6. **Estación JLByR** - José Luis Bustamante y Rivero (-16.4200, -71.5300)

## 🎨 Índice de Calidad del Aire (AQI)

El sistema calcula el AQI según el estándar EPA:

| AQI | Categoría | Color | Descripción |
|-----|-----------|-------|-------------|
| 0-50 | Buena | 🟢 Verde | Calidad satisfactoria |
| 51-100 | Moderada | 🟡 Amarillo | Aceptable para la mayoría |
| 101-150 | Insalubre para grupos sensibles | 🟠 Naranja | Precaución para sensibles |
| 151-200 | Insalubre | 🔴 Rojo | Todos pueden experimentar efectos |
| 201-300 | Muy insalubre | 🟣 Púrpura | Advertencia de salud |
| 301+ | Peligrosa | 🟤 Granate | Alerta de emergencia |

## 🔧 Configuración

### Variables de Entorno

```bash
# APIs externas (opcional para desarrollo)
OPENWEATHER_API_KEY=tu_api_key_openweather
WAQI_API_KEY=tu_api_key_waqi
ML_SERVICE_URL=http://localhost:8001

# Base de datos (PostgreSQL en producción)
DB_USERNAME=arequipa_user
DB_PASSWORD=arequipa_pass

# JWT
JWT_SECRET=tu_clave_secreta_jwt
```

### Perfiles de Configuración

- **dev** - Desarrollo con H2 en memoria
- **prod** - Producción con PostgreSQL
- **test** - Pruebas automatizadas

## 📈 Monitoreo y Observabilidad

### Spring Actuator Endpoints

- `/actuator/health` - Estado de salud del sistema
- `/actuator/info` - Información de la aplicación
- `/actuator/metrics` - Métricas de rendimiento

## 🔮 Próximos Pasos

- [ ] **Clientes APIs externas** (OpenWeatherMap, WAQI)
- [ ] **Microservicio ML** para predicciones
- [ ] **Schedulers** para actualización automática
- [ ] **WebSockets** para actualizaciones en tiempo real
- [ ] **Frontend React** con mapas interactivos
- [ ] **Despliegue en contenedores** Docker
- [ ] **CI/CD** con GitHub Actions

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 📞 Contacto

**Equipo Arequipa Aire**
- Email: soporte@arequipa-aire.com
- Web: https://arequipa-aire.com

---

⭐ **¡Dale una estrella al proyecto si te resulta útil!**