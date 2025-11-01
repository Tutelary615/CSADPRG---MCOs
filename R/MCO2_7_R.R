# ********************
#  Last names: Almero, Aquino, Dolot, Marquez
#  Language: R
#  Paradigm(s): Functional Programming, Procedural Programming
# ********************

# HOW TO RUN
# Download and install R via https://cran.r-project.org/
# Open Command Prompt in this file's directory
# Run R console -> source("MCO2_7_R.R")
# !! DATASET CSV FILE MUST BE LOCATED IN THE SAME DIRECTORY !!

suppressPackageStartupMessages({
  library(tidyverse)
  library(scales)
  library(jsonlite)
})

#############
# LOAD FILE #
#############

#' @title Load csv file
#' @description Function for loading, parsing, and filtering the contents of dpwh_flood_control_projects.csv
load_file <- function() {
  cat("Processing dataset...\n")
  
  # Produce csv file containing all rows with invalid data (non-numeric budget/cost, outside 2021-2023)
  invalid_data <- read_csv("dpwh_flood_control_projects.csv", show_col_types = FALSE) %>%
    filter(grepl("MYCA", ApprovedBudgetForContract) | grepl("Clustered", ApprovedBudgetForContract) | !between(FundingYear, 2021, 2023))
  
  write_csv(invalid_data, "invalid_data.csv")
  nrows_invalid <- nrow(invalid_data)
  
  # Initialize tibble with appropriate data types per column
  data <- read_csv("dpwh_flood_control_projects.csv", col_types = cols(
    MainIsland = col_character(),
    Region = col_character(),
    Province = col_character(),
    LegislativeDistrict = col_character(),
    Municipality = col_character(),
    DistrictEngineeringOffice = col_character(),
    ProjectId = col_character(),
    ProjectName = col_character(),
    TypeOfWork = col_character(),
    FundingYear = col_integer(),
    ContractId = col_character(),
    ApprovedBudgetForContract = col_character(),
    ContractCost = col_character(),
    ActualCompletionDate = col_date(format = "%Y-%m-%d"),
    Contractor = col_character(),
    ContractorCount = col_integer(),
    StartDate = col_date(format = "%Y-%m-%d"),
    ProjectLatitude = col_double(),
    ProjectLongitude = col_double(),
    ProvincialCapital = col_character(),
    ProvincialCapitalLatitude = col_double(),
    ProvincialCapitalLongitude = col_double(),
    )
  )
  
  nrows_total <- nrow(data)
  
  # Filter invalid rows, convert type of budget/cost columns to numeric, produce required derived fields
  data <- data %>%
    filter(
      !grepl("MYCA", ApprovedBudgetForContract) &
      !grepl("Clustered", ApprovedBudgetForContract) &
      between(FundingYear, 2021, 2023)
    ) %>%
    mutate(
      ApprovedBudgetForContract = as.numeric(ApprovedBudgetForContract),
      ContractCost = as.numeric(ContractCost),
      CostSavings = ApprovedBudgetForContract - ContractCost,
      CompletionDelayDays = as.numeric(StartDate - ActualCompletionDate)
    )
  
  nrows_valid <- nrow(data)
  
  write_csv(data, "valid_data.csv")
  
  cat(sprintf("Total rows loaded: %d\n  Valid rows: %d (exported to valid_data.csv)\n  Invalid rows: %d (exported to invalid_data.csv)\n", nrows_total, nrows_valid, nrows_invalid))

  return (data)
}

####################
# GENERATE REPORTS #
####################

#' @title Generate reports
#' @description Function for producing csv and json output of required reports
#' @param data Tibble from which to obtain report data; must have been processed through load_file()
generate_reports <- function(data) {
  cat("Generating reports...\n\n")
  
  # REPORT 1 #
  
  # Produce initial dbl type columns
  report_1 <- data %>%
    group_by(Region, MainIsland) %>%
    summarize(
      DblTotalBudget = sum(ApprovedBudgetForContract),
      DblMedianSavings = median(CostSavings),
      DblAvgDelay = mean(CompletionDelayDays),
      DblHighDelayPct = sum(CompletionDelayDays > 30) / n(),
      DblUnscaledEfficiencyScore = median(CostSavings) / mean(CompletionDelayDays) * 100,
      .groups = "drop"
    )
  
  # Normalize Efficiency Score to 0-100, format dbl columns, arrange by desc Efficiency Score, drop unformatted columns
  report_1 <- report_1 %>%
    mutate(
      TotalBudget = label_comma(accuracy = 0.01)(DblTotalBudget),
      MedianSavings = label_comma(accuracy = 0.01)(DblMedianSavings),
      AvgDelay = label_comma(accuracy = 0.1)(DblAvgDelay),
      HighDelayPct = percent(DblHighDelayPct, accuracy = 0.01),
      DblEfficiencyScore = 100 * (
        (DblUnscaledEfficiencyScore - min(report_1$DblUnscaledEfficiencyScore)) /
          (max(report_1$DblUnscaledEfficiencyScore) - min(report_1$DblUnscaledEfficiencyScore))
      ),
      EfficiencyScore = label_comma(accuracy = 0.01)(DblEfficiencyScore)
    ) %>%
    arrange(desc(DblEfficiencyScore)) %>%
    select(-DblTotalBudget, -DblMedianSavings, -DblAvgDelay, -DblHighDelayPct, -DblUnscaledEfficiencyScore, -DblEfficiencyScore)
  
  cat("Report 1: Regional Flood Mitigation Efficiency Summary\n")
  cat(" (Filtered: 2021-2023 Projects)\n\n")
  print(head(report_1, 3))
  
  write_csv(report_1, "report1_regional_summary.csv")
  cat("\n(Full table exported to report1_regional_summary.csv)\n\n")
  
  # REPORT 2 #
  
  # Filter Contractors with at least five projects, produce initial dbl type columns
  report_2 <- data %>%
    group_by(Contractor) %>%
    filter(n() >= 5) %>%
    summarize(
      DblTotalCost = sum(ContractCost),
      IntNumProjects = n(),
      DblAvgDelay = mean(CompletionDelayDays),
      DblTotalSavings = sum(CostSavings),
      DblReliabilityIndex = (1 - (DblAvgDelay / 90)) * (DblTotalSavings / DblTotalCost) * 100,
      .groups = "drop"
    ) %>%
    arrange(desc(DblTotalCost))
  
  # Format dbl type columns, add RiskFlag and Rank columns, drop dbl type columns
  report_2 <- report_2 %>%
    mutate (
      TotalCost = label_comma(accuracy = 0.01)(DblTotalCost),
      NumProjects = label_comma(accuracy = 1)(IntNumProjects),
      AvgDelay = label_comma(accuracy = 0.1)(DblAvgDelay),
      TotalSavings = label_comma(accuracy = 0.01)(DblTotalSavings),
      ReliabilityIndex = label_comma(accuracy = 0.01)(DblReliabilityIndex),
      RiskFlag = ifelse(DblReliabilityIndex < 50, "High Risk", "Low Risk"),
      Rank = row_number()
    ) %>%
    select(-DblTotalCost, -IntNumProjects, -DblAvgDelay, -DblTotalSavings, -DblReliabilityIndex) %>%
    select(Rank, everything())
  
  report_2 <- head(report_2, 15)
  
  cat("Report 2: Top Contractors Performance Ranking\n")
  cat(" (Top 15 by TotalCost, >= 5 Projects)\n\n")
  print(head(report_2, 3))
  
  write_csv(report_2, "report2_contractor_ranking.csv")
  cat("\n(Full table exported to report2_contractor_ranking.csv)\n\n")
  
  
  # REPORT 3 #
  
  # Produce initial num type columns until overrun rate, arrange by TypeOfWork then FundingYear for processing of next field
  report_3 <- data %>%
    group_by(FundingYear, TypeOfWork) %>%
    summarize(
      IntTotalProjects = n(),
      DblAvgSavings = mean(CostSavings),
      DblOverrunRate = sum(CostSavings < 0) / n(),
      .groups = "drop"
    ) %>%
    arrange(TypeOfWork, FundingYear)
  
  # Produce formatted columns, year-over-year % change, rearrange by FundingYear and AvgSavings
  report_3 <- report_3 %>%
    mutate(
      TotalProjects = label_comma(accuracy = 1)(IntTotalProjects),
      AvgSavings = label_comma(accuracy = 0.01)(DblAvgSavings),
      OverrunRate = percent(DblOverrunRate, accuracy = 0.01),
      DblYoYChange = ifelse(TypeOfWork == lag(TypeOfWork) & lag(FundingYear) == FundingYear - 1, (DblAvgSavings - lag(DblAvgSavings)) / abs(lag(DblAvgSavings)), NA),
      YoYChange = label_percent(accuracy = 0.01)(DblYoYChange)
    ) %>%
    arrange(FundingYear, desc(DblAvgSavings)) %>%
    select(-IntTotalProjects, -DblAvgSavings, -DblOverrunRate, -DblYoYChange)
  
  cat("Report 3: Annual Project Type Cost Overrun Trends\n")
  cat(" (Grouped by FundingYear and TypeOfWork)\n\n")
  print(head(report_3, 3))
  
  write_csv(report_3, "report3_annual_trends.csv")
  cat("\n(Full table exported to report3_annual_trends.csv)\n\n")
  
  
  # SUMMARY #
  
  # List for later conversion to json string and json file
  summary_stats <- list()
  
  # Tibble for count statistics; counts number of unique value instances per column
  counts <- data %>%
    summarize(across(everything(), n_distinct))
  
  # Tibble for sum and mean statistics
  avgs_sums <- data %>%
    summarize(
      GlobalAvgDelay = mean(CompletionDelayDays),
      TotalSavings = sum(CostSavings)
    )
  
  summary_stats$total_projects <- counts$ProjectId
  summary_stats$total_contractors <- counts$Contractor
  summary_stats$total_provinces <- counts$Province
  summary_stats$global_avg_delay <- round(avgs_sums$GlobalAvgDelay, 1)
  summary_stats$total_savings <- round(avgs_sums$TotalSavings, 2)
  
  cat("Summary Stats (summary.json):\n")
  cat(toJSON(summary_stats, auto_unbox = TRUE, pretty = TRUE))
  cat("\n\n")
  
  write_json(summary_stats, "summary.json", pretty = TRUE, auto_unbox = TRUE)
}

##################
# USER INTERFACE #
##################

#' @title Retrieve user choice in main menu
#' @description Function for retrieving user input in main menu
#' @return User choice: an integer from 1 to 3
menu_choice <- function() {
  cat("Select Language Implementation:\n")
  cat("[1] Load the file\n")
  cat("[2] Generate Reports\n")
  cat("[3] Quit\n\n")
  
  repeat {
    choice <- as.integer(readline("Enter choice: "))  
    
    if (is.na(choice) || !between(as.integer(choice), 1, 3)) {
      cat("Enter a valid choice.\n")
      next
    }
      
    return (choice)
  }
}

#' @title Retrieve user choice for main menu return
#' @description Function for retrieving user choice in returning to main menu
#' @return User choice: yes (Y) or no (N)
prompt_menu_return <- function() {
  repeat {
    choice <- toupper(readline("Back to menu (Y/N)? "))  
    
    if (is.na(choice) || choice != 'Y' && choice != 'N') {
      cat("Enter a valid choice.\n")
      next
    }
    
    return (choice)
  }
}

# Program Entry Point #

data <- NA

repeat {
  choice <- menu_choice()
  
  if (choice == 1) {
    repeat {
      data <- load_file()
      
      back_to_menu <- prompt_menu_return()  
      
      if (back_to_menu == 'Y')
        break
    }
  }
  
  else if (choice == 2) {
    repeat {
      generate_reports(data)
      
      back_to_menu <- prompt_menu_return()  
      
      if (back_to_menu == 'Y')
        break
    }
  }
  
  else if (choice == 3) {
    cat("Goodbye!\n")
    break
  }
}

