fun makeWithdrawal(ac : Account) {
    var withdrawAmountInput : String = ""

    fun getWithdrawAmount() {
        print("Amount to withdraw: ")
        withdrawAmountInput = readln()
    }

    fun executeWithdrawal() {
        val withdrawAmount : Double = withdrawAmountInput.trim().toDouble()
        if (withdrawAmount <= ac.getBalance()) {
            ac.withdraw(withdrawAmount)
            println("Transaction Successful")
            println("Deposit Amount: ${String.format("%.2f", withdrawAmount)}")
            println("Updated Balance: ${String.format("%.2f", ac.getBalance())}")
        } else {
            println("Insufficient balance")
        }
    }

    println("Make Withdrawal")
    do {
        displayAccountDetails(ac)
        getWithdrawAmount()
        if (isValidAmount(withdrawAmountInput)) {
            executeWithdrawal()
        }
        println()
    } while (!willReturnToMainMenu())
}