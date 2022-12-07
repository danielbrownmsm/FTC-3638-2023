package org.firstinspires.ftc.teamcode;

import android.text.style.DynamicDrawableSpan;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;


/**
 * A simple tele-op program I threw together so we could test our intake prototype
 * The robot consists of a simple (very janky) chassis that looks kind of like a modified strafer chassis
 * Hardware configuration
 */
@TeleOp(name="Tele-Op", group="test")
public class MainTeleOp extends OpMode {
    private Drivetrain drivetrain;
    private Intake intake;

    @Override
    public void init() {
        drivetrain = new Drivetrain(telemetry, hardwareMap);
        intake = new Intake(telemetry, hardwareMap);

        drivetrain.init();
        intake.init();
    }

    @Override
    public void loop() {
        /**
         * Put the joystick readings into variables so we can use short names instead of writing out
         * gamepad1.left_stick_y four times
         */
        double drive = gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double steer = gamepad1.right_stick_x;


        drivetrain.drive(drive, strafe, steer);

        if (gamepad1.left_bumper) {
            intake.setIntake(1);
        } else if (gamepad1.right_bumper) {
            intake.setIntake(-1);
        } else {
            intake.setIntake(0);
        }

        if (gamepad1.dpad_up) {
            intake.raise();
        } else if (gamepad1.dpad_down) {
            intake.lower();
        } else {
            // nothing because periodic() will handle everything except no because we're not doing that rn
            intake.freeze();
        }

        drivetrain.periodic();
        intake.periodic();
    }
}