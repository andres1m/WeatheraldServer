package ru.hse.struct;

import ru.hse.struct.forecast.Forecast;

public class ParseResult {
    private String name;
    private Forecast forecast;

    public ParseResult(String name, Forecast forecast) {
        this.name = name;
        this.forecast = forecast;
    }

    public ParseResult() {
        this.name = "404";
        this.forecast = null;
    }
}
