import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat


fun generateReport1(df : DataFrame<*>) : DataFrame<*>{
    val dfGroupedByRegion  = df.groupBy( "Region", "MainIsland")
    var report1Df : DataFrame<*>
    val maxEfficiencyScore : BigDecimal
    val minEfficiencyScore : BigDecimal

    report1Df = dfGroupedByRegion.aggregate {
        sum("ApprovedBudgetForContract") into "TotalBudget"
        median("CostSavings") into "MedianSavings"
        mean("CompletionDelayDays") into "AvgDelay"
    }

    report1Df = report1Df.add("HighDelayPct") { computeHighDelayPct(it, df) }
    report1Df = report1Df.add("EfficiencyScore") { computeEfficiencyScore(it) }

    maxEfficiencyScore = (report1Df.min("EfficiencyScore") as Double).toBigDecimal()
    minEfficiencyScore = (report1Df.max("EfficiencyScore") as Double).toBigDecimal()


    fun scaleEfficiencyScore(efficiencyScore : BigDecimal) : BigDecimal {


        return ((efficiencyScore - minEfficiencyScore) /
                (maxEfficiencyScore - minEfficiencyScore)) * BigDecimal(100.0)
    }

    report1Df = report1Df.replace { it["EfficiencyScore"] }
                         .with {efficiencyScore -> efficiencyScore.map{scaleEfficiencyScore((it as Double).toBigDecimal())}}

    report1Df = report1Df.sortByDesc("EfficiencyScore")

    report1Df = formatReport1(report1Df)
    return report1Df
}

private fun computeHighDelayPct(row : DataRow<*>, source : DataFrame<*>) : Double {
    val mainIsland : String = row["MainIsland"] as String
    val region : String = row["Region"] as String
    val projectsMainIslandRegionDf : DataFrame<*> = source.filter{ (it["MainIsland"] == mainIsland)
                                                                   && it["Region"] == region }

    val totalProjects : Int = projectsMainIslandRegionDf.rowsCount()
    val withDelayOver30 : Int  = (projectsMainIslandRegionDf.count { (it["CompletionDelayDays"] as Int) > 30 })


    return (withDelayOver30.toDouble() / totalProjects.toDouble()) * 100
}

private fun computeEfficiencyScore(row : DataRow<*>) : Double {
    val medianSavings = row["MedianSavings"] as Double
    val averageDelay = row["AvgDelay"] as Double

    return (medianSavings / averageDelay) * 100
}

private fun formatReport1(report1Df : DataFrame<*>) : DataFrame<*>{
    var formattedReport = report1Df
    val twoDecimalFormat = DecimalFormat("#,##0.00")

    twoDecimalFormat.roundingMode = RoundingMode.HALF_UP


    formattedReport = formattedReport.replace("TotalBudget", "MedianSavings", "AvgDelay",
                                              "HighDelayPct", "EfficiencyScore")
                                     .with{ cellVal -> cellVal.map {twoDecimalFormat.format(it)} }


    return formattedReport
}


