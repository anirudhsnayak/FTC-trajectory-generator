import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.trajectory.MarkerCallback
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumConstraints
import continuousPathConstructor.ContinuousDriveConstructor
import continuousPathConstructor.ContinuousPathConstructor
import continuousPathConstructor.Menu
import continuousPathConstructor.Menu.Companion.continuousConstructor
import continuousPathConstructor.getPoint
import java.awt.geom.Point2D
import kotlin.math.PI


object TrajectoryGen {
    private const val usingComputer = false

    private const val RedAlliance = false

    private val FP = if (RedAlliance) {-1} else {1} //Field Parity

    private val sqX = 24.0
    private val sqY = sqX * FP

    private var state = 2

    // Remember to set these constraints to the same values as your DriveConstants.java file in the quickstart
    private val driveConstraints = DriveConstraints(50.0, 50.0, 0.0, 180.0.toRadians, 180.0.toRadians, 0.0)

    // Remember to set your track width to an estimate of your actual bot to get accurate trajectory profile duration!
    private const val trackWidth = 16.0

    val combinedConstraints = MecanumConstraints(driveConstraints, trackWidth)

    lateinit var currentPose : Pose2d

    fun initialize(startPose: Pose2d, startHeading: Double) : TrajectoryBuilder{
        return TrajectoryBuilder(startPose, startHeading, driveConstraints)
    }

    fun createTraj(poses: Array<Pose2d>, buildTraj: (Array<Pose2d>) -> Trajectory) : Trajectory{
        currentPose = poses.last();
        return buildTraj(poses)
    }
    fun angleToPose(a: Pose2d, b: Pose2d): Double{
        return (b.vec()-a.vec()).angle();
    }
    fun autoMax(): ArrayList<Trajectory> {
        val list = ArrayList<Trajectory>()

        currentPose = Pose2d(-1.5*sqX , 2.5*sqY, 0.0);

        //Detect
        //Carousel
        list.add(createTraj(arrayOf(
            currentPose,
            Pose2d(-2.1*sqX, 2.5*sqY)
        )) { poses: Array<Pose2d> ->
            initialize(poses[0], -PI / 2)
                .lineToConstantHeading(poses[1].vec())
                .build()
        })
        //Deposit
        list.add(createTraj(arrayOf(
            currentPose,
            Pose2d(-1*sqX, 2*sqY, 0.0),
            Pose2d(-0.5*sqX, 1.8*sqY, PI/2)
        )) { poses: Array<Pose2d> ->
            initialize(poses[0], angleToPose(poses[0], poses[1]))
                .splineToSplineHeading(poses[1], angleToPose(poses[0], poses[1]))
                .splineToSplineHeading(poses[2], angleToPose(poses[1], poses[2]))
                .build()
        })
        //Park
        list.add(createTraj(arrayOf(
            currentPose,
            Pose2d(0.5*sqX, 1.9*sqY, 0.0),
            Pose2d(1.6*sqX, 1.9*sqY, 0.0)
        )){ poses: Array<Pose2d> ->
            initialize(poses[0], PI/4)
                .splineToSplineHeading(poses[1], 0.0)
                .splineToSplineHeading(poses[2], 0.0)
                .build()
        })
        return list
    }

    fun autoMaxAlliance(): ArrayList<Trajectory> {
        val list = ArrayList<Trajectory>()

        currentPose = Pose2d(0.4*sqX , 2.5*sqY, 0.0);

        //Detect
        //Deposit
        list.add(createTraj(arrayOf(
            currentPose,
            Pose2d(0.4*sqX, 1*sqY, 0.0)
        )) { poses: Array<Pose2d> ->
            initialize(poses[0], angleToPose(poses[0], poses[1]))
                .splineToSplineHeading(poses[1], angleToPose(poses[0], poses[1]))
                .build()
        })
        //Park
        list.add(createTraj(arrayOf(
            currentPose,
            Pose2d(0.4*sqX, 1.9*sqY, 0.0)
        )){ poses: Array<Pose2d> ->
            initialize(poses[0], PI/2)
                .splineToConstantHeading(poses[1].vec(), PI/2)
                .build()
        })
        //Park
        list.add(createTraj(arrayOf(
            currentPose,
            Pose2d(1.6*sqX, 1.9*sqY, 0.0)
        )){ poses: Array<Pose2d> ->
            initialize(poses[0], 0.0)
                .splineToConstantHeading(poses[1].vec(), 0.0)
                .build()
        })
        return list
    }

    fun park(): ArrayList<Trajectory> {
        return ArrayList() //TODO
    }

    fun parkAlliance(): ArrayList<Trajectory> {
        return ArrayList() //TODO
    }

    fun createTrajectory(): ArrayList<Trajectory> {
        return autoMax()
    }

//    fun createTrajectory(): ArrayList<Trajectory> {
//        val list =  ArrayList<Trajectory>()
//        list.add(computeOptimizedTrajectory(startPose, Pose2d(sq, 2*sq, PI/4)))
//        return list
//    }
    fun computeOptimizedTrajectory(start: Pose2d, end: Pose2d): Trajectory{
        continuousConstructor.initializeGraph()
        continuousConstructor.calculatePath(getPoint(start.vec()), getPoint(end.vec()))
        return(ContinuousDriveConstructor.getOptimalTrajectory(start.heading, end.heading, continuousConstructor.path))
    }
    fun drawOffbounds() {
    }
}

val Double.toRadians get() = (Math.toRadians(this))
