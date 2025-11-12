import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.*
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.min

/**
 * generates Report 2 : Contractor Ranking
 * @param df dataframe containing the source data
 * @return the dataframe containing the report
 * NOTE: numerical values in the returned dataframe are represented as strings
 */
fun generateContractorRanking(df : DataFrame<*>) : DataFrame<*> {
    val dfGroupedByContractor = df.groupBy("Contractor")
    var contractorRankingDf = dfGroupedByContractor.aggregate {
        sum("ContractCost") into "TotalCost"
        rowsCount() into "NumProjects"
        mean("CompletionDelayDays") into "AvgDelay"
        sum("CostSavings") into "TotalSavings"
    }
    contractorRankingDf = contractorRankingDf.filter {(it["NumProjects"] as Int) >= 5}

    contractorRankingDf = contractorRankingDf.add("ReliabilityIndex") { computeReliabilityIndex(it) }
    contractorRankingDf = contractorRankingDf.sortByDesc {it["TotalCost"].convertToDouble()}
    contractorRankingDf = contractorRankingDf.head(15)
    contractorRankingDf = contractorRankingDf.insert("Rank") {it.index() + 1}.at(0)
    contractorRankingDf = contractorRankingDf.add("RiskFlag") { if ((it["ReliabilityIndex"] as Double) < 5) "High Risk" else "Low Risk"}
    contractorRankingDf = formatContractorRanking(contractorRankingDf)
    return contractorRankingDf
}

/**
 * @param row the row of report 2 whose reliabilityIndex value is being computed
 * @return the reliabilityIndex value of row (capped at 100)
 */
private fun computeReliabilityIndex(row : DataRow<*>): Double {
    val avgDelay = row["AvgDelay"] as Double
    val totalSavings = row["TotalSavings"] as Double
    val totalCost = row["TotalCost"] as Double
    val reliabilityIndex = (1 - (avgDelay / 90)) * (totalSavings / totalCost) * 100

    return min(reliabilityIndex, 100.0)
}

/**
 * formats the report 2 dataframe for export
 * @param contractorRankingDf the unformatted dataframe
 * @return the formatted dataframe
 */
private fun formatContractorRanking(contractorRankingDf : DataFrame<*>) : DataFrame<*> {
    var formattedDf : DataFrame<*> = contractorRankingDf
    val twoDecimalFormat = DecimalFormat("#,##0.00")
    val intWithCommaFormat = DecimalFormat("#,###")

    twoDecimalFormat.roundingMode = RoundingMode.HALF_UP

    formattedDf = formattedDf.replace("NumProjects")
                             .with {cellVal -> cellVal.map{intWithCommaFormat.format(it)} }

    formattedDf = formattedDf.replace("TotalCost", "TotalSavings", "AvgDelay", "ReliabilityIndex")
                             .with { column -> column.map{ twoDecimalFormat.format(it)} }

    return formattedDf
}
