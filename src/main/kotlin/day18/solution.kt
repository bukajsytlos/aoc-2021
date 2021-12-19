package day18

import java.io.File

fun main() {
    val lines = File("src/main/kotlin/day18/input.txt").readLines()
    val snailFishNumbers = lines.map { parseSnailFish(it) }
    println(snailFishNumbers.reduce { acc, snailFishNumber -> acc.copy() + snailFishNumber.copy() }.magnitude())

    var highestMagnitude = 0

    for (number1 in snailFishNumbers) {
        for (number2 in snailFishNumbers) {
            val magnitude1 = (number1.copy() + number2.copy()).magnitude()
            if (magnitude1 > highestMagnitude) highestMagnitude = magnitude1
            val magnitude2 = (number2.copy() + number1.copy()).magnitude()
            if (magnitude2 > highestMagnitude) highestMagnitude = magnitude2
        }
    }

    println(highestMagnitude)
}

fun parseSnailFish(value: String): SnailFishNumber {
    if (!value.startsWith('[')) return Regular(value.toInt())
    val inside = value.removeSurrounding("[", "]")
    var nestLevel = 0
    val index = inside.indexOfFirst {
        if (it == '[') nestLevel++
        else if (it == ']') nestLevel--
        else if (it == ',' && nestLevel == 0) return@indexOfFirst true
        false
    }
    return Pair(parseSnailFish(inside.substring(0, index)), parseSnailFish(inside.substring(index + 1)))
        .also {
            it.childsParent(it)
        }
}

sealed class SnailFishNumber(var parent: Pair? = null) {

    operator fun plus(other: SnailFishNumber): SnailFishNumber = Pair(this, other).also { it.childsParent(it) }.reduce()

    abstract fun print()

    abstract fun magnitude(): Int

    fun copy(): SnailFishNumber {
        return when(this) {
            is Pair -> Pair(this.left.copy(), this.right.copy()).also { it.childsParent(it) }
            is Regular -> Regular(this.value)
        }
    }

    protected fun reduce(): SnailFishNumber {
        while (true) {
//            this.print()
//            println()
            val exploding = findExploding()
            if (exploding != null) {
                exploding.explode()
                continue
            }
            val splitting = findSplitting()
            if (splitting != null) {
                splitting.split()
                continue
            }
            break
        }
        return this
    }

    private fun findExploding(nestLevel: Int = 0): Pair? {
        if (this is Pair) {
            if (nestLevel == 4) return this
            return this.left.findExploding( nestLevel + 1) ?: this.right.findExploding(nestLevel + 1)
        }
        return null
    }

    private fun findSplitting(): Regular? {
        return when (this) {
            is Regular -> if (this.value >= 10) this else null
            is Pair -> this.left.findSplitting() ?: this.right.findSplitting()
        }
    }
}

class Regular(var value: Int, parent: Pair? = null) : SnailFishNumber(parent) {
    override fun magnitude(): Int = value

    override fun print() {
        print(value)
    }

    fun add(addition: Int) {
        value += addition
    }

    fun split() {
        val newValue = Pair(Regular(value / 2), Regular(value / 2 + value % 2), parent)
            .also {
                it.childsParent(it)
            }
        if (parent != null) {
            if (parent!!.left == this) {
                parent!!.left = newValue
            } else {
                parent!!.right = newValue
            }
        }
    }
}

class Pair(var left: SnailFishNumber, var right: SnailFishNumber, parent: Pair? = null) : SnailFishNumber(parent) {
    override fun magnitude(): Int = 3 * left.magnitude() + 2 * right.magnitude()
    override fun print() {
        print("[");left.print();print(",");right.print();print("]")
    }

    fun explode() {
        findClosestLeftRegular()?.add((left as Regular).value)
        findClosestRightRegular()?.add((right as Regular).value)
        if (parent != null) {
            val newValue = Regular(0, parent)
            if (parent!!.left == this) {
                parent!!.left = newValue
            } else {
                parent!!.right = newValue
            }
        }
    }

    private fun findClosestLeftRegular(): Regular? {
        return if (parent != null) {
            if (parent!!.left == this) {
                parent!!.findClosestLeftRegular()
            } else {
                when (parent!!.left) {
                    is Regular -> parent!!.left as Regular
                    is Pair -> {
                        return (parent!!.left as Pair).findRightMostChild()
                    }
                }
            }
        } else {
            null
        }
    }

    private fun findRightMostChild(): Regular = when (this.right) {
        is Regular -> this.right as Regular
        else -> (this.right as Pair).findRightMostChild()
    }

    private fun findClosestRightRegular(): Regular? {
        return if (parent != null) {
            if (parent!!.right == this) {
                parent!!.findClosestRightRegular()
            } else {
                when (parent!!.right) {
                    is Regular -> parent!!.right as Regular
                    is Pair -> {
                        return (parent!!.right as Pair).findLeftMostChild()
                    }
                }
            }
        } else {
            null
        }
    }

    private fun findLeftMostChild(): Regular = when (this.left) {
        is Regular -> this.left as Regular
        else -> (this.left as Pair).findLeftMostChild()
    }

    fun childsParent(parent: Pair?) {
        left.parent = parent
        right.parent = parent
    }
}

