package frc.robot.auto.actions;

import frc.robot.HatchIntake;
import frc.robot.auto.util.Action;

/**
 *
 */
public class CloseHatchIntake implements Action {

    HatchIntake hatchIntake;
    boolean grabbedHatch;
    boolean extend;
    int delay;

    public CloseHatchIntake(HatchIntake hatchIntake, boolean extend) {
        this.hatchIntake = hatchIntake;
        this.extend = extend;
    }

    @Override
    public boolean isFinished() {
        return grabbedHatch;
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
        if (extend) {
            hatchIntake.fullExtend();
        }

        while (delay < 10) {
            delay++;
        }
        hatchIntake.grabHatch();
        grabbedHatch = true;
    }

    @Override
    public void end() {
        hatchIntake.fullRetract();
    }

}
