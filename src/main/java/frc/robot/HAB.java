
package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;

public class HAB {
    TalonSRX leftRearRaise;
    TalonSRX rightRearRaise;
    TalonSRX leftFrontRaise;
    TalonSRX rightFrontRaise;

    DriveTrain driveTrain;
    
    TalonSRX HABDrive;

    DigitalInput HABElevatorLimit;
    DigitalInput HABArmLimit;

    boolean limitState;

    HAB(int left_front_raise_ID, int right_front_raise_ID, int left_rear_raise_ID, int right_rear_raise_ID, int HAB_drive_ID, int HAB_Elevator_Limit_ID, int HAB_Arm_Limit_ID, DriveTrain driveTrain) {
            leftFrontRaise = new TalonSRX(left_front_raise_ID);
            rightFrontRaise = new TalonSRX(right_front_raise_ID);
            leftRearRaise = new TalonSRX(left_rear_raise_ID);
            rightRearRaise = new TalonSRX(right_rear_raise_ID);
            HABElevatorLimit = new DigitalInput(HAB_Elevator_Limit_ID);
            HABArmLimit = new DigitalInput(HAB_Arm_Limit_ID);
            HABDrive = new TalonSRX(HAB_drive_ID);

            this.driveTrain = driveTrain;

            rightFrontRaise.setInverted(true);
            rightRearRaise.setInverted(true);

            limitState = true;
    }

    void raiseRobot(double speed){
        HABLift(-speed);
    }

    void lowerRobot(double speed){
        if(limitState == false){
            if(!HABElevatorLimit.get()){
                limitState = true;
            }
            HABLift(speed);
        }
        else{
            HABLift(0);
        }
       
    }

    public void HABLift(double speed) {
        if(!HABArmLimit.get()){
            leftFrontRaise.set(ControlMode.PercentOutput, 0);
            rightFrontRaise.set(ControlMode.PercentOutput, 0);
            leftRearRaise.set(ControlMode.PercentOutput, speed * Constants.MAX_HAB_REAR_SPEED);
            rightRearRaise.set(ControlMode.PercentOutput, speed * Constants.MAX_HAB_REAR_SPEED);
        }
        else{
            leftRearRaise.set(ControlMode.PercentOutput, speed * Constants.MAX_HAB_REAR_SPEED);
            rightRearRaise.set(ControlMode.PercentOutput, speed * Constants.MAX_HAB_REAR_SPEED);
            leftFrontRaise.set(ControlMode.PercentOutput, 0);
            rightFrontRaise.set(ControlMode.PercentOutput, 0);   
        }
    }

    boolean climbMode = false;
    public void GyroHABClimb(){
        if(climbMode == false){
            leftFrontRaise.set(ControlMode.PercentOutput, (-1));
            rightFrontRaise.set(ControlMode.PercentOutput, (-1));
            if(driveTrain.getGyroPitch() < -5){
                climbMode = true;
            }
        }
        else{
            leftRearRaise.set(ControlMode.PercentOutput, (-1) * Constants.MAX_HAB_REAR_SPEED * (1));
            rightRearRaise.set(ControlMode.PercentOutput, (-1) * Constants.MAX_HAB_REAR_SPEED * (1));

            double speed = driveTrain.getGyroPitch()/5;
            speed = Math.max(speed, 0);
            speed = Math.min(speed, 1);

            leftFrontRaise.set(ControlMode.PercentOutput, (-1) * speed * Constants.MAX_HAB_ARM_SPEED * (1));
            rightFrontRaise.set(ControlMode.PercentOutput, (-1) * speed * Constants.MAX_HAB_ARM_SPEED * (1));

            if(HABElevatorLimit.get()){
                limitState = false;
            }

        }
    }

    public void GyroClimbReset(){
        climbMode = false;
    }

    public void HABForward(double speed) {
        speed *= Constants.MAX_HAB_DRIVE_SPEED;

        HABDrive.set(ControlMode.PercentOutput, speed);
    }

    public void ArmsUp(){
        leftFrontRaise.set(ControlMode.PercentOutput, 1);
        rightFrontRaise.set(ControlMode.PercentOutput, 1);
    }

    public void ArmsDown(){
        leftFrontRaise.set(ControlMode.PercentOutput, -0.5);
        rightFrontRaise.set(ControlMode.PercentOutput, -0.5);
    }
}