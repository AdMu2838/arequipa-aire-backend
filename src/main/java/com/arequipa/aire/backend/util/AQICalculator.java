package com.arequipa.aire.backend.util;

import org.springframework.stereotype.Component;

/**
 * Calculadora de Índice de Calidad del Aire (AQI) según estándar EPA.
 */
@Component
public class AQICalculator {

    /**
     * Estructura para almacenar información del AQI.
     */
    public static class AQIInfo {
        private final int aqi;
        private final String categoria;
        private final String color;
        private final String recomendacion;

        public AQIInfo(int aqi, String categoria, String color, String recomendacion) {
            this.aqi = aqi;
            this.categoria = categoria;
            this.color = color;
            this.recomendacion = recomendacion;
        }

        public int getAqi() { return aqi; }
        public String getCategoria() { return categoria; }
        public String getColor() { return color; }
        public String getRecomendacion() { return recomendacion; }
    }

    /**
     * Calcula el AQI basado en las concentraciones de contaminantes.
     */
    public AQIInfo calcularAQI(Double pm25, Double pm10, Double no2, Double o3, Double co) {
        int maxAqi = 0;
        String contaminantePrincipal = "";

        if (pm25 != null) {
            int aqiPm25 = calcularAQIPM25(pm25);
            if (aqiPm25 > maxAqi) {
                maxAqi = aqiPm25;
                contaminantePrincipal = "PM2.5";
            }
        }

        if (pm10 != null) {
            int aqiPm10 = calcularAQIPM10(pm10);
            if (aqiPm10 > maxAqi) {
                maxAqi = aqiPm10;
                contaminantePrincipal = "PM10";
            }
        }

        if (no2 != null) {
            int aqiNo2 = calcularAQINO2(no2);
            if (aqiNo2 > maxAqi) {
                maxAqi = aqiNo2;
                contaminantePrincipal = "NO₂";
            }
        }

        if (o3 != null) {
            int aqiO3 = calcularAQIO3(o3);
            if (aqiO3 > maxAqi) {
                maxAqi = aqiO3;
                contaminantePrincipal = "O₃";
            }
        }

        if (co != null) {
            int aqiCo = calcularAQICO(co);
            if (aqiCo > maxAqi) {
                maxAqi = aqiCo;
                contaminantePrincipal = "CO";
            }
        }

        return new AQIInfo(maxAqi, getCategoria(maxAqi), getColor(maxAqi), 
                          getRecomendacion(maxAqi, contaminantePrincipal));
    }

    /**
     * Calcula AQI para PM2.5 (μg/m³).
     */
    private int calcularAQIPM25(double concentracion) {
        if (concentracion <= 12.0) return calcularIndice(concentracion, 0, 50, 0.0, 12.0);
        if (concentracion <= 35.4) return calcularIndice(concentracion, 51, 100, 12.1, 35.4);
        if (concentracion <= 55.4) return calcularIndice(concentracion, 101, 150, 35.5, 55.4);
        if (concentracion <= 150.4) return calcularIndice(concentracion, 151, 200, 55.5, 150.4);
        if (concentracion <= 250.4) return calcularIndice(concentracion, 201, 300, 150.5, 250.4);
        return calcularIndice(concentracion, 301, 500, 250.5, 500.4);
    }

    /**
     * Calcula AQI para PM10 (μg/m³).
     */
    private int calcularAQIPM10(double concentracion) {
        if (concentracion <= 54) return calcularIndice(concentracion, 0, 50, 0, 54);
        if (concentracion <= 154) return calcularIndice(concentracion, 51, 100, 55, 154);
        if (concentracion <= 254) return calcularIndice(concentracion, 101, 150, 155, 254);
        if (concentracion <= 354) return calcularIndice(concentracion, 151, 200, 255, 354);
        if (concentracion <= 424) return calcularIndice(concentracion, 201, 300, 355, 424);
        return calcularIndice(concentracion, 301, 500, 425, 604);
    }

    /**
     * Calcula AQI para NO₂ (μg/m³).
     */
    private int calcularAQINO2(double concentracion) {
        // Convertir de μg/m³ a ppb (aproximación: ppb = μg/m³ * 0.532)
        double ppb = concentracion * 0.532;
        
        if (ppb <= 53) return calcularIndice(ppb, 0, 50, 0, 53);
        if (ppb <= 100) return calcularIndice(ppb, 51, 100, 54, 100);
        if (ppb <= 360) return calcularIndice(ppb, 101, 150, 101, 360);
        if (ppb <= 649) return calcularIndice(ppb, 151, 200, 361, 649);
        if (ppb <= 1249) return calcularIndice(ppb, 201, 300, 650, 1249);
        return calcularIndice(ppb, 301, 500, 1250, 2049);
    }

    /**
     * Calcula AQI para O₃ (μg/m³).
     */
    private int calcularAQIO3(double concentracion) {
        // Convertir de μg/m³ a ppb (aproximación: ppb = μg/m³ * 0.5)
        double ppb = concentracion * 0.5;
        
        if (ppb <= 54) return calcularIndice(ppb, 0, 50, 0, 54);
        if (ppb <= 70) return calcularIndice(ppb, 51, 100, 55, 70);
        if (ppb <= 85) return calcularIndice(ppb, 101, 150, 71, 85);
        if (ppb <= 105) return calcularIndice(ppb, 151, 200, 86, 105);
        if (ppb <= 200) return calcularIndice(ppb, 201, 300, 106, 200);
        return calcularIndice(ppb, 301, 500, 201, 504);
    }

    /**
     * Calcula AQI para CO (μg/m³).
     */
    private int calcularAQICO(double concentracion) {
        // Convertir de μg/m³ a ppm (aproximación: ppm = μg/m³ * 0.000873)
        double ppm = concentracion * 0.000873;
        
        if (ppm <= 4.4) return calcularIndice(ppm, 0, 50, 0.0, 4.4);
        if (ppm <= 9.4) return calcularIndice(ppm, 51, 100, 4.5, 9.4);
        if (ppm <= 12.4) return calcularIndice(ppm, 101, 150, 9.5, 12.4);
        if (ppm <= 15.4) return calcularIndice(ppm, 151, 200, 12.5, 15.4);
        if (ppm <= 30.4) return calcularIndice(ppm, 201, 300, 15.5, 30.4);
        return calcularIndice(ppm, 301, 500, 30.5, 50.4);
    }

    /**
     * Fórmula para calcular el índice AQI.
     */
    private int calcularIndice(double concentracion, int aqiBajo, int aqiAlto, 
                              double concBaja, double concAlta) {
        return (int) Math.round(((aqiAlto - aqiBajo) / (concAlta - concBaja)) * 
                               (concentracion - concBaja) + aqiBajo);
    }

    /**
     * Obtiene la categoría basada en el valor AQI.
     */
    private String getCategoria(int aqi) {
        if (aqi <= 50) return "Buena";
        if (aqi <= 100) return "Moderada";
        if (aqi <= 150) return "Insalubre para grupos sensibles";
        if (aqi <= 200) return "Insalubre";
        if (aqi <= 300) return "Muy insalubre";
        return "Peligrosa";
    }

    /**
     * Obtiene el color asociado al AQI.
     */
    private String getColor(int aqi) {
        if (aqi <= 50) return "#00E400";      // Verde
        if (aqi <= 100) return "#FFFF00";     // Amarillo
        if (aqi <= 150) return "#FF7E00";     // Naranja
        if (aqi <= 200) return "#FF0000";     // Rojo
        if (aqi <= 300) return "#8F3F97";     // Púrpura
        return "#7E0023";                     // Granate
    }

    /**
     * Obtiene recomendaciones de salud basadas en el AQI.
     */
    private String getRecomendacion(int aqi, String contaminante) {
        if (aqi <= 50) {
            return "La calidad del aire es satisfactoria. El aire no presenta riesgo.";
        } else if (aqi <= 100) {
            return "La calidad del aire es aceptable para la mayoría. Los grupos sensibles pueden experimentar síntomas menores.";
        } else if (aqi <= 150) {
            return "Los grupos sensibles pueden experimentar síntomas de salud. El público general no se ve afectado.";
        } else if (aqi <= 200) {
            return "Todos pueden experimentar síntomas de salud. Los grupos sensibles pueden experimentar efectos más graves.";
        } else if (aqi <= 300) {
            return "Advertencia de salud: todos pueden experimentar efectos graves en la salud.";
        } else {
            return "Alerta de salud: condiciones de emergencia. Toda la población puede verse afectada.";
        }
    }

    /**
     * Calcula el AQI solo para PM2.5.
     */
    public AQIInfo calcularAQIPM25Solo(double pm25) {
        int aqi = calcularAQIPM25(pm25);
        return new AQIInfo(aqi, getCategoria(aqi), getColor(aqi), getRecomendacion(aqi, "PM2.5"));
    }

    /**
     * Calcula el AQI solo para PM10.
     */
    public AQIInfo calcularAQIPM10Solo(double pm10) {
        int aqi = calcularAQIPM10(pm10);
        return new AQIInfo(aqi, getCategoria(aqi), getColor(aqi), getRecomendacion(aqi, "PM10"));
    }
}