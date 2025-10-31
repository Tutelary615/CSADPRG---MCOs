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

import readline from 'readline';
import csv from 'csv-parser';
import fs from 'fs';

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

// FOR TESTING
const results = [];
fs.createReadStream('dpwh_flood_control_projects.csv')
    .pipe(csv())
    .on('data', (data) => results.push(data))
    .on('end', () => {
    console.log(results);
    // [
    //   { NAME: 'Daffy Duck', AGE: '24' },
    //   { NAME: 'Bugs Bunny', AGE: '22' }
    // ]
});

// Handles the termination of the program via Ctrl + C or main menu
rl.on('close', () => {
    console.log("\n\nTerminating program...");
    process.exit(0);
})