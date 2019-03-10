
package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.DigitalInput;
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

        leftElevator.setInverted(true);

        leftElevator.set(ControlMode.Position, 0);

        leftElevator.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
        leftElevator.setSensorPhase(false);
    }

    void Extend(double speed){
        elevatorLift(-speed);
        
    }

    void Retract(double speed){
        elevatorLift(speed);
    }

    public void elevatorLift(double speed) {
		speed *= Constants.MAX_ELEVATOR_SPEED;
		
        if(speed > 0 && !elevatorLimit.get()) {
			elevatorSpeed = 0;
			leftElevator.setSelectedSensorPosition(0, 0, 0);
        }
        
        else if(leftElevator.getSelectedSensorPosition(0) > 58000 && speed < 0){
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
        rightElevator.set(ControlMode.PercentOutput, elevatorSpeed);


        SmartDashboard.putNumber("Elevator enc", leftElevator.getSelectedSensorPosition());
        
    }

}