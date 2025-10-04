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
}

const exchangeRatesToPHP = new Map([
    ["PHP", 0],
    ["USD", 0],
    ["JPY", 0],
    ["GBP", 0],
    ["EUR", 0],
    ["CNY", 0]
]);

function format(value) {
    return value.toFixed(2);
}

const account = {
    name: "user",
    balance: 0.00,

    deposit: function(amount) {
        this.balance += amount;
        console.log("Updated Balance: " + format(this.balance) + "\n");
    },

    withdraw: function(amount) {
        if (amount > this.balance) {
            console.log("Insufficient funds.\n");
        } else {
            this.balance -= amount;
            console.log("Updated Balance: " + format(this.balance) + "\n");
        }
    },

    details: function() {
        console.log("Account Name: " + this.name);
        console.log("Current Balance: " + format(this.balance));
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
};

function printCurrencies() {
    console.log("[1] Philippine Peso (PHP)");
    console.log("[2] United States Dollar (USD)");
    console.log("[3] Japanese Yen (JPY)");
    console.log("[4] British Pound Sterling (GBP)");
    console.log("[5] Euro (EUR)");
    console.log("[6] Chinese Yuan Renminni (CNY)\n");
}

async function registerAccountName() {
    rl.resume();
    console.log("Register Account Name");

    let input;
    do {
        input = await getInput("Account Name: ");
    } while (input === "");
    
    account.name = input; // assign input;
}

async function depositAmount() {
    console.log("Deposit Amount");
    account.details();
    console.log();
    
    let input = await getInput("Deposit Amount: ");

    if (Number(input) > 0) {
        account.deposit(Number(input));
    } else {
        console.log("Invalid input.");
    }
}

async function withdrawAmount() {
    console.log("Withdraw Amount");
    account.details();
    console.log();
    
    let input = await getInput("Withdraw Amount: ");

    if (Number(input) > 0) {
        account.withdraw(Number(input));
    } else {
        console.log("Invalid input.");
    }
}

// Currency Exchange
async function currencyExchange() {
    console.log("Foreign Currency Exchange");
    console.log("Source Currency Option:");
    printCurrencies();

    // inputs; add validation
    console.log("Source Currency: ");
    console.log("Source Amount: ");

    console.log("Exchange Currency Option:");
    printCurrencies();

    // inputs; add validation
    console.log("Exchange Currency: ");

    // convert to php then convert to exchange currency
    console.log("Exchange Amount: ");
}

async function recordExchangeRate() {
    console.log("Record Exchange Rate\n");
    printCurrencies();
    
    let currency = await getInput("Select Foreign Currency: ");
    let newRate = await getInput("Exchange Rate: ");

    exchangeRatesToPHP[currency] = newRate; // input
}

async function showInterestComputation() {
    console.log("Show Interest Amount");
    console.log(account.details + "Interest Rate: 5%\n");
    
    console.log("Total Number of Days: ");
    // input & validation

    let { balance } = account;

    //

}

// Interest Amount

function printMainMenu() {
    console.log("\nSelect Transaction");
    console.log("[1] Register Account Name");
    console.log("[2] Deposit Amount");
    console.log("[3] Withdraw Amount");
    console.log("[4] Currency Exchange");
    console.log("[5] Record Exchange Rates");
    console.log("[6] Show Interest Computation\n");
}

let option;

async function mainMenu() {
    while (true) {
        printMainMenu();
        option = await getInput("Enter option: ");
        console.log();
        
        try {
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
        } catch (err) {
            console.log(err);
            //console.log("Invalid input.\n");
        }
    }
}

// Start the program by opening the main menu
mainMenu();

// Handles the termination of the program
rl.on('SIGINT', () => {
    console.log("\n\nTerminating program...");
    rl.close();
});