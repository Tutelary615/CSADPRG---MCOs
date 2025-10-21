/**
 * contains all functions and procedures required for the show
 * interest computation feature
 * @param ac account to show computation of
 */
fun showInterestAmount(ac : Account) {
    var periodInput : String = ""

    /**
     * displays interests computation
     */
    fun displayComputation() {
        var endOfDayBalance = ac.getBalance()
        val period = periodInput.toInt()
        val dailyInterestRate = ac.getInterestRate() / 365.0
        var dailyInterestAmount : Double

        println()
        if (period > 0) {
            println("day | interest | balance")
            for (i in 1..period) {
                dailyInterestAmount = endOfDayBalance * dailyInterestRate
                endOfDayBalance += dailyInterestAmount
                println(String.format("%3d | %8.2f | %2.2f", i, dailyInterestAmount, endOfDayBalance))
            }
        } else {
            println("Period entered invalid")
        }
        println()
    }

    /**
     * checks if specified period is valid
     * @return true if valid and false otherwise
     */
    fun isPeriodInputValid() : Boolean {
        if (periodInput.isEmpty()) {
            println("No input")
            return false
        } else if (periodInput.toIntOrNull() == null) {
            println("Input invalid")
            return false
        } else {
            return true
        }
    }

    // procedure
    println("Show Interest Amounts")
    println()
    displayAccountDetails(ac)
    println("Interest rate: ${String.format("%.0f", ac.getInterestRate() * 100)}%")
    println()

    if (ac.getBalance() == 0.00) {
        println("No amounts to show. Account balance is 0.00")
    } else {
        do {
            periodInput = getUserInput("Period (in days, only whole numbers accepted)")
            if (isPeriodInputValid()) {
                displayComputation()
            }
        } while (willRepeatTransaction("Would you like to compute again"))
    }

}