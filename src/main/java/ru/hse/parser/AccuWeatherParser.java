package ru.hse.parser;

import org.json.JSONArray;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.hse.struct.wind.Wind;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AccuWeatherParser extends WeatherParser{
    private String pageUrl;
    private int maxDate;
    public AccuWeatherParser(String locationName) {
        super(locationName);
        maxDate = 0;
        parserName = "AccuWeather";
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
        maxDate = 0;
        Elements today = getMonthPage().select("a[class=monthly-daypanel is-today]");

        if (today.size() == 0) {
            today = getMonthPage().select("a[class=monthly-daypanel has-alert  is-today]");
        }

        Elements normalDays = getMonthPage().select("a[class=monthly-daypanel]");
        Elements alertDays = getMonthPage().select("a[class=monthly-daypanel has-alert  ]");

        Map<Integer, Double> result = new HashMap<>();
        processMonthDay(today, result, "high high-temp-past ");
        processMonthDay(today, result, "high  ");
        processMonthDay(alertDays, result, "high  ");
        processMonthDay(normalDays, result, "high  ");

        return result;
    }

    @Override
    protected Map<Integer, Double> getMonthMinTemperature() {
        maxDate = 0;
        Elements today = getMonthPage().select("a[class=monthly-daypanel is-today]");

        if (today.size() == 0) {
            today = getMonthPage().select("a[class=monthly-daypanel has-alert  is-today]");
        }

        Elements normalDays = getMonthPage().select("a[class=monthly-daypanel]");
        Elements alertDays = getMonthPage().select("a[class=monthly-daypanel has-alert  ]");

        Map<Integer, Double> result = new HashMap<>();
        processMonthDay(today, result, "low");
        processMonthDay(alertDays, result, "low");
        processMonthDay(normalDays, result, "low");

        return result;
    }

    private void processMonthDay(Elements elements, Map<Integer, Double> map, String selector){
        for(Element element : elements){
            int date = Integer.parseInt(element.select("div[class=date]").text().trim());
            String rawTemp = element.select(String.format("div[class=%s]", selector)).text();

            if(rawTemp.equals(""))
                continue;

            double temp = Double.parseDouble(rawTemp.replace("°", ""));

            if(maxDate > date)
                return;

            maxDate = date;

            map.put(date, temp);
        }
    }

    @Override
    protected String getSuggestUrl() {
        return "https://www.accuweather.com/web-api/autocomplete?query=%s&language=ru";
    }

    @Override
    protected JSONArray formatSuggestion(Document suggestionDocument) {
        return new JSONArray(suggestionDocument.text());
    }

    @Override
    protected String getTodayPageUrl() {
        return String.format(getPageUrl(), "hourly-weather-forecast");
    }

    @Override
    protected String getTomorrowPageUrl() {
        return getTodayPageUrl() + "?day=2";
    }

    @Override
    protected String getMonthPageUrl() {
        return String.format(getPageUrl(), "june-weather");
    }

    private String getPageUrl(){
        if(pageUrl == null) {
            String key = suggestion.getString("key");

            Document document;

            try {
                document = getConnection("https://www.accuweather.com/web-api/three-day-redirect?key=" + key)
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
                document = null;
            }

            assert document != null;

            pageUrl = (document.selectFirst("link").attr("href")).replace("weather-forecast", "%s").replaceFirst("en", "ru");
        }

        return pageUrl;
    }

    private List<Double> getHumidity(Document page){
        Elements humidityElements = page
                .select("p:contains(Влажность)");

        List<Double> temp = new ArrayList<>();

        for(Element element : humidityElements){
            String humidity = element.select("span[class=value]").text().replace("%", "").trim();
            temp.add(Double.parseDouble(humidity));
        }

        Collections.reverse(temp);

        for(int i = temp.size(); i < 24; i++){
            temp.add(-1.);
        }

        Collections.reverse(temp);

        List<Double> result = new ArrayList<>();

        for(int i = 2; i < 24; i+=3){
            result.add(temp.get(i));
        }

        return result;
    }

    private List<Double> getTemperature(Document page){
        Elements temperatureElements = page.select("div[class=temp metric]");

        List<Double> temp = temperatureElements.stream()
                .map(e -> Double.parseDouble(e.text().replace("°", "")))
                .collect(Collectors.toList());

        Collections.reverse(temp);

        for(int i = temp.size(); i < 24; i++){
            temp.add(-273.);
        }

        Collections.reverse(temp);

        List<Double> result = new ArrayList<>();

        for(int i = 2; i < 24; i+=3){
            result.add(temp.get(i));
        }

        return result;
    }

    private List<Wind> getWind(Document page){
        List<Wind> temp = new ArrayList<>();

        Elements windElements = page.select("div[class=accordion-item-header-container ]")
                .select("p:contains(Ветер)");

        for(Element element : windElements){
            String wind = element.select("span[class=value]").text();

            double speed = 1000 * Double.parseDouble(wind.split(" ")[1]) / 3600;
            String direction = wind.split(" ")[0].replace("Ветер", "");

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
}