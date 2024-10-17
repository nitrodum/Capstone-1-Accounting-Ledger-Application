package com.pluralsight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AccountingLedger {
    private static ArrayList<Transaction> ledger = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static float balance = 0f;
    private static boolean running = true;
    private static boolean runLedger;
    private static boolean runReport;

    public static void main(String[] args) {
        loadLedger();
        while (running) {
            homeScreen();
        }
        scanner.close();
    }

    // Menus
    static void homeScreen() {
        String input = enterInput(
                        "Home\n" +
                        "========================================================================================================================\n" +
                        "Welcome to your account! Your current balance is: " + String.format("$%.2f", balance) + "\n" +
                        "Please enter the letter corresponding to the command you would like to perform.\n" +
                        "D) Add Deposit\n" +
                        "P) Make Payment\n" +
                        "L) View Ledger\n" +
                        "X) Exit\n" +
                        "========================================================================================================================").toUpperCase();

        switch (input) {
            case "D":
                addTransaction(true);
                break;
            case "P":
                addTransaction(false);
                break;
            case "L":
                ledger.sort(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getTime).reversed());
                runLedger = true;
                while (runLedger) {
                    ledgerScreen();
                }
                break;
            case "X":
                System.out.println("Thank you for using our app!");
                running = false;
                return;
            default:
                System.out.println("Invalid option. Please select a valid option from the menu.");
        }
        buffer();
    }

    static void ledgerScreen() {
        String input = enterInput(
                        "Ledger\n" +
                        "========================================================================================================================\n" +
                        "Welcome to your ledger!\n" +
                        "Please enter the letter corresponding to the command you would like to perform.\n" +
                        "A) Display All Transactions\n" +
                        "D) Display All Deposits\n" +
                        "P) Display All Payments\n" +
                        "R) View Reports\n" +
                        "H) Return to Home Screen\n" +
                        "========================================================================================================================").toUpperCase();

        switch (input) {
            case "A":
                display(ledger);
                graph(ledger);
                break;
            case "D":
                ArrayList<Transaction> deposits = filterTransactions(true);
                display(deposits);
                graph(deposits);
                break;
            case "P":
                ArrayList<Transaction> payments = filterTransactions(false);
                display(payments);
                graph(payments);
                break;
            case "R":
                runReport = true;
                while (runReport) {
                    reportScreen();
                }
                break;
            case "H":
                System.out.println("Returning to Home Screen!");
                runLedger = false;
                return;
            default:
                System.out.println("Invalid option. Please select a valid option from the menu.");
        }
        buffer();
    }

    static void reportScreen() {
        String input = enterInput(
                        "Reports\n" +
                        "========================================================================================================================\n" +
                        "Welcome to your reports!\n" +
                        "Please enter the number corresponding to the command you would like to perform.\n" +
                        "1) Month To Date\n" +
                        "2) Previous Month\n" +
                        "3) Year To Date\n" +
                        "4) Previous Year\n" +
                        "5) Search by Vendor\n" +
                        "6) Custom Search\n" +
                        "0) Return to Ledger\n" +
                        "========================================================================================================================");

        switch (input) {
            case "1":
                LocalDate monthToDate = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth());
                ArrayList<Transaction> monthToDateReport = filterSinceDate(monthToDate);
                display(monthToDateReport);
                graph(monthToDateReport);
                break;
            case "2":
                ArrayList<Transaction> previousMonth = filterPrevious(false);
                display(previousMonth);
                graph(previousMonth);
                break;
            case "3":
                LocalDate yearToDate = LocalDate.now().minusDays(LocalDate.now().getDayOfYear());
                ArrayList<Transaction> yearToDateReport = filterSinceDate(yearToDate);
                display(yearToDateReport);
                graph(yearToDateReport);
                break;
            case "4":
                ArrayList<Transaction> previousYear = filterPrevious(true);
                display(previousYear);
                graph(previousYear);
                break;
            case "5":
                String vendor = enterInput("Enter the vendor name that you would like to search for.\n");
                ArrayList<Transaction> filteredByVendor = filterVendor(vendor, ledger);
                display(filteredByVendor);
                break;
            case "6":
                ArrayList<Transaction> search = customSearch();
                display(search);
                break;
            case "0":
                System.out.println("Returning to Ledger!");
                runReport = false;
                return;
            default:
                System.out.println("Invalid option. Please select a valid option from the menu.");
        }
        buffer();
    }

    // Home Screen Methods
    static void addTransaction(boolean deposit) {
        String action;
        if (deposit) {
            action = "deposit";
        } else {
            action = "payment";
        }

        String answer = enterInput("Please enter the details for the " + action + "\n" +
                "Date and Time will default to current if not set.\n" +
                "Would you like to set a Date and Time?");

        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        if (answer.equalsIgnoreCase("yes")) {
            do {
                date = validateDate("Please enter a date in format (YYYY-MM-DD): ");
            } while (date == null);
            time = validateTime("Please enter a time in format (HH:MM:SS): ");
        }

        String description = enterInput("Description: ");

        String vendor = enterInput("Vendor name: ");

        float amount = 0;
        boolean valid = false;

        while (!valid) {
            try {
                System.out.println("Amount: ");
                amount = scanner.nextFloat();
                if (deposit || amount < balance) {
                    valid = true;
                } else {
                    System.out.println("Insufficient funds for this payment");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            } finally {
                scanner.nextLine();
            }
        }

        if (!deposit) {
            amount *= -1;
        }

        Transaction t = new Transaction(date, time, description, vendor, amount);
        ledger.add(t);
        balance += t.getAmount();

        try {
            FileWriter writer = new FileWriter("transactions.csv", true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.newLine();
            bufferedWriter.write(date.toString() + "|" + time.format(formatter) + "|" + description + "|" + vendor + "|" + amount);
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println("Error writing to file!");
            e.printStackTrace();
        }
        System.out.println("Transaction added successfully!");
    }

    static LocalDate validateDate(String message) {
        boolean valid = false;
        LocalDate date = null;

        while (!valid) {
            String input = enterInput(message);
            if (input.isEmpty()) {
                return null;
            }
            try {
                date = LocalDate.parse(input);
                valid = true;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid input. Please enter a valid date in format (YYYY-MM-DD)");
            }
        }
        return date;
    }

    static LocalTime validateTime(String message) {
        boolean valid = false;
        LocalTime time = null;

        while (!valid) {
            try {
                time = LocalTime.parse(enterInput(message));
                valid = true;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid input. Please enter a valid time in format (HH:MM:SS)");
            }
        }
        return time;
    }

    // Ledger Methods
    static ArrayList<Transaction> filterTransactions(boolean deposit) {
        ArrayList<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction t : ledger) {
            if ((t.getAmount() > 0 && deposit) || (t.getAmount() < 0 && !deposit)) {
                filteredTransactions.add(t);
            }
        }
        return filteredTransactions;
    }

    // Report Methods
    static ArrayList<Transaction> filterSinceDate(LocalDate limit) {
        ArrayList<Transaction> filteredSinceDate = new ArrayList<>();
        for (Transaction t : ledger) {
            if (t.getDate().isAfter(limit)) {
                filteredSinceDate.add(t);
            }
        }
        return filteredSinceDate;
    }

    static ArrayList<Transaction> filterToDate(LocalDate limit, ArrayList<Transaction> toFilter) {
        ArrayList<Transaction> filteredToDate = new ArrayList<>();
        for (Transaction t : toFilter) {
            if (t.getDate().isBefore(limit)) {
                filteredToDate.add(t);
            }
        }
        return filteredToDate;
    }

    static ArrayList<Transaction> filterPrevious(boolean filterPreviousYear) {
        int previousMonth = LocalDate.now().minusMonths(1).getMonthValue();

        // Adjust yearComparison to previous year if filterPreviousYear is true or if the current month is January
        int yearComparison;
        if (filterPreviousYear || previousMonth == 12) {
            yearComparison = LocalDate.now().getYear() - 1;
        } else {
            yearComparison = LocalDate.now().getYear();
        }

        ArrayList<Transaction> filtered = new ArrayList<>();

        // Filter the transactions based on the adjusted year and month
        for (Transaction t : ledger) {
            if (t.getDate().getYear() == yearComparison) {
                // If filtering by previous year, include all transactions from that year
                // Otherwise, only include transactions from the previous month
                if (filterPreviousYear || (t.getDate().getMonthValue() == previousMonth)) {
                    filtered.add(t);
                }
            }
        }
        return filtered;
    }

    static ArrayList<Transaction> filterVendor(String vendor, ArrayList<Transaction> toFilter) {
        ArrayList<Transaction> filteredByVendor = new ArrayList<>();
        for (Transaction t : toFilter) {
            if (t.getVendor().equalsIgnoreCase(vendor)) {
                filteredByVendor.add(t);
            }
        }
        return filteredByVendor;
    }

    static ArrayList<Transaction> filterDescription(String description, ArrayList<Transaction> toFilter) {
        ArrayList<Transaction> filteredByDescription = new ArrayList<>();
        for (Transaction t : toFilter) {
            if (t.getVendor().equalsIgnoreCase(description)) {
                filteredByDescription.add(t);
            }
        }
        return filteredByDescription;
    }

    static ArrayList<Transaction> filterAmount(float amount, ArrayList<Transaction> toFilter) {
        ArrayList<Transaction> filteredByAmount = new ArrayList<>();
        for (Transaction t : toFilter) {
            if (t.getAmount() == amount) {
                filteredByAmount.add(t);
            }
        }
        return filteredByAmount;
    }

    static ArrayList<Transaction> customSearch() {
        ArrayList<Transaction> custom = new ArrayList<>(ledger);

        LocalDate startDate = validateDate("Enter the start date:");
        if (startDate != null) {
            custom = filterSinceDate(startDate);
        }

        LocalDate endDate = validateDate("Enter the end date:");
        if (endDate != null) {
            custom = filterToDate(endDate, ledger);
        }

        String description = enterInput("Enter the description:");
        if (!description.isEmpty()) {
            custom = filterDescription(description, ledger);
        }

        String vendor = enterInput("Enter the vendor name:");
        if (!vendor.isEmpty()) {
            custom = filterVendor(vendor, ledger);
        }

        boolean valid = false;
        String amount;

       while (!valid) {
           amount = enterInput("Enter the amount:");
           if (amount.isEmpty()) {
               valid = true;
           } else {
               try {
                   float parsedAmount = Float.parseFloat(amount);
                   custom = filterAmount(parsedAmount, ledger);
                   valid = true;
               } catch (NumberFormatException e) {
                   System.out.println("Invalid amount. Please enter a valid number.");
               }
           }
       }

        return custom;
    }

    // Helper Methods
    static void display(ArrayList<Transaction> toDisplay) {
        System.out.println(
                "Date       | Time     | Description          | Amount    | Vendor\n" +
                "--------------------------------------------------------------------------");
        for (Transaction t : toDisplay) {
            System.out.printf("%-10s | %-8s | %-20s | %9.2f | %-15s\n",
                    t.getDate(), t.getTime().format(formatter), t.getDescription(), t.getAmount(), t.getVendor());
        }
        System.out.println();
    }

    static void graph(ArrayList<Transaction> toDraw) {
        String draw = enterInput("Would you like to visualize this data? (yes/no)");
        if (draw.equalsIgnoreCase("yes")) {
            Graph.drawGraph(toDraw);
        }
    }

    static void buffer() {
        enterInput("Enter any button to continue");
    }

    static void loadLedger() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("transactions.csv"));
            String input = bufferedReader.readLine();

            while ((input = bufferedReader.readLine()) != null) {
                String[] data = input.split("\\|");
                Transaction t = new Transaction(LocalDate.parse(data[0]), LocalTime.parse(data[1]), data[2], data[3], Float.parseFloat(data[4]));
                ledger.add(t);
                balance += t.getAmount();
            }
        } catch (Exception e) {
            System.out.println("Error reading file!");
            e.printStackTrace();
        }
    }

    static String enterInput(String message) {
        System.out.println(message);
        return scanner.nextLine();
    }

    static void testLedger() {
        for (Transaction t : ledger) {
            System.out.println("Date: " + t.getDate() + " Time: " + t.getTime() + " Description: " + t.getDescription() + " Vendor: " + t.getVendor() + " Amount: " + t.getAmount());
        }
    }
}
