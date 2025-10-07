fun showInterestAmount(ac : Account) {
    val dailyInterestRate = ac.getInterestRate() / 365.0
    var daysInput : String = ""

    fun displayInterestAmounts() {
        var endOfDayBalance = ac.getBalance()
        val numberOfDays = daysInput.toInt()

        if (numberOfDays > 0) {
            println("day | interest | balance")
            for (i in 1..numberOfDays) {

                fun computeEndOfDayBalance() : Double {
                    return endOfDayBalance + (1 + i * dailyInterestRate)
                }
                endOfDayBalance = computeEndOfDayBalance()
                println(String.format("%3d | %8.2f | %2.2f", i, (dailyInterestRate * 100), computeEndOfDayBalance()))
            }
        } else {
            println("Period entered invalid")
        }
    }

    fun isDayInputValid() : Boolean {
        if (daysInput.isEmpty()) {
            println("No input")
            return false
        } else if (daysInput.toIntOrNull() == null) {
            println("Input invalid")
            return false
        } else {
            return true
        }
    }

    println("Show Interest Amounts")
    println()
    displayAccountDetails(ac)
    println("Interest rate: ${String.format("%.0f", ac.getInterestRate() * 100)}%")
    println()

    if (ac.getBalance() == 0.00) {
        println("No amounts to show. Account balance is 0.00")
    } else {
        do {
            daysInput = getUserInput("Period (in days, only whole numbers accepted)")
            if (isDayInputValid()) {
                displayInterestAmounts()
            }
        } while (willRepeatTransaction("Would you like to repeat this transaction?"))
    }

}