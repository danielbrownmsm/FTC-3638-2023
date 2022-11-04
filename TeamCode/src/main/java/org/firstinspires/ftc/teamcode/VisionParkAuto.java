package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Vision;

@Autonomous(name = "Vision Park Auto 20pts but probably won't work", group = "comp", preselectTeleOp="Tele-Op")
public class VisionParkAuto extends LinearOpMode {
    private Drivetrain drivetrain;
    private Intake intake;
    private Vision vision;

    private int zone = -1;


    @Override
    public void runOpMode() throws InterruptedException {
        // initialize all the subsystems of the robot
        drivetrain = new Drivetrain(telemetry, hardwareMap);
        intake = new Intake(telemetry, hardwareMap);
        vision = new Vision(telemetry, hardwareMap);

        drivetrain.init();
        intake.init();
        vision.init();

        // wait for the robot to start
        waitForStart();

        // just drive forwards for a second for now
        drivetrain.drive(1, 0, 0);
        sleep(1000);
        drivetrain.drive(0, 0, 0);

        zone = vision.getConeZone();

        if (zone == -1) {
            // we haven't spotted it, just drive forwards
            drivetrain.drive(1, 0, 0);
        } else if (zone == 0) { // the left zone
            drivetrain.drive(0, -1, 0); // strafe left
        } else if (zone == 1) { // the middle, drive forwards
            drivetrain.drive(1, 0, 0);
        } else if (zone == 2) {
            drivetrain.drive(0, 1, 0); // the right zone, strafe right
        }

        sleep(1000); // wait a second
        drivetrain.drive(0, 0, 0); // stop the motors

        // and that's a wrap
    }
}
