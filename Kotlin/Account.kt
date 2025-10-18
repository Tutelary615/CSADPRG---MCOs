/**
 * class that represents an account
 *
 * @constructor creates an account with name set to a placeholder string "<No name registered>",
 * balance of 0.00, an interest rate of 0.05 and a set currency
 * @param accountCurrency
 */
class Account(accountCurrency: Currency) {
    private var name : String = "<No name registered>"
    private var balance : Double = 0.0
    private val interestRate : Double = 0.05
    private val accountCurrency : Currency

    init {
        this.accountCurrency = accountCurrency
    }

    /**
     * deposits an amount to the account
     * @param amount
     */
    fun deposit(amount: Double) {
        balance += amount
    }

    /**
     * withdraws an amount from the account
     * @param amount
     */
    fun withdraw(amount: Double) {
       balance -= amount;
    }

    /**
     * @return the name of the account holder
     */
    fun getName() : String {
       return name
    }

    /**
     * @return the balance of the account
     */
    fun getBalance() : Double {
        return balance
    }

    /**
     * @return the interest rate of the account
     */
    fun getInterestRate() : Double {
        return interestRate
    }

    /**
     * @return the currency of the account
     */
    fun getAccountCurrency() : Currency {
        return accountCurrency
    }

    /**
     * sets the name of the account holder
     * @param name name that account name will be set to
     */
    fun setName(name: String) {
        this.name = name
    }

}