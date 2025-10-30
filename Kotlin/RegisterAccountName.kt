/**
 * Contains all functions and procedures for registering an account name
 * @param account account whose name will be registered
 */
fun registerAccountName(ac : Account) {
    var nameInput : String = ""

    println("Register Account Name")
    do {
       nameInput = getUserInput("Account name")

        if (!nameInput.isEmpty()) {
            ac.setName(nameInput)
            println("Account name registered")
        } else {
            println("No name entered")
        }
        println()
    } while (willRepeatTransaction("Would you like to re-register your account name?"))
}