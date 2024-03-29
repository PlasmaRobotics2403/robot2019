package frc.robot.auto.actions;

import frc.robot.auto.util.Action;
import edu.wpi.first.wpilibj.*;

/**
 *
 */
public class Wait implements Action {
	
	double timeToWait;
	double startTime;
	
	public Wait(double time){
		this.timeToWait = time;
	}
	
	@Override
	public boolean isFinished() {
		return Timer.getFPGATimestamp() >= timeToWait + startTime;
	}

	@Override
	public void start() {
		startTime = Timer.getFPGATimestamp();

	}

	@Override
	public void update() {
		

	}

	@Override
	public void end() {
		

	}

}
