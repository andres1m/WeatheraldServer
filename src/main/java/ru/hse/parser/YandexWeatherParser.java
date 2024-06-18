package ru.hse.parser;

import org.json.JSONArray;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.hse.struct.wind.Wind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO refactoring
public class YandexWeatherParser extends WeatherParser{
    public YandexWeatherParser(String locationName) {
        super(locationName);
        parserName = "YandexWeather";
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
        return getMonthTemperature("div[class=temp climate-calendar-day__temp-day]");
    }
    @Override
    protected Map<Integer, Double> getMonthMinTemperature() {
        return getMonthTemperature("div[class=temp climate-calendar-day__temp-night]");
    }

    private Map<Integer, Double> getMonthTemperature(String selector){
        Map<Integer, Double> result = new HashMap<>();
        Elements cells = getMonthPage().select("td[class=climate-calendar__cell]");

        for(Element cell : cells){
            Double temperature = Double.parseDouble(cell.selectFirst(selector).text());
            String day = cell.selectFirst("div[class=climate-calendar-day__day]").text();

            if(day.split(" ").length == 2){
                break;
            }else {
                result.put(Integer.parseInt(day), temperature);
            }
        }

        return result;
    }

    private List<Double> getHumidity(Document page){
        List<Double> result = new ArrayList<>();

        Elements humidityElements = page
                .select("div[class=sc-55c0bcc8-0 jlFOZR]")
                .get(3)
                .select("span[class=sc-a9fb3bce-5 jEpyhm]");

        for(int i = 2; i < 24; i += 3){
            String content = humidityElements.get(i).text();
            result.add(Double.valueOf(content.replace("%", "")));
        }

        return result;
    }

    private List<Double> getTemperature(Document page){
        List<Double> result = new ArrayList<>();

        Elements temperatureElements = page
                .select("div[class=sc-55c0bcc8-0 jlFOZR]")
                .get(0)
                .select("div[class=sc-72d65afc-5 eGLuAs]");

        for(int i = 2; i < 24; i += 3){
            String content = temperatureElements.get(i).text();
            result.add(Double.valueOf(content.replace("°", "")));
        }

        return result;
    }

    private List<Wind> getWind(Document page){
        List<Wind> result = new ArrayList<>();

        Elements windElements = page
                .select("div[class=sc-55c0bcc8-0 jlFOZR]")
                .get(1)
                .select("li[class=sc-72c83b8c-5 dICytF]");

        for(int i = 2; i < 24; i += 3){
            Element currentElement = windElements.get(i);
            String speed = currentElement.select("div[class=sc-72c83b8c-1 jndbzj]").text();

            if(speed.equals("Штиль")){
                result.add(new Wind("С", 0));
                continue;
            }

            String direction = currentElement.select("span[class=sc-72c83b8c-3 bOxJmJ]").text();
            result.add(new Wind(direction, Double.parseDouble(speed.replace(",", "."))));
        }

        return result;
    }

    @Override
    protected String getSuggestUrl() {
        return "https://suggest-maps.yandex.ru/suggest-geo?v=8&lang=ru_RU&search_type=weather_v2&n=10&ll=56.229398,58.010374&spn=0.5,0.5&" +
                "client_id=weather_v2&svg=1&part=%s&pos=1&callback=jQuery54097";
    }

    @Override
    protected JSONArray formatSuggestion(Document suggestionDocument) {
        String rawJson = suggestionDocument.select("body").text();
        JSONArray jsonArray = new JSONArray(formatString(rawJson));

        return jsonArray.getJSONArray(1);
    }

    @Override
    protected String getTodayPageUrl() {
        return formatLatLon("https://yandex.ru/pogoda/ru-RU/details/fishing/today?lat=%s&lon=%s");
    }

    @Override
    protected String getTomorrowPageUrl() {
        return formatLatLon("https://yandex.ru/pogoda/ru-RU/details/fishing/tomorrow?lat=%s&lon=%s)");
    }

    @Override
    protected String getMonthPageUrl() {
        return formatLatLon("https://yandex.ru/pogoda/month?lat=%s&lon=%s");
    }

    private String formatString(String source){
        int index = source.indexOf("(");
        String jsonString = source.substring(index);

        return jsonString
                .replace("(", "")
                .replace(")", "");
    }

    // Возможно придется реализовать дополнительное условие для поиска geoid локации
    private String formatLatLon(String source){
        String lat = String.valueOf(suggestion.getDouble("lat"))
                .replace(",", ".");

        String lon = String.valueOf(suggestion.getDouble("lon"))
                .replace(",", ".");

        return String.format(source, lat, lon);
    }
}