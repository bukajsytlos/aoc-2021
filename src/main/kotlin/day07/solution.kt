package day07

import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt

fun main() {
    val crabSubmarinePositions =
        File("src/main/kotlin/day07/input.txt").readLines().first().split(",").map { it.toInt() }

    val medianDistance = crabSubmarinePositions.sorted()[crabSubmarinePositions.size / 2]
    println(calculateFuelSpent(crabSubmarinePositions, medianDistance) { it })

    val meanDistance = crabSubmarinePositions.average().roundToInt()
    val minFuelSpent = ((meanDistance - 1)..(meanDistance + 1)) //correct answer should be around mean
        .map { position -> calculateFuelSpent(crabSubmarinePositions, position) { it * (it + 1) / 2 } }
        .minOf { it }
    println(minFuelSpent)
}

private fun calculateFuelSpent(crabSubmarinePositions: List<Int>, position: Int, fuelFormula: (Int) -> Int): Int =
    crabSubmarinePositions.sumOf { fuelFormula(abs(it - position)) }