import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.*
import java.math.RoundingMode
import java.text.DecimalFormat

fun generateReport2(df : DataFrame<*>) : DataFrame<*> {
    val dfGroupedByContractor = df.groupBy("Contractor")
    var report2Df = dfGroupedByContractor.aggregate {
        sum("ContractCost") into "TotalCost"
        rowsCount() into "NumProjects"
        mean("CompletionDelayDays") into "AvgDelay"
        sum("CostSavings") into "TotalSavings"
    }
    report2Df = report2Df.filter {(it["NumProjects"] as Int) >= 5}

    report2Df = report2Df.add("ReliabilityIndex") { computeReliabilityIndex(it) }
    report2Df = report2Df.sortByDesc {it["TotalCost"].convertToDouble()}
    report2Df = report2Df.head(15)
    report2Df = report2Df.insert("Rank") {it.index() + 1}.at(0)
    report2Df = report2Df.add("RiskFlag") { if ((it["ReliabilityIndex"] as Double) < 5) "HIGH RISK" else "LOW RISK"}
    report2Df = formatReport2(report2Df)
    return report2Df
}

private fun computeReliabilityIndex(row : DataRow<*>): Double {
    val avgDelay = row["AvgDelay"] as Double
    val totalSavings = row["TotalSavings"] as Double
    val totalCost = row["TotalCost"] as Double
    val reliabilityIndex = (1 - (avgDelay / 90)) * (totalSavings / totalCost) * 100

    if (reliabilityIndex > 100.0) {
        return 100.0
    } else {
        return reliabilityIndex
    }
}

private fun formatReport2(report2Df : DataFrame<*>) : DataFrame<*> {
    var formattedDf : DataFrame<*> = report2Df
    val twoDecimalFormat = DecimalFormat("#,##0.00")
    val intWithCommaFormat = DecimalFormat("#,###")

    twoDecimalFormat.roundingMode = RoundingMode.HALF_UP

    formattedDf = formattedDf.replace("NumProjects")
        .with {cellVal -> cellVal.map{intWithCommaFormat.format(it)} }

    formattedDf = formattedDf.replace("TotalCost", "TotalSavings", "AvgDelay", "ReliabilityIndex")
                             .with { cellVal -> cellVal.map{ twoDecimalFormat.format(it)} }

    return formattedDf
}
