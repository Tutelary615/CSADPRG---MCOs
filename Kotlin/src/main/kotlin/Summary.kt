import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * generates summary
 * @param df source dataframe
 * @return a map the summary fields and their corresponding values
 * NOTE: numerical values in the returned map are represented as strings
 */
fun generateSummary (df : DataFrame<*>) : Map<String, String> {

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
    formattedSummary = formatSummaryMap(summary)
    return formattedSummary
}

/**
 * makes the json string of the summary map for printing
 * @param summary the summary map
 * @return the json string
 */
fun makeSummaryJSONString(summary : Map<String, String>) : String {
    val sb : StringBuilder = StringBuilder()
    var ctr : Int = 0

    sb.append("{\n")
    for ((key, value) in summary) {
        sb.append("\t\"$key\": $value")

        if (ctr < summary.size - 1) {
            sb.append(",")
        }
        ctr++
        sb.append("\n")
    }
    sb.append("}")
    return sb.toString()
}

/**
 * formats the summary map for conversion to json string
 * @param summary the summary map
 * @return the formatted summary map
 */
private fun formatSummaryMap(summary : Map<String, Number>) : Map<String, String> {
    val formattedMap: Map<String, String>

    val twoDecimalFormat = DecimalFormat("###.00")
    twoDecimalFormat.roundingMode = RoundingMode.HALF_UP

    formattedMap = mapOf(
        "total_projects" to (summary["total_projects"]).toString(),
        "total_contractors" to (summary["total_contractors"]).toString(),
        "total_provinces" to (summary["total_provinces"]).toString(),
        "global_avg_delay" to twoDecimalFormat.format(summary["global_avg_delay"]),
        "total_savings" to twoDecimalFormat.format(summary["total_savings"])
    )
    return formattedMap

}
