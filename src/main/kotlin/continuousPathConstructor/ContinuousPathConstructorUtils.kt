package continuousPathConstructor

import com.acmerobotics.roadrunner.geometry.Vector2d
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import kotlin.math.PI
import kotlin.math.abs

internal fun getSmallestAngleDifference(ang1: Double, ang2: Double): Double {
    val diff = Vector2d.polar(1.0, ang1).angleBetween(Vector2d.polar(1.0, ang2))
    return if (diff.isNaN()) { //linearly dependent
        abs((ang1 - ang2) % (2 * PI))
    } else {
        diff
    }
}

internal fun getLineAngle(line: Line2D): Double {
    return Vector2d(line.x2 - line.x1, line.y2 - line.y1).angle()
}

internal fun getVector2d(point: Point2D): Vector2d {
    return (Vector2d(point.x, point.y))
}

internal fun getPoint(vec: Vector2d): Point2D {
    return (Point2D.Double(vec.x, vec.y))
}

internal fun sq(a: Double): Double {
    return a * a
}