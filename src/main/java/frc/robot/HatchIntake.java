
package frc.robot;

import edu.wpi.first.wpilibj.Solenoid;

public class HatchIntake {
    public Solenoid clawPiston;
    public Solenoid backExtender;
    public Solenoid frontExtender;

    boolean isClamped;

    public HatchIntake(int CLAW_PISTON_ID, int BACK_EXTENDER_ID, int FRONT_EXTENDER_ID) {
        clawPiston = new Solenoid(CLAW_PISTON_ID);
        backExtender = new Solenoid(BACK_EXTENDER_ID);
        frontExtender = new Solenoid(FRONT_EXTENDER_ID);

        isClamped = true;
    }

    public void grabHatch() {
        clawPiston.set(false);
        isClamped = true;
    }

    public void releaseHatch() {
        clawPiston.set(true);
        isClamped = false;
    }

    public void fullExtend() {
        frontExtender.set(true);
        backExtender.set(true);
    }

    public void halfExtend() {
        backExtender.set(true);
        frontExtender.set(false);
    }

    public void fullRetract() {
        frontExtender.set(false);
        backExtender.set(false);
    }

    public boolean getIsClamped() {
        return isClamped;
    }

}