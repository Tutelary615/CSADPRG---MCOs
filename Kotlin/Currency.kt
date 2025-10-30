/**
 * class that represents a currency
 *
 * @constructor creates a currency a set name, currency code, and an exchange rate of -1
 * @param name
 * @param currencyCode
 */
class Currency(name : String, currencyCode : String)  {

    private val name : String
    private val currencyCode : String
    private var exchangeRate : Double = -1.0

    init {
        this.name = name
        this.currencyCode = currencyCode
    }

    /**
     * @return name of the currency
     */
    fun getName() : String {
        return name
    }

    /**
     * sets the exchange rate of the currency
     * @param exchangeRate
     */
    fun setExchangeRate(exchangeRate : Double) {
        this.exchangeRate = exchangeRate
    }

    /**
     * @return the currency code
     */
    fun getCurrencyCode() : String {
        return currencyCode
    }

    /**
     * @return the exchange rate of the currency
     */
    fun getExchangeRate() : Double {
        return exchangeRate
    }
}