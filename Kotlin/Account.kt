class Account(accountCurrency: Currency) {
    private var name = "(No name registered)"
    private var balance : Double = 0.0
    private val interestRate : Double = 0.05
    private val accountCurrency : Currency

    init {
        this.accountCurrency = accountCurrency
    }

    fun deposit(amount: Double) : Boolean {
        if (amount > 0.0) {
            balance += amount
            return true
        } else {
            return false
        }
    }

    fun deposit(sourceCurrency: Currency, amount: Double) : Boolean {
        if (amount > 0.0) {
            balance += amount * sourceCurrency.getExchangeRate();
            return true
        } else {
            return false
        }
    }

   fun withdraw(amount: Double) : Boolean {
       if (balance >= amount && amount > 0.0) {
           balance -= amount
           return true
       } else {
           return false
       }
   }

   fun withdraw(sourceCurrency: Currency, amount: Double) : Boolean {
       val amountToWithdraw = amount * sourceCurrency.getExchangeRate();

       if (balance >= amountToWithdraw) {
           balance -= amountToWithdraw
           return true
       } else {
           return false
       }
   }

   fun getName() : String {
       return name
   }

    fun getBalance() : Double {
        return balance
    }

    fun getInterestRate() : Double {
        return interestRate
    }

    fun getAccountCurrency() : Currency {
        return accountCurrency
    }

    fun setName(name: String) {
        this.name = name
    }

}