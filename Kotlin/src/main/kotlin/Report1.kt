import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * contains all functions need for generating Report 1 : Regional Summary
 * @param df dataframe containing the source data
 * @return dataframe containing the report
 * NOTE: numerical values in the returned dataframe are represented as strings
 */
fun generateRegionalSummary(df : DataFrame<*>) : DataFrame<*>{
    val dfGroupedByRegion  = df.groupBy( "Region", "MainIsland")
    var regionalSummaryDf : DataFrame<*>
    val maxEfficiencyScore : BigDecimal
    val minEfficiencyScore : BigDecimal
    
    regionalSummaryDf = dfGroupedByRegion.aggregate {
        sum("ApprovedBudgetForContract") into "TotalBudget"
        median("CostSavings") into "MedianSavings"
        mean("CompletionDelayDays") into "AvgDelay"
    }

    regionalSummaryDf = regionalSummaryDf.add("HighDelayPct") { computeHighDelayPct(it, df) }
    regionalSummaryDf = regionalSummaryDf.add("EfficiencyScore") { computeEfficiencyScore(it) }

    minEfficiencyScore = (regionalSummaryDf.min("EfficiencyScore") as Double).toBigDecimal()
    maxEfficiencyScore = (regionalSummaryDf.max("EfficiencyScore") as Double).toBigDecimal()

    /**
     * normalizes efficiency score
     * @param efficiencyScore 
     * @return efficiency score normalized (0-100)
     */
    fun normalizeEfficiencyScore(efficiencyScore : BigDecimal) : BigDecimal {
        return  ((efficiencyScore - minEfficiencyScore) /
                    (maxEfficiencyScore - minEfficiencyScore)) * BigDecimal(100.0)
    }

    regionalSummaryDf = regionalSummaryDf.replace { it["EfficiencyScore"] }
                         .with {efficiencyScore -> efficiencyScore.map{normalizeEfficiencyScore((it as Double).toBigDecimal())}}

    regionalSummaryDf = regionalSummaryDf.sortByDesc("EfficiencyScore")

    regionalSummaryDf = formatRegionalSummary(regionalSummaryDf)
    return regionalSummaryDf
}

/**
 * @param row the row of report 1 whose HighDelayPct field is being computed
 * @param df dataframe containing the source data
 * @return the high-delay percentage field of row
 */
private fun computeHighDelayPct(row : DataRow<*>, df : DataFrame<*>) : Double {
    val mainIsland : String = row["MainIsland"] as String
    val region : String = row["Region"] as String
    val projectsMainIslandRegionDf : DataFrame<*> = df.filter{ (it["MainIsland"] == mainIsland)
                                                                   && it["Region"] == region }

    val totalProjects : Int = projectsMainIslandRegionDf.rowsCount()
    val withDelayOver30 : Int  = (projectsMainIslandRegionDf.count { (it["CompletionDelayDays"] as Int) > 30 })


    return (withDelayOver30.toDouble() / totalProjects.toDouble()) * 100
}

private fun computeEfficiencyScore(row : DataRow<*>) : Double {
    val medianSavings = row["MedianSavings"] as Double
    val averageDelay = row["AvgDelay"] as Double

    return (medianSavings / averageDelay)
}

/**
 * formats the report 1 dataframe for export
 * @return the formatted dataframe
 */
private fun formatRegionalSummary(regionalSummaryDf : DataFrame<*>) : DataFrame<*>{
    var formattedReport = regionalSummaryDf
    val twoDecimalFormat = DecimalFormat("#,##0.00")

    twoDecimalFormat.roundingMode = RoundingMode.HALF_UP

    formattedReport = formattedReport.replace("TotalBudget", "MedianSavings", "AvgDelay",
                                              "HighDelayPct", "EfficiencyScore")
                                     .with{ cellVal -> cellVal.map {twoDecimalFormat.format(it)} }
    return formattedReport
}


