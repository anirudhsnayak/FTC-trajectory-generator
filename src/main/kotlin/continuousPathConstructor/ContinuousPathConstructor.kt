package continuousPathConstructor

import GraphicsUtil
import com.acmerobotics.roadrunner.geometry.Vector2d
import continuousPathConstructor.BuilderConstants.clearanceRadius
import continuousPathConstructor.BuilderConstants.finder
import continuousPathConstructor.BuilderConstants.showBoundaries
import continuousPathConstructor.BuilderConstants.showPotentialPaths
import javafx.scene.paint.Color
import org.jgrapht.Graph
import org.jgrapht.GraphPath
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm
import org.jgrapht.alg.shortestpath.AStarShortestPath
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import kotlin.math.PI

class ContinuousPathConstructor {
    lateinit var graph: Graph<Point2D, DefaultWeightedEdge>
    var visibleBarriers = ArrayList<Line2D>()
    var graphPoints = ArrayList<Point2D>()
    var circularBarriers = ArrayList<Pair<Point2D, Double>>()
    var barriers = ArrayList<Line2D>()
    var edges = ArrayList<Line2D>()
    lateinit var path: ArrayList<Line2D>

    fun addBarrier(lineBarrier: Line2D) {
        //barriers.add(lineBarrier)
        visibleBarriers.add(lineBarrier)
        addLineCircleBarrier(clearanceRadius, lineBarrier.p1, lineBarrier.p2)
        addCircleBarrier(clearanceRadius, lineBarrier.p1)
        addCircleBarrier(clearanceRadius, lineBarrier.p2)
    }

    private fun addLineCircleBarrier(r: Double, point1: Point2D, point2: Point2D) {
        val midpoint = (getVector2d(point1) + getVector2d(point2)) / 2.0
        val lineVector = (getVector2d(point2) - getVector2d(point1)) / 2.0
        val lineAngle = lineVector.angle()
        val perpendicularVector = Vector2d.polar(r, lineAngle + (PI / 2))
        val lineEndVector = Vector2d.polar(r, lineAngle)
        graphPoints.add(getPoint(Vector2d.polar(r, lineAngle) + midpoint + lineVector))
        graphPoints.add(getPoint(Vector2d.polar(r, lineAngle + PI) + midpoint - lineVector))
        barriers.add(
            Line2D.Double(
                getPoint(midpoint + perpendicularVector + lineVector),
                getPoint(midpoint - perpendicularVector + lineVector)
            )
        )
        graphPoints.add(barriers.last().p1); graphPoints.add(barriers.last().p2);
        barriers.add(
            Line2D.Double(
                getPoint(midpoint + perpendicularVector - lineVector),
                getPoint(midpoint - perpendicularVector - lineVector)
            )
        )
        graphPoints.add(barriers.last().p1); graphPoints.add(barriers.last().p2);
        barriers.add(
            Line2D.Double(
                getPoint(midpoint + lineEndVector + lineVector),
                getPoint(midpoint - lineEndVector - lineVector)
            )
        )
        graphPoints.add(barriers.last().p1); graphPoints.add(barriers.last().p2);
    }

    private fun addCircleBarrier(r: Double, point: Point2D) {
        circularBarriers.add(Pair(point, r))
    }

    fun initializeGraph() {
        edges.clear()
        graph = SimpleWeightedGraph<Point2D, DefaultWeightedEdge>(
            { Point2D.Double(0.0, 0.0) },
            { DefaultWeightedEdge() })
        updateCircleOverlap()
        for (point in graphPoints) {
            graph.addVertex(point)
        }
        for (i in 0 until graph.vertexSet().size) {
            for (j in i until graph.vertexSet().size) {
                addEdgeToGraph(graph.vertexSet().elementAt(i), graph.vertexSet().elementAt(j))
            }
        }
    }

    fun addPoint(point: Point2D) {
        graph.addVertex(point)
        for (i in 0 until graph.vertexSet().size - 1) { //without the point itself
            addEdgeToGraph(point, graph.vertexSet().elementAt(i))
        }
    }

    fun calculatePath(p1: Point2D, p2: Point2D) {
        addPoint(p1)
        addPoint(p2)
        val alg: ShortestPathAlgorithm<Point2D, DefaultWeightedEdge> =
            if (finder == BuilderConstants.pathFinder.AStar) {
                AStarShortestPath<Point2D, DefaultWeightedEdge>(graph) { p1: Point2D, p2: Point2D ->
                    p1.distance(
                        p2
                    )
                }
            } else {
                DijkstraShortestPath<Point2D, DefaultWeightedEdge>(graph)
            }

        val testPath: GraphPath<Point2D, DefaultWeightedEdge>? = alg.getPath(p1, p2)
        if (testPath != null) {
            path = createLinePath(testPath)
        } else {
            path = ArrayList()
            path.add(Line2D.Double(p1, p2))
            println("No path was found that meets the desired constraints.")
        }
    }

    fun updateCircleOverlap() {
        for (i in 0 until circularBarriers.size - 1) {
            for (j in i + 1 until circularBarriers.size) {
                if (checkCircleOverlap(circularBarriers[i], circularBarriers[j])) {
                    barriers.add(
                        Line2D.Double(
                            circularBarriers[i].first,
                            circularBarriers[j].first
                        )
                    )
                }
            }
        }
    }

    fun checkCircleOverlap(c1: Pair<Point2D, Double>, c2: Pair<Point2D, Double>): Boolean {
        return c1.first.distance(c2.first) < c1.second + c2.second
    }

    fun checkForBarrierOverlaps(testLine: Line2D): Boolean {
        for (line in barriers) {
            if (testLine.intersectsLine(line) && notCornerIntersection(line, testLine)) {
                return false
            }
        }
        return true
    }

    fun notCornerIntersection(l1: Line2D, l2: Line2D): Boolean {
        var i = 0
        if (l1.p1 == l2.p1) {
            i++
        } else if (l1.p1 == l2.p2) {
            i++
        }
        if (l1.p2 == l2.p1) {
            i++
        } else if (l1.p2 == l2.p2) {
            i++
        }
        if (i == 1) {
            return false
        }
        return true
    }

    fun addEdgeToGraph(p1: Point2D, p2: Point2D) {
        if (p1 != p2) {
            val edge = graph.addEdge(p1, p2)
            if (edge != null && checkForBarrierOverlaps(Line2D.Double(p1, p2))) {
                edges.add(Line2D.Double(p1, p2))
                graph.setEdgeWeight(edge, p1.distance(p2))
            } else {
                graph.removeEdge(edge)
            }
        }
    }

    fun drawAll() {
        if (showPotentialPaths) {
            GraphicsUtil.drawLines(edges, Color.AQUA)
        }
        if (showBoundaries) {
            GraphicsUtil.drawLines(barriers, Color.RED)
            GraphicsUtil.drawCircles(circularBarriers, Color.RED)
        }
       // GraphicsUtil.drawLines(path, Color.LIME)
        GraphicsUtil.drawLines(visibleBarriers, Color.RED)
    }

    private fun createLinePath(path: GraphPath<Point2D, DefaultWeightedEdge>): ArrayList<Line2D> {
        var list = ArrayList<Line2D>()
        var prevPos: Point2D? = null
        for (vertex in path.vertexList) {
            val currentPos = Point2D.Double(vertex.x, vertex.y)
            if (prevPos != null) {
                list.add(Line2D.Double(prevPos, currentPos))
            }
            prevPos = currentPos
        }
        return list
    }
}