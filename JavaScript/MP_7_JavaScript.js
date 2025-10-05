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

const readline = require('readline');

// Interface for reading input/output
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
};

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
};

function newline() {
    console.log();
};

function padNumber(value, spaces) {
    return String(value).padEnd(spaces, ' ');
}

const account = {
    name: "user",
    balance: 0.00,
    
    deposit: function(amount) {
        this.balance += roundValue(amount);
        console.log(`Updated Balance: ${roundValueToString(this.balance)}\n`);
    },

    withdraw: function(amount) {
        if (amount > this.balance) {
            console.log("Insufficient funds.\n");
        } else {
            this.balance -= roundValue(amount);
            console.log(`Updated Balance: ${roundValueToString(this.balance)}\n`);
        }
    },
    
    details: function() {
        console.log(`Account Name: ${this.name}`);
        console.log(`Current Balance: ${roundValueToString(this.balance)}`);
        console.log("Currency: PHP");
        return "";
    }
};

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
};

function printCurrencies() {
    console.log("[1] Philippine Peso (PHP)");
    console.log("[2] United States Dollar (USD)");
    console.log("[3] Japanese Yen (JPY)");
    console.log("[4] British Pound Sterling (GBP)");
    console.log("[5] Euro (EUR)");
    console.log("[6] Chinese Yuan Renminni (CNY)\n");
};

async function registerAccountName() {
    rl.resume();
    console.log("Register Account Name");

    let input;
    do {
        input = await getInput("Account Name: ");
    } while (input === "");
    
    account.name = input; // assign input;
};

async function depositAmount() {
    console.log("Deposit Amount");
    account.details();
    newline();
    
    let input = Number(await getInput("Deposit Amount: "));

    if (input > 0) {
        account.deposit(Number(input));
    } else {
        console.log("\nInvalid input.");
    }
};

async function withdrawAmount() {
    console.log("Withdraw Amount");
    account.details();
    newline();
    
    let input = Number(await getInput("Deposit Amount: "));

    if (input > 0) {
        account.withdraw(Number(input));
    } else {
        console.log("\nInvalid input.");
    }
};

// Currency Exchange
async function currencyExchange() {
    console.log("Foreign Currency Exchange");
    console.log("Source Currency Option:");
    printCurrencies();

    let sourceCurrency = Number(await getInput("Source Currency: "));
    sourceCurrency = getCurrency(sourceCurrency);

    if (sourceCurrency === null) {
        return console.log("\nInvalid input.")
    };
    if (exchangeRatesToPHP.get(sourceCurrency) === 0) {
        return console.log(`\n${sourceCurrency} exchange rate hasn't been set.`);
    };

    let sourceAmount = Number(await getInput("Source Amount: "));
    
    if (!sourceAmount || sourceAmount <= 0) {
        return console.log("\nInvalid input.")
    };

    console.log("\nExchange Currency Options:");
    printCurrencies();

    let exchangeCurrency = Number(await getInput("Exchange Currency: "));
    exchangeCurrency = getCurrency(exchangeCurrency);

    if (exchangeCurrency === null) {
        return console.log("\nInvalid input.")
    };
    if (exchangeRatesToPHP.get(exchangeCurrency) === 0) {
        return console.log(`\n${exchangeCurrency} exchange rate hasn't been set.`);
    };

    exchangeAmount = sourceAmount * exchangeRatesToPHP.get(sourceCurrency) / exchangeRatesToPHP.get(exchangeCurrency);
    console.log(`Exchange Amount: ${roundValueToString(exchangeAmount)}`);
};

async function recordExchangeRate() {
    console.log("Record Exchange Rate\n");
    printCurrencies();
    
    let currency = Number(await getInput("Select Foreign Currency: "));
    currency = getCurrency(currency);

    if (currency === null) {
        return console.log("\nInvalid input.")
    };

    let newRate = Number(await getInput("Exchange Rate: "));
    
    if (!newRate || newRate <= 0) {
        return console.log("\nInvalid input.")
    };

    exchangeRatesToPHP.set(currency, roundValue(newRate));
};

async function showInterestComputation() {
    console.log("Show Interest Amount");
    account.details();
    console.log("Interest Rate: 5%\n");
    
    days = Number(await getInput("Total Number of Days: "));

    if (days <= 0) {
        return console.log("\nInvalid input.")
    };

    let balance = roundValue(account.balance);

    console.log("\nDay | Interest | Balance |");

    for (let i = 0; i < days; i++) {
        let interest = roundValue(balance * 0.05 / 365);
        balance = roundValue(balance + interest);
        console.log(`${padNumber(i + 1, 3)} | ${padNumber(interest, 8)} | ${padNumber(roundValueToString(balance), 7)} |`);
    }

    //Daily Interest = (End-of-Day Balance) x (Annual Interest Rate / 365)

};

// Interest Amount

function printMainMenu() {
    console.log("\nSelect Transaction");
    console.log("[1] Register Account Name");
    console.log("[2] Deposit Amount");
    console.log("[3] Withdraw Amount");
    console.log("[4] Currency Exchange");
    console.log("[5] Record Exchange Rates");
    console.log("[6] Show Interest Computation\n");
};

let option;

async function mainMenu() {
    while (true) {
        printMainMenu();
        option = await getInput("Enter option: ");
        console.log();
        
        switch (Number(option)) {
            case 1: 
                await registerAccountName();
                break;
            case 2:
                await depositAmount();
                break;
            case 3:
                await withdrawAmount();
                break;
            case 4:
                await currencyExchange();
                break;
            case 5:
                await recordExchangeRate();
                break;
            case 6:
                await showInterestComputation();
                break;
            default:
                console.log("Invalid input.\n");
                break;
        }
    }
};

// Start the program by opening the main menu
mainMenu();

// Handles the termination of the program
rl.on('SIGINT', () => {
    console.log("\n\nTerminating program...");
    rl.close();
});