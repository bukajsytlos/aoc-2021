package day11

import java.io.File


fun main() {
    val lines = File("src/main/kotlin/day11/input.txt").readLines()
    val mapSize = lines.size
    val energyLevelsMap: Array<IntArray> = Array(mapSize) { i -> lines[i].map { it.digitToInt() }.toIntArray() }
    val dumboOctopuses: MutableMap<Coordinate, DumboOctopus> = mutableMapOf()
    for (y in energyLevelsMap.indices) {
        for (x in energyLevelsMap[y].indices) {
            val coordinate = Coordinate(x, y)
            val dumboOctopus = DumboOctopus(coordinate, energyLevelsMap[y][x])
            dumboOctopuses[coordinate] = dumboOctopus
            if (y > 0) dumboOctopuses.getValue(Coordinate(x, y - 1)).addAdjacent(dumboOctopus)
            if (x > 0) dumboOctopuses.getValue(Coordinate(x - 1, y)).addAdjacent(dumboOctopus)
            if (x > 0 && y > 0) dumboOctopuses.getValue(Coordinate(x - 1, y - 1)).addAdjacent(dumboOctopus)
            if (x < mapSize - 1 && y > 0) dumboOctopuses.getValue(Coordinate(x + 1, y - 1)).addAdjacent(dumboOctopus)
        }
    }
    var flashCount = 0
    for (step in 1..100) {
        dumboOctopuses.values.forEach { it.increaseChargeLevel() }
        flashCount += dumboOctopuses.values.count { it.flash() }
    }
    println(flashCount)

    var stepAllFlashed = 100
    var allFlashed = false
    while(!allFlashed) {
        stepAllFlashed++
        dumboOctopuses.values.forEach { it.increaseChargeLevel() }
        allFlashed = dumboOctopuses.values.count { it.flash() } == dumboOctopuses.size
    }
    println(stepAllFlashed)
}

class DumboOctopus(val coordinate: Coordinate, var energyLevel: Int) {
    private val adjacents: MutableSet<DumboOctopus> = mutableSetOf()

    fun increaseChargeLevel() {
        energyLevel++
        if (energyLevel == FLASH_ENERGY_LEVEL) {
            adjacents.forEach { it.increaseChargeLevel() }
        }
    }

    fun addAdjacent(adjacent: DumboOctopus) {
        if (adjacent !in adjacents) {
            adjacents.add(adjacent)
            adjacent.addAdjacent(this)
        }
    }

    fun flash(): Boolean {
        val charged = energyLevel >= FLASH_ENERGY_LEVEL
        if (charged) energyLevel = 0
        return charged
    }

    companion object {
        const val FLASH_ENERGY_LEVEL = 10
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DumboOctopus

        if (coordinate != other.coordinate) return false

        return true
    }

    override fun hashCode(): Int {
        return coordinate.hashCode()
    }

    override fun toString(): String {
        return "DumboOctopus(coordinate=$coordinate, chargeLevel=$energyLevel)"
    }

}

data class Coordinate(val x: Int, val y: Int)