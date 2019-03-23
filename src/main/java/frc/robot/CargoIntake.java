
package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class CargoIntake {

    TalonSRX pivotMotor;
    TalonSRX intakeMotor;
    
    double pivotSpeed;

    DigitalInput pivotLimit;

    Servo cameraMount;

    CargoIntake(int pivot_motor_ID, int intake_motor_ID, int pivot_limit_ID, int camera_servo_ID){
        pivotMotor = new TalonSRX(pivot_motor_ID);
        intakeMotor = new TalonSRX(intake_motor_ID);
        pivotLimit = new DigitalInput(pivot_limit_ID);
        cameraMount = new Servo(camera_servo_ID); 


        pivotSpeed = 0;
        
        pivotMotor.setSelectedSensorPosition(0,0,0);
        pivotMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);

        pivotMotor.setSensorPhase(true);

        limitCurrent(intakeMotor);

    }
        
    void pivotIntake(double speed){

        speed *= Constants.MAX_INTAKE_PIVOT_SPEED;
        if(pivotLimit.get()){
            //cameraMount.setAngle(180);
        }
        else{
            //cameraMount.setAngle(135);
        }

        if(speed > 0 && !pivotLimit.get()) {
           pivotSpeed = 0;
        }
        else if(speed > 0) {
            while(pivotSpeed < speed){
                pivotSpeed += Constants.PIVOT_RAMP_RATE;
            }
        }
        else if(speed < 0) {
            while(pivotSpeed > speed){
                pivotSpeed -= Constants.PIVOT_RAMP_RATE;
            }
        }
        else {
            pivotSpeed = 0;
        }
        pivotMotor.set(ControlMode.PercentOutput, pivotSpeed);

        SmartDashboard.putNumber("Pivot Enc", pivotMotor.getSelectedSensorPosition(0));
        SmartDashboard.putBoolean("Pivot Limit", pivotLimit.get());
    }

    void intakeCargo(double speed){
        intakeMotor.set(ControlMode.PercentOutput, speed);
    }

    public void limitCurrent(TalonSRX talon) {
		talon.configPeakCurrentDuration(0, 1000);
		talon.configPeakCurrentLimit(45, 1000);
		talon.configContinuousCurrentLimit(45, 1000);
		talon.enableCurrentLimit(true);
		talon.configClosedloopRamp(1);
    }
    
}