package frc.robot.auto.actions;

import java.io.File;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FollowerType;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.DriveTrain;
import frc.robot.auto.util.Action;
import jaci.pathfinder.*;
import jaci.pathfinder.followers.EncoderFollower;

public class TestTrajectory implements Action {

	DriveTrain drive;
	Notifier followLoop;
	EncoderFollower leftFollower;
	EncoderFollower rightFollower;
	Trajectory leftTrajectory;
	Trajectory rightTrajectory;

	int i = 0;

	class PeriodicRunnable implements java.lang.Runnable {
		public void run() {
			if (leftFollower.isFinished() || rightFollower.isFinished()) {
				return;
			}
			double l = leftFollower.calculate(drive.leftDrive.getSelectedSensorPosition(0));
			double r = rightFollower.calculate(drive.rightDrive.getSelectedSensorPosition(0));

			SmartDashboard.putNumber("leftFollower Calculates: ", l);

			double currentHeading = -drive.getGyroAngle();
			double desiredHeading = Pathfinder.r2d(leftFollower.getHeading());

			double angleDifference = Pathfinder.boundHalfDegrees(desiredHeading - currentHeading);
			double turn = .28 * angleDifference;

			SmartDashboard.putNumber("angleDiff", angleDifference);

			double speedL = (l - turn) * .1 / Constants.DRIVE_ENCODER_CONVERSION;
			double speedR = (r + turn) * .1 / Constants.DRIVE_ENCODER_CONVERSION;

			SmartDashboard.putNumber("left target speed = ", speedL);

			drive.leftDrive.set(ControlMode.Velocity, speedL);
			SmartDashboard.putNumber("left actual speed = ", drive.leftDrive.getSelectedSensorVelocity());
			drive.leftDriveSlaveMid.follow(drive.leftDrive, FollowerType.PercentOutput);
			drive.leftDriveSlaveFront.follow(drive.leftDrive, FollowerType.PercentOutput);
			// drive.leftDriveSlaveMid.set(ControlMode.Follower,
			// drive.leftDrive.getDeviceID());
			// drive.leftDriveSlaveFront.set(ControlMode.Follower,
			// drive.leftDrive.getDeviceID());

			drive.rightDrive.set(ControlMode.Velocity, speedR);
			drive.rightDriveSlaveMid.follow(drive.rightDrive, FollowerType.PercentOutput);
			drive.rightDriveSlaveFront.follow(drive.rightDrive, FollowerType.PercentOutput);
			// drive.rightDriveSlaveMid.set(ControlMode.Follower,
			// drive.rightDrive.getDeviceID());
			// drive.rightDriveSlaveFront.set(ControlMode.Follower,
			// drive.rightDrive.getDeviceID());
		}
	}

	public TestTrajectory(String name, DriveTrain drive) {
		DriverStation.reportWarning("Entering Constructor", false);
		this.drive = drive;
		DriverStation.reportWarning("creating notifier", false);
		this.followLoop = new Notifier(new PeriodicRunnable());
		DriverStation.reportWarning("starting loading", false);

		try {
			DriverStation.reportWarning("trying to load File", false);
			File leftFile = new File("/media/sda1/" + name + "Left.traj");
			DriverStation.reportError("status:" + leftFile.exists(), false);
			DriverStation.reportError("Left File: " + leftFile, false);
			leftTrajectory = Pathfinder.readFromCSV(leftFile);
			DriverStation.reportError("left Trajectory = " + leftTrajectory, false);
			leftFollower = new EncoderFollower(leftTrajectory);
			DriverStation.reportError("Left Follower created", false);
		} catch (Exception e) {
			DriverStation.reportWarning(e.getMessage(), false);
			e.printStackTrace();
		}

		try {
			File rightFile = new File("/media/sda1/" + name + "Right.traj");
			rightTrajectory = Pathfinder.readFromCSV(rightFile);
			DriverStation.reportError("Right File: " + rightFile, false);
			rightFollower = new EncoderFollower(rightTrajectory);
			DriverStation.reportError("Right Follower created", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isFinished() {
		return leftFollower.isFinished() || rightFollower.isFinished();
	}

	@Override
	public void start() {

		drive.leftDrive.setSelectedSensorPosition(0, 0, Constants.TALON_TIMEOUT);
		drive.rightDrive.setSelectedSensorPosition(0, 0, Constants.TALON_TIMEOUT);
		drive.zeroGyro();

		leftFollower.configureEncoder(drive.leftDrive.getSelectedSensorPosition(0),
				(int) (1 / Constants.DRIVE_ENCODER_CONVERSION), 1 / Math.PI);
		rightFollower.configureEncoder(drive.rightDrive.getSelectedSensorPosition(0),
				(int) (1 / Constants.DRIVE_ENCODER_CONVERSION), 1 / Math.PI);
		leftFollower.configurePIDVA(.4, 0, 0, 1, 0);
		rightFollower.configurePIDVA(.4, 0, 0, 1, 0);
		followLoop.startPeriodic(.025);

	}

	@Override
	public void update() {
		SmartDashboard.putNumber("Left Error", drive.leftDrive.getClosedLoopError(0));
		SmartDashboard.putNumber("Right Error", drive.rightDrive.getClosedLoopError(0));
		// SmartDashboard.putNumber("leftPosition Error",
		// leftFollower.getSegment().position -
		// (drive.leftDrive.getSelectedSensorPosition(0) *
		// Constants.DRIVE_ENCODER_CONVERSION));
	}

	@Override
	public void end() {
		followLoop.stop();
		drive.leftDrive.setSelectedSensorPosition(0, 0, Constants.TALON_TIMEOUT);
		drive.rightDrive.setSelectedSensorPosition(0, 0, Constants.TALON_TIMEOUT);
		drive.zeroGyro();
		followLoop.stop();

		drive.leftDrive.set(ControlMode.PercentOutput, 0);
		drive.leftDriveSlaveMid.set(ControlMode.PercentOutput, 0);
		drive.leftDriveSlaveFront.set(ControlMode.PercentOutput, 0);

		drive.rightDrive.set(ControlMode.PercentOutput, 0);
		drive.rightDriveSlaveMid.set(ControlMode.PercentOutput, 0);
		drive.rightDriveSlaveFront.set(ControlMode.PercentOutput, 0);
	}

}