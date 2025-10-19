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

// ADD ENTER TO INSTRUCTIONS (ENTER AMOUNT)
// ADD COMMENTS

const readline = require('readline');

// Interface for reading input
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
    prompt: ''
});

// Promise-based function that gets user input
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

function roundValue(value) {
    return Math.round(value * 100) / 100;
}

function roundValueToString(value) {
    return value.toFixed(2);
}

function newline() {
    console.log();
}

function padNumber(value, spaces) {
    return String(value).padEnd(spaces, ' ');
}

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

function getCurrency(num) {
    switch (num) {
        case 1: return "PHP";
        case 2: return "USD";
        case 3: return "JPY";
        case 4: return "GBP";
        case 5: return "EUR";
        case 6: return "CNY";
        default: return null;
    }
}

function printCurrencies() {
    console.log("Exchange Rates to PHP")
    console.log(`[1] Philippine Peso (PHP)         | ${roundValueToString(exchangeRatesToPHP.get("PHP"))}`);
    console.log(`[2] United States Dollar (USD)    | ${roundValueToString(exchangeRatesToPHP.get("USD"))}`);
    console.log(`[3] Japanese Yen (JPY)            | ${roundValueToString(exchangeRatesToPHP.get("JPY"))}`);
    console.log(`[4] British Pound Sterling (GBP)  | ${roundValueToString(exchangeRatesToPHP.get("GBP"))}`);
    console.log(`[5] Euro (EUR)                    | ${roundValueToString(exchangeRatesToPHP.get("EUR"))}`);
    console.log(`[6] Chinese Yuan Renminni (CNY)   | ${roundValueToString(exchangeRatesToPHP.get("CNY"))}\n`);
}

async function returnToMainMenu() {
    let prompt, input;

    prompt = "Return to Main Menu (Y/N)? ";

    do {
        input = await getInput(prompt);
    } while (input !== "Y" && input !== "N");

    return input === "Y";
} 

async function registerAccount() {
    do {
        console.log("\nRegister Account");
    
        let input;
        do {
            input = await getInput("Enter Account Name: ");
        } while (input === "");
        
        account.name = input; // assign input;
        newline();
        console.log(`Account has been registered with name: ${account.name}`)
    } while (!(await returnToMainMenu()));
}

async function depositAmount() {
    if (account.name === null) {
        return console.log("\nPlease register your account first.");
    }

    do {
        console.log("\nDeposit Amount");
        account.details();
        newline();
        
        let input = Number(await getInput("Enter Deposit Amount: "));
        newline();

        if (input > 0) {
            account.deposit(Number(input));
        } else {
            console.log("\nInvalid input.");
        }

        newline();
    } while (!(await returnToMainMenu()));
}

async function withdrawAmount() {
    if (account.name === null) {
        return console.log("\nPlease register your account first.");
    }

    do {
        console.log("\nWithdraw Amount");
        account.details();
        newline();
        
        let input = Number(await getInput("Enter Withdraw Amount: "));
        newline();

        if (input > 0) {
            account.withdraw(Number(input));
        } else {
            console.log("\nInvalid input.");
        }

        newline();
    } while (!(await returnToMainMenu()));
}

// Currency Exchange
async function currencyExchange() {
    do {
        console.log("\nForeign Currency Exchange");
        console.log("Source Currency Options:");
        printCurrencies();
    
        let sourceCurrency = Number(await getInput("Enter Source Currency: "));
        sourceCurrency = getCurrency(sourceCurrency);
    
        if (sourceCurrency === null) {
            console.log("\nInvalid input.");
            continue;
        }
        if (exchangeRatesToPHP.get(sourceCurrency) === 0) {
            console.log(`\n${sourceCurrency} exchange rate hasn't been set.`);
            continue;
        }
    
        let sourceAmount = Number(await getInput(`Enter Source Amount (in ${sourceCurrency}): `));
        
        if (!sourceAmount || sourceAmount <= 0) {
            console.log("\nInvalid input.");
            continue;
        }
    
        console.log("\nExchange Currency Options:");
        printCurrencies();
    
        let exchangeCurrency = Number(await getInput("Enter Exchange Currency: "));
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
        exchangeAmount = sourceAmount * exchangeRatesToPHP.get(sourceCurrency) / exchangeRatesToPHP.get(exchangeCurrency);
        console.log(`Exchange Amount: ${roundValueToString(exchangeAmount)}`);
        newline();
    } while (!(await returnToMainMenu()));
}

async function recordExchangeRate() {
    do {
        console.log("\nRecord Exchange Rate\n");
        printCurrencies();
        
        let currency = Number(await getInput("Select Foreign Currency: "));
        currency = getCurrency(currency);
    
        if (currency === null) {
            console.log("\nInvalid input.");
            continue;
        }
        if (currency === "PHP") {
            console.log("\nPHP exchange rate must not be changed.");
            continue;
        }
    
        let newRate = Number(await getInput("Select Exchange Rate: "));
        
        if (newRate <= 0 || !newRate) {
            console.log("\nInvalid input.");
            continue;
        }
    
        newline();
        exchangeRatesToPHP.set(currency, roundValue(newRate));
        newline();
        console.log("Exchange rate has been successfully set.");
    } while (!(await returnToMainMenu()));
}

async function showInterestComputation() {
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
        
        days = Number(await getInput("Enter Duration of Interest (in Days): "));
        
        if (days > 0) {
            let balance = roundValue(account.balance);
            console.log("\nDay    | Interest     | Balance      |");
            console.log("----------------------------------------");
            
            for (let i = 0; i < days; i++) {
                let interest = roundValue(balance * 0.05 / 365);
                balance = roundValue(balance + interest);
                console.log(`${padNumber(i + 1, 6)} | ${padNumber(interest, 12)} | ${padNumber(roundValueToString(balance), 12)} |`);
            }
        } else {
            console.log("\nInvalid input.");
        }

        newline();
    } while (!(await returnToMainMenu()));
}

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

// Handles the termination of the program
rl.on('close', () => {
    console.log("\n\nTerminating program...");
    process.exit(0);
})