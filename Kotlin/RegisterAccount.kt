fun registerAccountName(ac : Account) {
    var nameInput : String = ""

    println("Register Account Name")
    do {
       nameInput = getUserInput("Account name: ")

        if (nameInput != "") {
            ac.setName(nameInput)
            println("Account name registered")
        } else {
            println("No name entered")
        }
        println()
    } while (willRepeatTransaction("Would you like to re-register your account name?"))
}