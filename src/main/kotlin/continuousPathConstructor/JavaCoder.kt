package continuousPathConstructor

import continuousPathConstructor.Menu.Companion.continuousConstructor
import java.awt.geom.Line2D

/*
This could be made more efficient by just packaging the points, and then having it automatically
convert to lines on run. However, it's not that big of an issue anyways.
 */
object JavaCoder {
    val header = "package org.firstinspires.ftc.teamcode.drive.autonomous.opmodes;\n" +
            "\n" +
            "import com.acmerobotics.roadrunner.geometry.Pose2d;\n" +
            "import com.acmerobotics.roadrunner.geometry.Vector2d;\n" +
            "import com.acmerobotics.roadrunner.trajectory.Trajectory;\n" +
            "import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;\n" +
            "import java.util.Arrays; \n"+
            "import java.awt.geom.Line2D; \n"+
            "import com.qualcomm.robotcore.eventloop.opmode.Autonomous;\n" +
            "import com.qualcomm.robotcore.hardware.DcMotor;\n" +
            "import com.qualcomm.robotcore.hardware.DcMotorEx;\n" +
            "import org.firstinspires.ftc.teamcode.drive.MecanumDrivetrain;\n" +
            "import java.util.ArrayList;\n" +
            "\n" +
            "@Autonomous (name = \"COMPUTED\")\n" +
            "public class COMPUTED extends Autonomous2021{\n"+
            "\n" +
            "    @Override\n" +
            "    public void runOpMode() throws InterruptedException {\n" +
            "        initializeRobot();\n" +
            "\n" +
            "        ArrayList<Trajectory> trajectories = new ArrayList<Trajectory>();"
    val footer = "\n" +
            "        drive.setMotorPowers(0,0,0,0);\n" +
            "    }\n" +
            "}"
    fun generateLineCode (full: Boolean, line: Line2D): String{
        if(full){
            return "\n                Line2D.Double(" + line.x1 + ", " + line.y1 + ", " + line.x2 + ", " + line.y2 + ")";
        } else {
            return "\n        Line2D.Double(" + line.x1 + ", " + line.y1 + ", " + line.x2 + ", " + line.y2 + ")";
        }
    }
    fun generateCode (full: Boolean, path: ArrayList<Line2D>): String{
        var code: String
        if(full){
            code = "\n\n        ArrayList<Line2D> path = Arrays.asList("
        }else{
            code = "ArrayList<Line2D> path = Arrays.asList("
        }

        code += generateLineCode(full, path[0])
        for (i in 1 until path.size){
            val line = path[i]
            code += "," + generateLineCode(full, line)
        }
        code += ");\n"
        return code;
    }
    fun assemble(full: Boolean, path: ArrayList<Line2D>): String {
        var code = generateCode(full, path)
        return if(full){
            header + code + footer
        } else {
            code
        }
    }
}