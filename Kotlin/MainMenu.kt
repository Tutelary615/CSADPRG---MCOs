
fun mainMenu() {
    val allCurrencies : MutableList<Currency> = mutableListOf<Currency>()
    var foreignCurrencies : MutableList<Currency>
    var ac : Account
    val transactions : List<String> = listOf("Register Account Name", "Deposit Amount", "Withdraw Amount",
                                             "Currency Exchange", "Record Exchange Rates",
                                             "Show Interest Computation", "Exit")
    var transactionInput : String = ""


    fun initializeCurrencies() {
        allCurrencies.add(Currency("Philippine Peso", "PHP"))
        allCurrencies[0].setExchangeRate(1.0)
        allCurrencies.add(Currency("United States Dollar", "USD"))
        allCurrencies.add(Currency("Japanese Yen", "JPY"))
        allCurrencies.add(Currency("British Pound Sterling", "GBP"))
        allCurrencies.add(Currency("Euro", "EUR"))
        allCurrencies.add(Currency("Chinese Yuan Renminni", "CNY"))
    }

    fun getTransaction() {
        do {
            transactionInput = getUserInput("Select Transaction")
        } while (!isValidMenuInput(transactionInput, transactions.size))
    }

    initializeCurrencies()
    ac = Account(allCurrencies[0])
    foreignCurrencies = allCurrencies.subList(1, allCurrencies.size)
    do {
        println()
        println("Main Menu")
        displayAsMenu(transactions)
        getTransaction()
        println()

        when (transactionInput) {
            "1" -> registerAccountName(ac)
            "2" -> depositAmount(ac)
            "3" -> withdrawAmount(ac)
            "4" -> foreignCurrencyExchange(allCurrencies)
            "5" -> recordExchangesRates(foreignCurrencies)
            "6" -> showInterestAmount(ac)
        }

    } while (transactionInput != "7")
}


