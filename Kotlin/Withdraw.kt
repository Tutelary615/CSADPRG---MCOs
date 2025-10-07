fun withdrawAmount(ac : Account) {
    var withdrawAmountInput : String = ""
    var willRepeatTransaction : Boolean = false

    fun executeWithdrawal() {
        val withdrawAmount : Double = withdrawAmountInput.trim().toDouble()
        println()
        if (withdrawAmount <= ac.getBalance()) {
            ac.withdraw(withdrawAmount)
            println("Transaction Successful")
            println("Withdraw Amount: ${String.format("%.2f", withdrawAmount)}")
            println("Updated Balance: ${String.format("%.2f", ac.getBalance())}")
        } else {
            println("Insufficient balance")
        }
    }

    println("Make Withdrawal")
    if (ac.getBalance() == 0.00) {
        println("No funds to withdraw (balance: 0.00). Returning to main menu")
    } else {
        do {
            displayAccountDetails(ac)
            println()
            withdrawAmountInput = getUserInput("Withdraw Amount")
            if (isValidAmount(withdrawAmountInput)) {
                executeWithdrawal()
                println()
            }
            if (ac.getBalance() == 0.00) {
                println("No more funds to withdraw (balance: 0.00). Returning to main menu")
                willRepeatTransaction = false;
            } else {
                willRepeatTransaction = willRepeatTransaction("Would you like to make another withdrawal?")
            }
        } while (willRepeatTransaction)
    }
}