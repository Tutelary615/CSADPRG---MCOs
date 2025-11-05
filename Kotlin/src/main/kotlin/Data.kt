import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.api.map
import kotlinx.datetime.LocalDate
import org.jetbrains.kotlinx.dataframe.api.dropNulls

fun String.toDateOrNull() : LocalDate? {
    val segments: List<String>
    val month: Int
    val day: Int
    val year: Int
    val date : LocalDate

    try {
        segments = this.split("/")
        month = segments[0].toInt()
        day = segments[1].toInt()
        year = segments[2].toInt()
        date = LocalDate(year, month, day)

    } catch (e: Exception) {
        return null
    }
    return date
}

fun LocalDate.toString() : String {
    return "${month}/${day}/${year}"
}

fun cleanData(df : DataFrame<*> ) : DataFrame<*> {
    var cleanDF: DataFrame<*> = df


    // formatting financial fields
    cleanDF = cleanDF.replace {cols("ApprovedBudgetForContract", "ContractCost")}
        .with{column ->
            column.map{it.toString().toDoubleOrNull()}}

    // formatting dates
    cleanDF = cleanDF.replace { cols("StartDate", "ActualCompletionDate")}
             .with { column ->
                 column.map{it.toString().toDateOrNull()}
             }

    // removing rows with null values
    cleanDF = cleanDF.dropNulls()
    return cleanDF
}

fun isFrom2021To2023(projectInstance : DataRow<*>) : Boolean {
    val year : Int = (projectInstance["StartDate"] as LocalDate).year
    return (year >= 2021 && year <= 2023)
}
