package day04

import java.io.File

fun main() {
    var drawnNumbers: List<Int> = listOf()
    var bingoBoards: List<BingoBoard> = listOf()
    File("src/main/kotlin/day04/input.txt").useLines { seq ->
        val iterator = seq.iterator()
        drawnNumbers = List(1) { iterator.next() }.single().split(",").map { it.toInt() }

        bingoBoards = iterator.asSequence()
            .filter { it.isNotEmpty() }
            .chunked(5)
            .map { BingoBoard(it.map { it.split(" ").filter { it.isNotEmpty() }.map { it.toInt() } }.toList()) }
            .toList()

    }
    for (drawnNumber in drawnNumbers) {
        val indexOfWinningBoard = bingoBoards.indexOfFirst { it.isWinning(drawnNumber) }
        if (indexOfWinningBoard != -1) {
            println(drawnNumber * bingoBoards[indexOfWinningBoard].unmarkedNumbers().sum())
            break
        }
    }
    val notYetWonBoards = ArrayList(bingoBoards)
    val wonBoards = mutableListOf<BingoBoard>()
    for (drawnNumber in drawnNumbers) {
        val wonBoardsForDrawnNumber = notYetWonBoards.filter { it.isWinning(drawnNumber) }
        wonBoards.addAll(wonBoardsForDrawnNumber)
        notYetWonBoards.removeAll(wonBoardsForDrawnNumber)
        if (notYetWonBoards.isEmpty()) {
            println(drawnNumber * wonBoards.last().unmarkedNumbers().sum())
            break
        }
    }

}

class BingoBoard(private val board: List<List<Int>>) {
    private val markedNumbers: MutableList<Int> = mutableListOf()

    fun isWinning(drawnNumber: Int): Boolean {
        board.forEach {
            if (it.contains(drawnNumber)) {
                markedNumbers.add(drawnNumber)
            }
        }
        return checkBoard()
    }

    private fun checkBoard(): Boolean {
        return board.any {
            it.all { it in markedNumbers }
        } || (0..4).map { column ->
            board.map { it[column] }
        }.any {
            it.all { it in markedNumbers }
        }
    }

    fun unmarkedNumbers(): List<Int> = board.flatten().filter { it !in markedNumbers }
}