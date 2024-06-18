package ru.hse.struct.forecast;

import java.util.Map;

public class MonthForecast {
    private Map<Integer, Double> minTemperatureData;
    private Map<Integer, Double> maxTemperatureData;

    public void setMinTemperatureData(Map<Integer, Double> minTemperatureData) {
        this.minTemperatureData = minTemperatureData;
    }

    public void setMaxTemperatureData(Map<Integer, Double> maxTemperatureData) {
        this.maxTemperatureData = maxTemperatureData;
    }
}