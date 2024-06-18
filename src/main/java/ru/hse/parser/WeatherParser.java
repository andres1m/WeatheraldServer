package ru.hse.parser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.hse.struct.ParseResult;
import ru.hse.struct.forecast.DayForecast;
import ru.hse.struct.forecast.Forecast;
import ru.hse.struct.forecast.MonthForecast;
import ru.hse.struct.wind.Wind;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class WeatherParser {
    private Document todayPage;
    private Document tomorrowPage;
    private Document monthPage;
    protected String parserName;

    protected final String locationName;
    protected JSONObject suggestion;

    public WeatherParser(String locationName){
        this.locationName = locationName;
    }

    public ParseResult parse(){
        try {
            setSuggestion();
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println(parserName);

        return new ParseResult(getParserName(), getForecast());
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
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 YaBrowser/24.4.0.0 Safari/537.36")
                .timeout(10000)
                .referrer("www.google.com");
    }

    protected Document getTodayPage(){
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

    protected Document getTomorrowPage(){
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

    protected Document getMonthPage(){
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

    protected Document getSuggestionDocument(){
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

    public String getParserName() {
        return parserName;
    }
}