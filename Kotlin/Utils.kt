/**
 * checks if a String is a valid amount
 * @param str string to check
 * @return true if str is a valid amount and false otherwise
 */
fun isValidAmount(str : String): Boolean {
    var amount : Double

    if (str.isEmpty()) {
        println("No amount entered")
        return false
    }

    try {
        amount = str.toDouble()
    } catch (e: NumberFormatException) {
        println("Invalid amount entered")
        return false
    }

    // checking if str has more than 2 decimal places
    if (str.substring(str.indexOf('.')  + 1).length > 2 && str.indexOf('.') != -1) {
        println("Invalid amount entered")
        return false
    }

    if (amount < 0.0) {
        println("Invalid amount entered")
        return false;
    }
    return true
}

/**
 * asks the user if a transaction will be repeated
 * @param prompt prompt the user will be presented with upon input
 * @return true if the user chooses to repeat the transaction and false otherwise
 */
fun willRepeatTransaction(prompt : String) : Boolean {
    var input : String = ""
    var isInputValid : Boolean = false

    do {
        input = getUserInput("${prompt} (Enter Y or N)")
        if (!input.equals("Y", ignoreCase = true) &&  !input.equals("N", ignoreCase = true)) {
            println("Invalid input")
        } else {
            isInputValid = true
        }
    } while (!isInputValid)
    println()
    return (input.equals("Y", ignoreCase = true))
}

/**
 * displays the details of an account
 * @param ac
 */
fun displayAccountDetails(ac : Account) {
    println("Account Name: ${ac.getName()}")
    println("Current Balance: ${String.format("%.2f", ac.getBalance())}")
    println("Currency: ${ac.getAccountCurrency().getCurrencyCode()}")
}

/**
 * prints a list of Strings in a menu format
 * @param options list of Strings to print as menu
 */
@JvmName ("fString")
fun displayAsMenu(options : List<String>) {
    for (i in 0..options.size - 1) {
        println("[${i + 1}] - ${options[i]}")
    }
}

/**
 * prints a collection of currencies as a menu format
 * @param currencies collection of currencies to print as menu
 */
@JvmName ("fCurrency")
fun displayAsMenu(currencies : List<Currency>) {
    for (i in 0..currencies.size - 1) {
        println("[${i + 1}] - ${currencies[i].getName()} (${currencies[i].getCurrencyCode()})")
    }
    println()
}

/**
 * function for getting inputs from the user
 * @param prompt prompt that user will be presented with upon input
 */
fun getUserInput(prompt : String) : String {
    print("${prompt}: ")
    return readln().trim()
}

/**
 * checks if an input is a valid menu selection
 * @param input
 * @param maxSelection highest number that is a valid menu input
 */
fun isValidMenuInput(input : String, maxSelection : Int) : Boolean {
    val integerInput : Int

    if (input.isEmpty()) {
        println("No selection entered")
        return false
    }

    try {
        integerInput = input.toInt()
    } catch (e: NumberFormatException) {
        println("Invalid input")
        return false
    }

    if (integerInput > maxSelection) {
        println("Invalid selection")
        return false
    }
    return true
}