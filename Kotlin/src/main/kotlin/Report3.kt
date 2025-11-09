import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import java.math.RoundingMode
import java.text.DecimalFormat

fun generateReport3(df : DataFrame<*>) : DataFrame<*> {
    var report3Df : DataFrame<*>
    val dfGrouped = df.groupBy("FundingYear", "TypeOfWork")

    report3Df = dfGrouped.aggregate {
        count { it["ProjectId"] != null } into "TotalProjects"
        mean("CostSavings") into "AvgSavings"
        computeOverrunRate(it as DataFrame<*>) into "OverrunRate"
    }


    report3Df = report3Df.sortBy {it["FundingYear"] and it["AvgSavings"].desc()}

    report3Df = formatReport3(report3Df)
    return report3Df
}

private fun computeOverrunRate(grouping : DataFrame<*>) : Double {
    val totalProjects : Int = grouping.count()
    val overrunProjects : Int = grouping.count { (it["CostSavings"] as Double) < 0}
    val overrunRate : Double = overrunProjects.toDouble() / totalProjects.toDouble()
    return overrunRate
}


private fun formatReport3(df : DataFrame<*>) : DataFrame<*> {
    var formattedDf: DataFrame<*> = df

    val currencyFormat = DecimalFormat("#,##0.00")
    val pctFormat = DecimalFormat("##0.00%")
    val intWithCommaFormat = DecimalFormat("#,###")

    currencyFormat.roundingMode = RoundingMode.HALF_UP
    pctFormat.roundingMode = RoundingMode.HALF_UP

    formattedDf = formattedDf.replace("TotalProjects")
                             .with { cellVal -> cellVal.map { intWithCommaFormat.format(it) } }

    formattedDf = formattedDf.replace("AvgSavings")
                             .with { cellVal -> cellVal.map { currencyFormat.format(it) } }

    formattedDf = formattedDf.replace("OverrunRate")
                             .with { cellVal -> cellVal.map { pctFormat.format(it) } }

    return formattedDf
}

