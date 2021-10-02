package continuousPathConstructor

import TrajectoryGen.combinedConstraints
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.path.*
import com.acmerobotics.roadrunner.path.heading.SplineInterpolator
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.acmerobotics.roadrunner.trajectory.TrajectoryGenerator
import java.awt.geom.Line2D
import kotlin.math.PI


object ContinuousDriveConstructor {
    fun getOptimalTrajectory(
        startHeading: Double,
        endHeading: Double,
        path: ArrayList<Line2D>
    ): Trajectory {
        val rrPath = createPath(startHeading, endHeading, path)
        val traj = TrajectoryGenerator.generateTrajectory(
            rrPath,
            combinedConstraints
        ) //Do advanced motion profiling if possible
        return (traj)
    }

    private fun createPath(
        startHeading: Double, endHeading: Double, path: ArrayList<Line2D>
    ): Path {
        return (Path(createPathWithInterpolation(startHeading, endHeading, createTranslationalPath(path))))
    }

    private fun createTranslationalPath(path: ArrayList<Line2D>): ArrayList<ParametricCurve> {
        val straightPaths = ArrayList<Line2D>()
        val list = ArrayList<ParametricCurve>()
        if (path.size == 0) {
            list.add(LineSegment(Vector2d(), Vector2d()))
            return list
        }
        for (line in path) {
            straightPaths.add(scaleInPlace(line, 0.5))
            //adjust scale factor as necessary
        }
        for ((i, line) in straightPaths.withIndex()) {
            val diff = getVector2d(line.p2) - getVector2d(line.p1)
            if (i == 0) {
                list.add(
                    QuinticSpline(
                        QuinticSpline.Knot(
                            getVector2d(path.first().p1),
                            getVector2d(line.p1) - getVector2d(path.first().p1)
                        ),
                        QuinticSpline.Knot(
                            getVector2d(line.p1),
                            getVector2d(line.p1) - getVector2d(path.first().p1)
                        )
                    )
                )
            }
            list.add(
                QuinticSpline(
                    QuinticSpline.Knot(getVector2d(line.p1), diff),
                    QuinticSpline.Knot(getVector2d(line.p2), diff)
                )
            )
            if (i == straightPaths.size - 1) {
                list.add(
                    QuinticSpline(
                        QuinticSpline.Knot(
                            getVector2d(line.p2),
                            getVector2d(path.last().p2) - getVector2d(line.p2)
                        ),
                        QuinticSpline.Knot(
                            getVector2d(path.last().p2),
                            getVector2d(path.last().p2) - getVector2d(line.p2)
                        )
                    )
                )
            } else {
                val nextDiff = getVector2d(straightPaths[i + 1].p2) - getVector2d(straightPaths[i + 1].p1)
                list.add(
                    QuinticSpline(
                        QuinticSpline.Knot(getVector2d(line.p2), diff),
                        QuinticSpline.Knot(getVector2d(straightPaths[i + 1].p1), nextDiff)
                    )
                )
            }
        }
        return list
    }

    private fun scaleInPlace(line: Line2D, scaleFactor: Double): Line2D {
        val p1 = getVector2d(line.p1)
        val p2 = getVector2d(line.p2)
        val mid = (p1 + p2) / 2.0
        var diff = p1 - mid
        diff *= scaleFactor
        return Line2D.Double(getPoint(mid + diff), getPoint(mid - diff))
    }

    private fun createPathWithInterpolation(
        startHeading: Double,
        endHeading: Double,
        curvePath: ArrayList<ParametricCurve>
    ): ArrayList<PathSegment> {
        val list = ArrayList<PathSegment>()
        var i = 0;
        var lastHeading = startHeading
        while (i < curvePath.size - 1) {
            val currentHeading = getClosestHeading(lastHeading, curvePath[i].endDeriv().angle())
            list.add(
                PathSegment(
                    curvePath[i],
                    SplineInterpolator(lastHeading, currentHeading, 0.0, 0.0, 0.0, 0.0)
                )
            )
            lastHeading = currentHeading
            i += 1
        }
        list.add(
            PathSegment(
                curvePath.last(),
                SplineInterpolator(lastHeading, endHeading, 0.0, 0.0, 0.0, 0.0)
            )
        )
        return list
    }

    private fun getClosestHeading(currentHeading: Double, pathHeading: Double): Double {
        var closestHeadingDiff = 2 * PI
        var lastI = 0
        for (i in 0..3) {
            var headingDiff = getSmallestAngleDifference(pathHeading + i * (PI / 2), currentHeading)
            if (headingDiff < closestHeadingDiff) {
                closestHeadingDiff = headingDiff
                lastI = i
            }
        }
        return (pathHeading + lastI * (PI / 2)) % (2 * PI)
    }
}