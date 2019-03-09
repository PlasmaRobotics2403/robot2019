package frc.robot.auto.actions;

import frc.robot.auto.util.Action;

import edu.wpi.first.wpilibj.DriverStation;

/**
 *
 */
public class Pause implements Action {

	int duration;
	int currentTime;
	boolean test;
	
	public Pause(int seconds){
		duration = seconds;
	}
	
	@Override
	public boolean isFinished() {
		return currentTime >= duration;
	}

	@Override
	public void start() {
		test = false;
		currentTime = 0;

	}

	@Override
	public void update() {
		currentTime++;
		DriverStation.reportWarning("" + currentTime, false);
	}

	@Override
	public void end() {

	}

}
