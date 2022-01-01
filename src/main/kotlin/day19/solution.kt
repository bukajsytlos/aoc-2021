package day19

import java.io.File
import kotlin.math.abs

fun main() {
    val unidentifiedScanners = mutableListOf<UnidentifiedScanner>()
    File("src/main/kotlin/day19/input.txt").useLines { linesSeq ->
        val linesIterator = linesSeq.iterator()
        while (linesIterator.hasNext()) {
            val scannerId = linesIterator.next().substringAfter("scanner ").substringBefore(" ---")
            val scannerBeacons = mutableListOf<Vector3D>()
            while (linesIterator.hasNext()) {
                val line = linesIterator.next()
                if (line.isEmpty()) break
                val (x, y, z) = line.split(",")
                scannerBeacons.add(Vector3D.from(end = Point3D(x.toInt(), y.toInt(), z.toInt())))
            }
            unidentifiedScanners.add(UnidentifiedScanner(scannerId, scannerBeacons))
        }
    }
    val identifiedScanners = mutableSetOf<IdentifiedScanner>()
    var baseScanner = unidentifiedScanners.removeAt(0).run { IdentifiedScanner(id, Point3D.CENTER, beaconVectors) }
    identifiedScanners.add(baseScanner)

    val testedScanners = mutableListOf(baseScanner)
    while (unidentifiedScanners.isNotEmpty()) {
        val baseScannerInterBeaconVectors = baseScanner.beaconVectors.eachVector()
        val unidentifiedScannersIterator = unidentifiedScanners.iterator()
        for (unidentifiedScanner in unidentifiedScannersIterator) {
            val unidentifiedScannerInterBeaconsVectors = unidentifiedScanner.beaconVectors.eachVector()
            val commonDistances = baseScannerInterBeaconVectors.keys
                    .map { it.squaredLength() }
                    .intersect(unidentifiedScannerInterBeaconsVectors.keys.map { it.squaredLength() }.toSet())
            //scanner have at least 12 common beacons (12 inter beacon combinations == 66 (12 * 11 / 2))
            if (commonDistances.size >= 66) {
                val commonDistance = commonDistances.first()
                val identifiedBeaconsForCommonDistance = baseScannerInterBeaconVectors
                    .filter { it.key.squaredLength() == commonDistance }
                    .entries.first()
                val unidentifiedBeaconsForCommonDistance = unidentifiedScannerInterBeaconsVectors
                    .filter { it.key.squaredLength() == commonDistance }
                val scannerTransformation = TRANSFORMATIONS.first { transformation ->
                    unidentifiedBeaconsForCommonDistance
                        .map { it.key.transform(transformation) }
                        .any { it == identifiedBeaconsForCommonDistance.key }
                }
                val unidentifiedBeaconVector = unidentifiedBeaconsForCommonDistance.entries.first {
                    it.key.transform(scannerTransformation) == identifiedBeaconsForCommonDistance.key
                }.value.first
                val identifiedBeaconVector = identifiedBeaconsForCommonDistance.value.first
                val transformedBeaconVector = unidentifiedBeaconVector.transform(scannerTransformation)
                val scannerPositionShift = identifiedBeaconVector - transformedBeaconVector
                val identifiedScanner = IdentifiedScanner(
                    unidentifiedScanner.id,
                    baseScanner.position + scannerPositionShift,
                    unidentifiedScanner.beaconVectors.map { it.transform(scannerTransformation) }
                )
                identifiedScanners.add(identifiedScanner)
                unidentifiedScannersIterator.remove()
            }
        }
        testedScanners.add(baseScanner)
        baseScanner = identifiedScanners.first { it !in testedScanners }
    }
    println(identifiedScanners.flatMap { scanner -> scanner.beaconVectors.map { scanner.position + it } }.toSet().size)

    println(identifiedScanners.flatMap { identifiedScanner1 ->
        identifiedScanners.map { identifiedScanner2 ->
            identifiedScanner1.position.manhattanDistance(identifiedScanner2.position)
        }
    }.maxOrNull())
}

fun List<Vector3D>.eachVector(): Map<Vector3D, Pair<Vector3D, Vector3D>> = buildMap {
    this@eachVector.forEach { v1 ->
        this@eachVector.forEach { v2 ->
            if (v1 != v2) {
                put(v2 - v1, v1 to v2)
            }
        }
    }
}

data class UnidentifiedScanner(val id: String, val beaconVectors: List<Vector3D>)
data class IdentifiedScanner(val id: String, val position: Point3D, val beaconVectors: List<Vector3D>)

data class Vector3D(val dx: Int, val dy: Int, val dz: Int) {
    operator fun plus(other: Vector3D) = Vector3D(dx + other.dx, dy + other.dy, dz + other.dz)
    operator fun minus(other: Vector3D) = Vector3D(dx - other.dx, dy - other.dy, dz - other.dz)

    fun squaredLength(): Int = dx * dx + dy * dy + dz * dz

    fun transform(matrix: Matrix3D): Vector3D = Vector3D(
        dx * matrix.m00 + dy * matrix.m01 + dz * matrix.m02,
        dx * matrix.m10 + dy * matrix.m11 + dz * matrix.m12,
        dx * matrix.m20 + dy * matrix.m21 + dz * matrix.m22
    )

    companion object {
        fun from(start: Point3D = Point3D.CENTER, end: Point3D): Vector3D = Vector3D(end.x - start.x, end.y - start.y, end.z - start.z)
    }
}

data class Matrix3D(
    val m00: Int, val m01: Int, val m02: Int,
    val m10: Int, val m11: Int, val m12: Int,
    val m20: Int, val m21: Int, val m22: Int
)

data class Point3D(val x: Int, val y: Int, val z: Int) {
    operator fun plus(vector: Vector3D) = Point3D(x + vector.dx, y + vector.dy, z + vector.dz)
    operator fun minus(vector: Vector3D) = Point3D(x - vector.dx, y - vector.dy, z - vector.dz)

    fun manhattanDistance(other: Point3D) = abs(x - other.x) + abs(y - other.y) + abs(z - other.z)

    companion object {
        val CENTER = Point3D(0, 0, 0)
    }
}

val TRANSFORMATIONS: List<Matrix3D> = listOf(
    Matrix3D(
        1, 0, 0,
        0, 1, 0,
        0, 0, 1
    ),
    Matrix3D(
        1, 0, 0,
        0, 0, -1,
        0, 1, 0
    ),
    Matrix3D(
        1, 0, 0,
        0, -1, 0,
        0, 0, -1
    ),
    Matrix3D(
        1, 0, 0,
        0, 0, 1,
        0, -1, 0
    ),
    Matrix3D(
        -1, 0, 0,
        0, -1, 0,
        0, 0, 1
    ),
    Matrix3D(
        -1, 0, 0,
        0, 0, -1,
        0, -1, 0
    ),
    Matrix3D(
        -1, 0, 0,
        0, 1, 0,
        0, 0, -1
    ),
    Matrix3D(
        -1, 0, 0,
        0, 0, 1,
        0, 1, 0
    ),
    Matrix3D(
        0, -1, 0,
        1, 0, 0,
        0, 0, 1
    ),
    Matrix3D(
        0, 0, 1,
        1, 0, 0,
        0, 1, 0
    ),
    Matrix3D(
        0, 1, 0,
        1, 0, 0,
        0, 0, -1
    ),
    Matrix3D(
        0, 0, -1,
        1, 0, 0,
        0, -1, 0
    ),
    Matrix3D(
        0, 1, 0,
        -1, 0, 0,
        0, 0, 1
    ),
    Matrix3D(
        0, 0, 1,
        -1, 0, 0,
        0, -1, 0
    ),
    Matrix3D(
        0, -1, 0,
        -1, 0, 0,
        0, 0, -1
    ),
    Matrix3D(
        0, 0, -1,
        -1, 0, 0,
        0, 1, 0
    ),
    Matrix3D(
        0, 0, -1,
        0, 1, 0,
        1, 0, 0
    ),
    Matrix3D(
        0, 1, 0,
        0, 0, 1,
        1, 0, 0
    ),
    Matrix3D(
        0, 0, 1,
        0, -1, 0,
        1, 0, 0
    ),
    Matrix3D(
        0, -1, 0,
        0, 0, -1,
        1, 0, 0
    ),
    Matrix3D(
        0, 0, -1,
        0, -1, 0,
        -1, 0, 0
    ),
    Matrix3D(
        0, -1, 0,
        0, 0, 1,
        -1, 0, 0
    ),
    Matrix3D(
        0, 0, 1,
        0, 1, 0,
        -1, 0, 0
    ),
    Matrix3D(
        0, 1, 0,
        0, 0, -1,
        -1, 0, 0
    ),
)