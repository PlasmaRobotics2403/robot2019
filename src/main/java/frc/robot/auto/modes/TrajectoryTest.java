package frc.robot.auto.modes;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.DriveTrain;
import frc.robot.auto.actions.*;
import frc.robot.auto.util.AutoMode;
import frc.robot.auto.util.AutoModeEndedException;


public class TrajectoryTest extends AutoMode {
	
	DriveTrain drive;
	
	public TrajectoryTest(DriveTrain drive) {
		this.drive = drive;
	}

	@Override
	protected void routine() throws AutoModeEndedException {
		
		try{
			runAction(new FollowTrajectory("45 left", drive));	
		}
		catch(Exception ex){
			DriverStation.reportError("Failed to follow trajectory. msg=" + ex.getMessage(), ex.getStackTrace());
		}
    }
}