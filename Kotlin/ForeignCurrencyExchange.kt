fun foreignCurrencyExchange(currencies: List<Currency>) {
    var sourceAmount : Double = -1.0
    var sourceCurrency : Currency = currencies[0]
    var exchangeCurrency : Currency = currencies[0]
    var sourceCurrencyInput : String = ""
    var exchangeCurrencyInput : String = ""
    var sourceAmountInput : String = ""
    var willProceed : Boolean = true

    fun getSourceCurrency() {
        displayAsMenu(currencies)
        sourceCurrencyInput = getUserInput("Source currency")
        if (!isValidMenuInput(sourceCurrencyInput, currencies.size)) {
            willProceed = false
        } else {
            sourceCurrency = currencies[sourceCurrencyInput.toInt() - 1]
        }
        if (willProceed) {
            if (sourceCurrency.getExchangeRate() == -1.00) {
                println("Exchange rate for ${sourceCurrency.getCurrencyCode()} has not been set")
                willProceed = false
            }
        }
    }

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
            if (exchangeCurrency.getExchangeRate() == -1.00) {
                println("Exchange rate for ${exchangeCurrency.getCurrencyCode()} has not been set")
                willProceed = false
            }
        }
    }


    fun getSourceAmount() {
        sourceAmountInput = getUserInput("Source amount")
        if (isValidAmount(sourceAmountInput)) {
            sourceAmount = sourceAmountInput.toDouble()
        } else {
            willProceed = false
        }
    }


    fun executeConversion() {
        var exchangeAmount : Double = sourceAmount  *  sourceCurrency.getExchangeRate() /  exchangeCurrency.getExchangeRate()
        println()
        println("Exchange Amount: ${String.format("%.2f", exchangeAmount)}")
    }

    println("Foreign Currency Exchange")
    println()

    do {

        willProceed = true
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
    } while (willRepeatTransaction("Convert another currency?"))
}