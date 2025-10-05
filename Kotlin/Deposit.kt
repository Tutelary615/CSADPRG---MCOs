fun makeDeposit(ac : Account) {
    var depositAmountInput : String = ""

    fun getDepositAmount() {
        print("Amount to deposit: ")
        depositAmountInput = readln()
    }

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
       getDepositAmount()

       if (isValidAmount(depositAmountInput)) {
           executeDeposit()
       }
       println()
   } while (!willReturnToMainMenu())
}

