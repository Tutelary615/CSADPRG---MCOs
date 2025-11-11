import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.*
import java.math.BigDecimal
import kotlin.math.abs
import kotlin.isNaN
import java.math.RoundingMode
import java.text.DecimalFormat

fun generateReport3(df : DataFrame<*>) : DataFrame<*> {
    var report3Df : DataFrame<*>
    val dfGrouped = df.groupBy("FundingYear", "TypeOfWork")

    report3Df = dfGrouped.aggregate {
        rowsCount() into "TotalProjects"
        mean("CostSavings") into "AvgSavings"
        computeOverrunRate(it as DataFrame<*>) into "OverrunRate"
    }

    report3Df = report3Df.add("YoYChange") {computeYoYChange(it)}
    report3Df = report3Df.sortBy {it["FundingYear"] and it["AvgSavings"].desc()}

    report3Df = formatReport3(report3Df)
    return report3Df
}

private fun computeOverrunRate(grouping : DataFrame<*>) : Double {
    val totalProjects : Int = grouping.count()
    val overrunProjects : Int = grouping.count { (it["CostSavings"] as Double) < 0}
    val overrunRate : Double = (overrunProjects.toDouble() / totalProjects.toDouble()) * 100

    return overrunRate
}

private fun computeYoYChange(row : DataRow<*>) : Double {
    val fundingYear : Int = row["FundingYear"] as Int
    val typeOfWork : String = row["TypeOfWork"] as String
    val curYearSavings : Double = row["AvgSavings"] as Double
    val prevYearSavings : Double
    val prevYearRow : DataRow<*>

    if (fundingYear == 2021) {
        return 0.0
    }
    try {
        prevYearRow = (row.df().filter { (it["FundingYear"] == fundingYear - 1 ) && it["TypeOfWork"] == typeOfWork})[0]
    } catch (e: Exception) {
        return curYearSavings
    }
    prevYearSavings = prevYearRow["AvgSavings"] as Double

    return ((curYearSavings - prevYearSavings) / prevYearSavings) * 100

}
private fun formatReport3(df : DataFrame<*>) : DataFrame<*> {
    var formattedDf: DataFrame<*> = df

    val twoDecimalFormat = DecimalFormat("#,##0.00")
    val intWithCommaFormat = DecimalFormat("#,###")

    twoDecimalFormat.roundingMode = RoundingMode.HALF_UP

    formattedDf = formattedDf.replace("TotalProjects")
                             .with { cellVal -> cellVal.map { intWithCommaFormat.format(it) } }

    formattedDf = formattedDf.replace("AvgSavings", "OverrunRate", "YoYChange")
                             .with { cellVal -> cellVal.map { twoDecimalFormat.format(it) } }

    return formattedDf
}

