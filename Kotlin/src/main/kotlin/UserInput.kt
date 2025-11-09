fun getMenuInput(maxChoice : Int) : String{
    var input : String = ""
    do {
        input = getUserInput("Enter choice")
    } while (!isValidMenuInput(input, maxChoice))
    return input
}



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