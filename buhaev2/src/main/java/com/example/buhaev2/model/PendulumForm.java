package com.example.buhaev2.model;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;


@Getter
@Setter
public class PendulumForm {

    public static final Map<String, Double> PLANETS_G = new LinkedHashMap<>();
    static {
        PLANETS_G.put("Меркурий", 3.70);
        PLANETS_G.put("Венера", 8.87);
        PLANETS_G.put("Земля", 9.81);
        PLANETS_G.put("Луна", 1.62);
        PLANETS_G.put("Марс", 3.71);
        PLANETS_G.put("Юпитер", 24.79);
        PLANETS_G.put("Сатурн", 10.44);
        PLANETS_G.put("Уран", 8.87);
        PLANETS_G.put("Нептун", 11.15);
    }

    private String planet = "Земля";
    private Double length;
    private Double timeTotal;
    private Integer oscillations;
    private Double angle;

    private Double gValue;
    private Double period;
    private Double frequency;
    private Double gCalculated;
    private Double gError;
    private Double theoreticalPeriod;
    private String error;

    public boolean isValidBase() {
        return length != null && length > 0 &&
                timeTotal != null && timeTotal > 0 &&
                oscillations != null && oscillations > 0;
    }

    public void calculate() {
        if (!isValidBase()) {
            error = "Заполните длину, время и число колебаний (> 0)";
            return;
        }

        gValue = PLANETS_G.getOrDefault(planet, 9.81);
        period = timeTotal / oscillations;
        frequency = 1.0 / period;
        gCalculated = (4 * Math.PI * Math.PI * length) / (period * period);

        if (angle != null && angle > 0 && angle < 30) {
            double rad = Math.toRadians(angle);
            double correction = 1 + (rad * rad) / 16.0;
            gCalculated = gCalculated / correction;
        }

        gError = Math.abs(gCalculated - gValue) / gValue * 100;
        theoreticalPeriod = 2 * Math.PI * Math.sqrt(length / gValue);
        error = null;
    }


}