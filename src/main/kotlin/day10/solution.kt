package day10

import java.io.File


val chunkPairs = mapOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>',
)
val illegalCharacterPoints = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137,
)
val autocompleteCharacterPoints = mapOf(
    ')' to 1,
    ']' to 2,
    '}' to 3,
    '>' to 4,
)

fun main() {
    val lines = File("src/main/kotlin/day10/input.txt").readLines()
    val autoCompletesByState = lines
        .map { it.toCharArray() }
        .map { autocomplete(it) }
        .partition { it.failed() }

    println(autoCompletesByState.first
            .mapNotNull { it.illegalChar }
            .mapNotNull { illegalCharacterPoints[it] }
            .sumOf { it }
        )
    println(autoCompletesByState.second
        .mapNotNull { it.missedChars }
        .map { it.fold(0L) { acc, unit -> acc * 5 + (autocompleteCharacterPoints[unit] ?: 0) } }
        .sorted().let { it[it.size / 2] })
}

fun autocomplete(line: CharArray): Autocomplete {
    val stack = ArrayDeque<Char>()
    for (char in line) {
        if (char in chunkPairs.keys) {
            stack.addLast(char)
        } else {
            stack.removeLastOrNull()?.let {
                if (chunkPairs[it] != char) {
                    return Autocomplete(illegalChar = char)
                }
            }
        }
    }
    return Autocomplete(missedChars = stack.mapNotNull { chunkPairs[it] }.reversed())
}

data class Autocomplete(val illegalChar: Char? = null, val missedChars: List<Char>? = null) {
    fun failed() = illegalChar != null
}