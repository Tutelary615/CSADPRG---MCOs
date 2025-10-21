/**
 * contains all procedures and functions required for the foreign currency exchange feature
 * @param currencies currencies that will be used for currency exchange
 */
fun foreignCurrencyExchange(currencies: List<Currency>) {
    var sourceAmount : Double = -1.0
    var sourceCurrency : Currency = currencies[0]
    var exchangeCurrency : Currency = currencies[0]
    var sourceCurrencyInput : String = ""
    var exchangeCurrencyInput : String = ""
    var sourceAmountInput : String = ""
    var willProceed : Boolean = true

    /**
     * handles selection of source currency
     */
    fun getSourceCurrency() {
        displayAsMenu(currencies)
        sourceCurrencyInput = getUserInput("Source currency")
        if (!isValidMenuInput(sourceCurrencyInput, currencies.size)) { // checking if input for selection is valid
            willProceed = false // if not valid, transaction will not continue
        } else {
            sourceCurrency = currencies[sourceCurrencyInput.toInt() - 1]
        }
        if (willProceed) {
            if (sourceCurrency.getExchangeRate() == -1.00) { // checks if exchange rate of selected currency is set
                println("Exchange rate for ${sourceCurrency.getCurrencyCode()} has not been set")
                willProceed = false // if not set, transaction will not continue
            }
        }
    }

    /**
     * handles selection of exchange currency
     */
    fun getExchangeCurrency() {
        println()
        displayAsMenu(currencies)
        exchangeCurrencyInput = getUserInput("Exchange currency")
        if (!isValidMenuInput(exchangeCurrencyInput, currencies.size)) {
            willProceed = false
        } else {
            exchangeCurrency = currencies[exchangeCurrencyInput.toInt() - 1]
        }
        if (willProceed) {
            if (exchangeCurrency.getExchangeRate() == -1.00) { // checks if exchange rate of selected currency is set
                println("Exchange rate for ${exchangeCurrency.getCurrencyCode()} has not been set")
                willProceed = false // if not set, transaction will not continue
            }
        }
    }
    /**
     * gets and validates amount to exchange
     */
    fun getSourceAmount() {
        sourceAmountInput = getUserInput("Source amount")
        if (isValidAmount(sourceAmountInput)) { // checking if amount is valid
            sourceAmount = sourceAmountInput.toDouble()
        } else {
            willProceed = false // if not valid, transaction will not continue
        }
    }

    /**
     * executes the conversion and displays results
     */
    fun executeConversion() {
        val exchangeAmount : Double = sourceAmount  *  sourceCurrency.getExchangeRate() /  exchangeCurrency.getExchangeRate()
        println()
        println("Exchange Amount: ${String.format("%.2f", exchangeAmount)}")
    }

    // procedure
    println("Foreign Currency Exchange")
    println()
    do {

        willProceed = true // flag that determines if the  transaction will proceed
        getSourceCurrency()
        if (willProceed) {
            getSourceAmount()
        }
        if (willProceed) {
            getExchangeCurrency()
        }
        if (willProceed) {
            executeConversion()
        }
    } while (willRepeatTransaction("Convert again?"))
}