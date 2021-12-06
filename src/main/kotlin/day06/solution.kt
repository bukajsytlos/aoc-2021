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
        val dayIndex = day % REPRODUCTION_INTERVAL
        val newFishes = fishCountByTimer[dayIndex]
        fishCountByTimer[dayIndex] += fishCountByTimer[7]
        fishCountByTimer[7] = fishCountByTimer[8]
        fishCountByTimer[8] = newFishes //new fishes
    }
    println(fishCountByTimer.asSequence().sumOf { it })

    for (day in 0..255) {
        val dayIndex = day % REPRODUCTION_INTERVAL
        val newFishes = fishCountByTimer2[dayIndex]
        fishCountByTimer2[dayIndex] += fishCountByTimer2[7]
        fishCountByTimer2[7] = fishCountByTimer2[8]
        fishCountByTimer2[8] = newFishes //new fishes
    }
    println(fishCountByTimer2.asSequence().sumOf { it })
}
