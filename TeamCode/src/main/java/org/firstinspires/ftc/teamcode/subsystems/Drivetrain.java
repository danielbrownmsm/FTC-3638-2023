package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Subsystem;

public class Drivetrain extends Subsystem {
    /**
     * Declare the motor objects
     */
    private DcMotorImplEx leftFront;
    private DcMotorImplEx leftBack;
    private DcMotorImplEx rightFront;
    private DcMotorImplEx rightBack;

    /**
     * Telemetry object so we can print values for debugging
     */
    private Telemetry telemetry;

    /**
     * Hardware map object to access hardware objects from the configuration file
     */
    private HardwareMap map;

    /**
     * Speed modifier that gets multiplied to tele-op drive methods, to reduce twitchiness
     */
    private static final double speedMod = 0.8;

    public Drivetrain(Telemetry telemetry, HardwareMap hardwareMap) {
        this.telemetry = telemetry;
        map = hardwareMap;

        /**
         * Initialize our motors with the names from the configuration file
         */
        leftFront = map.get(DcMotorImplEx.class, "left front");
        leftBack = map.get(DcMotorImplEx.class, "left back");
        rightFront = map.get(DcMotorImplEx.class, "right front");
        rightBack = map.get(DcMotorImplEx.class, "right back");


        /**
         * Invert the left motors
         * We do this because when we have two motors facing opposite directions,
         * having each of them go forward (1.0 power) will cause them to turn in opposite directions.
         * So we invert the left motors so now we can set all of the motors to +1.0 power
         * and it causes the robot to drive forwards
         */
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.FORWARD);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    /**
     * A simple tele-op drive method. Drives using mecanum control.
     * Left stick controls directional movement
     * Right stick controls steering
     * Not field-oriented
     * @param drive forward/backward power
     * @param strafe left/right power
     * @param steer turning power
     */
    public void drive(double drive, double strafe, double steer) {
        leftFront.setPower((drive - steer - strafe) * speedMod);
        rightFront.setPower((drive + steer + strafe) * speedMod);
        leftBack.setPower((drive - steer + strafe) * speedMod);
        rightBack.setPower((drive + steer - strafe) * speedMod);
    }
}
