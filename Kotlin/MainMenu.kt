/**
 * contains all functions and procedures required for
 * a. initialization of currencies and account
 * b. main menu of the program
 */
fun mainMenu() {
    val allCurrencies : MutableList<Currency> = mutableListOf<Currency>()
    var foreignCurrencies : MutableList<Currency>
    var ac : Account
    val transactions : List<String> = listOf("Register Account Name", "Deposit Amount", "Withdraw Amount",
                                             "Currency Exchange", "Record Exchange Rates",
                                             "Show Interest Computation", "Exit")
    var transactionChoice : String = ""

    /**
     * procedure for initializing currencies
     */
    fun initializeCurrencies() {
        allCurrencies.add(Currency("Philippine Peso", "PHP"))
        allCurrencies[0].setExchangeRate(1.0)
        allCurrencies.add(Currency("United States Dollar", "USD"))
        allCurrencies.add(Currency("Japanese Yen", "JPY"))
        allCurrencies.add(Currency("British Pound Sterling", "GBP"))
        allCurrencies.add(Currency("Euro", "EUR"))
        allCurrencies.add(Currency("Chinese Yuan Renminni", "CNY"))
    }

    /**
     * procedure for getting of transaction choice from user
     */
    fun getTransaction() {
        do {
            transactionChoice = getUserInput("Select Transaction")
        } while (!isValidMenuInput(transactionChoice, transactions.size))
    }


    // procedures
    initializeCurrencies()
    ac = Account(allCurrencies[0])                   // account currency set to PHP
    foreignCurrencies = allCurrencies.subList(1, allCurrencies.size)  // all currencies except PHP
    do {
        println()
        println("Main Menu")
        println()
        displayAsMenu(transactions)
        getTransaction()
        println()

        when (transactionChoice) {
            "1" -> registerAccountName(ac)
            "2" -> depositAmount(ac)
            "3" -> withdrawAmount(ac)
            "4" -> foreignCurrencyExchange(allCurrencies)
            "5" -> recordExchangesRates(foreignCurrencies)
            "6" -> showInterestAmount(ac)
        }
    } while (transactionChoice != "7")
}