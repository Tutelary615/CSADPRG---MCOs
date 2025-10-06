fun makeDeposit(ac : Account) {
    var depositAmountInput : String = ""

    fun executeDeposit() {
        val depositAmount : Double = depositAmountInput.trim().toDouble()
        ac.deposit(depositAmount)
        println("Transaction Successful")
        println("Withdraw Amount: ${String.format("%.2f", depositAmount)}")
        println("Updated Balance: ${String.format("%.2f", ac.getBalance())}")
    }
    println("Make Deposit")
   do {

       displayAccountDetails(ac)
       depositAmountInput = getUserInput("Amount to deposit: ")

       if (isValidAmount(depositAmountInput)) {
           executeDeposit()
       }
       println()
   } while (!willReturnToMainMenu())
}

