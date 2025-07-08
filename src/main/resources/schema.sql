-- Schema para el Sistema de Monitoreo de Calidad del Aire - Arequipa
-- Base de datos: H2 (desarrollo) / PostgreSQL (producción)

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nombre_completo VARCHAR(100),
    telefono VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'CIUDADANO',
    tipo_sensibilidad VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_ultimo_acceso TIMESTAMP,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de estaciones de monitoreo
CREATE TABLE IF NOT EXISTS estaciones (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    latitud DECIMAL(10,7) NOT NULL,
    longitud DECIMAL(10,7) NOT NULL,
    distrito VARCHAR(100),
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_instalacion TIMESTAMP,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de mediciones
CREATE TABLE IF NOT EXISTS mediciones (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    estacion_id BIGINT NOT NULL,
    fecha_medicion TIMESTAMP NOT NULL,
    
    -- Contaminantes principales (μg/m³)
    pm25 DECIMAL(8,2),
    pm10 DECIMAL(8,2),
    no2 DECIMAL(8,2),
    o3 DECIMAL(8,2),
    co DECIMAL(8,2),
    so2 DECIMAL(8,2),
    
    -- Índice de Calidad del Aire
    aqi INTEGER,
    categoria_aqi VARCHAR(50),
    color_aqi VARCHAR(7),
    
    -- Datos meteorológicos
    temperatura DECIMAL(5,2),
    humedad INTEGER,
    presion DECIMAL(7,2),
    velocidad_viento DECIMAL(5,2),
    direccion_viento INTEGER,
    
    -- Metadatos
    fuente_datos VARCHAR(50),
    confiabilidad DECIMAL(3,2),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (estacion_id) REFERENCES estaciones(id)
);

-- Tabla de alertas
CREATE TABLE IF NOT EXISTS alertas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    estacion_id BIGINT,
    tipo VARCHAR(20) NOT NULL,
    severidad VARCHAR(20) NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    mensaje TEXT,
    valor_medido DECIMAL(8,2),
    umbral_configurado DECIMAL(8,2),
    contaminante VARCHAR(10),
    color_alerta VARCHAR(7),
    leida BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_lectura TIMESTAMP,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (estacion_id) REFERENCES estaciones(id)
);

-- Tabla de predicciones
CREATE TABLE IF NOT EXISTS predicciones (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    estacion_id BIGINT NOT NULL,
    fecha_prediccion TIMESTAMP NOT NULL,
    horizonte_horas INTEGER NOT NULL,
    
    -- Predicciones de contaminantes (μg/m³)
    pm25_predicho DECIMAL(8,2),
    pm10_predicho DECIMAL(8,2),
    no2_predicho DECIMAL(8,2),
    o3_predicho DECIMAL(8,2),
    co_predicho DECIMAL(8,2),
    aqi_predicho INTEGER,
    categoria_aqi_predicha VARCHAR(50),
    color_aqi_predicho VARCHAR(7),
    
    -- Niveles de confianza (0.0 - 1.0)
    confianza_pm25 DECIMAL(3,2),
    confianza_pm10 DECIMAL(3,2),
    confianza_global DECIMAL(3,2),
    
    -- Metadatos del modelo
    modelo_utilizado VARCHAR(100),
    version_modelo VARCHAR(50),
    parametros_modelo TEXT,
    
    -- Estado de la predicción
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    error_mensaje TEXT,
    fecha_calculo TIMESTAMP,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (estacion_id) REFERENCES estaciones(id)
);

-- Índices para optimización de consultas
CREATE INDEX IF NOT EXISTS idx_medicion_estacion_fecha ON mediciones(estacion_id, fecha_medicion);
CREATE INDEX IF NOT EXISTS idx_medicion_fecha ON mediciones(fecha_medicion);
CREATE INDEX IF NOT EXISTS idx_alerta_usuario ON alertas(usuario_id);
CREATE INDEX IF NOT EXISTS idx_alerta_fecha ON alertas(fecha_creacion);
CREATE INDEX IF NOT EXISTS idx_prediccion_estacion_fecha ON predicciones(estacion_id, fecha_prediccion);
CREATE INDEX IF NOT EXISTS idx_prediccion_fecha ON predicciones(fecha_prediccion);

-- Comentarios para documentación
COMMENT ON TABLE usuarios IS 'Usuarios del sistema con diferentes roles y configuraciones de sensibilidad';
COMMENT ON TABLE estaciones IS 'Estaciones de monitoreo de calidad del aire distribuidas en Arequipa';
COMMENT ON TABLE mediciones IS 'Mediciones de contaminantes y datos meteorológicos de las estaciones';
COMMENT ON TABLE alertas IS 'Alertas personalizadas para usuarios basadas en umbrales configurados';
COMMENT ON TABLE predicciones IS 'Predicciones de calidad del aire generadas por modelos de machine learning';