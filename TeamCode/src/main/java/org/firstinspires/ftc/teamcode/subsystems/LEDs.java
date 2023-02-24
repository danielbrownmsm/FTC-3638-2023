package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.PIDController;
import org.firstinspires.ftc.teamcode.Subsystem;

public class LEDs extends Subsystem {
    /**
     * Declare the hardware objects
     */
    private RevBlinkinLedDriver blinkin;

    /**
     * Telemetry object so we can print values for debugging
     */
    private Telemetry telemetry;

    private int pattern_;

    /**
     * Hardware map object to access hardware objects from the configuration file
     */
    private HardwareMap map;

    public LEDs(Telemetry telemetry, HardwareMap hardwareMap) {
        this.telemetry = telemetry;
        map = hardwareMap;

        blinkin = map.get(RevBlinkinLedDriver.class, "blinkin");
    }

    public void SetPattern(int pattern) {
        pattern_ = pattern;
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.values()[pattern]);
    }

    @Override
    public void periodic() {
        telemetry.addData("pattern", pattern_);
        telemetry.update();
    }
}