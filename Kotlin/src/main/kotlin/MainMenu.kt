import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.read
import org.jetbrains.kotlinx.dataframe.io.writeCsv
import java.io.File

fun mainMenu() {
    var df : DataFrame<*>? = null
    var excluded : DataFrame<*>
    var dataFramesTemp : List<DataFrame<*>>


    dataFramesTemp = loadData()
    df = dataFramesTemp[0]
    excluded = dataFramesTemp[1]

    generateReports(df)
}

private fun loadData() : List<DataFrame<*>> {
    val filename: String = "dpwh_flood_control_projects.csv"
    var filteredDf : DataFrame<*>
    var excludedDf : DataFrame<*>
    var unfilteredDf: DataFrame<*>
    val totalRowsLoaded: Int
    val rowsFiltered: Int

    print("Loading data...")

    unfilteredDf = DataFrame.read(filename)
    totalRowsLoaded = unfilteredDf.rowsCount()
    unfilteredDf = convertNumbers(unfilteredDf)

    // building dataframe to use for reports

    // filtering rows with null values; null values in "Municipality" column unchecked
    filteredDf = unfilteredDf.dropNulls { allExcept("Municipality") }
    filteredDf = filteredDf.filter { isFrom2021To2023(it) }
    filteredDf = addDerivedFields(filteredDf)
    rowsFiltered = filteredDf.rowsCount()

    // building dataframe of rows excluded
    excludedDf = unfilteredDf.filter { hasNullExceptMunicipality(it) or !isFrom2021To2023(it) }

    println("\t$totalRowsLoaded rows loaded, $rowsFiltered rows filtered for 2021-2023")
    return listOf(filteredDf, unfilteredDf)
}

private fun generateReports(df : DataFrame<*>) {
    val report1Df: DataFrame<*>
    val report2Df: DataFrame<*>
    val summaryMap: Map<String, Number>
    val report1Filename : String = "report1_regional_summary.csv"
    val report2Filename : String = "report2_contractor_ranking.csv"
    val summaryFilename : String = "summary.json"
    val summaryJSONString : String

    println("Generating reports...")

    // generating reports
    report1Df = generateReport1(df)
    report2Df = generateReport2(df)
    summaryMap = generateSummary(df)
    summaryJSONString = makeSummaryJSONString(summaryMap)

    // writing reports to files
    report1Df.writeCsv(File(report1Filename))
    report2Df.writeCsv(File(report2Filename))
    File(summaryFilename).writeText(summaryJSONString)

    println("Outputs saved to individual files...")
    println()

    // printing output summaries to console
    println("Report 1: Regional Flood Mitigation Efficiency Summary")
    println("Filtered: 2021-2023 Projects")
    println()

    println(report1Df.head(2))
    println("Full table exported to $report1Filename")
    println()

    println("Report 2: Top Contractors Performance Ranking")
    println("Top 15 by TotalCost, >= 5 Projects")
    println()

    println(report2Df.head(2))
    println("Full table exported to $report2Filename")
    println()

    println("Summary stats ($summaryFilename)")
    println(summaryJSONString.replace("\n", "").replace("\t", ""))

}


