import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.*
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * generates report 3: Annual trends
 * @param df the source dataframe
 * @return dataframe containing the report
 * NOTE: numerical values in the returned dataframe are represented as strings
 */
fun generateAnnualTrends(df : DataFrame<*>) : DataFrame<*> {
    var annualTrendsDf : DataFrame<*>
    val dfGrouped = df.groupBy("FundingYear", "TypeOfWork")

    annualTrendsDf = dfGrouped.aggregate {
        rowsCount() into "TotalProjects"
        mean("CostSavings") into "AvgSavings"
        computeOverrunRate(it as DataFrame<*>) into "OverrunRate"
    }

    annualTrendsDf = annualTrendsDf.add("YoYChange") {computeYoYChange(it)}
    annualTrendsDf = annualTrendsDf.sortBy {it["FundingYear"] and it["AvgSavings"].desc()}

    annualTrendsDf = formatAnnualTrends(annualTrendsDf)
    return annualTrendsDf
}

/**
 * computes the overrun rate
 * @param grouping the row group of the source dataframe (grouped by FundingYear and TypeOfWork)
 * whose overrun rate is being computed
 * @return the overrun rate
 */
private fun computeOverrunRate(grouping : DataFrame<*>) : Double {
    val totalProjects : Int = grouping.count()
    val overrunProjects : Int = grouping.count { (it["CostSavings"] as Double) < 0}
    val overrunRate : Double = (overrunProjects.toDouble() / totalProjects.toDouble()) * 100

    return overrunRate
}

/**
 * computes the YoYChange values of report 3
 * @param row the row of report 3 whose YoYChange value is being computed
 * @return the YoYChange value of row
 */
private fun computeYoYChange(row : DataRow<*>) : Double {
    val fundingYear : Int = row["FundingYear"] as Int
    val typeOfWork : String = row["TypeOfWork"] as String
    val curYearSavings : Double
    val prevYearSavings : Double
    val prevYearRow : DataRow<*>

    if (fundingYear == 2021) {
        return 0.0
    }

    // checking if there is a corresponding row from the previous year
    try {
        prevYearRow = (row.df().filter { (it["FundingYear"] == fundingYear - 1 ) && it["TypeOfWork"] == typeOfWork})[0]
    } catch (e: Exception) {
        return 0.0
    }
    prevYearSavings = prevYearRow["AvgSavings"] as Double
    curYearSavings = row["AvgSavings"] as Double

    return ((curYearSavings - prevYearSavings) / prevYearSavings) * 100

}

/**
 * formats the report 3 dataframe for export
 * @param annualTrendsDf the unformatted dataframe
 * @return the formatted dataframe
 */
private fun formatAnnualTrends(annualTrendsDf : DataFrame<*>) : DataFrame<*> {
    var formattedDf: DataFrame<*> = annualTrendsDf

    val twoDecimalFormat = DecimalFormat("#,##0.00")
    val intWithCommaFormat = DecimalFormat("#,###")

    twoDecimalFormat.roundingMode = RoundingMode.HALF_UP

    formattedDf = formattedDf.replace("TotalProjects")
                             .with { cellVal -> cellVal.map { intWithCommaFormat.format(it) } }

    formattedDf = formattedDf.replace("AvgSavings", "OverrunRate", "YoYChange")
                             .with { cellVal -> cellVal.map { twoDecimalFormat.format(it) } }

    return formattedDf
}

