package com.qualcomm.ftcrobotcontroller.opmodes;

/**
 * Created by alex on 1/15/16.
 */

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * TeleOp Mode
 * <p>
 * Enables control of the robot via the gamepad
 */
public class TestTeleOp extends OpMode {

    /*
     * Note: the configuration of the servos is such that
     * as the arm servo approaches 0, the arm position moves up (away from the floor).
     * Also, as the claw servo approaches 0, the claw opens up (drops the game element).
     */
    // TETRIX VALUES.
    final static double ARM_MIN_RANGE  = 0.20;
    final static double ARM_MAX_RANGE  = 0.90;
    //final static double CLAW_MIN_RANGE  = 0.20;
    //final static double CLAW_MAX_RANGE  = 0.7;

    // position of the arm servo.
    double armPosition;

    // Rate of change on the servo when the button is clicked
    double armDelta = 0.015;

    // position of the claw servo
    //double clawPosition;

    // amount to change the claw servo position by
    //double clawDelta = 0.1;

    DcMotor motorRightFront;
    DcMotor motorRightBack;
    DcMotor motorLeftFront;
    DcMotor motorLeftBack;

    DcMotor motorArm;
    //Servo claw;
    Servo arm;

    /**
     * Constructor
     */
    public TestTeleOp() {

    }

    /*
     * Code to run when the op mode is first enabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */
    @Override
    public void init() {
		/*
		 * Use the hardwareMap to get the dc motors and servos by name. Note
		 * that the names of the devices must match the names used when you
		 * configured your robot and created the configuration file.
		 */

		/*
		 * For the demo Tetrix K9 bot we assume the following,
		 *   There are two motors "motor_1" and "motor_2"
		 *   "motor_1" is on the right side of the bot.
		 *   "motor_2" is on the left side of the bot.
		 *
		 * We also assume that there are two servos "servo_1" and "servo_6"
		 *    "servo_1" controls the arm joint of the manipulator.
		 *    "servo_6" controls the claw joint of the manipulator.
		 */

        motorLeftFront = hardwareMap.dcMotor.get("m1");
        motorLeftBack = hardwareMap.dcMotor.get("m2");
        motorLeftBack.setDirection(DcMotor.Direction.REVERSE);
        motorLeftFront.setDirection(DcMotor.Direction.REVERSE);

        motorRightFront = hardwareMap.dcMotor.get("m3");
        motorRightBack = hardwareMap.dcMotor.get("m4");
        /*motorRightBack.setDirection(DcMotor.Direction.REVERSE);
        motorRightFront.setDirection(DcMotor.Direction.REVERSE);*/

        motorArm = hardwareMap.dcMotor.get("a1");
        motorArm.setDirection(DcMotor.Direction.REVERSE);

        arm = hardwareMap.servo.get("servo_1");
        //claw = hardwareMap.servo.get("servo_6");

        // assign the starting position of the wrist and claw
        armPosition = 0.2;
        //clawPosition = 0.2;
    }

    /*
     * This method will be called repeatedly in a loop
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
     */
    @Override
    public void loop() {

		/*
		 * Gamepad 1
		 *
		 * Gamepad 1 controls the motors via the left stick, and it controls the
		 * wrist/claw via the a,b, x, y buttons
		 */

        // tank drive
        // note that if y equal -1 then joystick is pushed all of the way forward.
        float leftPower = gamepad1.left_stick_y;
        float rightPower = gamepad1.right_stick_y;
        float middlePower = 0;

        if(gamepad1.a)
        {
            middlePower = 1f;
        }

        if(gamepad1.y)
        {
            middlePower = -1f;
        }

        // clip the right/left values so that the values never exceed +/- 1
        rightPower = Range.clip(rightPower, -1, 1);
        leftPower = Range.clip(leftPower, -1, 1);
        middlePower = Range.clip(middlePower, -1, 1);

        // scale the joystick value to make it easier to control
        // the robot more precisely at slower speeds.
        rightPower = (float)scaleInput(rightPower);
        leftPower =  (float)scaleInput(leftPower);
        middlePower = (float)scaleInput(middlePower);
        //Bacon strips

        // write the values to the motors
        motorLeftFront.setPower(leftPower);
        motorLeftBack.setPower(leftPower);
        motorRightFront.setPower(rightPower);
        motorRightBack.setPower(rightPower);
        motorArm.setPower(middlePower);

        /*// update the position of the arm.
        if (gamepad1.a) {
            // if the A button is pushed on gamepad1, increment the position of
            // the arm servo.
            armPosition += armDelta;
        }

        if (gamepad1.y) {
            // if the Y button is pushed on gamepad1, decrease the position of
            // the arm servo.
            armPosition -= armDelta;
        }*/

        /* update the position of the claw
        if (gamepad1.left_bumper) {
            clawPosition += clawDelta;
        }*/

        if (gamepad1.left_trigger > 0.25) {
            //clawPosition -= clawDelta;
            armPosition += armDelta;

        }

        if (gamepad1.right_trigger > 0.25) {
            //clawPosition -= clawDelta;
            armPosition -= armDelta;

        }
        /*
        // update the position of the claw
        if (gamepad1.x) {
            clawPosition += clawDelta;
        }

        if (gamepad1.b) {
            clawPosition -= clawDelta;
        }*/

        //clip the position values so that they never exceed their allowed range.
        armPosition = Range.clip(armPosition, ARM_MIN_RANGE, ARM_MAX_RANGE);
        //clawPosition = Range.clip(clawPosition, CLAW_MIN_RANGE, CLAW_MAX_RANGE);

        // write position values to the wrist and claw servo
        arm.setPosition(armPosition);
        //claw.setPosition(clawPosition);

		/*
		 * Send telemetry data back to driver station. Note that if we are using
		 * a legacy NXT-compatible motor controller, then the getPower() method
		 * will return a null value. The legacy NXT-compatible motor controllers
		 * are currently write only.
		 */

        telemetry.addData("Text", "*** Robot Data***");
        telemetry.addData("arm", "arm:  " + String.format("%.2f", armPosition));
        //telemetry.addData("claw", "claw:  " + String.format("%.2f", clawPosition));
        telemetry.addData("left tgt pwr",  "left  pwr: " + String.format("%.2f", leftPower));
        telemetry.addData("right tgt pwr", "right pwr: " + String.format("%.2f", rightPower));
    }

    /*
     * Code to run when the op mode is first disabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
     */
    @Override
    public void stop() {

    }

    /*
     * This method scales the joystick input so for low joystick values, the
     * scaled value is less than linear.  This is to make it easier to drive
     * the robot more precisely at slower speeds.
     */
    double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);

        // index should be positive.
        if (index < 0) {
            index = -index;
        }

        // index cannot exceed size of array minus 1.
        if (index > 16) {
            index = 16;
        }

        // get value from the array.
        double dScale;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        // return scaled value.
        return dScale;
    }

}

