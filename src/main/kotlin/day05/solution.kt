package day05

import java.io.File
import kotlin.math.abs

fun main() {
    val inputLines = File("src/main/kotlin/day05/input.txt").readLines()

    val segments = inputLines
        .mapNotNull { Regex("""(\d+),(\d+) -> (\d+),(\d+)""").matchEntire(it) }
        .map {
            Segment(
                Point(it.groupValues[1].toInt(), it.groupValues[2].toInt()),
                Point(it.groupValues[3].toInt(), it.groupValues[4].toInt())
            )
        }
    val ventMap = segments
        .filter { it.isLateral() }
        .fold(mutableMapOf<Point, Int>()) { acc, segment ->
            segment.getSegmentPoints().forEach {
                acc.compute(it) { _, v -> v?.inc() ?: 1 }
            }
            acc
        }
    println(ventMap.values.count { it > 1 })

    val ventMap2 = segments
        .fold(mutableMapOf<Point, Int>()) { acc, segment ->
            segment.getSegmentPoints().forEach {
                acc.compute(it) { _, v -> v?.inc() ?: 1 }
            }
            acc
        }
    println(ventMap2.values.count { it > 1 })
}

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
