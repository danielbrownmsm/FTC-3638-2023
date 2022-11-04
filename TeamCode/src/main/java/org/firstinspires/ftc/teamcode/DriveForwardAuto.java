package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Vision;

@Autonomous(name = "Drive Forward Auto 2pts", group = "comp", preselectTeleOp="Tele-Op")
public class DriveForwardAuto extends LinearOpMode {
    private Drivetrain drivetrain;
    private Intake intake;

    private int zone = -1;


    @Override
    public void runOpMode() throws InterruptedException {
        // initialize all the subsystems of the robot
        drivetrain = new Drivetrain(telemetry, hardwareMap);
        intake = new Intake(telemetry, hardwareMap);

        drivetrain.init();
        intake.init();

        // wait for the robot to start
        waitForStart();

        // just drive forwards for a second for now
        drivetrain.drive(1, 0, 0);
        sleep(1000);
        drivetrain.drive(0, 0, 0);

        // and that's a wrap
    }
}
