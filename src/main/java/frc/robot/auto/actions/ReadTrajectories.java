package frc.robot.auto.actions;

import java.io.File;

import frc.robot.auto.util.Action;

import edu.wpi.first.wpilibj.DriverStation;
import jaci.pathfinder.*;

public class ReadTrajectories implements Action {

	boolean finished;
	
	public ReadTrajectories(){
		finished = false;
	}
	
	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return finished;
	}

	@Override
	public void start() {
		File outsideFile = new File("/home/lvuser/outside");
		File insideFile = new File("/home/lvuser/inside");
		
		try{
			Trajectory outside = Pathfinder.readFromFile(outsideFile);
		Trajectory inside = Pathfinder.readFromFile(insideFile);
		
		DriverStation.reportError("outread-" + outside.length(), false);
		DriverStation.reportError("inread-" + inside.length(), false);
		finished = true;
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void end() {
		// TODO Auto-generated method stub

	}

}
