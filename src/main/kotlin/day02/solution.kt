package day02

import java.io.File

fun main() {
    val lines = File("src/main/kotlin/day02/input.txt").readLines()

    val submarineV1 = lines
        .fold(SubmarineV1()) { submarine, commandString ->
            submarine.execute(commandString.toCommand())
        }
    println(submarineV1.x * submarineV1.y)

    val submarineV2 = lines
        .fold(SubmarineV2()) { submarine, commandString ->
            submarine.execute(commandString.toCommand())
        }
    println(submarineV2.x * submarineV2.y)
}

sealed class Command
class Forward(val amount: Int): Command()
class Down(val amount: Int): Command()
class Up(val amount: Int): Command()

fun String.toCommand(): Command {
    val moveType = this.substringBefore(" ")
    val amount = this.substringAfter(" ").toInt()
    return when (moveType) {
        "forward" -> Forward(amount)
        "down" -> Down(amount)
        "up" -> Up(amount)
        else -> Forward(0)
    }
}

class SubmarineV1(var x: Int = 0, var y: Int = 0) {
    fun execute(command: Command): SubmarineV1 = when (command) {
        is Forward -> apply { x += command.amount }
        is Down -> apply { y += command.amount }
        is Up -> apply { y -= command.amount }
    }
}

class SubmarineV2(var x: Int = 0, var y: Int = 0, var aim: Int = 0) {
    fun execute(command: Command): SubmarineV2 = when (command) {
        is Forward -> apply { x += command.amount; y += aim * command.amount }
        is Down -> apply { aim += command.amount }
        is Up -> apply { aim -= command.amount }
    }
}


