package com.arequipa.aire.backend.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO específico para el frontend de calidad del aire.
 */
public class CalidadAireFrontendDTO {
    
    private String location;
    private String timestamp;
    private Integer aqi;
    private String category;
    private Map<String, Map<String, Object>> pollutants;
    private List<String> healthRecommendations;
    
    // Constructors
    public CalidadAireFrontendDTO() {}
    
    // Getters and Setters
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public Integer getAqi() {
        return aqi;
    }
    
    public void setAqi(Integer aqi) {
        this.aqi = aqi;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Map<String, Map<String, Object>> getPollutants() {
        return pollutants;
    }
    
    public void setPollutants(Map<String, Map<String, Object>> pollutants) {
        this.pollutants = pollutants;
    }
    
    public List<String> getHealthRecommendations() {
        return healthRecommendations;
    }
    
    public void setHealthRecommendations(List<String> healthRecommendations) {
        this.healthRecommendations = healthRecommendations;
    }
}
