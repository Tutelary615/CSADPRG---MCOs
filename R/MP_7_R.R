# Last names: Almero, Aquino, Dolot, Marquez
# Language: R
# Paradigm(s): Procedural Programming


# HOW TO RUN
# Download and install R via https://cran.r-project.org/
# Open Command Prompt in this file's directory
# Run R console -> source("MP_7_R.R")


#################################################
# UTILITIES - General Display & Input Functions #
#################################################

#' @title Print numbered menu
#' @description General function for printing numbered menu lists
#' @param prompt String to be displayed before option list
#' @param options String vector containing choices to be displayed
print_menu <- function(prompt, options) {
  cat(prompt, '\n')
  i <- 1
  while (i <= length(options)) {
    cat(sprintf("[%d] %s\n", i, options[i]))
    i <- i + 1
  }
}

#' @title Retrieve user choice
#' @description General function for retrieving user input, which may be binary (y/n) or numeric
#' @param choice_type String which determines what type of prompt was given
#' @return User choice, which may be yes (Y), no (N), or an integer
get_user_choice <- function(choice_type) {
  if (choice_type == "bin") {
    repeat {
      input <- toupper(readline("Select Option (Y/N): "))
      if (input == 'Y' || input == 'N')
        return (input)
      cat("Please enter a valid input.\n")
    }
  }
    
  else if (choice_type == "num") {
    repeat {
      input <- readline("Select Option: ")
      input_num <- suppressWarnings(as.integer(input))
      
      if (!is.na(input_num))
        return (as.integer(input))
      
      cat("Please enter a valid input.\n")
    }
  }
}

#' @title Retrieve amount input
#' @description General function for retrieving amount input in PHP
#' @param prompt String to be displayed before input cursor
#' @return Non-negative number
get_amount_input <- function(prompt) {
  repeat {
    input <- readline(prompt)
    input_num <- suppressWarnings(as.numeric(input))
    
    if (!is.na(input_num) && input_num >= 0)
      return (round(input_num, 2))
    
    cat("Please enter a valid amount.\n")
  }
}

#' @title Ask user if they wish to return to menu
#' @description Prompts user for their choice to return to main menu
#' @return User choice, yes (Y) or no (N)
prompt_menu_return <- function() {
  cat("Return to Main Menu?\n")
  return (get_user_choice("bin"))
}

main_menu_options <- c("Register Account Name",
                       "Deposit Amount",
                       "Withdraw Amount",
                       "Currency Exchange",
                       "Record Exchange Rates",
                       "Show Interest Computation",
                       "Exit")

##########################################################
# PROGRAM PROPER - Entry Point and Program Functionality #
##########################################################

# List containing account information: account name, account balance, currency
bank_account <- list(name = "<not set>",
                     balance = 0.00,
                     currency = "PHP")

# List containing exchange rate information: currency name, currency code, and exchange rate to PHP
exchange_rates <- list(list(name = "Philippine Peso", code = "PHP", rate = 1.00))
  
active <- TRUE

while (active) {
  print_menu("Select Transaction:", main_menu_options)
  user_choice <- get_user_choice("num")
  
  if (user_choice == 1) {
    repeat {
      cat("Register Account Name\n\n")
      cat(sprintf("Account Name: %s\n", bank_account$name))
      bank_account$name <- readline("Enter Account Name: ")
      if (prompt_menu_return() == 'Y')
        break
    }
  } 
  
  else if (user_choice == 2) {
    repeat {
      cat("Deposit Amount\n\n")
      cat(sprintf("Account Name: %s\n", bank_account$name))
      cat(sprintf("Current Balance: %.2f\n", bank_account$balance))
      cat(sprintf("Currency: %s\n\n", bank_account$currency))
      
      deposit_amt <- get_amount_input("Enter Deposit Amount: ")
      
      bank_account$balance <- bank_account$balance + deposit_amt
      
      cat(sprintf("\nDeposit Amount: %.2f\n", deposit_amt))
      cat(sprintf("Updated Balance: %.2f\n\n", bank_account$balance))
      
      if (prompt_menu_return() == 'Y')
        break
    }
  } 
  
  else if (user_choice == 3) {
    repeat {
      cat("Withdraw Amount\n\n")
      cat(sprintf("Account Name: %s\n", bank_account$name))
      cat(sprintf("Current Balance: %.2f\n", bank_account$balance))
      cat(sprintf("Currency: %s\n\n", bank_account$currency))
      
      repeat {
        withdraw_amt <- get_amount_input("Enter Withdrawal Amount: ")
        # Disallow withdrawals greater than balance
        if (withdraw_amt <= bank_account$balance)
          break
        cat("Please enter a valid amount.\n")
      }
      
      bank_account$balance = bank_account$balance - withdraw_amt
      
      cat(sprintf("\nWithdrawal Amount: %.2f\n", withdraw_amt))
      cat(sprintf("Updated Balance: %.2f\n\n", bank_account$balance))
      
      if (prompt_menu_return() == 'Y')
        break
    }
  } 
  
  else if (user_choice == 4) {
    repeat {
      cat("Foreign Currency Exchange\n\n")
      
      currency_list <- c()
      
      # Populate list of currencies to print
      for (currency in exchange_rates) {
        name_and_code <- paste(currency$name, sprintf("(%s)", currency$code))
        currency_list <- append(currency_list, name_and_code)
      }
      
      print_menu("Currency List:", currency_list)
      
      cat("\nSelect Source Currency\n")
      
      repeat {
        source_currency <- get_user_choice("num")
        # Disallow out of bounds choices
        if (source_currency > 0 && source_currency <= length(currency_list))
          break
        cat("Please enter a valid choice.\n")
      }
    
      source_amount <- get_amount_input("Enter amount to convert: ")

      
      cat("\nSelect Exchange Currency\n")
      
      repeat {
        exchanged_currency <- get_user_choice("num")
        if (exchanged_currency > 0 && exchanged_currency <= length(currency_list))
          break
        cat("Please enter a valid choice.\n")
      }
      
      exchanged_amount <- round(source_amount * exchange_rates[[source_currency]]$rate / exchange_rates[[exchanged_currency]]$rate, 2)
      
      cat(sprintf("Exchanged Amount (%s to %s): %.2f\n\n", exchange_rates[[source_currency]]$code, exchange_rates[[exchanged_currency]]$code, exchanged_amount))
      
      if (prompt_menu_return() == 'Y')
        break
    }
  }
  
  else if (user_choice == 5) {
    repeat {
      cat("Record Exchange Rates\n\n")
      
      currency_list <- c()
      
      if (length(exchange_rates) != 1) {
        # Populate list of currencies to print, excluding PHP
        for (i in 2:length(exchange_rates)) {
          currency <- exchange_rates[[i]]
          name_and_code <- paste(currency$name, sprintf("(%s)", currency$code))
          currency_list <- append(currency_list, name_and_code)
        }
      }
      # Add option to add new currency
      currency_list <- append(currency_list, "Add New Currency")
      
      print_menu("Currency List:", currency_list)
      
      repeat {
        user_choice <- get_user_choice("num")
        if (user_choice > 0 && user_choice <= length(currency_list))
          break
        cat("Please enter a valid choice.\n")
      }
      
      if (user_choice == length(currency_list)) {
        # New currency addition: ask for name and code
        currency_name <- readline("Enter Currency Name: ")
        currency_code <- readline("Enter Currency Code: ")
        
        repeat {
          input <- readline("Enter Exchange Rate: ")
          rate_to_php <- suppressWarnings(as.numeric(input))
          
          # Disallow non-numeric and numbers less than or equal to 0
          if (!is.na(rate_to_php) && rate_to_php > 0)
            break
          
          cat("Please enter a valid exchange rate.\n")
        }
        
        exchange_rates[[length(exchange_rates) + 1]] <- list(name = currency_name, code = currency_code, rate = rate_to_php)
      }
      
      else {
        repeat {
          input <- readline("Enter Exchange Rate: ")
          rate_to_php <- suppressWarnings(as.numeric(input))
          
          # Disallow non-numeric and numbers less than or equal to 0
          if (!is.na(rate_to_php) && rate_to_php > 0)
            break
          
          cat("Please enter a valid exchange rate.\n")
        }
        
        exchange_rates[[user_choice + 1]]$rate <- rate_to_php
      }
      
      if (prompt_menu_return() == 'Y')
        break
    }
  } 
  
  else if (user_choice == 6) {
    daily_interest <- round(bank_account$balance * (0.05 / 365), 2)
    
    repeat {
      balance <- bank_account$balance
      
      cat("Show Interest Amount\n\n")
      cat(sprintf("Account Name: %s\n", bank_account$name))
      cat(sprintf("Current Balance: %.2f\n", bank_account$balance))
      cat(sprintf("Currency: %s\n", bank_account$currency))
      cat("Interest Rate: 5%\n\n")
      
      repeat {
        num_days <- as.integer(readline("Enter number of days: "))
        # Disallow zero or less number of days
        if (num_days > 0)
          break
        cat("Please enter a valid number.\n")
      }
      
      cat(sprintf("Total Number of Days: %d days\n\n", num_days))
      cat(sprintf(" Day |    Interest    |        Balance        |\n"))
      
      for (i in 1:num_days) {
        balance <- balance + daily_interest
        cat(sprintf("%4d | %14.2f | %21.2f |\n", i, daily_interest, balance))
      }
      
      if (prompt_menu_return() == 'Y')
        break
    }
  }
  
  else if (user_choice == 7) {
    cat("\nThank you!\n")
    active <- FALSE
  } 
  
  else {
    cat("\nPlease select a valid choice.\n\n")
  }
}