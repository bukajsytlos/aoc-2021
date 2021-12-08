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

    println(signalToOutputList.sumOf { decodeNumber(it.first, it.second) })

}

/**
 * Segments
 *   111
 *  2   3
 *  2   3
 *   444
 *  5   6
 *  5   6
 *   777
 */
fun decodeNumber(uniqueSignalPatterns: List<SortedSet<Char>>, unknownNumber: List<SortedSet<Char>>): Int {
    val segmentsToDigit = HashMap<SortedSet<Char>, Int>()
    segmentsToDigit[uniqueSignalPatterns.first { it.size == 2 }] = 1
    segmentsToDigit[uniqueSignalPatterns.first { it.size == 3 }] = 7
    segmentsToDigit[uniqueSignalPatterns.first { it.size == 4 }] = 4
    segmentsToDigit[uniqueSignalPatterns.first { it.size == 7 }] = 8

    val segmentFrequencies = uniqueSignalPatterns.flatten()
        .groupBy { it }
        .mapValues { it.value.size }
    val fifthSegmentChar = segmentFrequencies
            .filter { it.value == 4 }.map { it.key }
            .first()
    val unknownFiveSegmentSignals = uniqueSignalPatterns.filter { it.size == 5 }.toMutableList()
    val unknownSixSegmentSignals = uniqueSignalPatterns.filter { it.size == 6 }.toMutableList()
    var segmentsRepresentingFive: SortedSet<Char>? = null
    var segmentsRepresentingSix: SortedSet<Char>? = null
    //5 with fifth segment is 6
    for (fiveSegmentSignal in unknownFiveSegmentSignals) {
            val fiveSegmentSignalWithFifthSegment = fiveSegmentSignal.toSortedSet()
            fiveSegmentSignalWithFifthSegment.add(fifthSegmentChar)
            if (unknownSixSegmentSignals.contains(fiveSegmentSignalWithFifthSegment)) {
                segmentsRepresentingFive = fiveSegmentSignal
                segmentsRepresentingSix = fiveSegmentSignalWithFifthSegment
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
