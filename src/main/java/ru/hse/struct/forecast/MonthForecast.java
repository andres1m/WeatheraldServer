package ru.hse.struct.forecast;

import java.util.HashMap;
import java.util.Map;

public class MonthForecast {
    private Map<Integer, Double> minTemperatureData;
    private Map<Integer, Double> maxTemperatureData;

    public Map<Integer, Double> getMinTemperatureData() {
        return minTemperatureData;
    }

    public void setMinTemperatureData(Map<Integer, Double> minTemperatureData) {
        this.minTemperatureData = minTemperatureData;
    }

    public Map<Integer, Double> getMaxTemperatureData() {
        return maxTemperatureData;
    }

    public void setMaxTemperatureData(Map<Integer, Double> maxTemperatureData) {
        this.maxTemperatureData = maxTemperatureData;
    }

    @Override
    public String toString() {
        return "{" +
                "minTemperatureData=" + minTemperatureData +
                ", maxTemperatureData=" + maxTemperatureData +
                '}';
    }
}