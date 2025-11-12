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
    4. Enter any of the following commands to run:
        npm run start
        node MCO2_7_JavaScript.js
    5. Press Ctrl + C to terminate the program. 
*********************/

const readline = require('readline');
const csvReader = require('csv-parser');
const fs = require('fs');
const csvFormatter = require('@fast-csv/format');

// Interface for reading input
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
    prompt: ''
});

/**
 * Promise-based function that gets user input
 * @param query prompt to be displayed to the user
 * @returns Promise of the output
 */
function getInput(query) {
    return new Promise(resolve => {
        rl.question(query, resolve);
    });
}

/**
 * Rounds a value to 2 decimals and returns it as a Number
 * @param value value to be rounded
 * @returns value rounded to 2 decimals
*/
function roundValue(value, decimals = 2) {
    return parseFloat(value.toFixed(decimals));
}

/**
 * Rounds a value to 2 decimals and returns it as a String
 * @param value value to be rounded
 * @returns string of value rounded to 2 decimals 
*/
function roundValueToString(value, decimals = 2) {
    return value.toFixed(decimals);
}

/**
 * Formats a whole number with commas
 * @param num number to be formatted
 * @returns string of formatted whole number
 */
function formatWholeNumber(num) {
    return num.toLocaleString('en-US');
}

/**
 * Formats a decimal number with commas (if necessary) and specified decimal places
 * @param num number to be formatted
 * @param decimals number of decimal places (default: 2)
 * @returns string of formatted decimal number
 */
function formatDecimalNumber(num, decimals = 2) {
    num = roundValue(num, decimals);
    return num.toLocaleString("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

/**
 * Gets the average of an array
 * @param arr array of numbers 
 * @param len length of the array (default: arr.length) 
 * @returns average of the array
 */
function getAverage(arr, len = 0) {
    if (len === 0) {
        len = arr.length;
    }
    return arr.reduce((sum, curr) => sum + curr, 0) / len;
}

/**
 * Formats all number values in an object to strings with commas
 * @param data object to be formatted 
 */
function formatData(data) {
    for (const row of data) {
        for (const key in row) {
            if (typeof row[key] === "number") {
                row[key] = formatDecimalNumber(row[key])
            }
        }
    }
}

/**
 * Parses and reads a CSV file
 * @returns Promise of array of objects representing the rows of the CSV file
 */
async function readFile() {
    return new Promise((resolve, reject) => {
        const results = [];
    
        fs.createReadStream('dpwh_flood_control_projects.csv')
            .pipe(csvReader())
            .on('data', (row) => results.push(row))
            .on('end', () => resolve(results))
            .on('error', (err) => reject(err));
    });
}

/**
 * Writes report data to a CSV file and exports it
 * @param data array of objects representing the rows to be written 
 * @param file output file name 
 * @returns Promise of completion of writing to the given CSV file
 */
async function writeCsvFile(data, file) {
    return new Promise((resolve, reject) => {
        const writeStream = fs.createWriteStream(file)
            .on('finish', resolve)          
            .on('error', reject);
        
        const csvStream = csvFormatter.format({ headers: true, writeHeaders: true })
            .on('error', reject)
            .pipe(writeStream)
            
        csvFormatter.writeToPath(file, data, { headers: true, writeHeaders: true })
            .on('error', reject)

        console.log(`Exported to CSV file: ${file}\n`)
        csvStream.end();
    })
}

/**
 * Writes report data to a JSON file and exports it
 * @param data objects to be written to the file 
 * @param file output file name 
 */
async function writeJsonFile(data, file) {
    await fs.promises.writeFile(file, JSON.stringify(data, null, 4), 'utf8');
    console.log(`Exported to JSON file: ${file}\n`);
}

/**
 * Loads and processes a CSV file through filtering out invalid rows and executing type conversions
 * @returns processed data as an array of objects
 */
async function loadFile() {
    process.stdout.write("Processing dataset...")

    // Initial parsing of CSV file
    const raw = await readFile();

    // Printing rows loaded from CSV
    process.stdout.write(` (${formatWholeNumber(raw.length)} rows loaded, `);

    // Filtering out invalid rows
    const invalid = raw
        .filter(row => Number.isNaN(Number(row.ApprovedBudgetForContract))
            || Number.isNaN(Number(row.ContractCost))
            || row.FundingYear < 2021 
            || row.FundingYear > 2023);

    // Filtering valid rows to be included in the final reports
    const data = raw
        // Filters out rows with incorrect values for monetary columns
        .filter(row => !Number.isNaN(Number(row.ApprovedBudgetForContract))
            && !Number.isNaN(Number(row.ContractCost)))
        // Filters out rows not within the 2021 - 2023 range
        .filter(row => row.FundingYear >= 2021 && row.FundingYear <= 2023)
        // Type conversions and new columns
        .map((curr) => {
            // Date conversion
            curr.ActualCompletionDate = new Date([curr.ActualCompletionDate, "08:00:00"]);
            curr.StartDate = new Date([curr.StartDate, "08:00:00"]);

            // Integer conversion
            curr.FundingYear = Number(curr.FundingYear);
            curr.ContractorCount = Number(curr.ContractorCount);

            // Floating point value conversion
            curr.ApprovedBudgetForContract = Number(curr.ApprovedBudgetForContract);
            curr.ContractCost = Number(curr.ContractCost);
            curr.ProjectLatitude = Number(curr.ProjectLatitude);
            curr.ProjectLongitude = Number(curr.ProjectLongitude);
            curr.ProvincialCapitalLatitude = Number(curr.ProvincialCapitalLatitude);
            curr.ProvincialCapitalLongitude = Number(curr.ProvincialCapitalLongitude);

            // Creation and computation of new rows
            curr.CostSavings = curr.ApprovedBudgetForContract - curr.ContractCost;
            curr.CompletionDelayDays = (curr.ActualCompletionDate - curr.StartDate) / (1000 * 60 * 60 * 24);

            return curr;
        })

    // Printing final row count after filtering
    process.stdout.write(`${formatWholeNumber(data.length)} filtered for 2021-2023), ${formatWholeNumber(invalid.length)} invalid rows filtered out.`);

    // Exporting error rows to a separate CSV file
    console.log("\n\nExporting invalid data...");
    await writeCsvFile(invalid, 'invalid_data.csv');
    return data;
}

/**
 * Generates Report 1: Regional Flood Mitigation Efficiency Summary (2021-2023)
 * @param data data to be processed 
 * @returns report data as an array of objects
 */
function generateReport1(data) {
    // Gets distinct regions (region and main island as one string)
    const regions = [...new Set(data.map(row => row.Region + "," + row.MainIsland))];

    // Creates initial report structure (one object per region)
    const reports = regions.map((curr) => { 
        const temp = curr.split(","); // Splits region and main island
        curr = {
            Region: temp[0],
            MainIsland: temp[1],
            TotalBudget: 0,
            MedianSavings: [],
            AvgDelay: [],
            HighDelayPct: 0
        }
        return curr
    });

    // Populates report data per region
    for (const row of data) {
        const obj = reports.find(x => x.Region === row.Region);

        obj.TotalBudget += row.ApprovedBudgetForContract;
        obj.MedianSavings.push(row.CostSavings);
        obj.AvgDelay.push(row.CompletionDelayDays);
        if (row.CompletionDelayDays > 30) {
            (obj.HighDelayPct)++;
        }
    }

    // Array of efficiency scores for normalization
    const efficiencies = [];

    // Final computations per region
    for (const row of reports) {
        // Median savings calculation
        const len = row.MedianSavings.length;
        const mid = Math.floor(len / 2);

        row.MedianSavings = row.MedianSavings.sort((a, b) => a - b);
        
        if (len % 2 === 0) {
            row.MedianSavings = (row.MedianSavings[mid - 1] + row.MedianSavings[mid]) / 2;
        } else {
            row.MedianSavings = row.MedianSavings[mid];
        }

        // Average delay and high delay percentage calculation
        row.AvgDelay = getAverage(row.AvgDelay);
        row.HighDelayPct = row.HighDelayPct / len * 100;

        // Efficiency score tallying
        row.EfficiencyScore = row.MedianSavings / row.AvgDelay * 100;
        efficiencies.push(row.EfficiencyScore);
    }

    // Normalization of efficiency scores
    const maxEff = Math.max(...efficiencies);
    const minEff = Math.min(...efficiencies);

    // Gets normalized efficiency scores per region
    for (const row of reports) {
        row.EfficiencyScore = ((row.EfficiencyScore - minEff) / (maxEff - minEff)) * 100;
    }

    // Sorts by efficiency score descending
    reports.sort((a, b) => b.EfficiencyScore - a.EfficiencyScore);

    formatData(reports);
    return reports;
}

/**
 * Generates Report 2: Top Contractors Performance Ranking (Top 15 by Total Cost, min. 5 Projects)
 * @param data data to be processed 
 * @returns report data as an array of objects
 */
function generateReport2(data) {
    // Gets distinct contractors
    const contractors = [...new Set(data.map(row => row.Contractor))];

    // Creates initial report structure (one object per contractor)
    const reports = contractors.map((curr) => {
        curr = {
            Contractor: curr,
            TotalCost: 0,
            NumProjects: 0,
            AvgDelay: [],
            TotalSavings: 0
        }
        return curr;
    });

    // Populates report data per contractor
    for (const row of data) {
        const obj = reports.find(x => x.Contractor === row.Contractor);

        obj.TotalCost += row.ContractCost;
        (obj.NumProjects)++;
        obj.AvgDelay.push(row.CompletionDelayDays);
        obj.TotalSavings += row.CostSavings;
    }

    // Filters contractors with at least 5 projects
    const filterReports = reports.filter(curr => curr.NumProjects >= 5);

    // Final computations per contractor
    for (const row of filterReports) {
        // Formats number of projects
        row.NumProjects = formatWholeNumber(row.NumProjects);
        
        // Average delay calculation
        row.AvgDelay = getAverage(row.AvgDelay);

        // Reliability index calculation
        row.ReliabilityIndex = (1 - (row.AvgDelay / 90)) * (row.TotalSavings / row.TotalCost) * 100;
        if (row.ReliabilityIndex > 100) {
            row.ReliabilityIndex = 100;
        }

        // Risk flag assignment
        if (row.ReliabilityIndex < 50) {
            row.RiskFlag = "High Risk";
        } else {
            row.RiskFlag = "Low Risk";
        }
    }

    // Sorts by total cost descending
    filterReports.sort((a, b) => b.TotalCost - a.TotalCost);

    formatData(reports);
    
    // Gets top 15 contractors and appends rank for the final report
    const finalReport = filterReports
        .slice(0, 15)
        .map((curr, index) => {
            currRank = { Rank: index + 1 }
            return Object.assign(currRank, curr);
    })

    return finalReport;
}

/**
 * Generates Report 3: Annual Project Type Cost Overrun Trends (by Funding Year and Type of Work)
 * @param data data to be processed 
 * @returns report data as an array of objects
 */
function generateReport3(data) {
    // Gets distinct categories (funding year and type of work as one string)
    const categories = [...new Set(data.map(row => row.FundingYear + "," + row.TypeOfWork))];

    // Creates initial report structure (one object per year and type of work)
    const reports = categories.map((curr) => { 
        const temp = curr.split(","); // Splits funding year and type of work
        curr = {
            FundingYear: temp[0],
            TypeOfWork: temp[1],
            TotalProjects: 0,
            AvgSavings: [],
            OverrunRate: 0,
        }
        return curr
    });

    // Populates report data per funding year and type of work
    for (const row of data) {
        const obj = reports.find(x => x.FundingYear === String(row.FundingYear) && x.TypeOfWork === row.TypeOfWork);

        (obj.TotalProjects)++;
        obj.AvgSavings.push(row.CostSavings);
        if (row.CostSavings < 0) {
            (obj.OverrunRate)++;
        }
    }

    // Final computations per funding year and type of work
    for (const row of reports) {
        const len = row.AvgSavings.length;

        // Average savings and overrun rate calculation
        row.AvgSavings = getAverage(row.AvgSavings);
        row.OverrunRate = row.OverrunRate / len * 100;
    }

    // Sorts by funding year ascending, then by average savings descending
    reports.sort((a, b) => a.FundingYear - b.FundingYear || b.AvgSavings - a.AvgSavings);

    // Year-over-year change calculation
    for (const row of reports) {
        // Formats number of total projects
        row.TotalProjects = formatWholeNumber(row.TotalProjects);
        
        // Finds previous year data for the same type of work
        const pYear = reports.find(x => x.FundingYear === String(row.FundingYear - 1) && x.TypeOfWork === row.TypeOfWork);

        // Year-over-year change calculation based on availability of previous year data
        if (pYear) {
            row.YoYChange = (row.AvgSavings - pYear.AvgSavings) / pYear.AvgSavings * 100;
        } else {
            row.YoYChange = 0;
        }
    }

    formatData(reports);
    return reports;
}

/**
 * Generates summary statistics of the dataset
 * @param data data to be processed 
 * @returns summary data as an object
 */
function generateSummaryJSON(data) {
    const summary = {};

    // Calculating summary statistics
    summary.total_projects = [...new Set(data.map(row => row.ProjectId))].length;
    summary.total_contractors = [...new Set(data.map(row => row.Contractor))].length;
    summary.total_provinces = [...new Set(data.map(row => row.Province))].length;
    summary.global_avg_delay = getAverage(data.map(curr => curr.CompletionDelayDays), summary.total_projects);
    summary.total_savings = data.reduce((total, curr) => total + curr.CostSavings, 0);

    // Formatting decimal values
    summary.global_avg_delay = roundValueToString(summary.global_avg_delay);
    summary.total_savings = roundValueToString(summary.total_savings);

    return summary;
}

/**
 * Retrieved data for each report and generates the reports as exported files
 * @param data data to be processed for the reports 
 */
async function generateReports(data) {
    // Function, Report Title, Output File Name
    const reports = [
        [generateReport1, "Report 1: Regional Flood Mitigation Efficiency Summary (2021-2023)", "report1_regional_summary.csv"], 
        [generateReport2, "Report 2: Top Contractors Performance Ranking (Top 15 by Total Cost, min. 5 Projects)", "report2_contractor_ranking.csv"], 
        [generateReport3, "Report 3: Annual Project Type Cost Overrun Trends (by Funding Year and Type of Work)", "report3_annual_trends.csv"],
        [generateSummaryJSON, "Summary Stats", "summary.json"]
    ];

    console.log("\nGenerating reports...");
    console.log("Outputs saved to individual files...\n");

    for (const report of reports) {
        // Retrieves data of the report
        console.log(report[1]);
        let reportData = report[0](structuredClone(data));

        // Exports data to the specified file type
        switch (report[2].split('.')[1]) {
            case 'csv': 
                console.table(reportData.slice(0, 2));
                await writeCsvFile(reportData, report[2]);
                break;
            case 'json':
                console.log(reportData);
                await writeJsonFile(reportData, report[2]);
                break;
            default:
                console.log(`Data could not be saved to ${report[2]}`);
        }
    }
}

/**
 * Prints the main menu options
 */
function displayMainMenu() {
    console.log("\nSelect Language Implementation");
    console.log("[1] Load the file");
    console.log("[2] Generate Reports");
    console.log("[3] Exit\n");
}

/**
 * Executes the main menu
 */
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
                
                await generateReports(data);
                break;
            case '3':
                rl.close();
            default:
                console.log("Invalid input.");
        }
    } 
}

// Start the main menu
mainMenu();

// Handles the termination of the program via Ctrl + C or main menu
rl.on('close', () => {
    console.log("\n\nTerminating program...");
    process.exit(0);
})