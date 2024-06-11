package ru.hse.parser;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GlobalParser {
    private List<WeatherParser> parsers;
    public GlobalParser(String locationName){
        parsers = new ArrayList<>();
        parsers.add(new AccuWeatherParser(locationName));
        parsers.add(new YandexWeatherParser(locationName));
        parsers.add(new GismeteoWeatherParser(locationName));
        parsers.add(new WorldWeatherParser(locationName));
    }

    public String parseJson(){
        return new Gson().toJson(parsers.parallelStream().map(WeatherParser::parse).collect(Collectors.toList()));
    }
}