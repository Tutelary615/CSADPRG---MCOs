import kotlinx.datetime.LocalDate
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.read

fun String.toDate() : LocalDate? {
    var convertedDate : LocalDate
    var tokens = this.split("/")
    try {
        convertedDate = LocalDate(tokens[2].toInt(), tokens[0].toInt(), tokens[1].toInt())
    } catch (e: Exception) {
        return null

    }
    return convertedDate
}


fun loadData() {
  var df : DataFrame<*> = DataFrame.read("dpwh_flood_control_projects.csv")
      //df = df.replace("StartDate").with { it.toString.toDate() }
}