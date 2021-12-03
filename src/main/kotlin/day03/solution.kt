package day03

import java.io.File

fun main() {
    val lines = File("src/main/kotlin/day03/input.txt").readLines()
    val binaryCodeCounts = IntArray(12) { 0 }
    val reportDatBitsHistogram = lines
        .map { it.toCharArray() }
        .fold(binaryCodeCounts) { counter, reportLine ->
            for (i in counter.indices) {
                counter[i] += reportLine[i].digitToInt()
            }
            counter
        }
    val gammaRate = reportDatBitsHistogram
        .map { if (it > lines.size / 2) 0 else 1 }
        .joinToString(separator = "")
        .toCharArray()
        .binaryToDecimal()
    val epsilonRate = reportDatBitsHistogram
        .map { if (it > lines.size / 2) 1 else 0 }
        .joinToString(separator = "")
        .toCharArray()
        .binaryToDecimal()

    println(gammaRate) //1519
    println(epsilonRate) //2576
    println(gammaRate * epsilonRate)

    //Part 2
    val diagnosticReportLines = lines
        .map { it.toCharArray() }

    val oxygenGeneratorRating = filterOxygenGeneratorRating(diagnosticReportLines, 0)[0].binaryToDecimal()
    println(oxygenGeneratorRating) //3597
    val co2ScrubberRating = filterCO2ScrubberRating(diagnosticReportLines, 0)[0].binaryToDecimal()
    println(co2ScrubberRating) //1389
    println(oxygenGeneratorRating * co2ScrubberRating)
}

fun filterOxygenGeneratorRating(reportData: List<CharArray>, reportIndex: Int): List<CharArray> =
    filterDiagnosticReport(
        reportData,
        reportIndex
    ) { filterSize, significantBit, reportSize -> (filterSize + if (reportSize % 2 == 0 && significantBit != null && significantBit == 1) 1 else 0) > reportSize / 2 }

fun filterCO2ScrubberRating(reportData: List<CharArray>, reportIndex: Int): List<CharArray> =
    filterDiagnosticReport(
        reportData,
        reportIndex
    ) { filterSize, significantBit, reportSize -> (filterSize - if (reportSize % 2 == 0 && significantBit != null && significantBit == 0) 1 else 0) < (reportSize + 1) / 2 }

fun filterDiagnosticReport(
    reportData: List<CharArray>,
    reportIndex: Int,
    evaluationPredicate: (Int, Int?, Int) -> Boolean
): List<CharArray> {
    if (reportIndex == reportData[0].size || reportData.size == 1) {
        return reportData
    }
    val filteredReportData = reportData
        .partition { it[reportIndex] == '1' }
        .toList()
        .first {
            evaluationPredicate.invoke(
                it.size,
                if (it.isNotEmpty()) it[0][reportIndex].digitToInt() else null,
                reportData.size
            )
        }
    return filterDiagnosticReport(filteredReportData, reportIndex + 1, evaluationPredicate)
}

fun CharArray.binaryToDecimal(): Int {
    var value = 0
    for (char in this) {
        value = value shl 1
        value += char - '0'
    }
    return value
}