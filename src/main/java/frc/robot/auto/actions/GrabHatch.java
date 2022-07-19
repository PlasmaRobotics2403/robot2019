package frc.robot.auto.actions;

import frc.robot.DriveTrain;
import frc.robot.HatchIntake;
import frc.robot.auto.util.Action;

/**
 *
 */
public class GrabHatch implements Action {

    DriveTrain drive;
    HatchIntake hatchIntake;
    boolean placedHatch;
    int delay;

    public GrabHatch(DriveTrain drive, HatchIntake hatchIntake) {
        this.drive = drive;
        this.hatchIntake = hatchIntake;
        delay = 0;
    }

    @Override
    public boolean isFinished() {
        return placedHatch;
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
        drive.autonTankDrive(0.333333, 0.333333);
        hatchIntake.fullExtend();
        while (delay < 10000000) {
            delay++;
        }
        hatchIntake.grabHatch();
        delay = 0;
        while (delay < 10000000) {
            delay++;
        }
        hatchIntake.fullRetract();
        placedHatch = true;
    }

    @Override
    public void end() {
        drive.stopDrive();
    }

}
