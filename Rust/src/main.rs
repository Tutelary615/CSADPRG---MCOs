
use std::io::{self, Write};

// data structure to record exchange rates
struct ExchangeRates {
    php: Option<f64>,
    usd: Option<f64>,
    jpy: Option<f64>,
    gbp: Option<f64>,
    eur: Option<f64>,
    cny: Option<f64>,
}

// methods to update the exchange rates
impl ExchangeRates {
    fn new() -> Self {
        ExchangeRates {
            php: Some(1.0),
            usd: None,
            jpy: None,
            gbp: None,
            eur: None,
            cny: None,
        }
    }

    fn set_rate(&mut self, code: &str, rate: f64) {
        match code {
            "PHP" => self.php = Some(rate),
            "USD" => self.usd = Some(rate),
            "JPY" => self.jpy = Some(rate),
            "GBP" => self.gbp = Some(rate),
            "EUR" => self.eur = Some(rate),
            "CNY" => self.cny = Some(rate),
            _ => println!("Unknown currency code"),
        }
    }

    fn get_rate(&self, code: &str) -> Option<f64> {
        match code {
            "PHP" => self.php,
            "USD" => self.usd,
            "JPY" => self.jpy,
            "GBP" => self.gbp,
            "EUR" => self.eur,
            "CNY" => self.cny,
            _ => None,
        }
    }
}


// data structure for each account
struct Account {
    name: String,
    balance: f64,
}

// methods for each account
impl Account {
    fn new(name: String) -> Self {
        Account { name, balance: 0.0 }
    }

    fn deposit(&mut self, amount: f64) {
        self.balance += amount;
    }

    fn withdraw(&mut self, amount: f64) -> bool {
        if amount <= self.balance {
            self.balance -= amount;
            true
        } else {
            false
        }
    }

    fn get_balance(&self) -> f64 {
        self.balance
    }
}


fn main() {
    let mut accounts: Vec<Account> = Vec::new();
    let mut rates = ExchangeRates::new();
    main_menu(&mut accounts, &mut rates);
}

fn main_menu(accounts: &mut Vec<Account>, rates: &mut ExchangeRates){

    loop {
        println!("Select Transaction:");
        println!("[1] Register Account Name");
        println!("[2] Deposit Amount");
        println!("[3] Withdraw Amount");
        println!("[4] Currency Exchange");
        println!("[5] Record Exchange Rates");
        println!("[6] Show Interest Computation");
        println!("[0] Exit Program");

        let choice: u8 = get_number("Choice: ");

        if choice == 0 {
            println!("Exiting program...");
            break;
        }

        match choice {
            1 => register_account(accounts),
            2 => deposit_amount(accounts),
            3 => withdraw_amount(accounts),
            4 => currency_exchange(rates),
            5 => record_exchange_rates(rates),
            6 => show_interest_computation(accounts),
            _ => println!("Invalid choice, please try again."),
        }
    }
}

fn register_account(accounts: &mut Vec<Account>) {

    loop {
        println!("Register Account Name");
        let name = get_input("Account Name:");
        accounts.push(Account::new(name));

        let action = get_input("\nBack to the Main Menu (Y/N): ");

        if action.eq_ignore_ascii_case("Y") {
            break;
        }
    }
}


fn deposit_amount(accounts: &mut Vec<Account>) {
    if accounts.is_empty() {
        println!("No accounts available!");
        return;
    }

    loop {
        println!("Deposit Amount");
        let name = get_input("Account Name:");

        // Find account
        if let Some(acc) = accounts.iter_mut().find(|a| a.name == name) {
            
            println!("Current Balance: {:.2}", acc.get_balance());
            println!("Currency: PHP\n");
            let amount_str = get_input("Deposit Amount:");
            
            match amount_str.trim().parse::<f64>() {

                Ok(amount) => {
                    
                    acc.deposit(amount);
                    println!("Updated Balance: {:.2}\n", acc.get_balance());                    
                }
                _ => {
                    println!("Invalid amount, try again.");
                    continue;
                }
            }

            let action = get_input("Back to the Main Menu (Y/N): ");
            if action.eq_ignore_ascii_case("Y") {
                break;
            } else {
                println!("Deposit Again...");
            }
        } else {
            println!("Account not found!");
        }
    }
}


fn withdraw_amount(accounts: &mut Vec<Account>) {

    if accounts.is_empty() {
        println!("No accounts available!");
        return;
    }

    loop {
        println!("Withdraw Amount");
        let name = get_input("Account Name:");

        // Find account
        if let Some(acc) = accounts.iter_mut().find(|a| a.name == name) {
            println!("Current Balance: {:.2}", acc.get_balance());
            println!("Currency: PHP\n");

            let amount_str = get_input("Withdraw Amount:");
            
            match amount_str.trim().parse::<f64>() {
                Ok(amount) if (acc.withdraw(amount)) => {

                    println!("Updated Balance: {:.2}\n", acc.get_balance());
                }
                _ => {
                    println!("Insufficient funds, try again.");
                    continue;
                }
            }

            let action = get_input("Back to the Main Menu (Y/N): ");
            if action.eq_ignore_ascii_case("Y") {
                break;
            } else {
                println!("Withdraw Again...");
            }
        } else {
            println!("Account not found!");
        }
    }

    

}

fn print_currency_menu(){

    println!("[1] Philippine Peso (PHP)");
    println!("[2] United States Dollar (USD)");
    println!("[3] Japanese Yen (JPY)");
    println!("[4] British Pound Sterling (GBP)");
    println!("[5] Euro (EUR)");
    println!("[6] Chinese Yuan Renminni (CNY)");
}

fn currency_exchange(rates: &ExchangeRates) {
    loop{
        println!("Foreign Currency Exchange");
        println!("Source Currency Option:");

        print_currency_menu();
        
        let source: u8 = get_number("Source Currency: ");
        let source_code = match source {
            1 => "PHP",
            2 => "USD",
            3 => "JPY",
            4 => "GBP",
            5 => "EUR",
            6 => "CNY",
            _ => {
                println!("Invalid choice.");
                return;
            }
        };

        // provide monetary amount in base currency
        let amount: f64  = get_number("Source Amount: ");

        println!("Exchanged Currency Options:");
        print_currency_menu();

        let exchange: u8 = get_number("Exchange Currency: ");
        let exchange_code = match exchange {
            1 => "PHP",
            2 => "USD",
            3 => "JPY",
            4 => "GBP",
            5 => "EUR",
            6 => "CNY",
            _ => {
                println!("Invalid choice.");
                return;
            }
        };

        if source_code == exchange_code {
            println!("Source and target currencies are the same");
            return;
        }

        let source_rate = rates.get_rate(source_code);
        let target_rate = rates.get_rate(exchange_code);

        if source_rate.is_none() || target_rate.is_none() {
            println!("One of the currencies has no recorded rate.");
            return;
        }

        let source_rate = source_rate.unwrap();
        let target_rate = target_rate.unwrap();

        let converted = amount * source_rate / target_rate;

        println!("Exchange Amount: {:.2}", converted);



        let action = get_input("Convert another currency (Y/N)? . . .");
        
        if action.eq_ignore_ascii_case("N") {
            break;
        } 
    }
    

}

fn record_exchange_rates(rates: &mut ExchangeRates) {

    loop {
        println!("Record Exchange Rates\n");
        print_currency_menu();

        
        let source_choice: u8 = get_number("Select Foreign Currency: ");
        let code = match source_choice {
            1 => "PHP",
            2 => "USD",
            3 => "JPY",
            4 => "GBP",
            5 => "EUR",
            6 => "CNY",
            _ => {
                println!("Invalid choice.");
                return;
            }
        };

        let rate: f64 = get_number("Exchange Rate: ");

        //sets rate for specified currency
        rates.set_rate(code, rate);

        let action = get_input("Back to the Main Menu (Y/N): ");
            
        if action.eq_ignore_ascii_case("Y") {
            break;
        } 
    }
    
}

fn show_interest_computation(accounts: &Vec<Account>) {
    println!("Show Interest Computation");

    if accounts.is_empty(){
        println!("No Accounts stored yet");
        return;
    }

    loop {
        let name = get_input("Account Name:");
        if let Some(acc) = accounts.iter().find(|a| a.name == name) {
                let mut balance = acc.get_balance();
                let rate = 0.05;
                let daily_rate = rate/ 365.0;

                println!("Current Balance: {:.2}", balance);
                println!("Currency: PHP\n");
                println!("Interest Rate: 5%");

                let days: u32 = get_number("Total Number of Days: ");

                println!("Day | Interest | Balance |");
                for day in 1..=days{
                     let interest = (balance * daily_rate * 100.0).round() / 100.0;
                    balance = ((balance + interest) * 100.0).round() / 100.0;
                    println!("{} | {:.2} | {:.2} |", day, interest, balance);
                }
        } else {
            println!("Account not found!");
        }

        let action = get_input("Back to the Main Menu (Y/N): ");
                
        if action.eq_ignore_ascii_case("Y") {
            break;
        } 
    }

}

fn get_number<T: std::str::FromStr>(prompt: &str) -> T {
    loop {
        let input = get_input(prompt);
        match input.trim().parse::<T>() {
            Ok(num) => return num,
            Err(_) => println!("Invalid number, please try again."),
        }
    }
}

fn get_input(prompt: &str) -> String {
    print!("{} ", prompt);
    io::stdout().flush().unwrap();

    let mut input = String::new();
    io::stdin().read_line(&mut input).expect("Failed to read");
    input.trim().to_string()
}