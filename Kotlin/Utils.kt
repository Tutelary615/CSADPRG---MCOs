fun isValidAmount(input : String): Boolean {
    var amount : Double

    if (input.isEmpty()) {
        println("No amount entered")
        return false
    }

    try {
        amount = input.toDouble()
    } catch (e: NumberFormatException) {
        println("Invalid amount entered")
        return false
    }

    if (input.substring(input.indexOf('.')  + 1).length > 2 && input.indexOf('.') != -1) {
        println("Invalid amount entered")
        return false
    }

    if (amount < 0.0) {
        println("Invalid amount entered")
        return false;
    }
    return true
}

fun willReturnToMainMenu() : Boolean {
    var input : String = ""
    var isInputValid : Boolean = false

    do {
        print("Back to main menu (Y/N): ")
        input = readln().trim()

        if (!input.equals("Y", ignoreCase = true) && !input.equals("N", ignoreCase = true)) {
            println("Invalid input")
        } else {
            isInputValid = true
        }
    } while (!isInputValid)
    println()
    return input.equals("Y", ignoreCase = true)
}

fun displayAccountDetails(ac : Account) {
    println("Account Name: ${ac.getName()}")
    println("Current Balance: ${String.format("%.2f", ac.getBalance())}")
    println("Currency: ${ac.getAccountCurrency().getCurrencyCode()}")
    println()
}