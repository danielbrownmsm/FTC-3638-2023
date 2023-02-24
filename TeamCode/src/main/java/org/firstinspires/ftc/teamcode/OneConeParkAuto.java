package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Vision;

@Autonomous(name = "One Cone Right Side Park Auto 25pts or something but probably won't work", group = "comp", preselectTeleOp="Tele-Op 2 drivers")
public class OneConeParkAuto extends LinearOpMode {
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

        // pipeline is tailored to see the cone from where the robot starts
        zone = vision.getConeZone();

        // just drive forwards for a second for now
        drivetrain.driveAuto(-.5, 0, 0);
        sleep(1300);
        drivetrain.driveAuto(0, 0, 0);
        sleep(300);

        // strafe right
        drivetrain.driveAuto(0, .4, 0);
        sleep(1100);
        drivetrain.driveAuto(0, 0, 0);
        sleep(300);

        // raise the intake
        intake.raise();
        sleep(1700);
        intake.freeze();
        intake.freeze();
        sleep(300);

        // drive forward a little
        drivetrain.driveAuto(-0.4, 0, 0);
        sleep(250);
        drivetrain.driveAuto(0, 0, 0);
        sleep(100);

        // lower slide
        intake.lower();
        sleep(500);
        intake.freeze();
        sleep(400);

        // spit it out and drive backwards
        intake.setIntake(1);
        sleep(500);

        drivetrain.driveAuto(0.4, 0, 0);
        sleep(400);
        drivetrain.driveAuto(0, 0, 0);
        sleep(300);

        if (zone == 1) { // the left zone
            drivetrain.driveAuto(0, -.5, 0); // strafe left
            sleep(2500); // wait a second
        } else if (zone == 2) { // the middle, drive forwards
            drivetrain.driveAuto(0, -0.3, 0); // the right zone, strafe right
            sleep(400);
        } else if (zone == 3) {
            drivetrain.driveAuto(0, .3, 0); // the right zone, strafe right
            sleep(400);
        }

        drivetrain.driveAuto(0, 0, 0); // stop the motors

        // and that's a wrap
    }
}
