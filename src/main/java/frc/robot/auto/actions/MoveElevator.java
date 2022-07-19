package frc.robot.auto.actions;

import frc.robot.DriveTrain;
import frc.robot.Elevator;
import frc.robot.HatchIntake;
import frc.robot.auto.util.Action;

/**
 *
 */
public class MoveElevator implements Action {

    Elevator elevator;
    int elevatorTarget;

    public MoveElevator(Elevator elevator, int elevatorTarget) {
        this.elevator = elevator;
        this.elevatorTarget = elevatorTarget;
    }

    @Override
    public boolean isFinished() {
        if (elevatorTarget - 300 < elevator.leftElevator.getSelectedSensorPosition()
                && elevator.leftElevator.getSelectedSensorPosition() < elevatorTarget + 300) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
        elevator.magicElevator(elevatorTarget);
    }

    @Override
    public void end() {

    }

}
