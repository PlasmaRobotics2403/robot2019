package frc.robot.auto.actions;

import java.io.File;
import java.io.IOException;

import frc.robot.Constants;
import frc.robot.DriveTrain;
import frc.robot.auto.util.Action;
import frc.robot.pathfinder.Pathfinder;
import frc.robot.pathfinder.Trajectory;
import frc.robot.pathfinder.followers.EncoderFollower;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class FollowTrajectory implements Action {

    DriveTrain drive;
    Notifier followLoop;
    EncoderFollower leftFollower;
    EncoderFollower rightFollower;

    int i = 0;

    class PeriodicRunnable implements java.lang.Runnable {
        public void run() {
            if (leftFollower.isFinished() || rightFollower.isFinished()) {
                return;
            }
            double l = leftFollower.calculate(drive.leftDrive.getSelectedSensorPosition(0));
            double r = rightFollower.calculate(drive.rightDrive.getSelectedSensorPosition(0));

            double currentHeading = -drive.getGyroAngle();
            double desiredHeading = Pathfinder.r2d(leftFollower.getHeading());

            double angleDifference = Pathfinder.boundHalfDegrees(desiredHeading - currentHeading);
            double turn = .28 * angleDifference;

            SmartDashboard.putNumber("angleDiff", angleDifference);

            double speedL = (l + turn) * .01 / Constants.DRIVE_ENCODER_CONVERSION;
            double speedR = (r - turn) * .01 / Constants.DRIVE_ENCODER_CONVERSION;

            drive.leftDrive.set(ControlMode.Velocity, speedL);
            drive.rightDrive.set(ControlMode.Velocity, speedR);
            // drive.leftDriveSlave.set(ControlMode.Follower,
            // drive.leftDrive.getDeviceID());
            // drive.rightDriveSlave.set(ControlMode.Follower,
            // drive.rightDrive.getDeviceID());
        }
    }

    public FollowTrajectory(String name, DriveTrain drive) throws IOException {
        this.drive = drive;
        this.followLoop = new Notifier(new PeriodicRunnable());


            File leftFile = new File("/media/sda1/" + name + "Left");
            File rightFile = new File("/media/sda1/" + name + "Right");
            DriverStation.reportError("LeftFile: " + leftFile, false);
            Trajectory left = Pathfinder.readFromFile(leftFile);
            Trajectory right = Pathfinder.readFromFile(rightFile);
		    DriverStation.reportError("base" + left, false);
		    leftFollower = new EncoderFollower(left);
		    rightFollower = new EncoderFollower(right);
       
	}

	@Override
	public boolean isFinished() {
		return leftFollower.isFinished() || rightFollower.isFinished();
	}
	
	@Override
	public void start() {
        
        DriverStation.reportWarning("starting FollowTrajectory", false);
		drive.leftDrive.setSelectedSensorPosition(0, 0, Constants.TALON_TIMEOUT);
		drive.rightDrive.setSelectedSensorPosition(0, 0, Constants.TALON_TIMEOUT);
		drive.zeroGyro();
		
		leftFollower.configureEncoder(drive.leftDrive.getSelectedSensorPosition(0), (int) (1/Constants.DRIVE_ENCODER_CONVERSION), .162);
		rightFollower.configureEncoder(drive.rightDrive.getSelectedSensorPosition(0), (int) (1/Constants.DRIVE_ENCODER_CONVERSION), .162);
		leftFollower.configurePIDVA(.4, 0, 0, 1, 0);
		rightFollower.configurePIDVA(.4, 0, 0, 1, 0);
        followLoop.startPeriodic(.01);
        DriverStation.reportWarning("Finished with Follow Trajectory Starting", false);
		
		
	}

	@Override
	public void update() {
		SmartDashboard.putNumber("Left Error", drive.leftDrive.getClosedLoopError(0));
		SmartDashboard.putNumber("Right Error", drive.rightDrive.getClosedLoopError(0));
		SmartDashboard.putNumber("leftPosition Error", leftFollower.getSegment().position - (drive.leftDrive.getSelectedSensorPosition(0) * Constants.DRIVE_ENCODER_CONVERSION));
	}

	@Override
	public void end() {
        DriverStation.reportWarning("Follow Trajectory ended", false);
		followLoop.stop();
		drive.leftDrive.setSelectedSensorPosition(0, 0, Constants.TALON_TIMEOUT);
		drive.rightDrive.setSelectedSensorPosition(0, 0, Constants.TALON_TIMEOUT);
		drive.zeroGyro();
		followLoop.stop();
		drive.leftDrive.set(ControlMode.PercentOutput, 0);
		drive.rightDrive.set(ControlMode.PercentOutput, 0);
		//drive.leftDriveSlaveMid.set(ControlMode.PercentOutput, 0);
        //drive.rightDriveSlaveMid.set(ControlMode.PercentOutput, 0);
        //drive.leftDriveSlaveFront.set(ControlMode.PercentOutput, 0);
		//drive.rightDriveSlaveFront.set(ControlMode.PercentOutput, 0);
		
	}

}