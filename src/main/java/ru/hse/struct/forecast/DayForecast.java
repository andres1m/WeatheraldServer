package ru.hse.struct.forecast;

import ru.hse.struct.wind.Wind;

import java.util.ArrayList;
import java.util.List;

public class DayForecast {
    private List<Double> temperatureData;
    private List<Double> humidityData;
    private List<Wind> windData;
    private List<Double> precipitationProbabilityData;

    public void setTemperatureData(List<Double> temperatureData) {
        checkList(temperatureData);

        if(temperatureData == null){
            this.temperatureData = new ArrayList<>();
            return;
        }

        this.temperatureData = temperatureData;
    }

    public void setHumidityData(List<Double> humidityData) {
        checkList(humidityData);

        if(humidityData == null){
            this.humidityData = new ArrayList<>();
            return;
        }

        this.humidityData = humidityData;
    }

    public void setWindData(List<Wind> windData) {
        checkList(windData);

        if(windData == null){
            this.windData = new ArrayList<>();
            return;
        }

        this.windData = windData;
    }

    public void setPrecipitationProbabilityData(List<Double> precipitationProbabilityData) {
        checkList(precipitationProbabilityData);

        if(precipitationProbabilityData == null){
            this.precipitationProbabilityData = new ArrayList<>();
            return;
        }

        this.precipitationProbabilityData = precipitationProbabilityData;
    }

    public List<Double> getTemperatureData() {
        return temperatureData;
    }

    public List<Double> getHumidityData() {
        return humidityData;
    }

    public List<Wind> getWindData() {
        return windData;
    }

    public List<Double> getPrecipitationProbabilityData() {
        return precipitationProbabilityData;
    }

    private <T> void checkList(List<T> input){
        if(input == null){
            return;
        }

        if(input.size() != 8){
            throw new IllegalArgumentException("Необходим массив с 8 элементами (2, 5, 8, 11, 14, 17, 20, 23 часа)");
        }
    }

    @Override
    public String toString() {
        return "DayForecast{" +
                "temperatureData=" + temperatureData +
                ", humidityData=" + humidityData +
                ", windData=" + windData +
                ", precipitationProbabilityData=" + precipitationProbabilityData +
                '}';
    }
}