package day17

import java.io.File
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.sqrt

fun main() {
    val lines = File("src/main/kotlin/day17/input.txt").readLines()
    val (x1, x2) = lines.first().substringAfter("target area: x=").substringBefore(", y=").split("..")
        .map { it.toInt() }
    val (y1, y2) = lines.first().substringAfter("y=").split("..").map { it.toInt() }

    println(y1 * (y1 + 1) / 2)

    val maxSteps = abs(2*y1)

    val validSpeeds: MutableSet<Pair<Int, Int>> = mutableSetOf()
    for (steps in 1..maxSteps) {
        val minYSpeed = decrementalSpeed(y1, steps)
        val maxYSpeed = decrementalSpeed(y2, steps)
        val minXSpeed = decrementalSpeedWithFloor(x1, steps)
        val maxXSpeed = decrementalSpeedWithFloor(x2, steps)
        for (speedX in minXSpeed..maxXSpeed) {
            for (speedY in minYSpeed..maxYSpeed) {
                if (targetCoordinateY(speedY, steps) in y1..y2 && targetCoordinateX(speedX, steps) in x1..x2) {
                    validSpeeds.add(speedX to speedY)
                }
            }
        }
    }
    println(validSpeeds.size)
}

fun targetCoordinateX(speed: Int, steps: Int): Int {
    val coefficient = minOf(speed, steps)
    return coefficient * speed - (coefficient - 1) * coefficient / 2
}

fun targetCoordinateY(speed: Int, steps: Int): Int = steps * speed - (steps - 1) * steps / 2

fun decrementalSpeed(targetCoordinate: Int, steps: Int): Int = (targetCoordinate.toDouble() / steps + (steps - 1).toDouble() / 2).toInt()

fun decrementalSpeedWithFloor(targetCoordinate: Int, steps: Int): Int {
    val minXSpeedToReachCoordinate = minXSpeedToReach(targetCoordinate)
    val speed = decrementalSpeed(targetCoordinate, steps)
    return if (steps > minXSpeedToReachCoordinate) minXSpeedToReachCoordinate else speed
}

fun minXSpeedToReach(targetCoordinate: Int): Int = round(sqrt((targetCoordinate * 8 + 1).toDouble()) / 2 - 0.5).toInt()