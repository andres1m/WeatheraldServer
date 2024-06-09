package org.example.struct.forecast;

import org.example.struct.wind.Wind;

import java.util.List;

public class DayForecast {
    private List<Double> temperatureData;
    private List<Double> humidityData;
    private List<Wind> windData;
    private List<Double> precipitationProbabilityData;

    public void setTemperatureData(List<Double> temperatureData) {
        checkList();
        this.temperatureData = temperatureData;
    }

    public void setHumidityData(List<Double> humidityData) {
        checkList();
        this.humidityData = humidityData;
    }

    public void setWindData(List<Wind> windData) {
        checkList();
        this.windData = windData;
    }

    public void setPrecipitationProbabilityData(List<Double> precipitationProbabilityData) {
        checkList();
        this.precipitationProbabilityData = precipitationProbabilityData;
    }

    private void checkList(){
        if(temperatureData.size() != 8){
            throw new IllegalArgumentException("Необходим массив с 8 элементами (2, 5, 8, 11, 14, 17, 20, 23 часа)");
        }
    }
}