
package frc.robot.auto.actions;

import java.io.File;

import frc.robot.Constants;
import frc.robot.DriveTrain;
import frc.robot.auto.util.Action;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.*;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.Pathfinder;


public class FollowTrajectory implements Action {

	DriveTrain drive;
	Notifier followLoop;
	EncoderFollower leftFollower;
	EncoderFollower rightFollower;
	
	int i = 0;
	
	class PeriodicRunnable implements java.lang.Runnable{
		public void run() {
			if(leftFollower.isFinished() || rightFollower.isFinished()) {
				return;
			}
			double l = leftFollower.calculate(drive.leftDrive.getSelectedSensorPosition(0));
			double r = rightFollower.calculate(drive.rightDrive.getSelectedSensorPosition(0));
			
			DriverStation.reportError("leftFollower: " + l, false);
			DriverStation.reportError("rightFollower: " + r, false);

			double currentHeading = -drive.getGyroAngle();
			double desiredHeading = Pathfinder.r2d(leftFollower.getHeading());
			
			double angleDifference = Pathfinder.boundHalfDegrees(desiredHeading - currentHeading);
			double turn = .28 * angleDifference;
			
			SmartDashboard.putNumber("angleDiff", angleDifference);
			
			double speedL = (l - turn) * .1 / Constants.DRIVE_ENCODER_CONVERSION;
			double speedR = (r + turn) * .1 / Constants.DRIVE_ENCODER_CONVERSION;
			
			DriverStation.reportError("speedL: " + speedL, false);
			DriverStation.reportError("speedR: " + speedR, false);
			SmartDashboard.putNumber("speedL", speedL);
			SmartDashboard.putNumber("speedR", speedR);

			drive.leftDrive.set(ControlMode.Velocity, speedL);
			drive.rightDrive.set(ControlMode.Velocity, speedR);
			drive.leftDriveSlaveMid.set(ControlMode.Velocity, speedL);
			drive.rightDriveSlaveMid.set(ControlMode.Velocity, speedR);
			drive.leftDriveSlaveFront.set(ControlMode.Velocity, speedL);
			drive.rightDriveSlaveFront.set(ControlMode.Velocity, speedR);

			DriverStation.reportError("left Velocity: " + drive.leftDrive.getActiveTrajectoryVelocity(), false);
			DriverStation.reportError("speedL afrer setting: " + speedL, false);
    		//drive.leftDriveSlaveMid.set(ControlMode.Follower, drive.leftDrive.getDeviceID());
    		//drive.leftDriveSlaveFront.set(ControlMode.Follower, drive.leftDrive.getDeviceID());
    		//drive.rightDriveSlaveMid.set(ControlMode.Follower, drive.rightDrive.getDeviceID());
    		//drive.rightDriveSlaveFront.set(ControlMode.Follower, drive.leftDrive.getDeviceID());
		}
	}
	
	public FollowTrajectory(String name, DriveTrain drive) {
		this.drive = drive;
		this.followLoop = new Notifier(new PeriodicRunnable());

		File leftFile = new File("/media/sda1/" + name + "Left");
		File rightFile = new File("/media/sda1/" + name + "Right");

		DriverStation.reportError("LeftFile: " + leftFile, false);
		DriverStation.reportError("Rightfile: " + rightFile, false);
		


		try
		{

			Trajectory left = Pathfinder.readFromFile(leftFile);
			Trajectory right = Pathfinder.readFromFile(rightFile);

			DriverStation.reportError("base" + left, false);
			leftFollower = new EncoderFollower(left);
			rightFollower = new EncoderFollower(right);
			
	    }
		catch(Exception ex)
		{
			DriverStation.reportWarning("trajectory error", ex.getStackTrace());
			throw new RuntimeException(ex.getMessage());
		}       
	}

	@Override
	public boolean isFinished() {
		boolean finished = leftFollower.isFinished() || rightFollower.isFinished();
		DriverStation.reportError("Trajectory finished" + finished, true);
		return finished;
	}
	
	@Override
	public void start() {
		
		drive.leftDrive.setSelectedSensorPosition(0, 0, Constants.TALON_TIMEOUT);
		drive.rightDrive.setSelectedSensorPosition(0, 0, Constants.TALON_TIMEOUT);
		drive.zeroGyro();
		
		leftFollower.configureEncoder(drive.leftDrive.getSelectedSensorPosition(0), (int) (Constants.DRIVE_ENCODER_CONVERSION), 6);
		rightFollower.configureEncoder(drive.rightDrive.getSelectedSensorPosition(0), (int) (Constants.DRIVE_ENCODER_CONVERSION), 6);
		leftFollower.configurePIDVA(.4, 0, 0, 1, 0);
		rightFollower.configurePIDVA(.4, 0, 0, 1, 0);
		followLoop.startPeriodic(.01);
		
		
	}

	@Override
	public void update() {
		SmartDashboard.putNumber("Left Error", drive.leftDrive.getClosedLoopError(0));
		SmartDashboard.putNumber("Right Error", drive.rightDrive.getClosedLoopError(0));
		//SmartDashboard.putNumber("leftPosition Error", leftFollower.getSegment().position - (drive.leftDrive.getSelectedSensorPosition(0) * Constants.DRIVE_ENCODER_CONVERSION));
	}

	@Override
	public void end() {
		followLoop.stop();
		drive.leftDrive.setSelectedSensorPosition(0, 0, Constants.TALON_TIMEOUT);
		drive.rightDrive.setSelectedSensorPosition(0, 0, Constants.TALON_TIMEOUT);
		drive.zeroGyro();
		followLoop.stop();
		drive.leftDrive.set(ControlMode.PercentOutput, 0);
		drive.rightDrive.set(ControlMode.PercentOutput, 0);
		drive.leftDriveSlaveMid.set(ControlMode.PercentOutput, 0);
        drive.rightDriveSlaveMid.set(ControlMode.PercentOutput, 0);
        drive.leftDriveSlaveFront.set(ControlMode.PercentOutput, 0);
		drive.rightDriveSlaveFront.set(ControlMode.PercentOutput, 0);
		
	}

}

