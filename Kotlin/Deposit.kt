fun makeDeposit(ac : Account) {
    var depositAmountInput : String = ""

    fun getDepositAmount() {
        print("Amount to deposit: ")
        depositAmountInput = readln()
    }

    fun executeDeposit() {
        val depositAmount : Double = depositAmountInput.toDouble()
        ac.deposit(depositAmount)
        println("Transaction Successful")
        println("Deposit Amount: ${String.format("%.2f", depositAmount)}")
        println("Updated Balance: ${String.format("%.2f", ac.getBalance())}")
    }

    println("Make Deposit")
    displayAccountDetails(ac)
    getDepositAmount()

    if (isValidAmount(depositAmountInput)) {
        executeDeposit()
    }
    println()
}

