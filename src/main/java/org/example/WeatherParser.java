package org.example;

import org.example.struct.forecast.DayForecast;
import org.example.struct.forecast.Forecast;
import org.example.struct.forecast.MonthForecast;
import org.example.struct.wind.Wind;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class WeatherParser {
    private Document todayPage;
    private Document tomorrowPage;
    private Document monthPage;

    protected final String locationName;
    protected JSONObject suggestion;

    public WeatherParser(String locationName){
        this.locationName = locationName;
    }

    public Forecast parse(){
        try {
            setSuggestion();
        }catch (Exception e){
            e.printStackTrace();
        }

        todayPage = getTodayPage();
        tomorrowPage = getTomorrowPage();
        monthPage = getMonthPage();

        return getForecast();
    }

    private Forecast getForecast(){
        return new Forecast(
                getTodayForecast(),
                getTomorrowForecast(),
                getMonthForecast()
        );
    }

    protected abstract List<Double> getTodayHumidity();
    protected abstract List<Double> getTodayTemperature();
    protected abstract List<Double> getTodayPrecipitationProbability();
    protected abstract List<Wind> getTodayWind();

    protected abstract List<Double> getTomorrowHumidity();
    protected abstract List<Double> getTomorrowTemperature();
    protected abstract List<Double> getTomorrowPrecipitationProbability();
    protected abstract List<Wind> getTomorrowWind();

    protected abstract Map<Integer, Double> getMonthMaxTemperature();
    protected abstract Map<Integer, Double> getMonthMinTemperature();

    protected abstract String getSuggestUrl();
    protected abstract JSONArray formatSuggestion(Document suggestionDocument);

    protected abstract String getTodayPageUrl();
    protected abstract String getTomorrowPageUrl();
    protected abstract String getMonthPageUrl();

    protected Connection getConnection(String url){
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.134 YaBrowser/22.7.0.1842 Yowser/2.5 Safari/537.36")
                .referrer("www.google.com");
    }

    private Document getTodayPage(){
        if(todayPage == null){
            try {
                todayPage = getConnection(getTodayPageUrl())
                        .get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return todayPage;
    }

    private Document getTomorrowPage(){
        if(tomorrowPage == null){
            try {
                tomorrowPage = getConnection(getTomorrowPageUrl())
                        .get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return tomorrowPage;
    }

    private Document getMonthPage(){
        if(monthPage == null){
            try {
                monthPage = getConnection(getMonthPageUrl())
                        .get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return monthPage;
    }

    private DayForecast getTodayForecast(){
        DayForecast forecast = new DayForecast();

        forecast.setHumidityData(getTodayHumidity());
        forecast.setTemperatureData(getTodayTemperature());
        forecast.setPrecipitationProbabilityData(getTodayPrecipitationProbability());
        forecast.setWindData(getTodayWind());

        return forecast;
    }

    private DayForecast getTomorrowForecast(){
        DayForecast forecast = new DayForecast();

        forecast.setHumidityData(getTomorrowHumidity());
        forecast.setTemperatureData(getTomorrowTemperature());
        forecast.setPrecipitationProbabilityData(getTomorrowPrecipitationProbability());
        forecast.setWindData(getTomorrowWind());

        return forecast;
    }

    private MonthForecast getMonthForecast(){
        MonthForecast forecast = new MonthForecast();
        forecast.setMaxTemperatureData(getMonthMaxTemperature());
        forecast.setMinTemperatureData(getMonthMinTemperature());

        return forecast;
    }

    private void setSuggestion(){
        Document suggestionDocument = getSuggestionDocument();

        JSONArray suggestionArray = formatSuggestion(suggestionDocument);

        suggestion = suggestionArray.optJSONObject(0);
    }

    private Document getSuggestionDocument(){
        String url = String.format(getSuggestUrl(), locationName);
        Document result = null;

        try {
            result = getConnection(url)
                    .ignoreContentType(true)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}