fun makeDeposit(ac : Account) {
    var depositAmount : Double = 0.0
    var inputStr = ""
    var isInputDouble : Boolean = true
    val prevBalance : Double = ac.getBalance()

    fun getDepositAmount() {
        print("Amount to deposit: ")
        inputStr = readln()
    }

    fun validateInput() {
        try {
            depositAmount = inputStr.toDouble()
        } catch (e: NumberFormatException) {
            println("No/Invalid amount entered")
            isInputDouble = false
        }
    }

    fun executeDeposit() {
        if  (ac.deposit(depositAmount)) {
            println("Transaction Successful")
            println("Deposit Amount: ${String.format("%.2f", depositAmount)}")
            println("Updated Balance: ${ac.getBalance()}")
        } else {
            println("Invalid amount entered")
        }
    }

    println("Make Deposit")
    displayAccountDetails(ac)
    getDepositAmount()
    validateInput()
    println()

    if (isInputDouble) {
        executeDeposit()
    }
}

