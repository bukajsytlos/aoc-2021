package day06

import java.io.File
import java.math.BigInteger

private const val REPRODUCTION_INTERVAL = 7

fun main() {
    val fishAges = File("src/main/kotlin/day06/input.txt").readLines().first().split(",").map { it.toInt() }
    val fishCountByTimer = fishAges.fold(Array(9) { BigInteger.ZERO }) { acc, fishAge ->
        acc[fishAge]++
        acc
    }

    val fishCountByTimer2 = fishCountByTimer.copyOf()

    for (day in 0..79) {
        calculateFishCount(day, fishCountByTimer)
    }
    println(fishCountByTimer.asSequence().sumOf { it })

    for (day in 0..255) {
        calculateFishCount(day, fishCountByTimer2)
    }
    println(fishCountByTimer2.asSequence().sumOf { it })
}

private fun calculateFishCount(day: Int, fishCountByTimer: Array<BigInteger>) {
    val reproductionDay = day % REPRODUCTION_INTERVAL
    val newFishes = fishCountByTimer[reproductionDay]
    fishCountByTimer[reproductionDay] += fishCountByTimer[7]
    fishCountByTimer[7] = fishCountByTimer[8]
    fishCountByTimer[8] = newFishes
}
