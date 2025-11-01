/*********************
    Last names: Almero, Aquino, Dolot, Marquez
    Language: JavaScript
    Paradigm(s): Procedural Programming
*********************/

/*********************
    Program guide:
    1. Install Node.js through this link: https://nodejs.org/en/download
    2. Open command prompt/terminal and go to the directory of this file.
    3. Enter the following command to install packages:
        npm install
    4. Enter the following command to run:
        node MCO2_7_JavaScript.js
    5. Press Ctrl + C to terminate the program. 
*********************/

const readline = require('readline');
const dataForge = require('data-forge');
require('data-forge-fs');
// import csv from 'csv-parser';
// import fs from 'fs';

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

function formatWholeNumber(num) {
    return num.toLocaleString('en-US');
}

// FOR TESTING
// const results = [];
// fs.createReadStream('dpwh_flood_control_projects.csv')
//     .pipe(csv())
//     .on('data', (data) => results.push(data))
//     .on('end', () => {
//     console.log(results);
// });

// const dataForge = require('data-forge');
// dataForge.readFile('./input-data-file.csv') // Read CSV file (or JSON!)
//     .parseCSV()
//     .parseDates(["Column B"]) // Parse date columns.
//     .parseInts(["Column B", "Column C"]) // Parse integer columsn.
//     .parseFloats(["Column D", "Column E"]) // Parse float columns.
//     .dropSeries(["Column F"]) // Drop certain columns.
//     .where(row => predicate(row)) // Filter rows.
//     .select(row => transform(row)) // Transform the data.
//     .asCSV() 
//     .writeFile("./output-data-file.csv") // Write to output CSV file (or JSON!)
//     .then(() => {
//         console.log("Done!");
//     })
//     .catch(err => {
//         console.log("An error occurred!");
//     });

async function loadFile() {
    console.log("Processing dataset...")

    // Initial parsing of CSV file
    const raw = await dataForge.readFile('dpwh_flood_control_projects.csv')
        .parseCSV()
    
    // FIX BACKSPACING
    console.log(`\b\b\b ${formatWholeNumber(raw.count())} rows loaded, `);
    
    // Parsing and filtering 
    const data = raw
        .parseDates(["ActualCompletionDate", "StartDate"], "YYYY-MM-DD") // FIX
        .parseInts(["FundingYear", "ContractorCount"])
        .parseFloats(["ApprovedBudgetForContract", "ContractCost", "ProjectLatitude", "ProjectLongitude", "ProvincialCapitalLatitude", "ProvincialCapitalLongitude"])
        .where(row => row.FundingYear >= 2021 && row.FundingYear <= 2023)

        console.log(data.at(9))
        console.log(formatWholeNumber(data.count()))
    return data;
}

function displayMainMenu() {
    console.log("\nSelect Language Implementation");
    console.log("[1] Load the file");
    console.log("[2] Generate Reports");
    console.log("[3] Exit\n");
}

async function mainMenu() {
    let option;
    let data;
    
    while (true) { 
        displayMainMenu();

        option = await getInput("Enter choice: ");

        switch (option) {
            case '1':
                data = await loadFile();
                break;
            case '2':
                if (data === undefined) {
                    console.log("Please load the file first.");
                    break;
                }
                break;
            case '3':
                rl.close();
            default:
                console.log("Invalid input.");
        }
    } 
}

mainMenu();

// Handles the termination of the program via Ctrl + C or main menu
rl.on('close', () => {
    console.log("\n\nTerminating program...");
    process.exit(0);
})