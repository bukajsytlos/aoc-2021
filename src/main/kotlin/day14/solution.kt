package day14

import java.io.File
import java.util.*


fun main() {
    var template: List<Char> = LinkedList()
    var pairInsertionsRules: Map<Pair<Char, Char>, Char> = emptyMap()
    File("src/main/kotlin/day14/input.txt").useLines { linesSequence ->
        val linesIterator = linesSequence.iterator()
        template = List(1) { linesIterator.next() }.first().toCollection(LinkedList<Char>())
        linesIterator.next()
        pairInsertionsRules = linesIterator.asSequence().map {
            (it.substringBefore(" -> ").first() to it.substringBefore(" -> ").last()) to it.substringAfter(" -> ")
                .first()
        }.toMap()
    }
    val edgeCharacters = arrayOf(template.first(), template.last()).groupBy { it }.mapValues { it.value.count() }

    val initialPairCount: Map<Pair<Char, Char>, Long> =
        template.windowed(2).map { it[0] to it[1] }.groupBy { it }.mapValues { it.value.size.toLong() }

    val pairCountAfter10Steps = (0..9).fold(initialPairCount) { acc, _ ->
        acc.react(pairInsertionsRules)
    }
    val characterCountAfter10Steps = pairCountAfter10Steps.frequencies(edgeCharacters)
    println(characterCountAfter10Steps.maxMinDiff())

    val pairCountAfter40Steps = (0..29).fold(pairCountAfter10Steps) { acc, _ ->
        acc.react(pairInsertionsRules)
    }
    val characterCountAfter40Steps = pairCountAfter40Steps.frequencies(edgeCharacters)
    println(characterCountAfter40Steps.maxMinDiff())

}

fun Map<Pair<Char, Char>, Long>.react(pairInsertionsRules: Map<Pair<Char, Char>, Char>): Map<Pair<Char, Char>, Long> =
    buildMap {
        this@react.forEach { entry ->
            val originalPair = entry.key
            val newChar = pairInsertionsRules.getValue(originalPair)
            val pairOne = originalPair.first to newChar
            val pairTwo = newChar to originalPair.second
            val originalPairCount = entry.value
            merge(pairOne, originalPairCount) { a, b -> a + b }
            merge(pairTwo, originalPairCount) { a, b -> a + b }
        }
    }

fun Map<Pair<Char, Char>, Long>.frequencies(edgeCharacters: Map<Char, Int>): Map<Char, Long> =
    this.flatMap { listOf(it.key.first to it.value, it.key.second to it.value) }
        .groupBy({ it.first }, { it.second })
        .mapValues { it.value.sum() + (edgeCharacters[it.key] ?: 0) }

fun Map<Char, Long>.maxMinDiff() = this.maxOf { it.value } / 2 - this.minOf { it.value } / 2
