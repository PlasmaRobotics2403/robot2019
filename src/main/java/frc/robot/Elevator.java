
package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator {

    TalonSRX leftElevator;
    TalonSRX rightElevator;

    DigitalInput elevatorLimit;

    double elevatorSpeed;

    Elevator(int left_Elevator_ID, int Right_Elevator_ID, int limit_ID){
        leftElevator = new TalonSRX(left_Elevator_ID);
        rightElevator = new TalonSRX(Right_Elevator_ID);
        elevatorLimit = new DigitalInput(limit_ID);

        leftElevator.configFactoryDefault();

        leftElevator.setInverted(false);
        
        leftElevator.set(ControlMode.Position, 0);

        leftElevator.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
        leftElevator.setSensorPhase(true);

        rightElevator.configFactoryDefault();
        rightElevator.setInverted(true);
        rightElevator.follow(leftElevator);

        leftElevator.configNominalOutputForward(0, Constants.ELEVATOR_TIMEOUT);
		leftElevator.configNominalOutputReverse(0, Constants.ELEVATOR_TIMEOUT);
		leftElevator.configPeakOutputForward(1, Constants.ELEVATOR_TIMEOUT);
		leftElevator.configPeakOutputReverse(-1, Constants.ELEVATOR_TIMEOUT);

        leftElevator.selectProfileSlot(Constants.ELEVATOR_SLOT_IDX, Constants.ELEVATOR_LOOP_IDX);
        leftElevator.config_kF(Constants.ELEVATOR_SLOT_IDX, Constants.ELEVATOR_F, Constants.ELEVATOR_TIMEOUT);
        leftElevator.config_kP(Constants.ELEVATOR_SLOT_IDX, Constants.ELEVATOR_P, Constants.ELEVATOR_TIMEOUT);
        leftElevator.config_kI(Constants.ELEVATOR_SLOT_IDX, Constants.ELEVATOR_I, Constants.ELEVATOR_TIMEOUT);
        leftElevator.config_kD(Constants.ELEVATOR_SLOT_IDX, Constants.ELEVATOR_D, Constants.ELEVATOR_TIMEOUT);

        leftElevator.configMotionCruiseVelocity(10000);
        leftElevator.configMotionAcceleration(5000);
    }

    void Extend(double speed){
        elevatorLift(speed);
        
    }

    void Retract(double speed){
        elevatorLift(-speed);
    }

    public void elevatorLift(double speed) {
		speed *= Constants.MAX_ELEVATOR_SPEED;
		
        if(speed < 0 && !elevatorLimit.get()) {
			elevatorSpeed = 0;
			leftElevator.setSelectedSensorPosition(0, 0, 0);
        }
        
        else if(leftElevator.getSelectedSensorPosition(0) > 58000 && speed > 0){
            elevatorSpeed = 0;
        }

		else if(speed > 0) {
			if(elevatorSpeed < speed) {
				elevatorSpeed += Constants.ELEVATOR_RAMP_RATE;
			}
		}
		
		else if(speed < 0) {
			if(elevatorSpeed > speed) {
				elevatorSpeed -= Constants.ELEVATOR_RAMP_RATE;
				
			}
        }
		
        else {
            elevatorSpeed = 0;
        }
            
        leftElevator.set(ControlMode.PercentOutput, elevatorSpeed);
        //rightElevator.set(ControlMode.PercentOutput, elevatorSpeed);


        SmartDashboard.putNumber("Elevator enc", leftElevator.getSelectedSensorPosition());
        
    }

    public void magicElevator(double position){
        if(position <= 0 && !elevatorLimit.get()) {
            leftElevator.setSelectedSensorPosition(0, 0, 0);
            leftElevator.set(ControlMode.PercentOutput, 0);
        }
        else{
            leftElevator.set(ControlMode.MotionMagic, position);
        }
        SmartDashboard.putNumber("Elevator enc", leftElevator.getSelectedSensorPosition());
        SmartDashboard.putNumber("Elevator error", leftElevator.getClosedLoopError());
    }

}