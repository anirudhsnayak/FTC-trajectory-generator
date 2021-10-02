package continuousPathConstructor

import App
import GraphicsUtil
import com.acmerobotics.roadrunner.geometry.Vector2d
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.io.File

class Menu(app: App) {
    companion object {
        lateinit var continuousConstructor: ContinuousPathConstructor
    }

    val mainApp = app
    var lineStarted = false
    lateinit var lineStart: Vector2d

    fun start() {//TODO: COMPLETE MENU
        val buttons = ArrayList<Button>()
        continuousConstructor = ContinuousPathConstructor()
        continuousConstructor.initializeGraph()
        continuousConstructor.calculatePath(Point2D.Double(0.0, 0.0), Point2D.Double(0.0, 0.0))
        mainApp.generateTrajectory()

        var recalcBtn = Button("Recalculate Path")
        var fileBtn = Button("Save Full Code to File")
        var pathBtn = Button("Save Path Segment to File")
        fileBtn.layoutX = 105.0
        pathBtn.layoutX = 235.0
        val recalculatePath = EventHandler<MouseEvent> {
            //btn.text = "Recalculate Path" //change this when advanced gui is added
            mainApp.generateTrajectory()
            lineStarted = false
        }
        val mouseClickedField = EventHandler<MouseEvent> {
            if (lineStarted) {
                val lineEnd = -Vector2d(
                    144 * (it.sceneY / (GraphicsUtil.halfFieldPixels * 2) - 0.5),
                    144 * (it.sceneX / (GraphicsUtil.halfFieldPixels * 2) - 0.5)
                )
                continuousConstructor.addBarrier(Line2D.Double(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y))
            } else {
                lineStart = -Vector2d(
                    144 * (it.sceneY / (GraphicsUtil.halfFieldPixels * 2) - 0.5),
                    144 * (it.sceneX / (GraphicsUtil.halfFieldPixels * 2) - 0.5)
                )
            }
            lineStarted = !lineStarted
        }
        val saveFullCodeToFile = EventHandler<MouseEvent> {
            File("src/out/Computed.txt").writeText(JavaCoder.assemble(true, continuousConstructor.path))
            lineStarted = false
        }
        val savePathToFile = EventHandler<MouseEvent> {
            File("src/out/Computed.txt").writeText(JavaCoder.assemble(false, continuousConstructor.path))
            lineStarted = false
        }
        recalcBtn.addEventFilter(MouseEvent.MOUSE_PRESSED, recalculatePath)
        fileBtn.addEventFilter(MouseEvent.MOUSE_PRESSED, saveFullCodeToFile)
        pathBtn.addEventFilter(MouseEvent.MOUSE_PRESSED, savePathToFile)
        GraphicsUtil.fieldGroup.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseClickedField)
        buttons.add(recalcBtn)
        buttons.add(fileBtn)
        buttons.add(pathBtn)

        for (button in buttons){
            button.removeEventFilter(MouseEvent.MOUSE_PRESSED, mouseClickedField)
        }

        GraphicsUtil.fieldGroup.children.addAll(buttons)
    }
}