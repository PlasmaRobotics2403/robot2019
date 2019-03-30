/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

//import com.sun.org.apache.bcel.internal.Const;

import frc.robot.auto.modes.*;
import frc.robot.auto.util.*;

import edu.wpi.first.wpilibj.TimedRobot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.controllers.PlasmaJoystick;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;


public class Robot extends TimedRobot {

    PlasmaJoystick joystick;
    DriveTrain driveTrain;
    HatchIntake hatchIntake;
    Elevator elevator;
    CargoIntake cargoIntake;
    HAB hab; 

    Compressor compressor;

    AutoModeRunner autoModeRunner;
    AutoMode[] autoModes;
    int autoModeSelection;

    NetworkTable table;
    NetworkTableEntry tx;
    NetworkTableEntry ty;
    NetworkTableEntry ta;

    CameraServer server;

    double elevatorTarget;
    double pivotTarget;
    //private static final String kDefaultAuto = "Default";
    //private static final String kCustomAuto = "My Auto";
    //private String m_autoSelected;
    //private final SendableChooser<String> m_chooser = new SendableChooser<>();

    
    @Override
    public void robotInit() {

        joystick = new PlasmaJoystick(Constants.JOYSTICK1_PORT);

        driveTrain = new DriveTrain(Constants.L_DRIVE_ID,
                                    Constants.L_DRIVE_MID_SLAVE_ID,
                                    Constants.L_DRIVE_FRONT_SLAVE_ID,
                                    Constants.R_DRIVE_ID,
                                    Constants.R_DRIVE_MID_SLAVE_ID,
                                    Constants.R_DRIVE_FRONT_SLAVE_ID);

        hatchIntake = new HatchIntake(Constants.CLAW_PISTON_ID,
                                      Constants.BACK_EXTENDER_ID,
                                      Constants.FRONT_EXTENDER_ID);

        elevator = new Elevator(Constants.L_ELEVATOR_ID,
                                Constants.R_ELEVATOR_ID,
                                Constants.ELEVATOR_LIMIT_ID);

        cargoIntake = new CargoIntake(Constants.PIVOT_ID,
                                      Constants.INTAKE_ID, 
                                      Constants.PIVOT_LIMIT_ID,
                                      Constants.CAMERA_SERVO_ID);

        hab = new HAB(Constants.L_HAB_ARM_ID,
                      Constants.R_HAB_ARM_ID,
                      Constants.L_HAB_ELEVATOR_ID,
                      Constants.R_HAB_ELEVATOR_ID,
                      Constants.HAB_DRIVE,
                      Constants.HAB_LIMIT_ID,
                      driveTrain);

        compressor = new Compressor(0);
        compressor.setClosedLoopControl(true);

        table = NetworkTableInstance.getDefault().getTable("limelight");
        tx = table.getEntry("tx");
        ty = table.getEntry("ty");
        ta = table.getEntry("ta");

        autoModeRunner = new AutoModeRunner();
        autoModes = new AutoMode[1];
        for(int i = 0; i < autoModes.length; i++){
            autoModes[i] = new Nothing();
        }

        DriverStation.reportWarning("automode generated", false);
        autoModeSelection = 0;
        SmartDashboard.putNumber("Auto Mode", 0);

        try{
            //driveTrain.resetEncoders();
            driveTrain.zeroGyro();
        }
        catch(Exception ex){
            DriverStation.reportError("reset encoder error", ex.getStackTrace());
        }
        DriverStation.reportWarning("error", false);

        CameraServer.getInstance().startAutomaticCapture("USB camera", 0);

        elevatorTarget = 0;
        pivotTarget = 0;
    }

    
    @Override
    public void robotPeriodic() {
        SmartDashboard.putNumber("GyroAngle", driveTrain.getGyroAngle());
        SmartDashboard.putNumber("Gyro pitch", driveTrain.getGyroPitch());

        double x = tx.getDouble(0.0);
        double y = ty.getDouble(0.0);
        double area = ta.getDouble(0.0);

        SmartDashboard.putNumber("LimelightX", x);
        SmartDashboard.putNumber("LimelightY", y);
        SmartDashboard.putNumber("LimelighArea", area);

        SmartDashboard.putBoolean("HAB Limit", hab.HABLimit.get());
        
    }

    public void disabledInit() {
        compressor.start();
        autoModeRunner.stop();
        driveTrain.zeroGyro();
    }

    public void disabledPeriodic() {
        autoModeSelection = (int)SmartDashboard.getNumber("Auto Mode", 0);
    }

    @Override
    public void autonomousInit() {
        
        DriverStation.reportWarning("starting auto", false);
        //driveTrain.resetEncoders();
        DriverStation.reportWarning("auto after reset encoders", false);
        compressor.start();
        driveTrain.zeroGyro();

        autoModes[0] = new TrajectoryTest(driveTrain);

        autoModeSelection = (autoModeSelection >= autoModes.length) ? 0 : autoModeSelection;
        autoModeSelection = (autoModeSelection < 0) ? 0 : autoModeSelection;
        if(autoModeSelection == 1) {
            DriverStation.reportWarning("auto mode selection works", false);
        }
        autoModeRunner.chooseAutoMode(autoModes[0]); 
        autoModeRunner.start();
        
    }

    @Override
    public void autonomousPeriodic() {
        driveTrain.getDistance();
        SmartDashboard.putNumber("GyroAngle", driveTrain.getGyroAngle());
        //driverControls(joystick);
    }

    @Override
    public void teleopPeriodic() {
        newDriverControls(joystick);
    }


   /* 
    public void driverControls(PlasmaJoystick joy){
        driveTrain.FPSDrive(joystick.LeftY, joystick.RightX);

        if(joystick.L3.isPressed()){
            hatchIntake.releaseHatch();
        }
        if(joystick.R3.isPressed()){
            hatchIntake.grabHatch();
        }

        if(joystick.dPad.getPOV() == 0){
            hatchIntake.fullExtend();
        }
        if(joystick.dPad.getPOV() == 90){
            hatchIntake.halfExtend();
        }
        if(joystick.dPad.getPOV() == 180){
            hatchIntake.fullRetract();
        }
        if(joystick.dPad.getPOV() == 270){
            hatchIntake.halfExtend();
        }
        
        if((joystick.LT.isPressed() && joystick.RT.isOffToOn()) || (joystick.RT.isPressed() && joystick.LT.isOffToOn())){
            elevatorTarget = 29000;
            DriverStation.reportWarning("Middle", false);
        }
        else{
            if(joystick.LT.isOffToOn()){
                elevatorTarget = -200;
                DriverStation.reportWarning("Low", false);
            }
            if(joystick.RT.isOffToOn()){
                elevatorTarget = 56000;
                DriverStation.reportWarning("High", false);
            }
        }
        elevator.magicElevator(elevatorTarget);

        if(joystick.Y.isToggledOn()){
            cargoIntake.motionMagicPivot(4000, hatchIntake);
        }
        else{
            cargoIntake.motionMagicPivot(0, hatchIntake);
        }
    
        if(joystick.RB.isPressed()){
            cargoIntake.intakeCargo(1);
        }
        else if(joystick.LB.isPressed()){
            cargoIntake.intakeCargo(-1);
        }
        else{
            cargoIntake.intakeCargo(0);
        }

        if(joystick.START.isPressed()){
            //hab.raiseRobot(1);
            hab.GyroHABClimb();
        }
        else if(joystick.BACK.isPressed()){
            hab.lowerRobot(1);
            hab.GyroClimbReset();
        }
        else{
            hab.raiseRobot(0);
            hab.lowerRobot(0);
        }



        if(joystick.B.isPressed()){
            hab.HABForward(0.5);
        }
        else if(joystick.X.isPressed()){
            hab.HABForward(-0.5);
        }
        else{
            hab.HABForward(0);
        }
    }
    */

    public void newDriverControls(PlasmaJoystick joystick){
        driveTrain.FPSDrive(joystick.LeftY, joystick.RightX);

        if(cargoIntake.getIsPivotUp()){ //hatch requirements
            if(joystick.dPad.getPOV() == 0){
                hatchIntake.fullExtend();
            }
            if(joystick.dPad.getPOV() == 90 || joystick.dPad.getPOV() == 270){
                hatchIntake.halfExtend();
            }
            if(joystick.dPad.getPOV() == 180){
                hatchIntake.fullRetract();
            }

            if(joystick.L3.isPressed()){
                hatchIntake.releaseHatch();
            }
            if(joystick.R3.isPressed()){
                hatchIntake.grabHatch();
            }    
        }

        if(!hatchIntake.getIsClamped() && elevator.getIsElevatorDown()){ //pivot out requirements
            if(joystick.Y.isToggledOn()){
                hatchIntake.fullRetract();
                hatchIntake.grabHatch();
                pivotTarget = 4000;
            }
        }

        if(!cargoIntake.getIsPivotUp()){ //pivot in requirements
            if(!joystick.Y.isToggledOn()){
                pivotTarget = -200;
            }
        }
        cargoIntake.motionMagicPivot(pivotTarget, hatchIntake);
         
        if(joystick.RB.isPressed()){
            cargoIntake.intakeCargo(1);
        }
        else if(joystick.LB.isPressed()){
            cargoIntake.intakeCargo(-1);
        }
        else{
            cargoIntake.intakeCargo(0);
        }

        if(cargoIntake.getIsPivotUp()){
            if((joystick.LT.isPressed() && joystick.RT.isOffToOn()) || (joystick.RT.isPressed() && joystick.LT.isOffToOn())){
                elevatorTarget = 29000;
                DriverStation.reportWarning("Middle", false);
            }
            else{
                if(joystick.LT.isOffToOn()){
                    elevatorTarget = -200;
                    DriverStation.reportWarning("Low", false);
                }
                if(joystick.RT.isOffToOn()){
                    elevatorTarget = 56000;
                    DriverStation.reportWarning("High", false);
                }
            }
        }
        elevator.magicElevator(elevatorTarget);

        if(cargoIntake.getIsPivotUp()){
            if(joystick.START.isPressed()){
                hatchIntake.fullRetract();
                hatchIntake.grabHatch();
                hab.GyroHABClimb();
            }
            else if(joystick.BACK.isPressed()){
                hatchIntake.releaseHatch();
                hab.lowerRobot(1);
                hab.GyroClimbReset();
            }
            else{
                hab.raiseRobot(0);
                hab.lowerRobot(0);
            }

            if(joystick.B.isPressed()){
                hab.HABForward(0.5);
            }
            else if(joystick.X.isPressed()){
                hab.HABForward(-0.5);
            }
            else{
                hab.HABForward(0);
            }
        }
    }

   
    @Override
    public void testPeriodic() {
    }
}
