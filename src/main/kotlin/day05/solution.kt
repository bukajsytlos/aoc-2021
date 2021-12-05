package day05

import java.io.File
import kotlin.math.abs

fun main() {
    val segmentPattern = Regex("""(\d+),(\d+) -> (\d+),(\d+)""")
    val inputLines = File("src/main/kotlin/day05/input.txt").readLines()
    val segments = inputLines
        .mapNotNull { segmentPattern.matchEntire(it) }
        .map {
            Segment(
                Point(it.groupValues[1].toInt(), it.groupValues[2].toInt()),
                Point(it.groupValues[3].toInt(), it.groupValues[4].toInt())
            )
        }
    val dangerousVentCount = segments.asSequence()
        .filter { it.isLateral() }
        .dangerousVentCount()
    println(dangerousVentCount)

    val dangerousVentCount2 = segments.asSequence()
        .dangerousVentCount()
    println(dangerousVentCount2)
}

fun Sequence<Segment>.dangerousVentCount() = this
    .map { it.getSegmentPoints() }
    .flatten()
    .groupBy { it }
    .count { it.value.size > 1 }

data class Segment(val p1: Point, val p2: Point) {

    fun isLateral(): Boolean = p1.x == p2.x || p1.y == p2.y

    fun getSegmentPoints(): List<Point> {
        val points = mutableListOf<Point>()
        var lastPoint: Point = p1
        for (i in 0..getLength()) {
            points.add(lastPoint)
            lastPoint += getDirectionVector()
        }
        return points
    }

    private fun getDirectionVector(): Vector {
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        return Vector(dx / getLength(), dy / getLength())
    }

    private fun getLength(): Int = maxOf(abs(p2.x - p1.x), abs(p2.y - p1.y))
}

data class Point(val x: Int, val y:Int) {
    operator fun plus(vector: Vector): Point = Point(x + vector.dx, y + vector.dy)
}

data class Vector(val dx: Int, val dy: Int)
