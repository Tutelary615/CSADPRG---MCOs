class Currency(name : String, exchangeRate : Double = 1.0)  {

    private val name : String
    private var exchangeRate : Double

    init {
        this.name = name
        this.exchangeRate = exchangeRate
    }

    fun getExchangeRate() : Double {
        return exchangeRate
    }

    fun getName() : String {
        return name
    }
}