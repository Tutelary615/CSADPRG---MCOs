import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import java.math.RoundingMode
import java.text.DecimalFormat

fun generateSummary (df : DataFrame<*>) : Map<String, Number> {


    val summary : Map<String, Number>
    val formattedSummary : Map<String, String>
    val totalProjects : Int =  df.countDistinct("ProjectId")
    val totalContractors : Int = df.countDistinct("Contractor")
    val totalProvinces :  Int = df.countDistinct("ProvincialCapital")
    val globalAvgDelay : Double = df.mean("CompletionDelayDays")
    val totalSavings : Double = df.sum("CostSavings") as Double

    summary = mutableMapOf(
        "total_projects" to totalProjects,
        "total_contractors" to totalContractors,
        "total_provinces" to totalProvinces,
        "global_avg_delay" to globalAvgDelay,
        "total_savings" to totalSavings
    )
    return summary
}



fun makeSummaryJSONString(map : Map<String, Number>) : String {
    val sb = StringBuilder()
    var ctr : Int = 0
    val formattedMap : Map<String, String> = formatSummaryMap(map)

    sb.append("{\n")
    for ((key, value) in formattedMap) {
        sb.append("\t\"$key\": $value")

        if (ctr < formattedMap.size - 1) {
            sb.append(",")
        }
        ctr++
        sb.append("\n")
    }
    sb.append("}")
    return sb.toString()
}

private fun formatSummaryMap(summary : Map<String, Number>) : Map<String, String> {
    val formattedMap: Map<String, String>

    val oneDecimalFormat = DecimalFormat("###.#")
    val twoDecimalFormat = DecimalFormat("###.##")
    oneDecimalFormat.roundingMode = RoundingMode.HALF_UP
    twoDecimalFormat.roundingMode = RoundingMode.HALF_UP

    formattedMap = mapOf(
        "total_projects" to (summary["total_projects"]).toString(),
        "total_contractors" to (summary["total_contractors"]).toString(),
        "total_provinces" to (summary["total_provinces"]).toString(),
        "global_avg_delay" to oneDecimalFormat.format(summary["global_avg_delay"]),
        "total_savings" to twoDecimalFormat.format(summary["total_savings"])
    )
    return formattedMap

}
