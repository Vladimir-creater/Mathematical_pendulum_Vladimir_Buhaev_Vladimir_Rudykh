package com.example.physic.modules.kirchhoff;

/**
 * Модель расчётов по законам Кирхгофа.
 */
public class Kirchhoff {

    // ── I закон ───────────────────────────────────────────────
    private double i1In, i2In, i3In;
    private double iOut;

    // ── II закон: входные данные ──────────────────────────────
    private double emf;   // ЭДС источника (В)
    private double r1;    // сопротивление ветви 1 параллельного блока (Ом)
    private double r2;    // сопротивление ветви 2 параллельного блока (Ом)
    private double r3;    // последовательное сопротивление (Ом)

    // ── II закон: результаты ──────────────────────────────────
    private double rPar;        // R1 ∥ R2
    private double rTotal;      // R_par + R3
    private double iTotal;      // полный ток I = E / R_total
    private double uPar;        // напряжение на параллельном блоке
    private double u3;          // напряжение на R3
    private double i1;          // ток через R1
    private double i2;          // ток через R2
    private boolean secondValid; // проверка: E ≈ U_par + U3

    // ═══════════════════════════════════════════════════════════
    // I ЗАКОН: I_вых = I1 + I2 + I3
    // ═══════════════════════════════════════════════════════════
    public void calculateFirst() {
        this.iOut = i1In + i2In + i3In;
    }

    // ═══════════════════════════════════════════════════════════
    // II ЗАКОН: схема E → (R1 ∥ R2) → R3
    // ═══════════════════════════════════════════════════════════
    public void calculateSecond() {
        // 1. Параллельное сопротивление R1 ∥ R2
        if (Math.abs(r1 + r2) < 1e-12) {
            this.rPar = 0;
        } else {
            this.rPar = (r1 * r2) / (r1 + r2);
        }

        // 2. Общее сопротивление цепи
        this.rTotal = rPar + r3;

        // 3. Полный ток
        if (Math.abs(rTotal) < 1e-12) {
            this.iTotal = 0;
        } else {
            this.iTotal = emf / rTotal;
        }

        // 4. Напряжения
        this.uPar = iTotal * rPar;
        this.u3   = iTotal * r3;

        // 5. Токи ветвей параллельного блока
        this.i1 = (Math.abs(r1) < 1e-12) ? 0 : uPar / r1;
        this.i2 = (Math.abs(r2) < 1e-12) ? 0 : uPar / r2;

        // 6. Проверка II закона: E = U_par + U3
        this.secondValid = Math.abs(emf - (uPar + u3)) < 1e-9;
    }

    // ── Getters / Setters ─────────────────────────────────────
    // I закон
    public double getI1In() { return i1In; }
    public void   setI1In(double v) { this.i1In = v; }
    public double getI2In() { return i2In; }
    public void   setI2In(double v) { this.i2In = v; }
    public double getI3In() { return i3In; }
    public void   setI3In(double v) { this.i3In = v; }
    public double getIOut() { return iOut; }

    // II закон — входные
    public double getEmf()  { return emf; }
    public void   setEmf(double v)  { this.emf = v; }
    public double getR1()   { return r1; }
    public void   setR1(double v)   { this.r1 = v; }
    public double getR2()   { return r2; }
    public void   setR2(double v)   { this.r2 = v; }
    public double getR3()   { return r3; }
    public void   setR3(double v)   { this.r3 = v; }

    // II закон — результаты
    public double  getRPar()       { return rPar; }
    public double  getRTotal()     { return rTotal; }
    public double  getITotal()     { return iTotal; }
    public double  getUPar()       { return uPar; }
    public double  getU3()         { return u3; }
    public double  getI1()         { return i1; }
    public double  getI2()         { return i2; }
    public boolean isSecondValid() { return secondValid; }
}