package frc.robot;


public class Constants {
	
	/*front of robot has electronics*/
	/*right & left sides from robot's perspective*/


	/* CONTROLLER CONSTANTS */
	public static final int JOYSTICK1_PORT = 0;
	
	
	/* TALON ID CONSTANTS */
    public static final int R_DRIVE_ID = 1;         //right side motor farthest from talons
	public static final int L_DRIVE_ID = 4;         //left side motor farthest from talons
	public static final int R_ELEVATOR_ID = 7;		
	public static final int L_ELEVATOR_ID = 10;
	public static final int PIVOT_ID = 13;			//motor id that pivots the intake
	public static final int INTAKE_ID = 9;			//motor id that intakes cargo
	public static final int L_HAB_ELEVATOR_ID = 11;
	public static final int R_HAB_ELEVATOR_ID = 8;
	public static final int HAB_DRIVE = 12;
	public static final int R_HAB_ARM_ID = 14;
	public static final int L_HAB_ARM_ID = 15;

	/* VICTORSPX ID CONSTANTS */
	public static final int R_DRIVE_MID_SLAVE_ID = 2;  //right side motor in the middle
    public static final int R_DRIVE_FRONT_SLAVE_ID = 3;  //right side motor closest to talons
	public static final int L_DRIVE_MID_SLAVE_ID = 5;  //left side motor in the middle
	public static final int L_DRIVE_FRONT_SLAVE_ID = 6;  //left side motor closest to talons

	/* PNEUMATICS ID CONTANTS */
	public static final int CLAW_PISTON_ID = 0;
	public static final int BACK_EXTENDER_ID = 1;
	public static final int FRONT_EXTENDER_ID = 2;

	/*DRIVETRAIN CONSTANTS*/
	public static final double MAX_AUTO_DRIVE_SPEED = 0.9;
	public static final double MAX_DRIVE_SPEED = 1;
	public static final double MAX_DRIVE_TURN = 1;	
	public static final double DRIVE_ENCODER_CONVERSION = 20.9528184;
	public static final double DRIVE_WHEEL_WIDTH = 27;

	/*HATCH CONSTANTS*/
	
	
	/*ELEVATOR CONSTANTS*/
	public static final double MAX_ELEVATOR_SPEED = 1;
	public static final double ELEVATOR_RAMP_RATE = 0.02;
	public static final double ELEVATOR_ENCODER_CONVERSION = 1;
	
	/*INTAKE CONSTANTS*/
	public static final double INTAKE_SPEED = 1;
	public static final double MAX_INTAKE_PIVOT_SPEED = 0.75;
	public static final double PIVOT_RAMP_RATE = 0.002;
	public static final int CAMERA_SERVO_ID = 0; 


	/*HAB CONSTANTS*/
	public static final double MAX_HAB_DRIVE_SPEED = 0.8;
	public static final double MAX_HAB_ARM_SPEED = .6;
	public static final double MAX_HAB_REAR_SPEED = 1;
	
	/*TALON CONFIG CONSTANTS*/
	public static final int TALON_TIMEOUT = 10;

	/*DIO ID Constants*/
	public static final int PIVOT_LIMIT_ID = 0;
	public static final int ELEVATOR_LIMIT_ID = 1;
	public static final int HAB_ELEVATOR_LIMIT_ID = 2;
	public static final int HAB_ARM_LIMIT_ID = 3;


	/* ELEVATOR MOTION MAGIC CONSTANTS */
	public static final double ELEVATOR_P = 0.6;
	public static final double ELEVATOR_I = 0;
	public static final double ELEVATOR_D = 6;
	public static final double ELEVATOR_F = .2;
	public static final int ELEVATOR_SLOT_IDX = 0;
	public static final int ELEVATOR_LOOP_IDX = 0;
	public static final int ELEVATOR_TIMEOUT = 60;
	public static final int ELEVATOR_CRUISE_VELOCITY = 10000;
	public static final int ELEVATOR_ACCELERATION = 5000;

	public static final double PIVOT_P = 0.6;
	public static final double PIVOT_I = 0;
	public static final double PIVOT_D = 6;
	public static final double PIVOT_F = .2;
	public static final int PIVOT_SLOT_IDX = 0;
	public static final int PIVOT_LOOP_IDX = 0;
	public static final int PIVOT_TIMEOUT = 60;
	public static final int PIVOT_CRUISE_VELOCITY = 2000;
	public static final int PIVOT_ACCELERATION = 800;


	/* WAYPOINTS */
	public static final double WAYPOINTS_DT = 0.05;
	public static final double WAYPOINTS_MAX_VELOCITY = 1.7;
	public static final double WAYPOINTS_MAX_ACCELERATION = 2.0;
	public static final double WAYPOINTS_MAX_JERK = 60.0;

	public static final double WHEELBASE_WIDTH = 0.6604;  //26 inches
}