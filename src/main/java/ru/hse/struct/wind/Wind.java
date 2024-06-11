package ru.hse.struct.wind;

public class Wind {
    private final String direction;
    private final double speed;

    public Wind(String direction, double speed) {
        this.direction = direction;
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "Wind{" +
                "direction='" + direction + '\'' +
                ", speed=" + speed +
                '}';
    }
}