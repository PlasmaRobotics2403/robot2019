package frc.robot.auto.actions;

import frc.robot.DriveTrain;
import frc.robot.HatchIntake;
import frc.robot.auto.util.Action;

/**
 *
 */
public class AutonTankDrive implements Action {

    DriveTrain drive;
    double leftSpeed;
    double rightSpeed;
    boolean finished;
    int delay;
    double time;

    public AutonTankDrive(DriveTrain drive, double leftSpeed, double rightSpeed, double time) {
        this.drive = drive;
        this.leftSpeed = leftSpeed;
        this.rightSpeed = rightSpeed;
        this.time = time;
        finished = false;
        delay = 0;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
        drive.autonTankDrive(leftSpeed, rightSpeed);

        // 300000000 ~ 1 second
        while (delay < 30000000 * time) {
            delay++;
        }
        finished = true;
    }

    @Override
    public void end() {
        drive.stopDrive();
    }

}