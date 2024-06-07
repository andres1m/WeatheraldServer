package org.example.struct.forecast;

public class Forecast {
    private final DayForecast todayForecast;
    private final DayForecast tomorrowForecast;
    private final MonthForecast monthForecast;

    public Forecast(DayForecast todayForecast, DayForecast tomorrowForecast, MonthForecast monthForecast) {
        this.todayForecast = todayForecast;
        this.tomorrowForecast = tomorrowForecast;
        this.monthForecast = monthForecast;
    }

    public DayForecast getTodayForecast() {
        return todayForecast;
    }

    public DayForecast getTomorrowForecast() {
        return tomorrowForecast;
    }

    public MonthForecast getMonthForecast() {
        return monthForecast;
    }
}