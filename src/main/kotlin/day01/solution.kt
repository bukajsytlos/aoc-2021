package day01

import java.io.File

fun main() {
    val lines = File("src/main/kotlin/day01/input.txt").readLines()
    println(lines
            .asSequence()
            .map(String::toInt)
            .zipWithNext { a, b -> a < b }
            .count { it })

    println(lines
            .asSequence()
            .map(String::toInt)
            .windowed(3)
            .map { it.sum() }
            .zipWithNext { a, b -> a < b }
            .count { it })
}