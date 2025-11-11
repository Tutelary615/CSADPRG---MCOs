/********************
Last names: ALMERO, AQUINO, DOLOT, MARQUEZ
Language: Rust
Paradigm(s): procedural, functional, object-oriented
********************/

use std::collections::HashMap;
use std::error::Error;
use std::io::{self, Write};
use std::sync::Mutex;
use chrono::{NaiveDate}; // Library for reading dates from csv
use once_cell::sync::Lazy;
use num_format::{Locale, ToFormattedString}; // Library for formatting numbers

static APP_STATE: Lazy<Mutex<AppState>> = Lazy::new(|| Mutex::new(AppState::default()));

// Holds central dataset
#[derive(Default)]
struct AppState {
    // List of all validated project records
    projects: Vec<Project>,
}

//  Represents project record with necessary data fields 
#[derive(Clone)]
struct Project {
    region: String,
    main_island: String,
    province: String,
    contractor: String,
    approved_budget: f64,
    contract_cost: f64,
    type_of_work: String,
    start_date: NaiveDate, // expected date of project completion
    actual_completion_date: NaiveDate, 
    funding_year: i32,
}


fn main() -> Result<(), Box<dyn Error>> {
    loop {
        println!("Select Language Implementation:");
        println!("[1] Load the file");
        println!("[2] Generate Reports");
        print!("Enter Choice: ");
        io::stdout().flush().unwrap();

        let mut choice = String::new();
        io::stdin().read_line(&mut choice)?;

        match choice.trim() {
            "1" => load_and_process_file()?,
            "2" => generate_reports()?,
            _ => println!("Invalid choice. Please try again."),
        }
        println!();
    }
}

fn load_and_process_file() -> Result<(), Box<dyn Error>> {
    print!("Enter CSV filename: ");
    io::stdout().flush().unwrap();
    let mut filename = String::new();
    io::stdin().read_line(&mut filename)?;
    let filename = filename.trim();

    // create CSV reader
    let mut rdr = csv::Reader::from_path(filename)?;
    // get headers of csv
    let headers = rdr.headers()?.clone();
    let mut total_rows = 0;
    let mut filtered_rows = 0;
    let mut error_count = 0;

    // get indexes of the headers in the file
    let funding_year_idx = headers.iter().position(|h| h == "FundingYear");
    let region_idx = headers.iter().position(|h| h == "Region");
    let main_island_idx = headers.iter().position(|h| h == "MainIsland");
    let province_idx = headers.iter().position(|h| h == "Province");
    let contractor_idx = headers.iter().position(|h| h == "Contractor");
    let type_of_work_idx = headers.iter().position(|h| h == "TypeOfWork");
    let approved_budget_idx = headers.iter().position(|h| h == "ApprovedBudgetForContract");
    let contract_cost_idx = headers.iter().position(|h| h == "ContractCost");
    let start_date_idx = headers.iter().position(|h| h == "StartDate");
    let actual_completion_idx = headers.iter().position(|h| h == "ActualCompletionDate");
    
    // operate on each row record
    for result in rdr.records() {
        total_rows += 1;

        // check for errors
        let record = match result {
            Ok(r) => r,
            Err(e) => {
                eprintln!("Row {}: CSV parse error: {}", total_rows, e);
                error_count += 1;
                continue;
            }
        };

        // FundingYear validation and filter
        let fy = funding_year_idx.and_then(|i| record.get(i));
        let fy_num = match fy.and_then(|f| f.parse::<i32>().ok()) {
            Some(y) if y >= 2021 && y <= 2023 => y, //check if record is inside of year specification
            Some(_) => continue,
            None => {
                eprintln!("Row {}: Invalid FundingYear: {:?}", total_rows, fy);
                error_count += 1;
                continue;
            },
        };

        // general checks if column exists and non empty
        let region = match region_idx.and_then(|i| record.get(i)) {
            Some(v) if !v.is_empty() => v.to_string(),
            _ => { error_count += 1; continue; }
        };
        let main_island = match main_island_idx.and_then(|i| record.get(i)) {
            Some(v) if !v.is_empty() => v.to_string(),
            _ => { error_count += 1; continue; }
        };

        let province = match province_idx.and_then(|i| record.get(i)) {
            Some(v) if !v.is_empty() => v.to_string(),
            _ => { error_count += 1; continue; }
        };

        let contractor = match contractor_idx.and_then(|i| record.get(i)) {
            Some(v) if !v.is_empty() => v.to_string(),
            _ => { error_count += 1; continue; }
        };

        let type_of_work = match type_of_work_idx.and_then(|i| record.get(i)) {
            Some(v) if !v.is_empty() => v.to_string(),
            _ => { error_count += 1; continue; }
        };

        // check if numbers can be parsed as floats
        let approved_budget = match approved_budget_idx.and_then(|i| record.get(i)).and_then(|v| v.parse::<f64>().ok()) {
            Some(v) => v,
            None => { error_count += 1; continue; }
        };
        let contract_cost = match contract_cost_idx.and_then(|i| record.get(i)).and_then(|v| v.parse::<f64>().ok()) {
            Some(v) => v,
            None => { error_count += 1; continue; }
        };

        // parse data according to year month day
        let start_date = match start_date_idx.and_then(|i| record.get(i)).and_then(|v| NaiveDate::parse_from_str(v, "%Y-%m-%d").ok()) {
            Some(d) => d,
            None => { error_count += 1; continue; }
        };

        let actual_completion_date = match actual_completion_idx.and_then(|i| record.get(i)).and_then(|v| NaiveDate::parse_from_str(v, "%Y-%m-%d").ok()) {
            Some(d) => d,
            None => { error_count += 1; continue; }
        };

        filtered_rows += 1;

        let mut state = APP_STATE.lock().unwrap();
        state.projects.push(Project {
            region,
            main_island,
            province,
            contractor,
            type_of_work,
            approved_budget,
            contract_cost,
            start_date,
            actual_completion_date,
            funding_year: fy_num,
        });
    }
    println!("Processing dataset... ({} rows loaded, {} filtered for 2021-2023)", total_rows, filtered_rows);
    if error_count > 0 {
        println!("{} parse/validation errors encountered.", error_count);
    }
    Ok(())
}

// Format floats to add commas and restrict to two decimal places
fn format_comma_float(val: f64) -> String {
    let sign = if val.is_sign_negative() { "-" } else { "" };
    let abs_val = val.abs();
    let whole = abs_val.trunc() as i64;
    let fraction = (abs_val.fract() * 100.0).round() as u8;
    format!("{}{}.{:02}", sign, whole.to_formatted_string(&Locale::en), fraction)
}

fn generate_reports() -> Result<(), Box<dyn Error>> {

    let projects = {
        let state = APP_STATE.lock().unwrap();
        state.projects.clone()
    };
    
    if projects.is_empty() {
        println!("No data loaded. Please choose [1] Load the file first.");
        return Ok(());
    }

    println!("Generating reports...");

    generate_report_1(&projects)?;
    generate_report_2(&projects)?;
    generate_report_3(&projects)?;

    // Summary Stats 
    let total_projects = projects.len();
    // Count unique contractors using a HashSet 
    let total_contractors = projects.iter().map(|p| p.contractor.clone()).collect::<std::collections::HashSet<_>>().len();
    // Count unique provinces using a HashSet.
    let total_provinces = projects.iter().map(|p| p.province.clone()).collect::<std::collections::HashSet<_>>().len();

    // Calculate total savings (ApprovedBudget - ContractCost).
    let total_savings: f64 = projects.iter().map(|p| p.approved_budget - p.contract_cost).sum();

    // Calculate the average project delay
    let global_avg_delay: f64 = if projects.is_empty() {
        0.0
    } else {
        projects
            .iter()
            .map(|p| (p.actual_completion_date - p.start_date).num_days() as f64)
            .sum::<f64>()
            / (projects.len() as f64)
    };

     // Use serde_json to create JSON file
    use serde_json::json;
    let summary = json!({
        "total_projects": total_projects,
        "total_contractors": total_contractors,
        "total_provinces": total_provinces,
        "global_avg_delay": format_comma_float(global_avg_delay),
        "total_savings": format_comma_float(total_savings)
    });

    let file = std::fs::File::create("summary.json")?;
    serde_json::to_writer_pretty(file, &summary)?;

    println!("\nSummary Stats (summary.json):");
    println!("{}", summary.to_string());
    println!("\nBack to Report Selection (Y/N):");

    Ok(())
}

fn generate_report_1(projects: &Vec<Project>) -> Result<(), Box<dyn Error>> {
    // Define row structure to be used for report1
    #[derive(Clone)]
    struct Row {
        region: String,
        main_island: String,
        total_budget: f64,
        median_savings: f64,
        avg_delay: f64,
        delay_over30_pct: f64,
        efficiency_score: f64,
    }
    
    // Group projects using a hashmap with key of (Region, MainIsland)
    let mut grouped: HashMap<(String, String), Vec<&Project>> = HashMap::new();
    for p in projects {
        grouped.entry((p.region.clone(), p.main_island.clone()))
            .or_default()
            .push(p);
    }

    let mut rows: Vec<Row> = Vec::new();
    const DELAY_THRESHOLD_DAYS: i64 = 30;

    // for each (region, mainIsland) combination
    for ((region, main_island), items) in grouped {

        //calculate sum of all budgets
        let total_budget: f64 = items.iter().map(|p| p.approved_budget).sum();

        // Compute savings and median
        let mut savings: Vec<f64> = items.iter().map(|p| p.approved_budget - p.contract_cost).collect();
        savings.retain(|v| !v.is_nan()); // remove values that are not a number
        savings.sort_by(|a, b| a.partial_cmp(b).unwrap()); // sort for median 

        let median_savings = if savings.is_empty() {
            0.0
        } else if savings.len() % 2 == 1 {
            savings[savings.len() / 2]
        } else {
            let mid = savings.len() / 2;
            (savings[mid - 1] + savings[mid]) / 2.0
        };

        // Compute completion delays
        let delays: Vec<i64> = items.iter().map(|p| {
            // cannot be negative
            (p.actual_completion_date - p.start_date).num_days().max(0)
        }).collect();

        let avg_delay = if delays.is_empty() { 0.0 } else { (delays.iter().sum::<i64>() as f64) / (delays.len() as f64) };

        let delay_over30_count = delays.iter().filter(|d| **d > DELAY_THRESHOLD_DAYS).count();

        let delay_over30_pct = if delays.is_empty() { 0.0 } else { (delay_over30_count as f64) * 100.0 / (delays.len() as f64) };

        // Compute efficiency score
        let raw_efficiency = if avg_delay > 0.0 {
            (median_savings / avg_delay) * 100.0
        } else {
            0.0
        };

        // push computations to results row
        rows.push(Row {
            region,
            main_island,
            total_budget,
            median_savings,
            avg_delay,
            delay_over30_pct,
            efficiency_score: raw_efficiency,
        });
    }

    // Normalize efficiency scores
    if let (Some(min), Some(max)) = (
        rows.iter().map(|r| r.efficiency_score).reduce(f64::min),
        rows.iter().map(|r| r.efficiency_score).reduce(f64::max),
    ) {
        for r in &mut rows {
            if max > min {
                // Formula application:
                r.efficiency_score = ((r.efficiency_score - min) / (max - min)) * 100.0;
            } else {
                r.efficiency_score = 100.0; // all same values
            }
        }
    }

    // Sort descending by EfficiencyScore
    rows.sort_by(|a, b| b.efficiency_score.partial_cmp(&a.efficiency_score).unwrap());

    // Display Report 1
    println!("\nReport 1: Regional Flood Mitigation Efficiency Summary");
    println!("(Aggregated by Region & MainIsland; 2021–2023 Projects)\n");

    // Header and Print loop (using format_comma_float)
    println!("| {:<40} | {:<10} | {:>18} | {:>15} | {:>13} | {:>12} | {:>17} |",
        "Region", "MainIsland", "TotalBudget", "MedianSavings", "AvgDelayDays", "HighDelayPct", "EfficiencyScore"
    );

    println!("{}", "-".repeat(147));

    // print the top 2 rows only
    for r in rows.iter().take(2) {
        println!("| {:<40} | {:<10} | {:>18} | {:>15} | {:>13.2} | {:>12.2} | {:>17.2} |",
            r.region.trim(),
            r.main_island.trim(),
            format_comma_float(r.total_budget),
            format_comma_float(r.median_savings),
            r.avg_delay,
            r.delay_over30_pct,
            r.efficiency_score
        );
    }
    println!("\nFull table exported to report1_regional_summary.csv");

    // Export CSV (sorted)
    let mut wtr = csv::Writer::from_path("report1_regional_summary.csv")?;
    wtr.write_record(["Region", "MainIsland", "TotalBudget", "MedianSavings", "AvgDelayDays", "HighDelayPct", "EfficiencyScore"])?;
    for r in rows {
        wtr.write_record(&[
            r.region,
            r.main_island,
            format!("{:.2}", r.total_budget),
            format!("{:.2}", r.median_savings),
            format!("{:.2}", r.avg_delay),
            format!("{:.1}", r.delay_over30_pct),
            format!("{:.2}", r.efficiency_score),
        ])?;
    }
    wtr.flush()?; // ensure buffer is written
    
    Ok(())
}


fn generate_report_2(projects: &Vec<Project>) -> Result<(), Box<dyn Error>> {
    // Helper function moved to top-level scope (for use here)
    fn truncate_name(name: &str, max_len: usize) -> String {
        if name.len() > max_len {
            format!("{}...", &name[..max_len - 3])
        } else {
            name.to_string()
        }
    }

    // Group by Contractor using hashmap
    let mut contractor_group: HashMap<String, Vec<&Project>> = HashMap::new();
    for p in projects {
        contractor_group.entry(p.contractor.clone()).or_default().push(p);
    }

    // create structure for report 2
    #[derive(Debug)]
    struct ContractorRow {
        contractor: String,
        total_cost: f64,
        num_projects: usize,
        avg_delay: f64,
        total_savings: f64,
        reliability_index: f64,
        risk_flag: String,
    }

    let mut contractor_rows: Vec<ContractorRow> = Vec::new();

    // for each contractor
    for (contractor, items) in contractor_group {
        // Filter: Must have at least 5 projects
        if items.len() < 5 {
            continue;
        }

        // calculate numerical data
        let total_cost: f64 = items.iter().map(|p| p.contract_cost).sum();
        let total_savings: f64 = items.iter().map(|p| p.approved_budget - p.contract_cost).sum();

        // calculate delay in days
        let delays: Vec<i64> = items.iter()
            .map(|p| (p.actual_completion_date - p.start_date).num_days().max(0))
            .collect();

        // calculate average delay
        let avg_delay = if delays.is_empty() {
            0.0
        } else {
            delays.iter().sum::<i64>() as f64 / delays.len() as f64
        };

        // formula implementation
        let mut reliability_index = (1.0 - (avg_delay / 90.0)) * (total_savings / total_cost) * 100.0;
        if reliability_index > 100.0 {
            reliability_index = 100.0;
        }


        let risk_flag = if reliability_index < 50.0 {
            "High Risk".to_string()
        } else {
            "Low Risk".to_string()
        };

        // place calculated data into results 
        contractor_rows.push(ContractorRow {
            contractor,
            total_cost,
            num_projects: items.len(),
            avg_delay,
            total_savings,
            reliability_index,
            risk_flag,
        });
    }

    // Sort by descending total cost
    contractor_rows.sort_by(|a, b| b.total_cost.partial_cmp(&a.total_cost).unwrap());

    // Take top 15 contractors
    let top_rows: Vec<_> = contractor_rows.into_iter().take(15).collect();

    // Print Report 2
    println!("\nReport 2: Top Contractors Performance Ranking");
    println!("(Top 15 by TotalCost, >=5 Projects)\n");

    println!("| {:<4} | {:<45} | {:<18} | {:<12} | {:<10} | {:<16} | {:<18} | {:<10} |",
        "Rank", "Contractor", "TotalCost", "NumProjects", "AvgDelay", "TotalSavings", "ReliabilityIndex", "RiskFlag"
    );
    println!("{}", "-".repeat(158));

    // display top two contractors
    for (i, r) in top_rows.iter().enumerate().take(2) {
        println!("| {:<4} | {:<45} | {:>18} | {:>12} | {:>10.1} | {:>16} | {:>18.2} | {:<10} |",
            i + 1,
            truncate_name(&r.contractor, 45),
            format_comma_float(r.total_cost),
            r.num_projects,
            r.avg_delay,
            format_comma_float(r.total_savings),
            r.reliability_index,
            r.risk_flag
        );
    }
    
    println!("\nFull table exported to report2_contractor_ranking.csv");

    // Export CSV
    let mut wtr2 = csv::Writer::from_path("report2_contractor_ranking.csv")?;
    wtr2.write_record(["Rank", "Contractor", "TotalCost", "NumProjects", "AvgDelay", "TotalSavings", "ReliabilityIndex", "RiskFlag"])?;

    for (i, r) in top_rows.iter().enumerate() {
        wtr2.write_record(&[
            (i + 1).to_string(),
            r.contractor.clone(),
            format!("{:.2}", r.total_cost),
            r.num_projects.to_string(),
            format!("{:.2}", r.avg_delay),
            format!("{:.2}", r.total_savings),
            format!("{:.2}", r.reliability_index),
            r.risk_flag.clone(),
        ])?;
    }
    wtr2.flush()?; // make sure buffer is written

    Ok(())
}


fn generate_report_3(projects: &Vec<Project>) -> Result<(), Box<dyn Error>> {
    // Group by (FundingYear, TypeOfWork) using hashmap
    let mut grouped3: HashMap<(i32, String), Vec<&Project>> = HashMap::new();
    for p in projects {
        grouped3
            .entry((p.funding_year, p.type_of_work.clone()))
            .or_default()
            .push(p);
    }

    // define structure for report 3
    #[derive(Clone)]
    struct Row3 {
        funding_year: i32,
        type_of_work: String,
        total_projects: usize,
        avg_savings: f64,
        overrun_rate: f64,
        yoy_change: f64,
    }

    let mut rows3: Vec<Row3> = Vec::new();
    let mut baseline_savings: HashMap<String, f64> = HashMap::new();

    // Compute 2021 baseline averages
    for ((year, work_type), items) in &grouped3 {
        if *year == 2021 {
            let avg_savings = if items.is_empty() {
                0.0
            } else {
                // calculate average savings
                items.iter()
                    .map(|p| p.approved_budget - p.contract_cost)
                    .sum::<f64>() / (items.len() as f64)
            };

            // save as baseline for this work type
            baseline_savings.insert(work_type.clone(), avg_savings);
        }
    }

    // Fill per (year, type_of_work)
    for ((year, work_type), items) in grouped3 {
        let total_projects = items.len();

        // calculate average savings for cur year and worktype
        let savings: Vec<f64> = items.iter().map(|p| p.approved_budget - p.contract_cost).collect();

        let avg_savings = if savings.is_empty() {
            0.0
        } else {
            savings.iter().sum::<f64>() / (savings.len() as f64)
        };

        // calculate overrun rate
        let overrun_rate = if savings.is_empty() {
            0.0
        } else {
            let negative_count = savings.iter().filter(|s| **s < 0.0).count();
            (negative_count as f64) * 100.0 / (savings.len() as f64)
        };

        // Compute YoY change from 2021 baseline
        // default = 0 if worktype wasnt present in 2021
        let baseline = baseline_savings.get(&work_type).cloned().unwrap_or(0.0);
        let yoy_change = if baseline.abs() < f64::EPSILON {
            0.0
        } else {
            ((avg_savings - baseline) / baseline) * 100.0
        };

        // push calculated fields into results
        rows3.push(Row3 {
            funding_year: year,
            type_of_work: work_type,
            total_projects,
            avg_savings,
            overrun_rate,
            yoy_change,
        });
    }

    // Sort by year then avg savings
    rows3.sort_by(|a, b| {
        
        a.funding_year.cmp(&b.funding_year) 
            .then_with(|| {

            b.avg_savings.partial_cmp(&a.avg_savings).unwrap_or(std::cmp::Ordering::Less)
        })
    });

    // Print formatted table
    println!("\nReport 3: Annual Project Type Cost Overrun Trends");
    println!("(Grouped by FundingYear and TypeOfWork)\n");
    
    println!("| {:<12} | {:<40} | {:>15} | {:>15} | {:>12} | {:>12} |",
        "FundingYear", "TypeOfWork", "TotalProjects", "AvgSavings", "OverrunRate", "YoYChange"
    );
    println!("{}", "-".repeat(125));

    // print top two rows
    for r in rows3.iter().take(2) {
        let type_of_work_display = if r.type_of_work.len() > 40 {
            format!("{}...", &r.type_of_work[..37])
        } else {
            r.type_of_work.trim().to_string()
        };

        println!("| {:<12} | {:<40} | {:>15} | {:>15.2} | {:>11.1} | {:>11.1} |",
            r.funding_year,
            type_of_work_display,
            r.total_projects,
            r.avg_savings,
            r.overrun_rate,
            r.yoy_change
        );
    }
    
    println!("\n(Full table exported to report3_annual_trends.csv)");

    // Export CSV
    let mut wtr3 = csv::Writer::from_path("report3_annual_trends.csv")?;
    wtr3.write_record(["FundingYear", "TypeOfWork", "TotalProjects", "AvgSavings", "OverrunRate", "YoYChange"])?;
    for r in &rows3 {
        wtr3.write_record(&[
            r.funding_year.to_string(),
            r.type_of_work.clone(),
            r.total_projects.to_string(),
            format!("{:.2}", r.avg_savings),
            format!("{:.2}", r.overrun_rate),
            format!("{:.2}", r.yoy_change),
        ])?;
    }
    wtr3.flush()?;

    Ok(())
}
