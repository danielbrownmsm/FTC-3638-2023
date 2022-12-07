package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

import java.util.List;
import java.util.ArrayList;

import org.firstinspires.ftc.teamcode.Subsystem;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.MatOfPoint;

import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;


//import org.firstinspires.ftc.teamcode.control.PIDController;
import org.firstinspires.ftc.teamcode.Constants;

/**
 * The vision-processing subsystem
 * We use the EasyOpenCV package, because I'm already familiar with OpenCV
 * and because it's really easy to import and use with FTC code
 * I'll admit some of it is a little hard to get working with the FTC control system,
 * but once you get the hang of it it's a really nice package
 */
public class Vision extends Subsystem {
    private OpenCvCamera camera;
    private Telemetry telemetry;

    private ConeZonePipeline coneZonePipeline;

    /**
     * Vision subsystem, to hold the related functions
     * The actual pipeline is contained in a subclass, because that's how the example code does it
     * @param telemetry a telemetry object so we can print debug values
     * @param hardwareMap hardware map object to get the camera object
     */
    public Vision(Telemetry telemetry, HardwareMap hardwareMap) {
        this.telemetry = telemetry;

        // example/template code, just copy and pasted
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        WebcamName webcamName = hardwareMap.get(WebcamName.class, "Webcam1"); // configuration file has "Webcam1" as its name
        camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);
    }

    /**
     * More template/example code
     */
    @Override
    public void init() {
        camera.openCameraDeviceAsync(
            new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() {
                    /**
                     * Resolution parameters and orientation parameter
                     */
                    camera.startStreaming(752, 416, OpenCvCameraRotation.UPRIGHT);
                    camera.setPipeline(coneZonePipeline);
                }

                @Override
                public void onError(int errorCode) {
                    //TODO do something here or something
                    // idk what but y'know
                }
            }
        );
    }

    /**
     * Wrapper method for the actual OpenCV pipeline
     * 1 is left, 2 is straight, 3 is right
     * -1 means have not yet acquired a target
     * @return what zone to drive to, based on the signal sleeve
     */
    public int getConeZone() {
        return coneZonePipeline.getConeZone();
    }

    /**
     * Returns if there is a target in view, based on the OpenCV pipeline
     * having identified a zone yet
     * @return if there has been a target in view
     */
    public boolean hasTargetInView() {
        return coneZonePipeline.getConeZone() > -1;
    }

    /**
     * Stops the camera pipeline
     * useful for freeing up resources and not crashing the app and stuff
     */
    public void stopPipeline() {
        camera.stopStreaming();
    }

    @Override
    public void periodic() {
        telemetry.addData("target hub level", getConeZone());
        // honestly we don't do much here. Everything's already called automatically with the pipeline and stuff
        //TODO telemetry
    }



    /**
     * A child/subclass/whatever to hold the
     */
    protected class ConeZonePipeline extends OpenCvPipeline {
        boolean viewportPaused = false;

        // the matrix to store the processed image
        private Mat matBlack = new Mat(Constants.CAMERA_HEIGHT, Constants.CAMERA_WIDTH, 24);
        private Mat matGreen = new Mat(Constants.CAMERA_HEIGHT, Constants.CAMERA_WIDTH, 24);
        private Mat matPurple = new Mat(Constants.CAMERA_HEIGHT, Constants.CAMERA_WIDTH, 24);

        // matrix to hold the thing we end up displaying
        private Mat matDisplay = new Mat(Constants.CAMERA_HEIGHT, Constants.CAMERA_WIDTH, 24);

        // matrix to store the bitmask returned by the inRange function
        Mat maskBlack = new Mat(Constants.CAMERA_HEIGHT, Constants.CAMERA_WIDTH, 24);
        Mat maskGreen = new Mat(Constants.CAMERA_HEIGHT, Constants.CAMERA_WIDTH, 24);
        Mat maskPurple = new Mat(Constants.CAMERA_HEIGHT, Constants.CAMERA_WIDTH, 24);

        // for contours, idk why you need it but you do
        Mat hierarchy = new Mat();

        // a list of all the contours
        List<MatOfPoint> contours = new ArrayList<>();

        //  lower and upper bounds for the inRange we do later to filter for the colors
        // values are in HSV
        Scalar lower_black = new Scalar(0, 0, 0);
        Scalar upper_black = new Scalar(360, 1, .1);

        Scalar lower_purple = new Scalar(260, .4, .3);
        Scalar upper_purple = new Scalar(280, 1, 1);

        Scalar lower_green = new Scalar(90, .66, .46);
        Scalar upper_green = new Scalar(132, 1, 1);

        // kernel for blurring
        Mat kernel = new Mat();

        // size for blurring
        Size size = new Size(3, 3);

        // to store the largest contour (we loop through all of them to find the largest)
        double maxValBlack = 0;
        double maxValGreen = 0;
        double maxValPurple = 0;
        int maxValIdBlack = 0;
        int maxValIdGreen = 0;
        int maxValIdPurple = 0;
        int coneZone = -1;
        int lastConeZone = -1;

        public int getConeZone() {
            // this logic so we don't flip between actual and -1 really quickly a whole bunch
            if (coneZone > 0) {
                lastConeZone = coneZone;
                return coneZone;
            }
            return lastConeZone;
        }

        @Override
        public Mat processFrame(Mat input) {


            // convert to HSV from native openCV
            Imgproc.cvtColor(input, matBlack, Imgproc.COLOR_BGR2HSV);
            Imgproc.cvtColor(input, matGreen, Imgproc.COLOR_BGR2HSV);
            Imgproc.cvtColor(input, matPurple, Imgproc.COLOR_BGR2HSV);



//            Imgproc.blur(processed, processed, size); // don't use blur for right now

            // filter for colors in our range
            Core.inRange(matBlack, lower_black, upper_black, maskBlack);
            Core.inRange(matGreen, lower_green, upper_green, maskGreen);
            Core.inRange(matPurple, lower_purple, upper_purple, maskPurple);

            // convert back to regular color (so we can display to the driver station)
            //Imgproc.cvtColor(matBlack, matBlack, Imgproc.COLOR_HSV2BGR); // convert back to OpenCV native

            // don't blur for now
            //Imgproc.erode(mask, mask, kernel); // erode the bitmask returned by the inRange()

            // apply the mask so only the parts that got through the inRange are displayed
            //Core.bitwise_and(matBlack, matBlack, input, maskBlack); // apply the mask so only the filtered part shows in the processed matrix


            // time for the actual computer vision
            // find contours
            Imgproc.findContours(maskBlack, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE); // find contours

            maxValBlack = 0;
            maxValIdBlack = 0;
            for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
                //Imgproc.drawContours(processed, contours, contourIdx, color);
                Rect rect = Imgproc.boundingRect(contours.get(contourIdx));
                double area = rect.width * rect.height;

                // make sure the rectangle is in our little search area
                if (rect.y > 200 && rect.y < 200 && rect.x > 200 && rect.x < 200) {
                    if (area > maxValBlack) { // if the area is larger than our current largest area
                        maxValBlack = area;
                        maxValIdBlack = contourIdx; // id of the contour in the list so we can retrieve it
                    }
                }
            }

            Imgproc.findContours(maskGreen, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE); // find contours

            maxValGreen = 0;
            maxValIdGreen = 0;
            for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
                //Imgproc.drawContours(processed, contours, contourIdx, color);
                Rect rect = Imgproc.boundingRect(contours.get(contourIdx));
                double area = rect.width * rect.height;

                // make sure the rectangle is in our little search area
                if (rect.y > 200 && rect.y < 200 && rect.x > 200 && rect.x < 200) {
                    if (area > maxValGreen) { // if the area is larger than our current largest area
                        maxValGreen = area;
                        maxValIdGreen = contourIdx; // id of the contour in the list so we can retrieve it
                    }
                }
            }

            Imgproc.findContours(maskPurple, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE); // find contours

            maxValPurple = 0;
            maxValIdPurple = 0;
            for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
                //Imgproc.drawContours(processed, contours, contourIdx, color);
                Rect rect = Imgproc.boundingRect(contours.get(contourIdx));
                double area = rect.width * rect.height;

                // make sure the rectangle is in our little search area
                if (rect.y > 260 && rect.y < 460 && rect.x > 440 && rect.x < 840) {
                    if (area > maxValPurple) { // if the area is larger than our current largest area
                        maxValPurple = area;
                        maxValIdPurple = contourIdx; // id of the contour in the list so we can retrieve it
                    }
                }
            }



            // time to decide what zone it is
            if (maxValPurple > maxValBlack) { // if purple is larger than black
                if (maxValGreen > maxValPurple) { // and green is larger than purple
                    telemetry.addData("color", "green");
                    coneZone = 1; // green
                } else { // otherwise, purple is larger than green and larger than black
                    telemetry.addData("color", "purple");
                    coneZone = 2; // purple
                }
            } else if (maxValGreen > maxValBlack){ // elif green is larger than black, and purple is smaller than black (because of the first if statement)
                telemetry.addData("color", "green");
                coneZone = 2; // then it's green
            } else {
                telemetry.addData("color", "black");
                coneZone = 3; // well, purple is smaller than black, and green is smaller than black, so black
            }


            // if the contour we found is valid (like, we actually saw something)
//            if (maxValId > 0 && contours.size() > 0 && maxValId < contours.size()) {
//                Rect largestRect = Imgproc.boundingRect(contours.get(maxValId));
//
//                // draw a rectangle around the contour in the appropriate color
//                if (largestRect.x > 500) {
//                    Imgproc.rectangle(processed, largestRect, level3Color, 5);
//                    targetHubAutoLevel = 3;
//                } else if (350 < largestRect.x && largestRect.x < 500) {
//                    Imgproc.rectangle(processed, largestRect, level2Color, 5);
//                    targetHubAutoLevel = 2;
//                } else if (0 < largestRect.x && largestRect.x < 350) {
//                    Imgproc.rectangle(processed, largestRect, level1Color, 5);
//                    targetHubAutoLevel = 1;
//                } else {
//                    // set it to -1 here? but like flickering between -1 and actual?
//                }
//            }

            contours.clear();
//            hierarchy.setTo(Scalar.all(0)); //???

            return matBlack;
        }
    }
}