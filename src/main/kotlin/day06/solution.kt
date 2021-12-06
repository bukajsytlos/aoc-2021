package day06

import java.io.File

private const val REPRODUCTION_INTERVAL = 7

fun main() {
    val fishReproductionTimers = File("src/main/kotlin/day06/input.txt").readLines().first().split(",").map { it.toInt() }
    val fishCountByTimer = fishReproductionTimers.fold(Array(9) { 0L }) { acc, fishReproductionTimer ->
        acc[fishReproductionTimer]++
        acc
    }

    for (day in 0..79) {
        calculateFishCount(day, fishCountByTimer)
    }
    println(fishCountByTimer.asSequence().sumOf { it })

    for (day in 80..255) {
        calculateFishCount(day, fishCountByTimer)
    }
    println(fishCountByTimer.asSequence().sumOf { it })
}

private fun calculateFishCount(day: Int, fishCountByTimer: Array<Long>) {
    val reproductionDayTimer = day % REPRODUCTION_INTERVAL
    val newFishCount = fishCountByTimer[reproductionDayTimer]
    fishCountByTimer[reproductionDayTimer] += fishCountByTimer[7]
    fishCountByTimer[7] = fishCountByTimer[8]
    fishCountByTimer[8] = newFishCount
}
