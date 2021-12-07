package day07

import java.io.File
import kotlin.math.abs

fun main() {
    val crabSubmarinePositions =
        File("src/main/kotlin/day07/input.txt").readLines().first().split(",").map { it.toInt() }
    println(calculateFuelSpent(crabSubmarinePositions) { it })
    println(calculateFuelSpent(crabSubmarinePositions) { it * (it + 1) / 2 })
}

private fun calculateFuelSpent(crabSubmarinePositions: List<Int>, fuelFormula: (Int) -> Int): Int {
    var minFuelSpent = Int.MAX_VALUE
    for (i in 0.. crabSubmarinePositions.maxOf { it }) {
        val distanceToPosition = crabSubmarinePositions.sumOf { fuelFormula.invoke(abs(it - i)) }
        if (distanceToPosition < minFuelSpent) {
            minFuelSpent = distanceToPosition
        }
    }
    return minFuelSpent
}
