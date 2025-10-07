fun depositAmount(ac : Account) {
    var depositAmountInput : String = ""

    fun executeDeposit() {
        val depositAmount : Double = depositAmountInput.trim().toDouble()
        ac.deposit(depositAmount)
        println()
        println("Transaction Successful")
        println("Deposit Amount: ${String.format("%.2f", depositAmount)}")
        println("Updated Balance: ${String.format("%.2f", ac.getBalance())}")
    }
    println("Make Deposit")

    do {
       displayAccountDetails(ac)
        println()
       depositAmountInput = getUserInput("Amount to deposit")
       if (isValidAmount(depositAmountInput)) {
           executeDeposit()
       }
       println()
   } while (willRepeatTransaction("Would you like to make another deposit?"))
}

