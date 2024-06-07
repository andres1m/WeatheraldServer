package org.example;

import org.example.struct.forecast.DayForecast;
import org.example.struct.forecast.Forecast;
import org.example.struct.forecast.MonthForecast;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public abstract class WeatherParser {
    protected final String locationName;
    private Document page;
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

        return getForecast();
    }

    private Forecast getForecast(){
        return new Forecast(
                getTodayForecast(),
                getTomorrowForecast(),
                getMonthForecast()
        );
    }

    protected abstract DayForecast getTodayForecast();

    protected abstract DayForecast getTomorrowForecast();

    protected abstract MonthForecast getMonthForecast();

    protected abstract String getSuggestUrl();

    protected void setSuggestion(){
        Document suggestionDocument = getSuggestionDocument();

        JSONArray suggestionArray = formatSuggestion(suggestionDocument);

        suggestion = suggestionArray.optJSONObject(0);
    }

    protected Connection getConnection(String url){
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.134 YaBrowser/22.7.0.1842 Yowser/2.5 Safari/537.36")
                .referrer("www.google.com");
    }

    protected abstract JSONArray formatSuggestion(Document suggestionDocument);

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