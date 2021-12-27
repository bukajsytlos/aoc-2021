package day23

import java.util.*
import kotlin.math.abs

fun main() {
    val initBurrowState = BurrowState(
        listOf(
            Amphipod(Spot(2, 1), Amphipod.Type.AMBER),
            Amphipod(Spot(2, 2), Amphipod.Type.DESERT),
            Amphipod(Spot(2, 3), Amphipod.Type.DESERT),
            Amphipod(Spot(2, 4), Amphipod.Type.BRONZE),
            Amphipod(Spot(4, 1), Amphipod.Type.DESERT),
            Amphipod(Spot(4, 2), Amphipod.Type.COPPER),
            Amphipod(Spot(4, 3), Amphipod.Type.BRONZE),
            Amphipod(Spot(4, 4), Amphipod.Type.COPPER),
            Amphipod(Spot(6, 1), Amphipod.Type.BRONZE),
            Amphipod(Spot(6, 2), Amphipod.Type.BRONZE),
            Amphipod(Spot(6, 3), Amphipod.Type.AMBER),
            Amphipod(Spot(6, 4), Amphipod.Type.DESERT),
            Amphipod(Spot(8, 1), Amphipod.Type.COPPER),
            Amphipod(Spot(8, 2), Amphipod.Type.AMBER),
            Amphipod(Spot(8, 3), Amphipod.Type.COPPER),
            Amphipod(Spot(8, 4), Amphipod.Type.AMBER),
        ),
        0,
        4,
        null
    )

    val candidateStates = PriorityQueue<BurrowState> { s1, s2 -> s1.spentEnergy - s2.spentEnergy }
    candidateStates.add(initBurrowState)
    val orderedBurrowStates = mutableListOf<BurrowState>()
    val seenStates = HashSet<List<Amphipod>>()
    while (candidateStates.isNotEmpty()) {
        val burrowState = candidateStates.poll()
        when {
            burrowState.amphipods in seenStates -> continue
            burrowState.isOrganized() -> {
                orderedBurrowStates.add(burrowState)
                break
            }
            else -> {
                seenStates += burrowState.amphipods
                candidateStates.addAll(burrowState.getPossibleStates())
            }
        }
    }

    val orderedState = orderedBurrowStates[0]
    var tmpState: BurrowState? = orderedState
    while (tmpState != null) {
        tmpState.print()
        tmpState = tmpState.previousState
    }
    println(orderedState.spentEnergy)
}

val HALLWAY = listOf(Spot(0, 0), Spot(1, 0), Spot(3, 0), Spot(5, 0), Spot(7, 0), Spot(9, 0), Spot(10, 0))

data class BurrowState(
    val amphipods: List<Amphipod>,
    val spentEnergy: Int,
    val spotsPerRoom: Int,
    val previousState: BurrowState?
) {
    fun isOrganized(): Boolean = amphipods.all { it.spot.x == it.type.roomPositionX }

    fun getPossibleStates(): List<BurrowState> = amphipods.flatMap { a ->
        val roomMates = amphipods.filter { it.spot.x == a.spot.x }
        when {
            a.canStayInRoomWith(roomMates) -> emptyList()
            a.canMoveOutOfRoomWith(roomMates) -> HALLWAY.filter { canMoveThroughHallway(a, it) }.map { move(a, it) }
            a.canMoveToRoomWith(amphipods.filter { it.spot.x == a.type.roomPositionX }) -> {
                val availableSpotInRoom = getAvailableSpotInRoom(a.type.roomPositionX)
                if (availableSpotInRoom != null && canMoveThroughHallway(a, availableSpotInRoom)) listOf(move(a, availableSpotInRoom))
                else emptyList()
            }
            else -> emptyList()
        }
    }

    private fun getAvailableSpotInRoom(roomPositionX: Int): Spot? {
        val amphipodsInRoom = amphipods.count { it.spot.x == roomPositionX }
        return if (amphipodsInRoom < spotsPerRoom) Spot(roomPositionX, spotsPerRoom - amphipodsInRoom) else null
    }

    private fun canMoveThroughHallway(amphipod: Amphipod, to: Spot): Boolean {
        val hallwayRange = if (amphipod.spot.x < to.x) amphipod.spot.x + 1 .. to.x else amphipod.spot.x - 1 downTo to.x
        return amphipods.none { it.spot.y == 0 && it.spot.x in hallwayRange }
    }

    private fun move(amphipod: Amphipod, toSpot: Spot): BurrowState {
        val amphipodsCopy = amphipods.toMutableList()
        amphipodsCopy[amphipodsCopy.indexOf(amphipod)] = amphipod.copy(spot = toSpot)
        return BurrowState(
            amphipodsCopy,
            spentEnergy + amphipod.type.energyPerStep * amphipod.spot.distanceTo(toSpot),
            spotsPerRoom,
            this
        )
    }
}

fun BurrowState.print() {
    val screen = Array(5) { CharArray(11) { '.' } }
    amphipods.forEach { screen[it.spot.y][it.spot.x] = it.type.id }
    for (line in screen) {
        for (char in line) {
            print(char)
        }
        println()
    }
    println("===========")
}

data class Amphipod(var spot: Spot, val type: Type) {
    fun canStayInRoomWith(others: List<Amphipod>) = inCorrectRoom() && others.all { it.inCorrectRoom() }

    private fun inCorrectRoom() = spot.x == type.roomPositionX

    fun canMoveToRoomWith(others: List<Amphipod>): Boolean = spot.y == 0 && others.all { it.inCorrectRoom() && it.type == type}

    fun canMoveOutOfRoomWith(others: List<Amphipod>): Boolean = spot.y > 0 && others.none { spot.x == it.spot.x && it.spot.y < spot.y}

    enum class Type(val id: Char, val energyPerStep: Int, val roomPositionX: Int) {
        AMBER('A', 1, 2),
        BRONZE('B', 10, 4),
        COPPER('C', 100, 6),
        DESERT('D', 1000, 8),
    }
}

data class Spot(val x: Int, val y: Int) {
    fun distanceTo(spot: Spot): Int = abs(x - spot.x) + abs(y - spot.y)
}