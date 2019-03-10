package frc.robot.auto.actions;

import frc.robot.DriveTrain;
import frc.robot.auto.util.Action;

import edu.wpi.first.wpilibj.DriverStation;
/**
 *
 */
public class DriveStraight implements Action {

	double speed;
	double distance;
	DriveTrain drive;
	
	public DriveStraight(double speed, double distance, DriveTrain drive){
		if(speed >= 0 && distance >=0){
			this.speed = Math.abs(speed);
		}
		else{
			this.speed = -Math.abs(speed);
		}
		this.distance = Math.abs(distance);
		this.drive = drive;
	}
	
	@Override
	public boolean isFinished() {
		DriverStation.reportWarning("finished", false);
		return Math.abs(drive.getDistance()) >= distance;
	}

	@Override
	public void start() {
		drive.zeroGyro();
		drive.resetEncoders();
		while(Math.abs(drive.getDistance()) >1){
			drive.resetEncoders();
		}
		drive.zeroGyro();
	}

	@Override
	public void update() {
		drive.gyroStraight(speed, 0);
	}

	@Override
	public void end() {
		drive.stopDrive();
	}

}