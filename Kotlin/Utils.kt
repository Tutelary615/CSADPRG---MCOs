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

fun displayAccountDetails(ac : Account) {
    println("Account Name: ${ac.getName()}")
    println("Current Balance: ${String.format("%.2f", ac.getBalance())}")
    println("Currency: ${ac.getAccountCurrency().getCurrencyCode()}")
}

@JvmName ("fString")
fun displayAsMenu(options : List<String>) {
    for (i in 0..options.size - 1) {
        println("${i + 1} - ${options[i]}")
    }
}

@JvmName ("fCurrency")
fun displayAsMenu(currencies : List<Currency>) {
    for (i in 0..currencies.size - 1) {
        println("${i + 1} - ${currencies[i].getName()} (${currencies[i].getCurrencyCode()})")
    }
    println()
}

fun getUserInput(prompt : String) : String {
    print("${prompt}: ")
    return readln().trim()
}

fun isValidMenuInput(input : String, maxSelection : Int) : Boolean {
    val integerInput : Int

    if (input.length == 0) {
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