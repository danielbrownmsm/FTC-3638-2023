package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.PIDCoefficients;

//DOCUMENT
public class PIDController {
    private double getMillis() {
        return System.nanoTime() / 1e6;
    }

    private double kP = 0;
    private double kI = 0;
    private double kD = 0;

    private double error = 0;
    private double lastError = 0;
    private double totalError = 0;
    private double velocityError = 0;

    private double tolerance = 0.2;
    private double velocityTolerance = 0.2;

    private double lastTime = 0;
    private double setpoint = 0;

    private boolean continuous = false;
    private double minInput = 0;
    private double maxInput = 0;

    private boolean hasRun = false; // so we don't immediately return true

    //DOCUMENT
    public PIDController(double kP, double kI, double kD) {
        this(kP, kI, kD, 0, 0, false);
    }

    //DOCUMENT
    public PIDController(PIDCoefficients coeffs) {
        this(coeffs.p, coeffs.i, coeffs.d, 0, 0, false);
    }

    //DOCUMENT
    public PIDController(PIDCoefficients coeffs, double minInput, double maxInput, boolean continuous) {
        this(coeffs.p, coeffs.i, coeffs.d, minInput, maxInput, continuous);
    }

    //DOCUMENT
    public PIDController(double kP, double kI, double kD, double minInput, double maxInput, boolean continuous) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;

        this.continuous = continuous;
        this.minInput = minInput;
        this.maxInput = maxInput;
    }

    //DOCUMENT
    public double inputModulus(double input) {
        double modulus = maxInput - minInput;

        int numMax = (int) ((input - minInput) / modulus);
        input -= numMax * modulus;

        int numMin = (int) ((input - maxInput) / modulus);
        input -= numMin * modulus;

        return input;
    }

    //DOCUMENT
    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
    }

    //DOCUMENT
    public double calculate(double measurement, double setpoint) {
        setSetpoint(setpoint);
        return calculate(measurement);
    }

    //DOCUMENT
    public double calculate(double measurement) {
        hasRun = true;
        if (continuous) {
            error = inputModulus(setpoint - measurement);
        } else {
            error = setpoint - measurement;
        }

        velocityError = (error - lastError) / (getMillis() - lastTime);
        //.velocityError = 0;
        totalError += error;

        double output = kP * error + kI * totalError + kD * velocityError;
        lastError = error;
        lastTime = getMillis();

        return output;
    }

    //DOCUMENT
    public void setTolerance(double tolerance, double velocityTolerance) {
        this.tolerance = tolerance;
        this.velocityTolerance = velocityTolerance;
    }

    //DOCUMENT
    public void reset() {
        error = 0;
        lastError = 0;
        totalError = 0;
        velocityError = 0;
        lastTime = getMillis();
        hasRun = false;
    }

    //DOCUMENT
    public double getError() {
        return error;
    }

    //DOCUMENT
    public double getSetpoint() {
        return setpoint;
    }

    //DOCUMENT
    public boolean atSetpoint() {
        if (hasRun) {
            return Math.abs(error) < tolerance && Math.abs(velocityError) < velocityTolerance;
        }
        return false;
    }
}