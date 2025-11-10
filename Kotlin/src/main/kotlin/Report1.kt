import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat


fun generateReport1(df : DataFrame<*>) : DataFrame<*>{
    val dfGroupedByRegion  = df.groupBy( "Region", "MainIsland")
    var report1Df : DataFrame<*>
    var mainIsland : String
    var region : String
    val maxEfficiencyScore : Double
    val minEfficiencyScore : Double

    report1Df = dfGroupedByRegion.aggregate {
        sum("ApprovedBudgetForContract") into "TotalBudget"
        median("CostSavings") into "MedianSavings"
        mean("CompletionDelayDays") into "AvgDelay"
    }

    report1Df = report1Df.add("HighDelayPct") {
                              mainIsland = it["MainIsland"] as String
                              region = it["Region"] as String
                              computeHighDelayPct(df, mainIsland, region)
                           }
                         .add("EfficiencyScore") {
                             computeEfficiencyScore(it)
                         }
    maxEfficiencyScore = report1Df.min("EfficiencyScore") as Double
    minEfficiencyScore = report1Df.max("EfficiencyScore") as Double


    fun scaleEfficiencyScore(efficiencyScore : Double) : BigDecimal {
        return ((efficiencyScore.toBigDecimal() - minEfficiencyScore.toBigDecimal()) /
                (maxEfficiencyScore.toBigDecimal() - minEfficiencyScore.toBigDecimal())) * BigDecimal(100)
    }

    report1Df = report1Df.replace { it["EfficiencyScore"] }
                         .with {efficiencyScore -> efficiencyScore.map{scaleEfficiencyScore(it as Double)}}
    report1Df = report1Df.sortByDesc("EfficiencyScore")
    report1Df = formatReport1(report1Df)
    return report1Df
}

private fun computeHighDelayPct(df : DataFrame<*>, mainIsland : String, region: String) : Double {
    val filteredIslandRegionDf = df.filter({it["MainIsland"].toString() == mainIsland &&
                                            it["Region"].toString() == region })

    val totalProjects = filteredIslandRegionDf.rowsCount()
    val withDelayOver30  = filteredIslandRegionDf.count { (it["CompletionDelayDays"] as Int) > 30 }

    return (withDelayOver30.toDouble() / totalProjects.toDouble()) * 100
}

private fun computeEfficiencyScore(row : DataRow<*>) : Double {
    val medianSavings = row["MedianSavings"] as Double
    val averageDelay = row["AvgDelay"] as Double

    return (medianSavings / averageDelay) * 100
}

private fun formatReport1(report1Df : DataFrame<*>) : DataFrame<*>{
    var formattedReport = report1Df
    val twoDecimalFormat = DecimalFormat("##0.00")

    twoDecimalFormat.roundingMode = RoundingMode.HALF_UP

    formattedReport = formattedReport.replace("TotalBudget", "MedianSavings")
        .with{ cellVal -> cellVal.map {twoDecimalFormat.format(it)} }

    formattedReport = formattedReport.replace("AvgDelay", "EfficiencyScore")
        .with{ cellVal -> cellVal.map {twoDecimalFormat.format(it)} }

    formattedReport = formattedReport.replace("HighDelayPct")
        .with{ cellVal -> cellVal.map {twoDecimalFormat.format(it)} }

    return formattedReport
}


