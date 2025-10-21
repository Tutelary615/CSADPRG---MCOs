/**
 * contains all procedures and functions for recording exchange rates
 * @param foreignCurrencies currencies whose exchange rate will be recorded
 */
fun recordExchangesRates(foreignCurrencies: List<Currency>) {
    var selectionInput : String = ""
    var exchangeRateInput : String = ""
    var selectedCurrencyIdx : Int = -1

    /**
     * checks if exchange amount is valid
     * @return true if valid and false otherwise
     */
    fun isValidExchangeAmount() : Boolean {
        if (!(isValidAmount(exchangeRateInput))) {
            return false;
        } else if (exchangeRateInput.toDouble() == 0.00) {
            println("Invalid amount entered")
            return false
        } else {
            return true
        }
    }

    /**
     * executes the recording and displays transaction details
     */
    fun executeRecordExchangeRate() {
        val selectedCurrencyIdx = selectionInput.toInt() - 1
        val newExchangeRate = exchangeRateInput.toDouble()
        foreignCurrencies[selectedCurrencyIdx].setExchangeRate(newExchangeRate)
        println("Exchange rate of ${foreignCurrencies[selectedCurrencyIdx].getCurrencyCode()} recorded")
    }

    // procedure
    println("Recording exchange rates")
    do {
        println()
        displayAsMenu(foreignCurrencies)
        selectionInput = getUserInput("Select foreign currency")
        println()
        if (isValidMenuInput(selectionInput, foreignCurrencies.size)) { // checking if the currency selection is valid
            exchangeRateInput = getUserInput("Exchange rate")

            if (isValidExchangeAmount()) { // checking if exchange rate entered is valid
                executeRecordExchangeRate()
            }
        }
        println()
    } while (willRepeatTransaction("Would you like to record another exchange rate?"))
    println()
}