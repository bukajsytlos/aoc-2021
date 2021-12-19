package day16

import java.io.File


fun main() {
    val lines = File("src/main/kotlin/day16/input.txt").readLines()
    val bitsIterator =
        lines.first().map { hexToBinary(it) }.joinToString(separator = "").iterator()
    val packet = Packet.parse(bitsIterator)

    println(packet.versionAggregate())

    println(packet.evaluate())
}


sealed class Packet(val version: Int) {

    abstract fun versionAggregate(): Int
    abstract fun evaluate(): Long

    class Literal(version: Int, val value: Long) : Packet(version) {
        override fun versionAggregate() = version
        override fun evaluate(): Long = value

        companion object {
            fun parse(version: Int, iterator: CharIterator): Literal {
                var lastBitChunk = false
                var numberBits = ""
                while (!lastBitChunk) {
                    lastBitChunk = iterator.take(1) == "0"
                    numberBits += iterator.take(4)
                }
                return Literal(version, numberBits.toLong(2))
            }
        }
    }

    class Operator(version: Int, val type: Int, val subPackets: List<Packet>) : Packet(version) {
        override fun versionAggregate(): Int = version + subPackets.sumOf { it.versionAggregate() }
        override fun evaluate(): Long = when (type) {
            0 -> subPackets.sumOf { it.evaluate() }
            1 -> subPackets.fold(1L) { acc, packet -> acc * packet.evaluate() }
            2 -> subPackets.minOf { it.evaluate() }
            3 -> subPackets.maxOf { it.evaluate() }
            5 -> if (subPackets[0].evaluate() > subPackets[1].evaluate()) 1 else 0
            6 -> if (subPackets[0].evaluate() < subPackets[1].evaluate()) 1 else 0
            7 -> if (subPackets[0].evaluate() == subPackets[1].evaluate()) 1 else 0
            else -> 0L
        }

        companion object {
            fun parse(version: Int, type: Int, iterator: CharIterator): Operator {
                val lengthType = iterator.take(1)
                return when (lengthType) {
                    "0" -> {
                        val numberOfBits = iterator.take(15).toInt(2)
                        val subPacketsIterator = iterator.take(numberOfBits).iterator()
                        val packets = mutableListOf<Packet>()
                        while (subPacketsIterator.hasNext()) {
                            packets.add(parse(subPacketsIterator))
                        }
                        Operator(version, type, packets)
                    }
                    else -> {
                        val numberOfPackets = iterator.take(11).toInt(2)
                        val packets = (1..numberOfPackets).map { parse(iterator) }
                        Operator(version, type, packets)
                    }
                }
            }
        }
    }

    companion object {
        fun parse(iterator: CharIterator): Packet {
            val version = iterator.take(3).toInt(2)
            val type = iterator.take(3).toInt(2)
            return when (type) {
                4 -> Literal.parse(version, iterator)
                else -> Operator.parse(version, type, iterator)
            }
        }
    }
}

fun CharIterator.take(n: Int): String = this.asSequence().take(n).joinToString("")

fun hexToBinary(hex: Char): String {
    return when (hex) {
        '0' -> "0000"
        '1' -> "0001"
        '2' -> "0010"
        '3' -> "0011"
        '4' -> "0100"
        '5' -> "0101"
        '6' -> "0110"
        '7' -> "0111"
        '8' -> "1000"
        '9' -> "1001"
        'A' -> "1010"
        'B' -> "1011"
        'C' -> "1100"
        'D' -> "1101"
        'E' -> "1110"
        'F' -> "1111"
        else -> ""
    }
}