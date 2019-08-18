
package frc.robot;

import frc.robot.controllers.PlasmaAxis;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.kauailabs.navx.frc.AHRS;

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class DriveTrain { 
	
	public TalonSRX leftDrive;
	public VictorSPX leftDriveSlaveMid;
	public VictorSPX leftDriveSlaveFront;
	public TalonSRX rightDrive;
	public VictorSPX rightDriveSlaveMid;
	public VictorSPX rightDriveSlaveFront;
	private int timer;

	private AHRS navX;
	private double gyroAngle;
	private double gyroPitch;
	
	public DriveTrain(int LEFT_ID, int LEFT_MID_S_ID, int LEFT_FRONT_S_ID, int RIGHT_ID, int RIGHT_MID_S_ID, int RIGHT_FRONT_S_ID) {

		leftDrive = new TalonSRX(LEFT_ID);
		leftDriveSlaveMid = new VictorSPX(LEFT_MID_S_ID);
		leftDriveSlaveFront = new VictorSPX(LEFT_FRONT_S_ID);
		rightDrive = new TalonSRX(RIGHT_ID);
		rightDriveSlaveMid = new VictorSPX(RIGHT_MID_S_ID);
		rightDriveSlaveFront = new VictorSPX(RIGHT_FRONT_S_ID);

		navX = new AHRS(SPI.Port.kMXP);
		
		timer = 0;

		leftDrive.configFactoryDefault();
		rightDrive.configFactoryDefault();
		leftDrive.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1);
		rightDrive.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1);
		
		
		leftDrive.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		leftDrive.configNominalOutputForward(0, 30);
    	leftDrive.configNominalOutputReverse(0, 30);
    	leftDrive.configPeakOutputForward(1, 30);
    	leftDrive.configPeakOutputReverse(-1, 30);
    	leftDrive.config_kF(0, .25, 30);
		leftDrive.config_kP(0, .001, 30);
		leftDrive.config_kI(0, 20, 30);
		leftDrive.config_kD(0, 1023.0/7200.0, 30);
		
		rightDrive.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		rightDrive.configNominalOutputForward(0, 30);
    	rightDrive.configNominalOutputReverse(0, 30);
    	rightDrive.configPeakOutputForward(1, 30);
    	rightDrive.configPeakOutputReverse(-1, 30);
    	rightDrive.config_kF(0, .25, 30);
		rightDrive.config_kP(0, .001, 30);
		rightDrive.config_kI(0, 20, 30);
		rightDrive.config_kD(0, 1023.0/7200.0, 30);

		leftDrive.setSelectedSensorPosition(0,0,0);
		rightDrive.setSelectedSensorPosition(0,0,0);

		leftDrive.set(ControlMode.Position, 0);
		rightDrive.set(ControlMode.Position, 0);
				

		DriverStation.reportError("left position: " + leftDrive.getSelectedSensorPosition(0), false);
		DriverStation.reportError("right position: " + rightDrive.getSelectedSensorPosition(0), false);

		limitCurrent(leftDrive);
		limitCurrent(rightDrive);
		
		leftDrive.setInverted(true);
		leftDriveSlaveMid.setInverted(true);
		leftDriveSlaveFront.setInverted(true);

		
	}
	
	public void resetEncoders(){
		//double dist = Math.abs(getDistance());
		leftDrive.setSelectedSensorPosition(0,0,0);
		rightDrive.setSelectedSensorPosition(0,0,0);
		DriverStation.reportWarning("resetting encoders", false);
		while(Math.abs(getDistance()) < -1 && Math.abs(getDistance()) > 1){
			leftDrive.setSelectedSensorPosition(0,0,0);
			rightDrive.setSelectedSensorPosition(0,0,0);
			DriverStation.reportWarning("Stuck in loop", false);
		}	
	}

	public double getDistance(){
		return (toDistance(rightDrive) + toDistance(leftDrive))/2;
	}
	
	public double getLeftDistance(){
		return toDistance(leftDrive);
	}
	
	private static double toDistance(TalonSRX talon){
		double distance = talon.getSelectedSensorPosition(talon.getDeviceID()) / Constants.DRIVE_ENCODER_CONVERSION;
		//DriverStation.reportWarning(talon.getDeviceID() + " - distance: " + distance, false);
		return distance;
	}

	public void updateGyro(){
		gyroAngle = navX.getYaw();
		gyroPitch = navX.getPitch();
	}
	
	public double getGyroAngle(){
		updateGyro();
		return gyroAngle;
	}
	
	public double getGyroPitch(){
		updateGyro();
		return gyroPitch;
	}

	public void zeroGyro(){
		navX.zeroYaw();
	}
	
	public void FPSDrive(PlasmaAxis forwardAxis, PlasmaAxis turnAxis){
		
		double forwardVal = forwardAxis.getFilteredAxis() * Math.abs(forwardAxis.getFilteredAxis());
		double turnVal = turnAxis.getFilteredAxis() * Math.abs(turnAxis.getFilteredAxis()) * Math.abs(turnAxis.getFilteredAxis());
		
		FPSDrive(forwardVal, turnVal);
	}
	
	public void FPSDrive(double forwardVal, double turnVal){
		
		turnVal *= Constants.MAX_DRIVE_TURN;
		
		double absForward = Math.abs(forwardVal);
		double absTurn = Math.abs(turnVal);
		
		int forwardSign = (forwardVal == 0) ? 0 : (int)(forwardVal / Math.abs(forwardVal));
		int turnSign = (turnVal == 0) ? 0 : (int)(turnVal / Math.abs(turnVal));
		
		double speedL;
		double speedR;
		
		if(turnVal == 0){ //Straight forward
			speedL = -forwardVal;
			speedR = -forwardVal;
		}
		else if(forwardVal == 0){ //Pivot turn
			speedL = -turnVal;
			speedR = turnVal;
		}
		else if(forwardSign == 1 && turnSign == 1){ //Forward right
			speedL = -forwardVal;
			speedR = (absForward - absTurn < 0) ? 0 : -(absForward - (absTurn));
		}
		else if(forwardSign == 1 && turnSign == -1){ //Forward left
			speedL = (absForward - absTurn < 0) ? 0 : -(absForward - (absTurn));
			speedR = -forwardVal;
		}
		else if(forwardSign == -1 && turnSign == 1){ //Backward right
			speedL = -forwardVal;
			speedR = (absForward - absTurn < 0) ? 0 : (absForward - absTurn);
		}
		else if(forwardSign == -1 && turnSign == -1){ //Backward left
			speedL = (absForward - absTurn < 0) ? 0 : (absForward - absTurn);
			speedR = -forwardVal;
		}
		else{
			speedL = 0;
			speedR = 0;
			DriverStation.reportError("Bug @ fps drive code - no case triggered)", false);
		}
		
		speedL *= Constants.MAX_DRIVE_SPEED;
		speedR *= Constants.MAX_DRIVE_SPEED;
		
		leftDrive.set(ControlMode.PercentOutput, speedL/3);
		rightDrive.set(ControlMode.PercentOutput, speedR/3);

		leftDriveSlaveFront.set(ControlMode.PercentOutput, speedL/3);
		leftDriveSlaveMid.set(ControlMode.PercentOutput, speedL/3);
		rightDriveSlaveFront.set(ControlMode.PercentOutput, speedR/3);
		rightDriveSlaveMid.set(ControlMode.PercentOutput, speedR/3);

		timer = 0;
		
		while(timer < 10) {
			timer++;
		SmartDashboard.putNumber("leftDriveSpeed", speedL);
		SmartDashboard.putNumber("rightDriveSpeed", speedR);
		}
		
		leftDrive.set(ControlMode.PercentOutput, speedL);
		rightDrive.set(ControlMode.PercentOutput, speedR);

		leftDriveSlaveFront.set(ControlMode.PercentOutput, speedL);
		leftDriveSlaveMid.set(ControlMode.PercentOutput, speedL);
		rightDriveSlaveFront.set(ControlMode.PercentOutput, speedR);
		rightDriveSlaveMid.set(ControlMode.PercentOutput, speedR);
		
	}

	public void limitCurrent(TalonSRX talon) {
		talon.configPeakCurrentDuration(0, 1000);
		talon.configPeakCurrentLimit(15, 1000);
		talon.configContinuousCurrentLimit(15, 1000);
		talon.enableCurrentLimit(true);

		talon.configClosedloopRamp(5);
	}

	
	public void autonTankDrive(double left, double right){
		leftWheelDrive(left);
		rightWheelDrive(right);
	}
	
	public void leftWheelDrive(double speed){
		leftDrive.set(ControlMode.PercentOutput ,speed * Constants.MAX_AUTO_DRIVE_SPEED);
	}
	
	public void rightWheelDrive(double speed){
		rightDrive.set(ControlMode.PercentOutput ,speed * Constants.MAX_AUTO_DRIVE_SPEED);
	}


	public void gyroStraight(double speed, double angle){
		if(getGyroAngle() > 0) {
			autonTankDrive(speed - 0.01*(getGyroAngle() - angle), speed - 0.01*(getGyroAngle() - angle));
		}
		else if(getGyroAngle() < 0) {
			autonTankDrive(speed - 0.01*(getGyroAngle() + angle), speed - 0.01*(getGyroAngle() + angle));
		}
		else {
			autonTankDrive(speed - 0.01*(getGyroAngle() + angle), speed - 0.01*(getGyroAngle()+ angle));
		}
	}
	
	public void pivotToAngle(double angle){
		double angleDiff = getGyroAngle() - angle;
		double speed = (Math.abs(angleDiff) < 10) ? (Math.abs(angleDiff) / 10.0) * 0.15 + 0.15 : .3;
		if(angleDiff > 0){
			autonTankDrive(-speed, speed);
		}
		else{
			autonTankDrive(speed, -speed);
		}
	}

	public void stopDrive(){
		autonTankDrive(0, 0);
	}
}
