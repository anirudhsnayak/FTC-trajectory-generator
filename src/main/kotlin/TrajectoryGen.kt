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

    private const val sq = 24.0

    private var state = 2
    // Remember to set these constraints to the same values as your DriveConstants.java file in the quickstart
    private val driveConstraints = DriveConstraints(50.0, 50.0, 0.0, 180.0.toRadians, 180.0.toRadians, 0.0)

    // Remember to set your track width to an estimate of your actual bot to get accurate trajectory profile duration!
    private const val trackWidth = 16.0

    val combinedConstraints = MecanumConstraints(driveConstraints, trackWidth)

    private val startPose = Pose2d(-5*sq/2 , FP*sq, (0.0).toRadians)

    fun autoMax(): ArrayList<Trajectory> {
        val trajectoryBuilder = TrajectoryBuilder(startPose, 0.0, driveConstraints)
        val pose1 = Pose2d(-5*sq/2, -3*sq, (0.0).toRadians)
        val list = ArrayList<Trajectory>()
        return list
    }

    fun createTrajectory(): ArrayList<Trajectory> {
        val list =  ArrayList<Trajectory>()
        list.add(computeOptimizedTrajectory(startPose, Pose2d(sq, 2*sq, PI/4)))
        return list
    }
    fun computeOptimizedTrajectory(start: Pose2d, end: Pose2d): Trajectory{
        continuousConstructor.initializeGraph()
        continuousConstructor.calculatePath(getPoint(start.vec()), getPoint(end.vec()))
        return(ContinuousDriveConstructor.getOptimalTrajectory(start.heading, end.heading, continuousConstructor.path))
    }
    fun drawOffbounds() {
    }
}

val Double.toRadians get() = (Math.toRadians(this))
