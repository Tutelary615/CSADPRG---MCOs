class Currency(name : String, currencyCode : String)  {

    private val name : String
    private val currencyCode : String
    private var exchangeRate : Double = 1.0

    init {
        this.name = name
        this.currencyCode = currencyCode
    }

    fun getName() : String {
        return name
    }

    fun getCurrencyCode() : String {
        return currencyCode
    }

    fun getExchangeRate() : Double {
        return exchangeRate
    }
}