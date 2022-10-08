package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


/**
 * A simple tele-op program I threw together so we could test our intake prototype
 * The robot consists of a simple (very janky) chassis that looks kind of like a modified 
 * Hardware configuration
 */
@TeleOp(name="Intake Test TeleOp 2", group="test")
public class IntakeTestTeleOpV2 extends OpMode {
    /**
     * Declare the motor objects
     */
    private DcMotorImplEx leftFront;
    private DcMotorImplEx leftBack;
    private DcMotorImplEx rightFront;
    private DcMotorImplEx rightBack;

    /**
     * Declare intake servo objects
     * These are CR (Continuous-Rotation) servos, which means they act basically like motors
     * for regular servos (those that go to and hold a position), use ServoImplEx
     */
    private CRServoImplEx leftIntake;
    private CRServoImplEx rightIntake;

    /**
     * You don't have to do this but I find it makes the code cleaner
     * This is basically your configuration file (y'know, when you tap "configure robot" on the driver station)
     * put into a java object
     */
    private HardwareMap map;

    @Override
    public void init() {
        map = hardwareMap;

        /**
         * Initialize our motors with the names from the configuration file
         */
        leftFront = map.get(DcMotorImplEx.class, "left front");
        leftBack = map.get(DcMotorImplEx.class, "left back");
        rightFront = map.get(DcMotorImplEx.class, "right front");
        rightBack = map.get(DcMotorImplEx.class, "right back");

        /**
         * Do the same thing with the servos
         */
        leftIntake = map.get(CRServoImplEx.class, "left intake servo");
        rightIntake = map.get(CRServoImplEx.class, "right intake servo");

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

    @Override
    public void loop() {
        double drive = gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double steer = gamepad1.right_stick_x;

        leftFront.setPower(drive - steer - strafe);
        rightFront.setPower(drive - steer + strafe);
        leftBack.setPower(drive + steer + strafe);
        rightBack.setPower(drive + steer - strafe);

        if (gamepad1.left_bumper) {
            leftIntake.setPower(-1);
            rightIntake.setPower(1);
        } else if (gamepad1.right_bumper) {
            leftIntake.setPower(1);
            rightIntake.setPower(-1);
        } else {
            leftIntake.setPower(0);
            rightIntake.setPower(0);
        }
    }
}