package ru.hse.parser;

import org.json.JSONArray;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.hse.struct.wind.Wind;

import java.io.IOException;
import java.util.*;

public class WorldWeatherParser extends WeatherParser{
    public WorldWeatherParser(String locationName) {
        super(locationName);
        parserName = "WorldWeather";
    }

    @Override
    protected List<Double> getTodayHumidity() {
        return getHumidity(getTodayPage().select("table[class=weather-today]").get(1));
    }

    @Override
    protected List<Double> getTodayTemperature() {
        return getTemperature(getTodayPage().select("table[class=weather-today]").get(1));
    }

    @Override
    protected List<Double> getTodayPrecipitationProbability() {
        return getPrecipitationProbability(getTodayPage().select("table[class=weather-today]").get(1));
    }

    @Override
    protected List<Wind> getTodayWind() {
        return getWind(getTodayPage().select("table[class=weather-today]").get(1));
    }

    @Override
    protected List<Double> getTomorrowHumidity() {
        return getHumidity(getTodayPage().select("table[class=weather-today]").get(2));
    }

    @Override
    protected List<Double> getTomorrowTemperature() {
        return getTemperature(getTodayPage().select("table[class=weather-today]").get(2));
    }

    @Override
    protected List<Double> getTomorrowPrecipitationProbability() {
        return getPrecipitationProbability(getTodayPage().select("table[class=weather-today]").get(2));
    }

    @Override
    protected List<Wind> getTomorrowWind() {
        return getWind(getTodayPage().select("table[class=weather-today]").get(2));
    }

    @Override
    protected Map<Integer, Double> getMonthMaxTemperature() {
        return getTemperature("span");
    }

    @Override
    protected Map<Integer, Double> getMonthMinTemperature() {
        return getTemperature("p");
    }

    protected Map<Integer, Double> getTemperature(String selector){
        int maxDay = 0;
        Elements days = getMonthPage().select("li[class=ww-month-weekdays foreacast]");
        days.addAll(getMonthPage().select("li[class=ww-month-weekend forecast-statistics]"));

        Map<Integer, Double> result = new HashMap<>();

        for(Element day : days){
            int date = Integer.parseInt(day.selectFirst("div").text());
            double temperature = Double.parseDouble(day.select(selector).get(0).text().replace("°", ""));

            if(maxDay > date)
                continue;

            result.put(date, temperature);
            maxDay = date;
        }
        return result;
    }

    @Override
    protected String getSuggestUrl() {
        return "https://world-weather.ru/search.php?term=%s";
    }

    @Override
    protected JSONArray formatSuggestion(Document suggestionDocument) {
        return new JSONArray(suggestionDocument.text());
    }

    @Override
    protected String getTodayPageUrl() {
        return String.format("https://world-weather.ru/pogoda/%s/24hours/", getLocationCode());
    }

    @Override
    protected String getTomorrowPageUrl() {
        return getTodayPageUrl();
    }

    @Override
    protected String getMonthPageUrl() {
        return String.format("https://world-weather.ru/pogoda/%s/month/", getLocationCode());
    }

    private String getLocationCode(){
        return suggestion.getString("chpu");
    }

    @Override
    protected Document getSuggestionDocument(){
        String url = String.format(getSuggestUrl(), locationName);
        Document result = null;

        try {
            result = getConnection(url)
                    .referrer("https://world-weather.ru/pogoda/russia/perm/month/")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 YaBrowser/24.4.0.0 Safari/537.36")
                    .data("Sec-Ch-Ua-Platform", "\"Windows\"")
                    .ignoreContentType(true)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private List<Double> getTemperature(Element dayElement){
        List<Double> temp = new ArrayList<>();

        for(Element e : dayElement.select("td[class=weather-temperature]")){
            Double temperature = Double.parseDouble(e.text().replace("°", ""));
            temp.add(temperature);
        }

        return fillList(temp, -273);
    }

    private List<Double> getHumidity(Element dayElement){
        List<Double> temp = new ArrayList<>();
        for(Element e : dayElement.select("td[class=weather-humidity]")){
            Double humidity = Double.parseDouble(e.text().replace("%", ""));
            temp.add(humidity);
        }

        return fillList(temp, -1);
    }

    private List<Double> getPrecipitationProbability(Element dayElement){
        List<Double> temp = new ArrayList<>();

        for(Element e : dayElement.select("td[class=weather-probability]")){
            String strProbability = e.text().replace("%", "");
            Double probability;
            if(strProbability.equals("")){
                probability = -1.;
            }else {
                probability = Double.parseDouble(strProbability);
            }
            temp.add(probability);
        }

        return fillList(temp, -1);
    }

    private List<Wind> getWind(Element dayElement){
        List<Wind> temp = new ArrayList<>();

        for(Element e : dayElement.select("td[class=weather-wind]")){
            double speed= Double.parseDouble(e.select("span").get(1).text());
            String direction;

            String rawDirection = e.selectFirst("span").attr("title");
            direction = matchWindDirection(rawDirection);

            temp.add(new Wind(direction, speed));
        }

        Collections.reverse(temp);

        for(int i = temp.size(); i < 24; i++){
            temp.add(new Wind("none", 0));
        }

        Collections.reverse(temp);

        List<Wind> result = new ArrayList<>();

        for(int i = 2; i < 24; i+=3){
            result.add(temp.get(i));
        }

        return result;
    }

    private List<Double> fillList(List<Double> list, double emptyValue){
        Collections.reverse(list);

        for(int i = list.size(); i < 24; i++){
            list.add(emptyValue);
        }

        Collections.reverse(list);

        List<Double> result = new ArrayList<>();

        for(int i = 2; i < 24; i+=3){
            result.add(list.get(i));
        }

        return result;
    }

    private String matchWindDirection(String input){
        String direction;

        switch (input){
            case "юго-западный":
                direction = "ЮЗ";
                break;
            case "южный":
                direction = "Ю";
                break;
            case "северный":
                direction = "С";
                break;
            case "западный":
                direction = "З";
                break;
            case "восточный":
                direction = "В";
                break;
            case "юго-восточный":
                direction = "ЮВ";
                break;
            case "северо-западный":
                direction = "СЗ";
                break;
            case "северо-восточный":
                direction = "СВ";
                break;
            default:
                direction = "С";
        }

        return direction;
    }
}