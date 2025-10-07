fun recordExchangesRates(foreignCurrencies: List<Currency>) {
    var selectionInput : String = ""
    var exchangeRateInput : String = ""
    var selectedCurrencyIdx : Int = -1
    var newExchangeRate : Double = 0.0

    fun executeRecordExchangeRate() {
        newExchangeRate = exchangeRateInput.toDouble()
        foreignCurrencies[selectedCurrencyIdx].setExchangeRate(newExchangeRate)
        println("Exchange rate of ${foreignCurrencies[selectedCurrencyIdx].getCurrencyCode()} recorded")
    }

    println("Recording exchange rates")
    do {
        println()
        displayAsMenu(foreignCurrencies)
        selectionInput = getUserInput("Select foreign currency")
        if (isValidMenuInput(selectionInput, foreignCurrencies.size)) {
            selectedCurrencyIdx = selectionInput.toInt() - 1
            exchangeRateInput = getUserInput("Exchange rate")
            if (isValidAmount(exchangeRateInput)) {
                executeRecordExchangeRate()
            }
        }
    } while (willRepeatTransaction("Would you like to record another exchange rate?"))
    println()
}