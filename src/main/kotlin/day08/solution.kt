package day08

import java.io.File
import java.util.*

fun main() {
    val lines =
        File("src/main/kotlin/day08/input.txt").readLines()

    val signalToOutputList = lines.map { line ->
        line.substringBefore(" | ").split(" ").map { it.toSortedSet() } to line.substringAfter(" | ").split(" ")
            .map { it.toSortedSet() }
    }

    println(signalToOutputList.flatMap { it.second }.count { it.size in setOf(2, 3, 4, 7) })

    println(signalToOutputList.sumOf { determineNumber(it.first, it.second) })

}

fun determineNumber(uniqueSignalPatterns: List<SortedSet<Char>>, unknownNumber: List<SortedSet<Char>>): Int {
    val segmentsToDigit = HashMap<SortedSet<Char>, Int>()
    segmentsToDigit[uniqueSignalPatterns.first { it.size == 2 }] = 1
    segmentsToDigit[uniqueSignalPatterns.first { it.size == 3 }] = 7
    segmentsToDigit[uniqueSignalPatterns.first { it.size == 4 }] = 4
    segmentsToDigit[uniqueSignalPatterns.first { it.size == 7 }] = 8

    val fifthSegmentChar = uniqueSignalPatterns.flatten()
            .groupBy { it }
            .mapValues { it.value.size }
            .filter { it.value == 4 }.map { it.key }
            .first()
    val unknownFiveSegmentSignals = uniqueSignalPatterns.filter { it.size == 5 }.toMutableList()
    val unknownSixSegmentSignals = uniqueSignalPatterns.filter { it.size == 6 }.toMutableList()
    var segmentsRepresentingFive: SortedSet<Char>? = null
    var segmentsRepresentingSix: SortedSet<Char>? = null
    for (fiveSegmentSignal in unknownFiveSegmentSignals) {
            val tmpSet = fiveSegmentSignal.toSortedSet()
            tmpSet.add(fifthSegmentChar)
            if (unknownSixSegmentSignals.contains(tmpSet)) {
                segmentsRepresentingFive = fiveSegmentSignal
                segmentsRepresentingSix = tmpSet
                unknownFiveSegmentSignals.remove(segmentsRepresentingFive)
                unknownSixSegmentSignals.remove(segmentsRepresentingSix)
                break
            }
    }

    segmentsToDigit[segmentsRepresentingFive!!] = 5
    segmentsToDigit[segmentsRepresentingSix!!] = 6

    segmentsToDigit[unknownFiveSegmentSignals.first { it.contains(fifthSegmentChar) }] = 2
    segmentsToDigit[unknownFiveSegmentSignals.first { !it.contains(fifthSegmentChar) }] = 3
    segmentsToDigit[unknownSixSegmentSignals.first { it.contains(fifthSegmentChar) }] = 0
    segmentsToDigit[unknownSixSegmentSignals.first { !it.contains(fifthSegmentChar) }] = 9

    return unknownNumber.mapNotNull { segmentsToDigit[it] }.joinToString("").toInt()
}
