import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.path.Path
import continuousPathConstructor.getVector2d
import javafx.scene.Group
import javafx.scene.canvas.GraphicsContext

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import kotlin.math.ceil


object GraphicsUtil {
    val DEFUALT_RESOLUTION = 2.0 // inches

    val FIELD_WIDTH = 144.0 // 12'

    val ROBOT_WIDTH = 18.0

    val LINE_THICKNESS = 3.0

    val PATH_COLOR = Color.YELLOW
    val ROBOT_COLOR = Color.MAROON
    val ROBOT_VECTOR_COLOR = Color.BLUE
    val END_BOX_COLOR = Color.GREEN

    lateinit var gc: GraphicsContext

    lateinit var fieldGroup: Group

    fun setColor(color: Color) {
        gc.stroke = color
        gc.fill = color
    }

    //    fun drawBarriers(windowLen: Double, grid: Array<Array<GridCell>>){
//        setColor(Color.RED)
//        gc.globalAlpha = 0.75
//        for(row in grid){
//            for(cell in row){
//                if (!cell.isWalkable){
//                    val rect = Rectangle()
//                    rect.height = windowLen / GridPathConstructor.resolution
//                    rect.width = rect.height
//                    rect.x = -windowLen * (2.5-row.indexOf(cell).toDouble())/ GridPathConstructor.resolution
//                    rect.y = -windowLen * (2.5-grid.indexOf(row).toDouble())/ GridPathConstructor.resolution
//                    fieldGroup.children.add(rect)
//                }
//            }
//        }
//    }
    fun drawLines(lines: ArrayList<Line2D>, color: Color){
        setColor(color)
        for(line in lines){
            strokeLine(Vector2d(line.x1, line.y1), Vector2d(line.x2, line.y2))
        }
    }
    fun drawCircles(circles: ArrayList<Pair<Point2D, Double>>, color: Color){
        setColor(color)
        for(circle in circles){
            strokeCircle(getVector2d(circle.first), circle.second)
        }
    }
    fun drawSampledPath(path: Path) {
        setColor(PATH_COLOR)
        val samples = ceil(path.length() / DEFUALT_RESOLUTION).toInt()
        val points = Array(samples) { Vector2d() }
        val dx = path.length() / (samples - 1).toDouble()
        for (i in 0 until samples) {
            val displacement = i * dx
            val pose = path[displacement]
            points[i] = pose.vec()
        }
        strokePolyline(points)
    }

    fun strokePolyline(points: Array<Vector2d>) {
        val pixels = points.map { it.toPixel }
        gc.strokePolyline(pixels.map { it.x }.toDoubleArray(), pixels.map { it.y }.toDoubleArray(), points.size)
    }
    fun strokeCircle(p: Vector2d, r: Double){
        val pixDiff = Vector2d(r*pixelsPerInch, r*pixelsPerInch)
        val pix = p.toPixel-pixDiff
        gc.strokeOval(pix.x, pix.y, 2.0*pixDiff.x, 2.0*pixDiff.y)
    }
    fun strokeLine(p1: Vector2d, p2: Vector2d) {
        val pix1 = p1.toPixel
        val pix2 = p2.toPixel
        gc.strokeLine(pix1.x, pix1.y, pix2.x, pix2.y)
    }

    fun drawRobotVector(pose2d: Pose2d) {
        gc.globalAlpha = 0.75

        val point1 = pose2d.vec()
        val v = pose2d.headingVec() * ROBOT_WIDTH / 2.0
        val point2 = point1 + v

        setColor(ROBOT_VECTOR_COLOR)
        strokeLine(point1, point2)

        gc.globalAlpha = 0.75
    }

    fun fillRect(center: Vector2d, w: Double, h: Double) {
        val center_pix = center.toPixel
        val pix_w = w * pixelsPerInch
        val pix_h = h * pixelsPerInch

        gc.fillRect(center_pix.x - pix_w / 2.0, center_pix.y - pix_h / 2.0, pix_w, pix_h)
    }

    fun updateRobotRect(rectangle: Rectangle, pose2d: Pose2d, color: Color, opacity: Double) {
        val pix_w = ROBOT_WIDTH * pixelsPerInch

        rectangle.width = pix_w
        rectangle.height = pix_w

        val center_pix = pose2d.vec().toPixel
        rectangle.x = center_pix.x - pix_w / 2.0
        rectangle.y = center_pix.y - pix_w / 2.0
        rectangle.fill = color
        rectangle.opacity = opacity
        rectangle.rotate = Math.toDegrees(-pose2d.heading)
    }

    var pixelsPerInch = 0.0
    var halfFieldPixels = 0.0
}


val Vector2d.toPixel
    get() = Vector2d(
        -y * GraphicsUtil.pixelsPerInch + GraphicsUtil.halfFieldPixels,
        -x * GraphicsUtil.pixelsPerInch + GraphicsUtil.halfFieldPixels
    )