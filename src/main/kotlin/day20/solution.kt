package day20

import java.io.File

fun main() {
    val lines = File("src/main/kotlin/day20/input.txt").readLines()
    val imageEnhancementAlgorithm: BooleanArray = lines[0].map { it == '#' }.toBooleanArray()
    val imageYSize = lines.size + 2
    val imageXSize = lines[2].length + 4
    val image = InfiniteImage(
        Array(imageYSize) { i ->
            if (i in (0..1) || i in (imageYSize - 2 until imageYSize)) {
                BooleanArray(imageXSize)
            } else {
                (".." + lines[i] + "..").map { it == '#' }.toBooleanArray()
            }
        },
        false
    )
    val enhancedImage = (0..1).fold(image) { acc, _ ->
        enhanceImage(acc, imageEnhancementAlgorithm)
    }
    println(enhancedImage.innerData.sumOf { imageLine -> imageLine.count { it } })

    val enhancedImage2 = (0..49).fold(image) { acc, _ ->
        enhanceImage(acc, imageEnhancementAlgorithm)
    }
    println(enhancedImage2.innerData.sumOf { imageLine -> imageLine.count { it } })
}

fun enhanceImage(image: InfiniteImage, imageEnhancementAlgorithm: BooleanArray): InfiniteImage {
    val imageYResolution = image.innerData.size + 2
    val imageXResolution = image.innerData[0].size + 2
    val newOuterPixelValue = image.outerPixelValue.not()
    val newInnerData = Array(imageYResolution) { y ->
        if (y in (0..1) || y in (imageYResolution - 2 until imageYResolution)) {
            BooleanArray(imageXResolution) { newOuterPixelValue }
        } else {
            BooleanArray(imageXResolution) { x ->
                if (x in (0..1) || x in (imageXResolution - 2 until imageXResolution)) {
                    newOuterPixelValue
                } else {
                    val pixelValueArray = BooleanArray(9)
                    pixelValueArray[0] = image.innerData[y - 2][x - 2]
                    pixelValueArray[1] = image.innerData[y - 2][x - 1]
                    pixelValueArray[2] = image.innerData[y - 2][x]
                    pixelValueArray[3] = image.innerData[y - 1][x - 2]
                    pixelValueArray[4] = image.innerData[y - 1][x - 1]
                    pixelValueArray[5] = image.innerData[y - 1][x]
                    pixelValueArray[6] = image.innerData[y][x - 2]
                    pixelValueArray[7] = image.innerData[y][x - 1]
                    pixelValueArray[8] = image.innerData[y][x]
                    imageEnhancementAlgorithm[pixelValueArray.toInt()]
                }
            }
        }
    }
    return InfiniteImage(
        newInnerData,
        newOuterPixelValue
    )
}

fun BooleanArray.toInt(): Int = this.map { if (it) 1 else 0 }.joinToString("").toInt(2)

data class InfiniteImage(val innerData: Array<BooleanArray>, val outerPixelValue: Boolean)