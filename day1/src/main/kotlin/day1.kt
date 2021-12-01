import java.io.File

fun main() {
    val lines = File("day1/src/main/kotlin/input.txt").readLines()
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