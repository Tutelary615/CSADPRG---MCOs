import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

fun hasNullExceptMunicipality(row : DataRow<*>) : Boolean {

    for (cn in row.columnNames()) {
        if (cn == "Municipality") {
            continue
        } else if (row[cn] == null) {
            return true
        }
    }
    return false
}

fun convertNumbers(df : DataFrame<*>) : DataFrame<*> {
    var convertedDf: DataFrame<*> = df
    
    // formatting financial fields
    convertedDf = convertedDf.replace("ApprovedBudgetForContract", "ContractCost")
                             .with{column -> column.map{ (it as String).toDoubleOrNull() }}

    return convertedDf
}

fun addDerivedFields(df : DataFrame<*>) : DataFrame<*> {
    var dfWithDerivedFields : DataFrame<*> = df

    dfWithDerivedFields = dfWithDerivedFields
                          .add("CompletionDelayDays") {
                            computeCompletionDelayDays(it)
                        }
                          .add("CostSavings") {
                              computeCostSavings(it)
                         }
    return dfWithDerivedFields
}

private fun computeCompletionDelayDays(projectInstance : DataRow<*>) : Int {
    val startDate : LocalDate = (projectInstance["StartDate"] as LocalDate)
    val actualCompletionDate : LocalDate = (projectInstance["ActualCompletionDate"] as LocalDate)
    return startDate.daysUntil(actualCompletionDate)
}

private fun computeCostSavings(projectInstance : DataRow<*>) : Double {
    val approvedBudget : Double = (projectInstance["ApprovedBudgetForContract"] as Double)
    val contractCost : Double = (projectInstance["ContractCost"] as Double)
    return approvedBudget - contractCost
}

fun isFrom2021To2023(projectInstance : DataRow<*>) : Boolean {
    val fundingYear : Int = (projectInstance["FundingYear"] as Int)
    return (fundingYear >= 2021 && fundingYear <= 2023)
}