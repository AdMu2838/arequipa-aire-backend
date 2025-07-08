-- Datos iniciales para el Sistema de Monitoreo de Calidad del Aire - Arequipa

-- Usuario administrador por defecto
INSERT INTO usuarios (username, email, password, nombre_completo, role, tipo_sensibilidad, activo) VALUES
('admin', 'admin@arequipa-aire.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPHqZGy3u', 'Administrador del Sistema', 'ADMIN', 'NORMAL', true);
-- Contraseña: admin123

-- Usuario de autoridad de ejemplo
INSERT INTO usuarios (username, email, password, nombre_completo, role, tipo_sensibilidad, activo) VALUES
('autoridad_arequipa', 'autoridad@municipalidad-arequipa.gob.pe', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPHqZGy3u', 'Autoridad Municipal Arequipa', 'AUTORIDAD', 'NORMAL', true);
-- Contraseña: admin123

-- Usuario ciudadano de ejemplo
INSERT INTO usuarios (username, email, password, nombre_completo, telefono, role, tipo_sensibilidad, activo) VALUES
('juan_perez', 'juan.perez@email.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPHqZGy3u', 'Juan Pérez García', '+51987654321', 'CIUDADANO', 'SENSIBLE', true);
-- Contraseña: admin123

-- Estaciones de monitoreo en diferentes distritos de Arequipa
INSERT INTO estaciones (nombre, descripcion, latitud, longitud, distrito, activa, fecha_instalacion) VALUES
('Estación Centro', 'Estación ubicada en el centro histórico de Arequipa', -16.4090, -71.5375, 'Cercado', true, '2023-01-15 10:00:00'),
('Estación Miraflores', 'Estación en el distrito de Miraflores', -16.3973, -71.5300, 'Miraflores', true, '2023-02-01 09:30:00'),
('Estación Cayma', 'Estación en la zona norte de Arequipa', -16.3833, -71.5500, 'Cayma', true, '2023-02-15 11:00:00'),
('Estación Paucarpata', 'Estación en zona industrial de Paucarpata', -16.4290, -71.5080, 'Paucarpata', true, '2023-03-01 14:00:00'),
('Estación Characato', 'Estación en zona rural de Characato', -16.4667, -71.4833, 'Characato', true, '2023-03-15 16:00:00'),
('Estación José Luis Bustamante y Rivero', 'Estación en zona residencial', -16.4200, -71.5300, 'José Luis Bustamante y Rivero', true, '2023-04-01 08:00:00');

-- Mediciones de ejemplo para las últimas 24 horas
-- Estación Centro - Calidad moderada
INSERT INTO mediciones (estacion_id, fecha_medicion, pm25, pm10, no2, o3, co, so2, aqi, categoria_aqi, color_aqi, temperatura, humedad, presion, velocidad_viento, direccion_viento, fuente_datos, confiabilidad) VALUES
(1, DATEADD('HOUR', -1, CURRENT_TIMESTAMP), 35.2, 45.8, 25.1, 80.5, 1200.3, 15.2, 85, 'Moderada', '#FFFF00', 22.5, 65, 1013.25, 8.5, 230, 'OpenWeatherMap', 0.95),
(1, DATEADD('HOUR', -2, CURRENT_TIMESTAMP), 32.8, 42.1, 22.8, 75.2, 1150.7, 14.8, 78, 'Moderada', '#FFFF00', 23.1, 62, 1012.80, 9.2, 225, 'OpenWeatherMap', 0.92),
(1, DATEADD('HOUR', -3, CURRENT_TIMESTAMP), 38.1, 48.3, 28.5, 85.1, 1280.5, 16.5, 92, 'Moderada', '#FFFF00', 21.8, 68, 1013.45, 7.8, 235, 'OpenWeatherMap', 0.88);

-- Estación Miraflores - Calidad buena
INSERT INTO mediciones (estacion_id, fecha_medicion, pm25, pm10, no2, o3, co, so2, aqi, categoria_aqi, color_aqi, temperatura, humedad, presion, velocidad_viento, direccion_viento, fuente_datos, confiabilidad) VALUES
(2, DATEADD('HOUR', -1, CURRENT_TIMESTAMP), 12.5, 25.3, 18.2, 65.8, 950.2, 8.5, 45, 'Buena', '#00E400', 21.2, 70, 1014.10, 12.3, 210, 'WAQI', 0.98),
(2, DATEADD('HOUR', -2, CURRENT_TIMESTAMP), 15.8, 28.7, 20.1, 70.2, 1020.8, 9.2, 52, 'Moderada', '#FFFF00', 20.8, 72, 1013.95, 11.8, 215, 'WAQI', 0.96),
(2, DATEADD('HOUR', -3, CURRENT_TIMESTAMP), 11.2, 22.1, 16.5, 62.3, 890.5, 7.8, 38, 'Buena', '#00E400', 21.5, 68, 1014.20, 13.1, 205, 'WAQI', 0.97);

-- Estación Paucarpata - Calidad insalubre para grupos sensibles
INSERT INTO mediciones (estacion_id, fecha_medicion, pm25, pm10, no2, o3, co, so2, aqi, categoria_aqi, color_aqi, temperatura, humedad, presion, velocidad_viento, direccion_viento, fuente_datos, confiabilidad) VALUES
(3, DATEADD('HOUR', -1, CURRENT_TIMESTAMP), 58.5, 85.2, 42.8, 110.5, 1850.3, 28.5, 135, 'Insalubre para grupos sensibles', '#FF7E00', 24.8, 55, 1012.15, 6.2, 180, 'OpenWeatherMap', 0.89),
(3, DATEADD('HOUR', -2, CURRENT_TIMESTAMP), 62.1, 88.7, 45.2, 115.8, 1920.7, 30.1, 142, 'Insalubre para grupos sensibles', '#FF7E00', 25.2, 52, 1011.80, 5.8, 175, 'OpenWeatherMap', 0.91),
(3, DATEADD('HOUR', -3, CURRENT_TIMESTAMP), 55.8, 82.5, 40.5, 105.2, 1780.2, 26.8, 128, 'Insalubre para grupos sensibles', '#FF7E00', 24.5, 58, 1012.30, 6.5, 185, 'OpenWeatherMap', 0.87);

-- Cayma - Calidad moderada
INSERT INTO mediciones (estacion_id, fecha_medicion, pm25, pm10, no2, o3, co, so2, aqi, categoria_aqi, color_aqi, temperatura, humedad, presion, velocidad_viento, direccion_viento, fuente_datos, confiabilidad) VALUES
(4, DATEADD('HOUR', -1, CURRENT_TIMESTAMP), 28.5, 38.2, 22.1, 72.8, 1100.5, 12.8, 72, 'Moderada', '#FFFF00', 19.8, 75, 1015.20, 15.2, 240, 'WAQI', 0.93),
(4, DATEADD('HOUR', -2, CURRENT_TIMESTAMP), 31.2, 41.5, 24.8, 78.2, 1180.2, 14.2, 78, 'Moderada', '#FFFF00', 19.5, 78, 1014.85, 14.8, 235, 'WAQI', 0.91),
(4, DATEADD('HOUR', -3, CURRENT_TIMESTAMP), 26.8, 35.8, 20.5, 68.5, 1050.8, 11.5, 68, 'Moderada', '#FFFF00', 20.1, 72, 1015.40, 16.1, 245, 'WAQI', 0.94);

-- Characato - Calidad buena (zona rural)
INSERT INTO mediciones (estacion_id, fecha_medicion, pm25, pm10, no2, o3, co, so2, aqi, categoria_aqi, color_aqi, temperatura, humedad, presion, velocidad_viento, direccion_viento, fuente_datos, confiabilidad) VALUES
(5, DATEADD('HOUR', -1, CURRENT_TIMESTAMP), 8.5, 18.2, 12.1, 55.8, 750.2, 5.8, 32, 'Buena', '#00E400', 18.2, 82, 1016.30, 18.5, 260, 'Estación Local', 0.99),
(5, DATEADD('HOUR', -2, CURRENT_TIMESTAMP), 9.8, 20.5, 13.8, 58.2, 780.5, 6.2, 35, 'Buena', '#00E400', 17.8, 85, 1015.95, 17.8, 255, 'Estación Local', 0.98),
(5, DATEADD('HOUR', -3, CURRENT_TIMESTAMP), 7.2, 16.8, 11.2, 52.5, 720.8, 5.2, 28, 'Buena', '#00E400', 18.5, 80, 1016.50, 19.2, 265, 'Estación Local', 0.99);

-- José Luis Bustamante y Rivero - Calidad moderada
INSERT INTO mediciones (estacion_id, fecha_medicion, pm25, pm10, no2, o3, co, so2, aqi, categoria_aqi, color_aqi, temperatura, humedad, presion, velocidad_viento, direccion_viento, fuente_datos, confiabilidad) VALUES
(6, DATEADD('HOUR', -1, CURRENT_TIMESTAMP), 42.5, 58.2, 32.1, 88.8, 1350.5, 18.8, 98, 'Moderada', '#FFFF00', 23.2, 60, 1013.80, 9.5, 220, 'OpenWeatherMap', 0.92),
(6, DATEADD('HOUR', -2, CURRENT_TIMESTAMP), 45.8, 62.5, 35.8, 92.2, 1420.2, 20.2, 105, 'Insalubre para grupos sensibles', '#FF7E00', 23.8, 57, 1013.45, 8.8, 215, 'OpenWeatherMap', 0.90),
(6, DATEADD('HOUR', -3, CURRENT_TIMESTAMP), 39.2, 54.8, 29.5, 85.5, 1280.8, 17.5, 92, 'Moderada', '#FFFF00', 22.8, 63, 1014.10, 10.2, 225, 'OpenWeatherMap', 0.94);

-- Alerta de ejemplo para el usuario sensible
INSERT INTO alertas (usuario_id, estacion_id, tipo, severidad, titulo, mensaje, valor_medido, umbral_configurado, contaminante, color_alerta, leida) VALUES
(3, 3, 'CALIDAD_AIRE', 'ALTA', 'Alerta de PM2.5', 'El nivel de PM2.5 en Estación Paucarpata ha alcanzado 58.5 μg/m³, superando su umbral configurado de 35.0 μg/m³', 58.5, 35.0, 'PM2.5', '#fd7e14', false);

-- Predicciones de ejemplo
INSERT INTO predicciones (estacion_id, fecha_prediccion, horizonte_horas, pm25_predicho, pm10_predicho, no2_predicho, o3_predicho, co_predicho, aqi_predicho, categoria_aqi_predicha, color_aqi_predicho, confianza_pm25, confianza_pm10, confianza_global, modelo_utilizado, version_modelo, estado) VALUES
(1, DATEADD('HOUR', 6, CURRENT_TIMESTAMP), 24, 38.5, 48.2, 26.8, 82.5, 1250.8, 88, 'Moderada', '#FFFF00', 0.85, 0.82, 0.84, 'RandomForest_v2.1', '2.1.0', 'COMPLETADA'),
(2, DATEADD('HOUR', 6, CURRENT_TIMESTAMP), 24, 16.2, 28.5, 19.5, 68.8, 980.2, 55, 'Moderada', '#FFFF00', 0.92, 0.88, 0.90, 'RandomForest_v2.1', '2.1.0', 'COMPLETADA'),
(3, DATEADD('HOUR', 6, CURRENT_TIMESTAMP), 24, 65.8, 92.5, 48.2, 118.5, 1980.5, 148, 'Insalubre para grupos sensibles', '#FF7E00', 0.78, 0.75, 0.77, 'RandomForest_v2.1', '2.1.0', 'COMPLETADA');