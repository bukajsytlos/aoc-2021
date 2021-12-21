package day21

fun main() {
    val player1 = Player(4)
    val player2 = Player(3)
    val die = DeterministicDie()
    var roundNumber = 0
    var losingPlayer: Player

    while (player1.score < 1000 && player2.score < 1000) {
        if (roundNumber % 2 == 0) {
            val score = die.roll() + die.roll() + die.roll()
            player1.addScore(score)
        } else {
            val score = die.roll() + die.roll() + die.roll()
            player2.addScore(score)
        }
        roundNumber++
    }
    losingPlayer = if (player1.score >= 1000) {
        player2
    } else {
        player1
    }
    println(losingPlayer.score * die.numberOfRolls)

    val (player1WonUniverses, player2WonUniverses) = GameRound.thrownValueToNumberOfUniverses.keys
        .map {
            GameRound(
                true,
                it,
                4,
                0,
                3,
                0,
                1
            ).playRound()
        }
        .reduce { p1, p2 -> p1.first + p2.first to p1.second + p2.second }
    println(if (player1WonUniverses > player2WonUniverses) player1WonUniverses else player2WonUniverses)
}

data class GameRound(
    val playsFirstPlayer: Boolean,
    val thrownValue: Int,
    val player1Position: Int,
    val player1Score: Int,
    val player2Position: Int,
    val player2Score: Int,
    val numberOfUniverses: Long
) {
    fun playRound(): Pair<Long, Long> {
        val newPlayer1Position: Int
        val newPlayer1Score: Int
        val newPlayer2Position: Int
        val newPlayer2Score: Int
        val newNumberOfUniverses: Long
        if (playsFirstPlayer) {
            newPlayer1Position = (player1Position + thrownValue - 1) % 10 + 1
            newPlayer1Score = player1Score + newPlayer1Position
            newNumberOfUniverses = numberOfUniverses * thrownValueToNumberOfUniverses.getValue(thrownValue)
            newPlayer2Position = player2Position
            newPlayer2Score = player2Score
            if (newPlayer1Score >= 21) return newNumberOfUniverses to 0L
        } else {
            newPlayer2Position = (player2Position + thrownValue - 1) % 10 + 1
            newPlayer2Score = player2Score + newPlayer2Position
            newNumberOfUniverses = numberOfUniverses * thrownValueToNumberOfUniverses.getValue(thrownValue)
            newPlayer1Position = player1Position
            newPlayer1Score = player1Score
            if (newPlayer2Score >= 21) return 0L to newNumberOfUniverses
        }
        return thrownValueToNumberOfUniverses.keys
            .map {
                GameRound(
                    !playsFirstPlayer,
                    it,
                    newPlayer1Position,
                    newPlayer1Score,
                    newPlayer2Position,
                    newPlayer2Score,
                    newNumberOfUniverses
                ).playRound()
            }
            .reduce { p1, p2 -> p1.first + p2.first to p1.second + p2.second}
    }

    companion object {
        val thrownValueToNumberOfUniverses = mapOf<Int, Long>(
            3 to 1,
            4 to 3,
            5 to 6,
            6 to 7,
            7 to 6,
            8 to 3,
            9 to 1,
        )
    }
}

data class Player(var position: Int) {
    var score: Int = 0
    fun addScore(value: Int) {
        position = (position + value - 1) % 10 + 1
        score += position
    }
}

data class DeterministicDie(var currentValue: Int = 0) {
    var numberOfRolls: Int = 0
    fun roll(): Int {
        numberOfRolls++
        return (++currentValue - 1) % 100 + 1
    }
}