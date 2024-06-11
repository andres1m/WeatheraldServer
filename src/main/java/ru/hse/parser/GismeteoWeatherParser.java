package ru.hse.parser;

import ru.hse.struct.wind.Wind;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GismeteoWeatherParser extends WeatherParser{
    public GismeteoWeatherParser(String locationName) {
        super(locationName);
        parserName = "Gismeteo";
    }

    @Override
    protected List<Double> getTodayHumidity() {
        return getHumidity(getTodayPage());
    }

    @Override
    protected List<Double> getTodayTemperature() {
        return getTemperature(getTodayPage());
    }

    @Override
    protected List<Double> getTodayPrecipitationProbability() {
        return null;
    }

    @Override
    protected List<Wind> getTodayWind() {
        return getWind(getTodayPage());
    }

    @Override
    protected List<Double> getTomorrowHumidity() {
        return getHumidity(getTomorrowPage());
    }

    @Override
    protected List<Double> getTomorrowTemperature() {
        return getTemperature(getTomorrowPage());
    }

    @Override
    protected List<Double> getTomorrowPrecipitationProbability() {
        return null;
    }

    @Override
    protected List<Wind> getTomorrowWind() {
        return getWind(getTomorrowPage());
    }

    @Override
    protected Map<Integer, Double> getMonthMaxTemperature() {
        Elements items = getMonthPage().select("a[class=row-item]");
        Map<Integer, Double> result = new HashMap<>();

        boolean isFirst = true;
        for(Element item : items){
            String date = item.selectFirst("div").text();
            Double temperature = Double.parseDouble(item.selectFirst("span[class=unit unit_temperature_c]").text());

            if(date.split(" ").length == 2){
                if(isFirst){
                    result.put(Integer.parseInt(date.split(" ")[0]), temperature);
                    isFirst = false;
                }else{
                    break;
                }
            }else {
                result.put(Integer.parseInt(date), temperature);
            }
        }

        return result;
    }

    @Override
    protected Map<Integer, Double> getMonthMinTemperature() {
        Elements items = getMonthPage().select("a[class=row-item]");
        Map<Integer, Double> result = new HashMap<>();

        boolean isFirst = true;
        for(Element item : items){
            String date = item.selectFirst("div").text();
            Double temperature = Double.parseDouble(item.selectFirst("div[class=mint]").selectFirst("span[class=unit unit_temperature_c]").text());

            if(date.split(" ").length == 2){
                if(isFirst){
                    result.put(Integer.parseInt(date.split(" ")[0]), temperature);
                    isFirst = false;
                }else{
                    break;
                }
            }else {
                result.put(Integer.parseInt(date), temperature);
            }
        }

        return result;
    }

    @Override
    protected String getSuggestUrl() {
        return "https://www.gismeteo.ru/mq/search/%s/9/";
    }

    @Override
    protected JSONArray formatSuggestion(Document suggestionDocument) {
        String jsonString = suggestionDocument.select("body").text();

        JSONObject jsonObject = new JSONObject(jsonString);
        return jsonObject.getJSONArray("data");
    }

    @Override
    protected String getTodayPageUrl() {
        return "https://www.gismeteo.ru" + suggestion.get("url");
    }

    @Override
    protected String getTomorrowPageUrl() {
        return "https://www.gismeteo.ru" + suggestion.get("url") + "tomorrow/";
    }

    @Override
    protected String getMonthPageUrl() {
        return "https://www.gismeteo.ru" + suggestion.get("url") + "month/";
    }

    private boolean tryParseDouble(String input){
        double d;
        try {
            d = Double.parseDouble(input);
            return true;
        }
        catch (NumberFormatException e) {
            // Use whatever default you like
            return false;
        }
    }

    private List<Double> getTemperature(Document page){
        List<Double> result = new ArrayList<>();
        Element temperatureElement = page.selectFirst("div[class=widget-row-chart widget-row-chart-temperature row-with-caption]");
        temperatureElement.select("span[class=unit unit_temperature_c]")
                .forEach(element -> {
                    if(tryParseDouble(element.text())) {
                        result.add(Double.parseDouble(element.text()));
                    }
                });
        return result;
    }

    private List<Double> getHumidity(Document page){
        List<Double> result = new ArrayList<>();

        Element humidityElement = page.selectFirst("div[class=widget-row widget-row-humidity row-with-caption]");
        humidityElement.select("div")
                .forEach(element -> {
                    if(tryParseDouble(element.text())) {
                        result.add(Double.parseDouble(element.text()));
                    }
                });

        return result;
    }

    private List<Wind> getWind(Document page){
        List<Double> speedList = new ArrayList<>();
        List<String> directionList = new ArrayList<>();

        Element windSpeedElement = page.selectFirst("div[class=widget-row widget-row-wind-gust row-with-caption]");

        for(Element rowItem : windSpeedElement.select("div[class=row-item]")){
            Element item = rowItem.selectFirst("span[class=wind-unit unit unit_wind_m_s]");

            if(item == null){
                speedList.add(0.);
                continue;
            }

            speedList.add(Double.parseDouble(item.text()));
        }

        Elements windDirectionElements = page.select("div[class=direction]");

        for(Element dir : windDirectionElements){
            String direction = dir.text();

            if(direction.equals("штиль")){
                direction = "С";
            }

            directionList.add(direction);
        }

        List<Wind> result = new ArrayList<>();
        for(int i = 0; i < 8; i++){
            result.add(new Wind(directionList.get(i), speedList.get(i)));
        }

        return result;
    }
}