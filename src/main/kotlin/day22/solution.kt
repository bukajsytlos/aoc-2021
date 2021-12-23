package day22

import java.io.File

fun main() {
    val inputLineRegex = """(on|off) x=([-]?\d+)..([-]?\d+),y=([-]?\d+)..([-]?\d+),z=([-]?\d+)..([-]?\d+)""".toRegex()
    val lines = File("src/main/kotlin/day22/input.txt").readLines()
    val rebootSteps = lines.map { line ->
        val (state, x0, x1, y0, y1, z0, z1) =
            inputLineRegex.matchEntire(line)?.destructured ?: throw IllegalArgumentException("Invalid input")
        RebootStep(state == "on", Cuboid(x0.toInt()..x1.toInt(), y0.toInt()..y1.toInt(), z0.toInt()..z1.toInt()))
    }
    val initializingRebootSteps = rebootSteps.filter {
        it.cube.x.first >= -50 && it.cube.x.last <= 50 &&
        it.cube.y.first >= -50 && it.cube.y.last <= 50 &&
        it.cube.z.first >= -50 && it.cube.z.last <= 50
    }

    println(initializingRebootSteps.calculateTurnedOn())
    println(rebootSteps.calculateTurnedOn())
}

fun List<RebootStep>.calculateTurnedOn(): Long {
    val xCutPoints = flatMap { listOf(it.cube.x.first, it.cube.x.last + 1) }.distinct().sorted()
    val yCutPoints = flatMap { listOf(it.cube.y.first, it.cube.y.last + 1) }.distinct().sorted()
    val zCutPoints = flatMap { listOf(it.cube.z.first, it.cube.z.last + 1) }.distinct().sorted()

    val space = Array(xCutPoints.size - 1) { Array(yCutPoints.size - 1) { BooleanArray(zCutPoints.size - 1) } }

    //compress
    val xCompressedCutPoints = xCutPoints.withIndex().associateBy({ it.value }, { it.index })
    val yCompressedCutPoints = yCutPoints.withIndex().associateBy({ it.value }, { it.index })
    val zCompressedCutPoints = zCutPoints.withIndex().associateBy({ it.value }, { it.index })

    for (rebootStep in this) {
        val cube = rebootStep.cube
        for (x in xCompressedCutPoints[cube.x.first]!! until xCompressedCutPoints[cube.x.last + 1]!!) {
            for (y in yCompressedCutPoints[cube.y.first]!! until yCompressedCutPoints[cube.y.last + 1]!!) {
                for (z in zCompressedCutPoints[cube.z.first]!! until zCompressedCutPoints[cube.z.last + 1]!!) {
                    space[x][y][z] = rebootStep.turnedOn
                }
            }
        }
    }
    //decompress and count
    var count = 0L
    for (x in space.indices) {
        for (y in space[x].indices) {
            for (z in space[x][y].indices) {
                if (space[x][y][z]) count += 1L * (xCutPoints[x + 1] - xCutPoints[x]) * (yCutPoints[y + 1] - yCutPoints[y]) * (zCutPoints[z + 1] - zCutPoints[z])
            }
        }
    }
    return count
}

data class RebootStep(val turnedOn: Boolean, val cube: Cuboid)
data class Cuboid(val x: IntRange, val y: IntRange, val z: IntRange)