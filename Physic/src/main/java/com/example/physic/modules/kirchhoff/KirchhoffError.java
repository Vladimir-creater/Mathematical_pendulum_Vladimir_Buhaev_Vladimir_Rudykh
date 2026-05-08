package com.example.physic.modules.kirchhoff;

/**
 * Расчёт погрешностей измерений по законам Кирхгофа.
 */
public class KirchhoffError {

    private double deltaI;   // погрешность амперметра (А)
    private double deltaE;   // погрешность вольтметра / ЭДС (В)
    private double deltaR;   // погрешность омметра (Ом)

    // I закон
    private double absErrorIOut;
    private double relErrorIOut;

    // II закон
    private double absErrorRPar;
    private double absErrorRTotal;
    private double absErrorITotal;
    private double relErrorITotal;
    private double absErrorUPar;
    private double relErrorUPar;
    private double absErrorU3;
    private double relErrorU3;
    private double absErrorI1;
    private double relErrorI1;
    private double absErrorI2;
    private double relErrorI2;

    public KirchhoffError() {
        this.deltaI = 0.05;
        this.deltaE = 0.1;
        this.deltaR = 0.5;
    }

    public KirchhoffError(double deltaI, double deltaE, double deltaR) {
        this.deltaI = deltaI;
        this.deltaE = deltaE;
        this.deltaR = deltaR;
    }

    // ── I закон ───────────────────────────────────────────────
    public void calculateFirstLawError(double iOut) {
        this.absErrorIOut = 3.0 * deltaI;
        this.relErrorIOut = (Math.abs(iOut) > 1e-12)
                ? (absErrorIOut / Math.abs(iOut)) * 100.0
                : Double.NaN;
    }

    // ── II закон: E → (R1∥R2) → R3 ──────────────────────────
    public void calculateSecondLawError(double r1, double r2, double r3,
                                        double rPar, double rTotal,
                                        double iTotal, double uPar,
                                        double u3, double i1, double i2) {
        double sum12 = r1 + r2;

        // ΔR_par
        if (Math.abs(sum12) < 1e-12) {
            this.absErrorRPar = 0;
        } else {
            double dR1coeff = (r2 * r2) / (sum12 * sum12);
            double dR2coeff = (r1 * r1) / (sum12 * sum12);
            this.absErrorRPar = dR1coeff * deltaR + dR2coeff * deltaR;
        }

        // ΔR_total = ΔR_par + ΔR3
        this.absErrorRTotal = absErrorRPar + deltaR;

        // ΔI_total = ΔE/R_total + E/R_total² · ΔR_total
        if (Math.abs(rTotal) < 1e-12) {
            this.absErrorITotal = Double.NaN;
            this.relErrorITotal = Double.NaN;
        } else {
            this.absErrorITotal = deltaE / rTotal
                    + Math.abs(iTotal) / rTotal * absErrorRTotal;
            this.relErrorITotal = (Math.abs(iTotal) > 1e-12)
                    ? (absErrorITotal / Math.abs(iTotal)) * 100.0
                    : Double.NaN;
        }

        // ΔU_par = R_par·ΔI_total + |I_total|·ΔR_par
        if (Double.isNaN(absErrorITotal)) {
            this.absErrorUPar = Double.NaN;
            this.relErrorUPar = Double.NaN;
        } else {
            this.absErrorUPar = rPar * absErrorITotal + Math.abs(iTotal) * absErrorRPar;
            this.relErrorUPar = (Math.abs(uPar) > 1e-12)
                    ? (absErrorUPar / Math.abs(uPar)) * 100.0
                    : Double.NaN;
        }

        // ΔU3 = R3·ΔI_total + |I_total|·ΔR3
        if (Double.isNaN(absErrorITotal)) {
            this.absErrorU3 = Double.NaN;
            this.relErrorU3 = Double.NaN;
        } else {
            this.absErrorU3 = r3 * absErrorITotal + Math.abs(iTotal) * deltaR;
            this.relErrorU3 = (Math.abs(u3) > 1e-12)
                    ? (absErrorU3 / Math.abs(u3)) * 100.0
                    : Double.NaN;
        }

        // ΔI1 = ΔU_par/R1 + |U_par/R1²|·ΔR1
        if (Double.isNaN(absErrorUPar) || Math.abs(r1) < 1e-12) {
            this.absErrorI1 = Double.NaN;
            this.relErrorI1 = Double.NaN;
        } else {
            this.absErrorI1 = absErrorUPar / r1
                    + Math.abs(uPar) / (r1 * r1) * deltaR;
            this.relErrorI1 = (Math.abs(i1) > 1e-12)
                    ? (absErrorI1 / Math.abs(i1)) * 100.0
                    : Double.NaN;
        }

        // ΔI2 = ΔU_par/R2 + |U_par/R2²|·ΔR2
        if (Double.isNaN(absErrorUPar) || Math.abs(r2) < 1e-12) {
            this.absErrorI2 = Double.NaN;
            this.relErrorI2 = Double.NaN;
        } else {
            this.absErrorI2 = absErrorUPar / r2
                    + Math.abs(uPar) / (r2 * r2) * deltaR;
            this.relErrorI2 = (Math.abs(i2) > 1e-12)
                    ? (absErrorI2 / Math.abs(i2)) * 100.0
                    : Double.NaN;
        }
    }

    // Getters
    public double getDeltaI()          { return deltaI; }
    public double getDeltaE()          { return deltaE; }
    public double getDeltaR()          { return deltaR; }
    public void   setDeltaI(double v)  { this.deltaI = v; }
    public void   setDeltaE(double v)  { this.deltaE = v; }
    public void   setDeltaR(double v)  { this.deltaR = v; }

    public double getAbsErrorIOut()    { return absErrorIOut; }
    public double getRelErrorIOut()    { return relErrorIOut; }

    public double getAbsErrorRPar()    { return absErrorRPar; }
    public double getAbsErrorRTotal()  { return absErrorRTotal; }
    public double getAbsErrorITotal()  { return absErrorITotal; }
    public double getRelErrorITotal()  { return relErrorITotal; }
    public double getAbsErrorUPar()    { return absErrorUPar; }
    public double getRelErrorUPar()    { return relErrorUPar; }
    public double getAbsErrorU3()      { return absErrorU3; }
    public double getRelErrorU3()      { return relErrorU3; }
    public double getAbsErrorI1()      { return absErrorI1; }
    public double getRelErrorI1()      { return relErrorI1; }
    public double getAbsErrorI2()      { return absErrorI2; }
    public double getRelErrorI2()      { return relErrorI2; }
}