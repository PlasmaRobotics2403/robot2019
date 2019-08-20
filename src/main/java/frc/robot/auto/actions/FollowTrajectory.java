package frc.robot.auto.actions;

import java.io.IOException;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Notifier;
import frc.robot.Constants;
import frc.robot.DriveTrain;
import frc.robot.auto.util.Action;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.PathfinderFRC;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;

public class FollowTrajectory implements Action {

    DriveTrain drive;

    Trajectory leftTrajectory;
    Trajectory rightTrajectory;

    EncoderFollower leftFollower;
    EncoderFollower rightFollower;

    Notifier followerNotifier;

    class PeriodicRunnable implements java.lang.Runnable {
        public void run() {
            if (leftFollower.isFinished() || rightFollower.isFinished()) {
                followerNotifier.stop();
            }
            else {
                double speedR = leftFollower.calculate(drive.leftDrive.getSelectedSensorPosition(0));
                double speedL = rightFollower.calculate(drive.rightDrive.getSelectedSensorPosition(0));
                double currentHeading = -drive.getGyroAngle();
                double desiredHeading = Pathfinder.r2d(leftFollower.getHeading());
                double headingDifference = Pathfinder.boundHalfDegrees(desiredHeading - currentHeading);
                double turn =  0.8 * (-1.0/80.0) * headingDifference;
                drive.leftDrive.set(ControlMode.Velocity, speedL + turn);
                drive.rightDrive.set(ControlMode.Velocity, speedR - turn);
            }
        }
    }

    

    public FollowTrajectory(String name, DriveTrain drive) {
        this.drive = drive;
        this.followerNotifier = new Notifier(new PeriodicRunnable());


        try {
            leftTrajectory = PathfinderFRC.getTrajectory(name + ".right");
            leftFollower = new EncoderFollower(leftTrajectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        try {
            rightTrajectory = PathfinderFRC.getTrajectory(name + ".left");
            rightFollower = new EncoderFollower(rightTrajectory);
        } catch (IOException e) {
            e.printStackTrace();
        }

	}

	@Override
	public boolean isFinished() {
		return leftFollower.isFinished() || rightFollower.isFinished();
	}
	
	@Override
	public void start() {
        
        drive.leftDrive.setSelectedSensorPosition(0,0,Constants.TALON_TIMEOUT);
        drive.rightDrive.setSelectedSensorPosition(0,0,Constants.TALON_TIMEOUT);
        drive.zeroGyro();

        leftFollower.configureEncoder(drive.leftDrive.getSelectedSensorPosition(0), Constants.TICKS_PER_REV, Constants.WHEEL_WIDTH);
        // You must tune the PID values on the following line!
        leftFollower.configurePIDVA(Constants.AUTON_P, Constants.AUTON_I, Constants.AUTON_D, Constants.AUTON_V, Constants.AUTON_A);

        rightFollower.configureEncoder(drive.rightDrive.getSelectedSensorPosition(0), Constants.TICKS_PER_REV, Constants.WHEEL_WIDTH);
        // You must tune the PID values on the following line!
        rightFollower.configurePIDVA(Constants.AUTON_P, Constants.AUTON_I, Constants.AUTON_D, Constants.AUTON_V, Constants.AUTON_A);
        followerNotifier.startPeriodic(.01);
	}

	@Override
	public void update() {
		
	}

	@Override
	public void end() {
        followerNotifier.stop();
        drive.leftDrive.setSelectedSensorPosition(0, 0, Constants.TALON_TIMEOUT);
        drive.rightDrive.setSelectedSensorPosition(0, 0, Constants.TALON_TIMEOUT);
        drive.zeroGyro();
        followerNotifier.stop();
        drive.leftDrive.set(ControlMode.PercentOutput, 0);
        drive.rightDrive.set(ControlMode.PercentOutput, 0);
		
	}

}