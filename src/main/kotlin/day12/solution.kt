package day12

import java.io.File


fun main() {
    val lines = File("src/main/kotlin/day12/input.txt").readLines()
    val cavesByName: MutableMap<String, Cave> = mutableMapOf()
    lines.forEach {
        val fromCaveName = it.substringBefore("-")
        val toCaveName = it.substringAfter("-")
        val fromCave = cavesByName.computeIfAbsent(fromCaveName) { Cave(fromCaveName) }
        val toCave = cavesByName.computeIfAbsent(toCaveName) { Cave(toCaveName) }
        fromCave.addConnection(toCave)
    }
    val startCave = cavesByName.getValue("start")
    val endCave = cavesByName.getValue("end")
    println(
        search(endCave, listOf(startCave)) { cave, visitedCavesPath -> cave.isLarge() || cave !in visitedCavesPath }
        .count()
    )
    println(
        search(endCave, listOf(startCave)) { cave, visitedCavesPath -> cave.isLarge() || cave !in visitedCavesPath
                || cave.name != "start" && visitedCavesPath.filter { !it.isLarge() }
                    .groupBy { it }
                    .mapValues { it.value.size }
                    .let { map -> map.isNotEmpty() && map.all { it.value == 1 } } }
            .count()
    )
}

fun search(toCave: Cave, visitedCavesPath: List<Cave>, visitStrategy: (Cave, List<Cave>) -> Boolean): Set<List<Cave>> {
    val lastVisited = visitedCavesPath.last()
    if (lastVisited == toCave) return mutableSetOf(visitedCavesPath)
    return lastVisited.connectsTo
        .filter { visitStrategy(it, visitedCavesPath) }
        .map { search(toCave, visitedCavesPath + it, visitStrategy) }
        .flatten().toSet()
}

data class Cave(val name: String) {
    val connectsTo: MutableSet<Cave> = mutableSetOf()
    fun addConnection(other: Cave) {
        if (other !in connectsTo) {
            connectsTo.add(other)
            other.addConnection(this)
        }
    }
    fun isLarge() = name.all { it.isUpperCase() }

    override fun toString(): String {
        return name
    }
}