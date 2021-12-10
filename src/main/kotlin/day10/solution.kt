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
    println(lines
        .mapNotNull { isCorrupted(it.toCharArray()) }
        .mapNotNull { illegalCharacterPoints[it] }
        .sumOf { it }
    )
    val autoCompletionPoints = lines
        .map { it.toCharArray() }
        .filter { isCorrupted(it) == null }
        .map { missingClosingChars(it).fold(0L) { acc, unit -> acc * 5 + autocompleteCharacterPoints[unit]!! } }
        .sorted()
    println(autoCompletionPoints[autoCompletionPoints.size / 2])
}

fun isCorrupted(line: CharArray): Char? {
    val stack = ArrayDeque<Char>()
    for (char in line) {
        if (char in chunkPairs.keys) {
            stack.addLast(char)
        } else {
            stack.removeLastOrNull()?.let {
                if (chunkPairs[it] != char) {
                    return char
                }
            }
        }
    }
    return null
}

fun missingClosingChars(line: CharArray): List<Char> {
    val stack = ArrayDeque<Char>()
    for (char in line) {
        if (char in chunkPairs.keys) {
            stack.addLast(char)
        } else {
            stack.removeLastOrNull()
        }
    }
    return stack.map { chunkPairs[it]!! }.reversed()
}