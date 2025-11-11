import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil


/**
 * converts the numerical fields of the source dataframe to Double or null if they cannot be converted
 * @param df the unformatted source dataframe
 * @return the formatted dataframe
 */
fun convertNumbers(df : DataFrame<*>) : DataFrame<*> {
    var convertedDf: DataFrame<*> = df
    
    // formatting financial fields
    convertedDf = convertedDf.replace("ApprovedBudgetForContract", "ContractCost")
                             .with{column -> column.map{ (it as String).toDoubleOrNull() }}

    return convertedDf
}

/**
 * adds the derived fields (CompletionDelayDays and CostSavings) to the source dataframe
 * @param df the source dataframe
 * @return the source dataframe with the derived fields
 */
fun addDerivedFields(df : DataFrame<*>) : DataFrame<*> {
    var dfWithDerivedFields : DataFrame<*> = df

    dfWithDerivedFields = dfWithDerivedFields.add("CompletionDelayDays") { computeCompletionDelayDays(it) }

    dfWithDerivedFields = dfWithDerivedFields.add("CostSavings") { computeCostSavings(it) }

    return dfWithDerivedFields
}

/**
 * @param projectInstance the row of the source dataframe whose CompletionDelayDays value is being computed
 * @return the completionDelayDays value of projectInstance
 */
private fun computeCompletionDelayDays(projectInstance : DataRow<*>) : Int {
    val startDate : LocalDate = (projectInstance["StartDate"] as LocalDate)
    val actualCompletionDate : LocalDate = (projectInstance["ActualCompletionDate"] as LocalDate)
    return startDate.daysUntil(actualCompletionDate)
}

/**
 * @param projectInstance the row of the source dataframe whose CostSavings value is being computed
 * @return the completionDelayDays value of projectInstance
 */
private fun computeCostSavings(projectInstance : DataRow<*>) : Double {
    val approvedBudget : Double = (projectInstance["ApprovedBudgetForContract"] as Double)
    val contractCost : Double = (projectInstance["ContractCost"] as Double)
    return approvedBudget - contractCost
}

fun isFrom2021To2023(projectInstance : DataRow<*>) : Boolean {
    val fundingYear : Int = (projectInstance["FundingYear"] as Int)
    return (fundingYear >= 2021 && fundingYear <= 2023)
}

/**
 * checks if a field of the source dataframe has a missing (null) value
 * the Municipality column is not checked
 * @return true if a null value is found, false otherwise
 */
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