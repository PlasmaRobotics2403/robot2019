
package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class HAB {
    TalonSRX leftRearRaise;
    TalonSRX rightRearRaise;
    TalonSRX leftFrontRaise;
    TalonSRX rightFrontRaise;
    
    TalonSRX HABDrive;

    HAB(int left_front_raise_ID, int right_front_raise_ID, int left_rear_raise_ID, int right_rear_raise_ID, int HAB_drive_ID) {
            leftFrontRaise = new TalonSRX(left_front_raise_ID);
            rightFrontRaise = new TalonSRX(right_front_raise_ID);
            leftRearRaise = new TalonSRX(left_rear_raise_ID);
            rightRearRaise = new TalonSRX(right_rear_raise_ID);
            HABDrive = new TalonSRX(HAB_drive_ID);

            rightFrontRaise.setInverted(true);
            rightRearRaise.setInverted(true);
    }

    void raiseRobot(double speed){
        HABLift(speed);
    }

    void lowerRobot(double speed){
        HABLift(-speed);
    }

    public void HABLift(double speed) {
        if(speed > 0){
            leftFrontRaise.set(ControlMode.PercentOutput, 0);
            rightFrontRaise.set(ControlMode.PercentOutput, 0);
            leftRearRaise.set(ControlMode.PercentOutput, speed * Constants.MAX_HAB_REAR_SPEED);
            rightRearRaise.set(ControlMode.PercentOutput, speed * Constants.MAX_HAB_REAR_SPEED);
        }
        else{
            leftRearRaise.set(ControlMode.PercentOutput, speed * Constants.MAX_HAB_REAR_SPEED);
            rightRearRaise.set(ControlMode.PercentOutput, speed * Constants.MAX_HAB_REAR_SPEED);
            leftFrontRaise.set(ControlMode.PercentOutput, speed * Constants.MAX_HAB_ARM_SPEED);
            rightFrontRaise.set(ControlMode.PercentOutput, speed * Constants.MAX_HAB_ARM_SPEED);   
        }
    }

    public void HABForward(double speed) {
        speed *= Constants.MAX_HAB_DRIVE_SPEED;

        HABDrive.set(ControlMode.PercentOutput, speed);
    }
}