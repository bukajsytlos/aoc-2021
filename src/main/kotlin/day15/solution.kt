package day15

import java.io.File
import java.util.*


fun main() {
    val lines = File("src/main/kotlin/day15/input.txt").readLines()
    val mapSize = lines.size
    val riskLevelGrid: Array<IntArray> = Array(mapSize) { i -> lines[i].map { it.digitToInt() }.toIntArray() }
    val minimumRiskLevelGrid: Array<IntArray> = findMinimumRiskLevels(mapSize, riskLevelGrid)
    println(minimumRiskLevelGrid[mapSize - 1][mapSize - 1])
    val riskLevelFullGrid: Array<IntArray> = Array(mapSize * 5) { y ->
        IntArray(mapSize * 5) { x ->
            (riskLevelGrid[y % mapSize][x % mapSize] + x / mapSize + y / mapSize - 1) % 9 + 1
        }
    }
    val minimumRiskLevelFullGrid: Array<IntArray> = findMinimumRiskLevels(mapSize * 5, riskLevelFullGrid)
    println(minimumRiskLevelFullGrid[mapSize * 5 - 1][mapSize * 5 - 1])
}

private fun findMinimumRiskLevels(
    mapSize: Int,
    riskLevelGrid: Array<IntArray>
): Array<IntArray> {
    val minimumRiskLevelGrid: Array<IntArray> = Array(mapSize) { IntArray(mapSize) { Int.MAX_VALUE } }
    val parents: MutableMap<Point, Point> = mutableMapOf()

    val unvisited: MutableSet<Point> =
        (0 until mapSize).flatMap { y -> (0 until mapSize).map { x -> Point(x, y) } }.toMutableSet()
    minimumRiskLevelGrid[0][0] = 0

    val visiting: PriorityQueue<Pair<Point, Int>> = PriorityQueue { o1, o2 -> o1.second - o2.second }
    visiting.add(Point(0, 0) to 0)

    while (visiting.isNotEmpty()) {
        val (nextCandidate, parentRiskLevel) = visiting.poll()
        for (adjacent in nextCandidate.adjacents(mapSize)) {
            if (adjacent in unvisited) {
                val adjacentRiskLevel = riskLevelGrid[adjacent.y][adjacent.x]
                val adjacentMinimumRiskLevel = minimumRiskLevelGrid[adjacent.y][adjacent.x]
                if (adjacentRiskLevel + parentRiskLevel < adjacentMinimumRiskLevel) {
                    minimumRiskLevelGrid[adjacent.y][adjacent.x] = adjacentRiskLevel + parentRiskLevel
                    parents[adjacent] = nextCandidate
                    visiting.offer(adjacent to adjacentRiskLevel + parentRiskLevel)
                }
            }
        }
        unvisited.remove(nextCandidate)
    }
    return minimumRiskLevelGrid
}

data class Point(val x: Int, val y: Int)

fun Point.adjacents(size: Int): Set<Point> {
    val points = mutableSetOf<Point>()
    if (this.x > 0) points.add(Point(this.x - 1, this.y))
    if (this.x < size) points.add(Point(this.x + 1, this.y))
    if (this.y > 0) points.add(Point(this.x, this.y - 1))
    if (this.y < size) points.add(Point(this.x, this.y + 1))
    return points
}