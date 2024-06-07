package org.example.struct.wind;

public class Wind {
    private final String direction;
    private final double speed;

    public Wind(String direction, double speed) {
        this.direction = direction;
        this.speed = speed;
    }

    public String getDirection() {
        return direction;
    }

    public double getSpeed() {
        return speed;
    }
}