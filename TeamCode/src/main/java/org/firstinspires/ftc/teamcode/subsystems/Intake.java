package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.PIDController;
import org.firstinspires.ftc.teamcode.Subsystem;

public class Intake extends Subsystem {
    /**
     * Declare intake servo objects
     * These are CR (Continuous-Rotation) servos, which means they act basically like motors
     * for regular servos (those that go to and hold a position), use ServoImplEx
     */
    private CRServoImplEx leftIntake;
    private CRServoImplEx rightIntake;

    /**
     * Our linear slide motor
     */
    private DcMotorImplEx slideMotor;

    // setpoint for the linear slide
    private double setpoint = 0;

    // pid for the linear slide to hold it at a position
    private PIDController slidePID;

    /**
     * Telemetry object so we can print values for debugging
     */
    private Telemetry telemetry;

    /**
     * Hardware map object to access hardware objects from the configuration file
     */
    private HardwareMap map;

    /**
     * Constructor for the intake
     * The Intake represents the entire manipulator, from the linear slide to the intake servos
     * @param telemetry
     * @param hardwareMap
     */
    public Intake(Telemetry telemetry, HardwareMap hardwareMap) {
        this.telemetry = telemetry;
        map = hardwareMap;


        /**
         * Initialize our motors with the names from the configuration file
         */
        leftIntake = map.get(CRServoImplEx.class, "left intake servo");
        rightIntake = map.get(CRServoImplEx.class, "right intake servo");

        // do the same thing for the motor
        slideMotor = map.get(DcMotorImplEx.class, "linear slide");
        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        /**
         * PID Controller to hold the linear slide at the set position
         * Not currently using it because haven't had time to tune it but working on it
         */
        slidePID = new PIDController(Constants.slideP, Constants.slideI, Constants.slideD);
    }

    /**
     * Reset the subsystem to an initial state so everything knows it's at a true 0 position
     */
    @Override
    public void init() {
        // pretty much just reset the encoder
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /**
     * Set the intake servos to the specified power
     * The servos automatically move the correct direction (opposite each other)
     * so negative intakes things and positive outtakes them
     * I think at least I don't remember
     * @param power the power to set the servos to
     */
    public void setIntake(double power) {
        leftIntake.setPower(power);
        rightIntake.setPower(-power);
    }

    /**
     * Raises the linear slide
     * while it is being called
     */
    public void raise() {
        // not using PID controller right now
        /*
        setpoint += 1;
        slidePID.setSetpoint(setpoint);
        */

        // this needs to be negative to move upwards. to make it go slower,
        // make the number smaller (ie -0.5 or -0.75)
        slideMotor.setPower(-1);
    }

    /**
     * Lowers the linear slide
     * while it is being called
     */
    public void lower() {
        /*
        setpoint -= 1;
        slidePID.setSetpoint(setpoint);
        */

        // keep this number small because gravity is helping the slide move down
        // we don't want to slam it into the robot/ground/whatever
        // but now our motor is geared to be a hefty boi so we can make it a little faster because
        // internal resistance from the gears
        slideMotor.setPower(0.2);
    }

    /**
     * Keeps the linear slide at the current position
     * This number must be negative for this to work
     */
    public void freeze() {
        // this might need to be smaller though
        slideMotor.setPower(-0.1);
    }

    @Override
    public void periodic() {
        // WE ARE NOT CURRENTLY USING THIS METHOD FOR ANYTHING,
        // however later in the season we will be using it probably
        //slideMotor.setPower(slidePID.calculate(slideMotor.getCurrentPosition()));
    }
}
