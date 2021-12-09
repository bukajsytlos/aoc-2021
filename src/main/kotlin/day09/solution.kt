package day09

import java.io.File

private const val MAX_HEIGHT = 9

fun main() {
    val lines = File("src/main/kotlin/day09/input.txt").readLines()
    val mapSize = lines.size
    val heightMap: Array<IntArray> = Array(mapSize) { i -> lines[i].map { it.digitToInt() }.toIntArray() }
    var riskLevel = 0
    val basins = mutableListOf<Set<Point>>()
    for (row in heightMap.indices) {
        for (col in heightMap[row].indices) {
            val currHeight = heightMap[row][col]
            val isLowPoint = (
                    (row == 0 || heightMap[row - 1][col] > currHeight) &&
                            (row == mapSize - 1 || row < mapSize - 1 && heightMap[row + 1][col] > currHeight) &&
                            (col == 0 || heightMap[row][col - 1] > currHeight) &&
                            (col == mapSize - 1 || col < mapSize - 1 && heightMap[row][col + 1] > currHeight)
                    )
            if (isLowPoint) {
                riskLevel += currHeight + 1
                val lowPoint = Point(row, col)
                basins += scanBasinAroundPoint(lowPoint, heightMap, mutableSetOf())
            }
        }
    }
    println(riskLevel)
    println(basins.map { it.size }.sortedDescending().take(3).reduce(Int::times))
}

fun scanBasinAroundPoint(point: Point, heightMap: Array<IntArray>, basinPoints: MutableSet<Point>): Set<Point> {
    val heightMapSize = heightMap.size
    if (
        point.row < 0 || point.row >= heightMapSize
        || point.col < 0 || point.col >= heightMapSize
        || heightMap[point.row][point.col] == MAX_HEIGHT
        || point in basinPoints
    ) return emptySet()
    basinPoints.add(point)
    scanBasinAroundPoint(Point(point.row - 1, point.col), heightMap, basinPoints)
    scanBasinAroundPoint(Point(point.row + 1, point.col), heightMap, basinPoints)
    scanBasinAroundPoint(Point(point.row, point.col - 1), heightMap, basinPoints)
    scanBasinAroundPoint(Point(point.row, point.col + 1), heightMap, basinPoints)
    return basinPoints
}

data class Point(val row: Int, val col: Int)