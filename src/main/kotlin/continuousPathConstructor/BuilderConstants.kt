package continuousPathConstructor

import kotlin.math.sqrt

object BuilderConstants {
    val clearanceRadius = 9.0 * sqrt(2.0)

    val showPotentialPaths = true
    val showBoundaries = true
    //add boolean for path correction
    val finder = pathFinder.Dijkstra

    val jerkCoefficent = 1.0;
    val strafingConfidence = 1.0;
    val maxStrafingSpeed = 30.0;
    val forwardConfidence = 1.0;
    val maxForwardSpeed = 30.0;
    val turningConfidence = 1.0;
    val maxTurningSpeed = Math.toRadians(180.0);

    enum class pathFinder {
        Dijkstra,
        AStar;
    }
}