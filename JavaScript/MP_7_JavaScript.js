/*********************
    Last names: Almero, Aquino, Dolot, Marquez
    Language: JavaScript
    Paradigm(s): Procedural Programming
*********************/

/*********************
    Program guide:
    1. Install Node.js through this link: https://nodejs.org/en/download
    2. Open command prompt/terminal and go to the directory of this file.
    3. Enter the following command to run:
        node MP_7_JavaScript.js
    4. Press Ctrl + C to terminate the program. 
*********************/

// ADD VALIDATION FOR VALUES
// ADD COMMENTS

const readline = require('readline');

// Interface for reading input
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
    prompt: ''
});

/**
 * @function getInput
 * @description promise-based function that gets user input
 * @param query prompt to be displayed to the user
 * @returns Promise of the output
 */
function getInput(query) {
    return new Promise(resolve => {
        rl.question(query, resolve);
    });
}

// Initialization of exchange rates map
const exchangeRatesToPHP = new Map([
    ["PHP", 1],
    ["USD", 0],
    ["JPY", 0],
    ["GBP", 0],
    ["EUR", 0],
    ["CNY", 0]
]);

/**
 * @function roundValue
 * @description rounds a value to 2 decimals and returns it as a Number
 * @param value value to be rounded
 * @returns value rounded to 2 decimals
 */
function roundValue(value) {
    return Math.round(value * 100) / 100;
}

/**
 * @function roundValueToString
 * @description rounds a value to 2 decimals and returns it as a String
 * @param value value to be rounded
 * @returns string of value rounded to 2 decimals 
 */
function roundValueToString(value) {
    return value.toFixed(2);
}

/**
 * @function newline
 * @description prints a newline
 */
function newline() {
    console.log();
}

/**
 * @function padNumber
 * @description adds left padding to the value
 * @param value value to be padded
 * @param spaces number of spaces of padding
 * @returns string of value with left padding
 */
function padNumber(value, spaces) {
    return String(value).padEnd(spaces, ' ');
}

/**
 * @function isValidValueInput
 * @description checks if the input value is valid (0 or 2 decimal spaces)
 * @param input input to be validated
 * @returns true if valid, false otherwise
 */
function isValidValueInput(input) {
    i = input.indexOf('.');
    len = input.length
    return (i != -1 && len - i == 3) || i == -1;
}

// Object containing the user details
const account = {
    name: null,
    balance: 0.00,
    
    deposit: function(amount) {
        this.balance += roundValue(amount);
        console.log(`Updated Balance: ${roundValueToString(this.balance)}`);
    },

    withdraw: function(amount) {
        if (amount > this.balance) {
            console.log("Insufficient funds.");
        } else {
            this.balance -= roundValue(amount);
            console.log(`Updated Balance: ${roundValueToString(this.balance)}`);
        }
    },
    
    details: function() {
        console.log(`Account Name: ${this.name}`);
        console.log(`Current Balance: ${roundValueToString(this.balance)}`);
        console.log("Currency: PHP");
        return "";
    }
}

/**
 * @function getCurrency
 * @description gets the currency corresponding to the input
 * @param num input of the user
 * @returns corresponding currency, null otherwise
 */
function getCurrency(num) {
    switch (num) {
        case '1': return "PHP";
        case '2': return "USD";
        case '3': return "JPY";
        case '4': return "GBP";
        case '5': return "EUR";
        case '6': return "CNY";
        default: return null;
    }
}

/**
 * @function printCurrencies
 * @description prints exchange rates of currencies to PHP
 */
function printCurrencies() {
    console.log("Exchange Rates to PHP")
    console.log(`[1] Philippine Peso (PHP)         | ${roundValueToString(exchangeRatesToPHP.get("PHP"))}`);
    console.log(`[2] United States Dollar (USD)    | ${roundValueToString(exchangeRatesToPHP.get("USD"))}`);
    console.log(`[3] Japanese Yen (JPY)            | ${roundValueToString(exchangeRatesToPHP.get("JPY"))}`);
    console.log(`[4] British Pound Sterling (GBP)  | ${roundValueToString(exchangeRatesToPHP.get("GBP"))}`);
    console.log(`[5] Euro (EUR)                    | ${roundValueToString(exchangeRatesToPHP.get("EUR"))}`);
    console.log(`[6] Chinese Yuan Renminni (CNY)   | ${roundValueToString(exchangeRatesToPHP.get("CNY"))}\n`);
}

/**
 * @function returnToMainMenu
 * @description prompts the user if return to main menu
 * @returns true if yes, false otherwise
 */
async function returnToMainMenu() {
    let prompt, input;

    prompt = "Return to Main Menu (Y/N)? ";

    do {
        input = await getInput(prompt);
        input = input.toUpperCase();
    } while (input !== "Y" && input !== "N");

    return input === "Y";
} 

/**
 * @function registerAccount
 * @description registers the account of the user, inclusive of account name
 */
async function registerAccount() {
    do {
        console.log("\nRegister Account");
    
        let input;

        // loop while input is invalid
        do {
            input = await getInput("Enter Account Name: ");
        } while (input === "");
        
        account.name = input;
        newline();
        console.log(`Account has been registered with name: ${account.name}`)
    } while (!(await returnToMainMenu()));
}

/**
 * @function depositAmount
 * @description deposits an amount specified by the user to the account
 */
async function depositAmount() {
    // checks if account is registered
    if (account.name === null) {
        return console.log("\nPlease register your account first.");
    }

    do {
        console.log("\nDeposit Amount");
        account.details();
        newline();
        
        // stores input as string and number separately for validation
        let input = await getInput("Enter Deposit Amount: ");
        let value = Number(input);
        newline();

        if (value > 0 && isValidValueInput(input)) {
            account.deposit(value); // deposits amount
        } else {
            console.log("Invalid input.");
        }

        newline();
    } while (!(await returnToMainMenu()));
}

/**
 * @function withdrawAmount
 * @description withdraws an amount specified by the user from the account
 */
async function withdrawAmount() {
    // checks if account is registered
    if (account.name === null) {
        return console.log("\nPlease register your account first.");
    }

    do {
        console.log("\nWithdraw Amount");
        account.details();
        newline();
        
        // stores input as string and number separately for validation
        let input = await getInput("Enter Withdraw Amount: ");
        let value = Number(input)
        newline();

        if (value > 0 && isValidValueInput(input)) {
            account.withdraw(value); //withdraws amount
        } else {
            console.log("Invalid input.");
        }

        newline();
    } while (!(await returnToMainMenu()));
}

/**
 * @function currencyExchange
 * @description allows user to see how currency is exchanged
 */
async function currencyExchange() {
    let ctrZero = 0;

    // checks if there are at least 2 exchange rates declared (including PHP)
    for (const value of exchangeRatesToPHP.values()) {
        if (value === 0) {
            ctrZero++;
        }
    }

    if (ctrZero >= 5) {
        return console.log("\nPlease set an exchange rate first.");
    }

    do {
        console.log("\nForeign Currency Exchange");
        console.log("Source Currency Options:");
        printCurrencies();
    
        // source currency
        let sourceCurrency = await getInput("Enter Source Currency: ");
        sourceCurrency = getCurrency(sourceCurrency);
    
        if (sourceCurrency === null) {
            console.log("\nInvalid input.");
            continue;
        }
        if (exchangeRatesToPHP.get(sourceCurrency) === 0) {
            console.log(`\n${sourceCurrency} exchange rate hasn't been set.`);
            continue;
        }
    
        // source amount
        let sourceAmountInput = await getInput(`Enter Source Amount (in ${sourceCurrency}): `);
        let sourceAmount = Number(sourceAmountInput);
        
        if (!sourceAmount || sourceAmount <= 0 || !isValidValueInput(sourceAmountInput)) {
            console.log("\nInvalid input.");
            continue;
        }
    
        console.log("\nExchange Currency Options:");
        printCurrencies();
    
        // exchange currency
        let exchangeCurrency = await getInput("Enter Exchange Currency: ");
        exchangeCurrency = getCurrency(exchangeCurrency);
    
        if (exchangeCurrency === null) {
            console.log("\nInvalid input.");
            continue;
        }
        if (exchangeRatesToPHP.get(exchangeCurrency) === 0) {
            console.log(`\n${exchangeCurrency} exchange rate hasn't been set.`);
            continue;
        }
        if (sourceCurrency === exchangeCurrency) {
            console.log(`Converting to same currency (${sourceCurrency} -> ${exchangeCurrency}) denied.`);
            continue;
        }
    
        newline();
        // computation and display of exchange amount
        exchangeAmount = sourceAmount * exchangeRatesToPHP.get(sourceCurrency) / exchangeRatesToPHP.get(exchangeCurrency);
        console.log(`Exchange Amount: ${roundValueToString(exchangeAmount)}`);
        newline();
    } while (!(await returnToMainMenu()));
}

/**
 * @function recordExchangeRate
 * @description records exchange rates set by the user
 */
async function recordExchangeRate() {
    do {
        console.log("\nRecord Exchange Rate\n");
        printCurrencies();
        
        // currency to record exchange
        let currency = await getInput("Select Foreign Currency: ");
        currency = getCurrency(currency);
    
        if (currency === null) {
            console.log("\nInvalid input.");
            continue;
        }
        if (currency === "PHP") {
            console.log("\nPHP exchange rate must not be changed.");
            continue;
        }
    
        // exchange rate
        let newRateInput = await getInput("Select Exchange Rate: ");
        let newRate = Number(newRateInput)
        
        if (!newRate || newRate <= 0 || !isValidValueInput(newRateInput)) {
            console.log("\nInvalid input.");
            continue;
        }
    
        exchangeRatesToPHP.set(currency, roundValue(newRate));
        newline();
        console.log("Exchange rate has been successfully set.");
    } while (!(await returnToMainMenu()));
}

/**
 * @function showInterestComputation
 * @description displays interest depending on the time period indicated by the user
 */
async function showInterestComputation() {
    // checks if the account is registered and has withstanding balance
    if (account.name === null) {
        return console.log("\nPlease register your account first.");
    }
    if (account.balance === 0) {
        return console.log("\nAccount balance is PHP 0.00.");
    }

    do {
        console.log("\nShow Interest Amount");
        account.details();
        console.log("Interest Rate: 5%\n");
        
        daysInput = await getInput("Enter Duration of Interest (in Days): ");
        days = Number(daysInput);
        
        if (days > 0 && days % 1 == 0 && !daysInput.includes(".")) {
            let balance = roundValue(account.balance);
            console.log("\nDay    | Interest       | Balance        |");
            console.log("--------------------------------------------");
            
            // computation of daily interest
            for (let i = 0; i < days; i++) {
                let interest = roundValue(balance * 0.05 / 365);
                balance = roundValue(balance + interest);
                console.log(`${padNumber(i + 1, 6)} | ${padNumber(roundValueToString(interest), 14)} | ${padNumber(roundValueToString(balance), 14)} |`);
            }
        } else {
            console.log("\nInvalid input.");
        }

        newline();
    } while (!(await returnToMainMenu()));
}

/**
 * @function printMainMenu
 * @description prints the main menu
 */
function printMainMenu() {
    console.log("\nWelcome to JS Bank!");
    console.log("\nSelect Transaction");
    console.log("[1] Register Account");
    console.log("[2] Deposit Amount");
    console.log("[3] Withdraw Amount");
    console.log("[4] Currency Exchange");
    console.log("[5] Record Exchange Rates");
    console.log("[6] Show Interest Computation");
    console.log("[0] Exit\n");
}

/**
 * @function mainMenu
 * @description executes the main menu
 */
async function mainMenu() {
    while (true) {
        printMainMenu();
        let option = await getInput("Enter option: ");
        
        switch (option) {
            case '0':
                rl.close();
            case '1': 
                await registerAccount();
                break;
            case '2':
                await depositAmount();
                break;
            case '3':
                await withdrawAmount();
                break;
            case '4':
                await currencyExchange();
                break;
            case '5':
                await recordExchangeRate();
                break;
            case '6':
                await showInterestComputation();
                break;
            default:
                console.log("Invalid input.\n");
                break;
        }
    }
}

// Start the program by opening the main menu
mainMenu();

// Handles the termination of the program via Ctrl + C or main menu
rl.on('close', () => {
    console.log("\n\nTerminating program...");
    process.exit(0);
})