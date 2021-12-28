package day25

import java.io.File

typealias SeaFloor = Array<Array<SeaCucumberType?>>

fun main() {
    val lines = File("src/main/kotlin/day25/input.txt").readLines()

    val seaFloor: SeaFloor =
        SeaFloor(lines.size) { i -> lines[i].map { SeaCucumberType.fromSymbol(it) }.toTypedArray() }
    var iterationCount = 0
    var tmpState = seaFloor.copyOf()
    do {
//        tmpState.print()
        val (newState, numberOfMoves) = evolve(tmpState)
        tmpState = newState
        iterationCount++
    } while (numberOfMoves != 0)
    println(iterationCount)
}

fun evolve(seaFloor: SeaFloor): Pair<SeaFloor, Int> {
    val seaFloorHeight = seaFloor.size
    val seaFloorWidth = seaFloor[0].size
    var movesCount = 0
    val newState = SeaFloor(seaFloorHeight) { Array(seaFloorWidth) { null } }

    for (row in seaFloor.indices) {
        for (col in seaFloor[row].indices) {
            val seaCucumberType = seaFloor[row][col]
            if (seaCucumberType == SeaCucumberType.EAST) {
                if (seaFloor[row][(col + 1) % seaFloorWidth] == null) {
                    newState[row][(col + 1) % seaFloorWidth] = seaCucumberType
                    movesCount++
                } else {
                    newState[row][col] = seaCucumberType
                }
            }
        }
    }

    for (col in 0 until seaFloorWidth) {
        for (row in 0 until seaFloorHeight) {
            val seaCucumberType = seaFloor[row][col]
            if (seaCucumberType == SeaCucumberType.SOUTH) {
                if (seaFloor[(row + 1) % seaFloorHeight][col] == SeaCucumberType.SOUTH || newState[(row + 1) % seaFloorHeight][col] != null) {
                    newState[row][col] = seaCucumberType
                } else {
                    newState[(row + 1) % seaFloorHeight][col] = seaCucumberType
                    movesCount++
                }
            }
        }
    }
    return newState to movesCount
}
fun SeaFloor.print() {
    for (row in indices) {
        for (col in get(row).indices) {
            print(get(row).get(col)?.symbol ?: '.')
        }
        println()
    }
    println("========================")
}

enum class SeaCucumberType(val symbol: Char) {
    EAST('>'),
    SOUTH('v'),
    ;

    companion object {
        fun fromSymbol(symbol: Char) = values().firstOrNull { it.symbol == symbol }
    }
}