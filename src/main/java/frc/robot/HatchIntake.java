
package frc.robot;

import edu.wpi.first.wpilibj.Solenoid;

public class HatchIntake {
    Solenoid clawPiston;
    Solenoid backExtender;
    Solenoid frontExtender;

    public HatchIntake(int CLAW_PISTON_ID, int BACK_EXTENDER_ID, int FRONT_EXTENDER_ID){
            clawPiston = new Solenoid(CLAW_PISTON_ID);
            backExtender = new Solenoid(BACK_EXTENDER_ID);
            frontExtender = new Solenoid(FRONT_EXTENDER_ID);

    }

    void grabHatch(){
        clawPiston.set(false);
    }

    void releaseHatch(){
        clawPiston.set(true);
    }

    void fullExtend(){
        frontExtender.set(true);
        backExtender.set(true);
    }

    void halfExtend(){
        backExtender.set(true);
    }

    void fullRetract(){
        frontExtender.set(false);
        backExtender.set(false);
    }


}