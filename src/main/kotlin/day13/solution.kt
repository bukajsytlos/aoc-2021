package day13

import java.io.File


fun main() {
    var points: Set<Point> = emptySet()
    var foldCommands: List<FoldCommand> = emptyList()
    File("src/main/kotlin/day13/input.txt").useLines { linesSequence ->
        val linesIterator = linesSequence.iterator()
        points = linesIterator.asSequence().takeWhile { s -> s.isNotBlank() }.map {
            Point(
                it.substringBefore(",").toInt(),
                it.substringAfter(",").toInt()
            )
        }.toSet()
        foldCommands = linesIterator.asSequence().map {
            FoldCommand(
                it.substringBefore('=').last(),
                it.substringAfter('=').toInt()
            )
        }.toList()
    }
    println(fold(points, foldCommands[0]).count())
    val codePoints = foldCommands.fold(points) { acc, foldCommand -> fold(acc, foldCommand) }
    codePoints.display()
}

fun fold(points: Set<Point>, foldCommand: FoldCommand): Set<Point> = points.map { point ->
    if (foldCommand.axis == 'x' && point.x > foldCommand.position) {
        Point(2 * foldCommand.position - point.x, point.y)
    } else if (foldCommand.axis == 'y' && point.y > foldCommand.position) {
        Point(point.x, 2 * foldCommand.position - point.y)
    } else {
        point
    }
}.toSet()

fun Set<Point>.display() {
    (0..this.maxOf { it.y }).forEach { y ->
        (0..this.maxOf { it.x }).forEach { x ->
            print(if (Point(x, y) in this) "\u2588" else "\u2591")
        }
        println()
    }
}

data class Point(val x: Int, val y: Int)
data class FoldCommand(val axis: Char, val position: Int)