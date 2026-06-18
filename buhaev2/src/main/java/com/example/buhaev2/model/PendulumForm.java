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

    // ===== ВВОДНЫЕ ПАРАМЕТРЫ =====
    private String planet = "Земля";
    private Double length;
    private Double lengthError;
    private Double timeTotal;
    private Double timeError;
    private Integer oscillations;
    private Double angle;
    private Double angleError;

    // ===== РЕЗУЛЬТАТЫ =====
    private Double gValue;
    private Double period;
    private Double periodError;
    private Double frequency;
    private Double frequencyError;
    private Double gCalculated;
    private Double gAbsoluteError;
    private Double gRelativeError;
    private Double gConfidenceLow;
    private Double gConfidenceHigh;
    private Double gErrorPercent;
    private Double theoreticalPeriod;
    private Double periodDeviation;

    // ===== ДОП. МЕТРИКИ =====
    private Double largeAngleCorrection;
    private String error;
    private String qualityAssessment;

    public boolean isValidBase() {
        return length != null && length > 0 && length <= 10 &&
                timeTotal != null && timeTotal > 0 &&
                oscillations != null && oscillations > 0 && oscillations <= 1000;
    }

    public void calculate() {
        if (!isValidBase()) {
            error = "Проверьте ввод: L (0.01-10 м), t (>0), N (1-1000)";
            return;
        }

        // 1. Базовые значения
        gValue = PLANETS_G.getOrDefault(planet, 9.81);
        period = timeTotal / oscillations;

        if (period <= 0) {
            error = "Период не может быть ≤ 0";
            return;
        }

        frequency = 1.0 / period;

        // 2. Угол в радианах
        double angleRad = (angle != null && angle > 0) ? Math.toRadians(angle) : 0;
        double angleErrRad = (angleError != null) ? Math.toRadians(angleError) : 0;

        // 3. Поправка на большой угол (ряд Тейлора)
        // T(θ) = T₀ × [1 + θ²/16 + 11θ⁴/3072 + ...]
        largeAngleCorrection = 1.0;
        if (angleRad > 0) {
            largeAngleCorrection += (angleRad * angleRad) / 16.0;
            largeAngleCorrection += (11.0 * Math.pow(angleRad, 4)) / 3072.0;
        }

        // 4. ===== КЛЮЧЕВОЙ РАСЧЁТ G =====
        // g = 4π²L / T₀², где T₀ = T_измеренный / correction
        // => g = 4π²L × correction² / T_измеренный²
        double correctionSquared = largeAngleCorrection * largeAngleCorrection;
        gCalculated = (4.0 * Math.PI * Math.PI * length * correctionSquared) / (period * period);

        // 5. Теоретический период для сравнения (без поправки, т.к. gValue — табличное)
        theoreticalPeriod = 2.0 * Math.PI * Math.sqrt(length / gValue);
        periodDeviation = Math.abs(period - theoreticalPeriod) / theoreticalPeriod * 100;

        // 6. Погрешности
        calculateUncertainties(angleRad, angleErrRad);

        // 7. Доверительный интервал (95%, k=2)
        if (gAbsoluteError != null && gAbsoluteError > 0) {
            gConfidenceLow = Math.max(0, gCalculated - 2 * gAbsoluteError);
            gConfidenceHigh = gCalculated + 2 * gAbsoluteError;
        }

        // 8. Отклонение от табличного
        gErrorPercent = Math.abs(gCalculated - gValue) / gValue * 100;

        // 9. Оценка качества
        assessQuality();

        error = null;
    }

    private void calculateUncertainties(double angleRad, double angleErrRad) {
        // Погрешность периода
        periodError = (timeError != null && timeError > 0)
                ? timeError / oscillations
                : 0.01 / oscillations;

        // Погрешность частоты
        frequencyError = periodError / (period * period);

        // ===== ПРОПАГАЦИЯ ПОГРЕШНОСТЕЙ ДЛЯ G =====
        // g ∝ L / T²  →  (Δg/g)² = (ΔL/L)² + (2ΔT/T)² + (вклад угла)²

        double relErrorL = (lengthError != null && lengthError > 0 && length > 0)
                ? lengthError / length
                : 0.005; // 0.5% по умолчанию

        double relErrorT = periodError / period;

        // Вклад угла: производная поправки по θ
        double relErrorAngle = 0;
        if (angleRad > 0 && largeAngleCorrection > 0) {
            // d(correction)/dθ ≈ θ/8 для малых θ
            double dCorrDTheta = angleRad / 8.0 + (11.0 * Math.pow(angleRad, 3)) / 768.0;
            relErrorAngle = Math.abs(dCorrDTheta * angleErrRad / largeAngleCorrection) * 2; // ×2 т.к. correction²
        }

        // Квадратурное сложение
        double relErrorG = Math.sqrt(
                relErrorL * relErrorL +
                        4.0 * relErrorT * relErrorT +  // коэффициент 2 возводится в квадрат
                        relErrorAngle * relErrorAngle
        );

        gRelativeError = relErrorG * 100;
        gAbsoluteError = gCalculated * relErrorG;
    }

    private void assessQuality() {
        if (gErrorPercent < 1.5 && gRelativeError < 3) {
            qualityAssessment = "✅ Отлично: высокая точность";
        } else if (gErrorPercent < 4 && gRelativeError < 6) {
            qualityAssessment = "👍 Хорошо: приемлемая точность";
        } else if (gErrorPercent < 8) {
            qualityAssessment = "⚠️ Удовлетворительно: есть погрешности";
        } else {
            qualityAssessment = "❌ Требуется повтор: большие отклонения";
        }
    }

    // ===== МЕТОДЫ ДЛЯ ШАБЛОНА =====

    public String getGFormatted() {
        if (gCalculated == null) return "—";
        if (gAbsoluteError == null || gAbsoluteError <= 0) {
            return String.format("%.3f м/с²", gCalculated);
        }
        return String.format("(%.3f ± %.3f) м/с²", gCalculated, gAbsoluteError);
    }

    public boolean isGConsistent() {
        if (gConfidenceLow == null || gConfidenceHigh == null || gValue == null) return false;
        return gValue >= gConfidenceLow && gValue <= gConfidenceHigh;
    }

    // Алиас для Thymeleaf
    public boolean getGConsistent() {
        return isGConsistent();
    }
}