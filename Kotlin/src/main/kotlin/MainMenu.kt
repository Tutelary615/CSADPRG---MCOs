import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.writeCsv
import java.io.File

/**
 * contains all functions related to the main menu of the program;
 * calls functions for the features of the program (loading the data, generating reports)
 */
fun mainMenu() {
    var df : DataFrame<*>? = null
    var choice : String = ""


    do {
        println("Main Menu")
        println("[1]: Load the file")
        println("[2]: Generate reports")
        println("[3]: Exit")
        choice = getMenuInput("1", "2", "3")
        println()
        when (choice) {
            "1" -> df = loadData()
            "2" -> generateReports(df)
        }
        println()
    } while (choice != "3")

}

/**
 * contains all the functions required for loading the data
 * @return a DataFrame containing the data that will be used for analysis
 */
fun loadData() : DataFrame<*> {
    val filename: String = "dpwh_flood_control_projects.csv"
    var filteredDf : DataFrame<*>
    var excludedDf : DataFrame<*>
    var unfilteredDf: DataFrame<*>
    val totalRowsLoaded: Int
    val rowsFiltered: Int
    val invalidDataFilename : String = "invalid_data.csv"

    print("Processing data...")

    unfilteredDf = DataFrame.readCsv(filename)
    totalRowsLoaded = unfilteredDf.rowsCount()
    unfilteredDf = convertNumbers(unfilteredDf)

    // building dataframe to use for reports

    // filtering rows with null values; null values in "Municipality" column unchecked
    filteredDf = unfilteredDf.dropNulls { allExcept("Municipality") }
    filteredDf = filteredDf.filter { isFrom2021To2023(it) }
    filteredDf = addDerivedFields(filteredDf)
    rowsFiltered = filteredDf.rowsCount()

    // building dataframe of rows excluded
    excludedDf = unfilteredDf.filter { hasNullExceptMunicipality(it) or !isFrom2021To2023(it)}
    excludedDf.writeCsv(File(invalidDataFilename))

    println("\t$totalRowsLoaded rows loaded, $rowsFiltered rows filtered for 2021-2023")
    println("Invalid/excluded rows exported to $invalidDataFilename")
    println("Returning to main menu...")
    return filteredDf
}

/**
 * generates all reports; displays console outputs
 * @param df the dataframe containing the source data
 */
 private fun generateReports(df : DataFrame<*>?) {
    val report1Df: DataFrame<*>
    val report2Df: DataFrame<*>
    val report3Df: DataFrame<*>
    val summaryMap: Map<String, String>
    val report1Filename : String = "report1_regional_summary.csv"
    val report2Filename : String = "report2_contractor_ranking.csv"
    val report3Filename : String = "report3_annual_trends.csv"
    val summaryFilename : String = "summary.json"
    val divider : String = "=================================================================================================================================="
    val summaryJSONString : String
    var menuInput : String

    if (df != null) {

        println("Generating reports...")

        // generating reports
        report1Df = generateRegionalSummary(df)
        report2Df = generateContractorRanking(df)
        report3Df = generateAnnualTrends(df)
        summaryMap = generateSummary(df)
        summaryJSONString = makeSummaryJSONString(summaryMap)

        // writing reports to files
        report1Df.writeCsv(File(report1Filename))
        report2Df.writeCsv(File(report2Filename))
        report3Df.writeCsv(File(report3Filename))
        File(summaryFilename).writeText(summaryJSONString)

        println("Outputs saved to individual files...")
        println()

        // printing output summaries to console
        println(divider)
        println("Report 1: Regional Flood Mitigation Efficiency Summary")
        println("Filtered: 2021-2023 Projects")
        println()

        println(report1Df.head(2))
        println("Full table exported to $report1Filename")
        println()

        println(divider)
        println("Report 2: Top Contractors Performance Ranking")
        println("Top 15 by TotalCost, >= 5 Projects")
        println()

        println(report2Df.head(2))
        println("Full table exported to $report2Filename")
        println()

        println(divider)
        println("Report 3: Annual Project Type Cost Overrun Trends")
        println("Grouped by FundingYear and TypeOfWork")
        println()

        println(report3Df.head(2))
        println("Full table exported to $report3Filename")
        println()

        println(divider)
        println("Summary stats ($summaryFilename)")
        println()
        println(summaryJSONString.replace("\n", "").replace("\t", ""))
        println()
        println(divider)

    } else {
        println("Data has not been loaded")
    }
    println()
    do {
        println("Return to main menu (Y/N)")
        menuInput = getMenuInput("Y", "y", "N", "n")
    } while (!menuInput.equals("Y", ignoreCase = true))
    println()
}

/**
 * gets menu input from the user
 * @param validChoices valid menu inputs
 * @return the choice of the user
 */
private fun getMenuInput(vararg validChoices : String) : String {
    var input : String = ""
    do {
        print("Enter choice: ")
        input = readln().trim()
    } while (!validChoices.contains(input))
    return input
}


