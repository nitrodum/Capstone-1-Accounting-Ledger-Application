package com.pluralsight;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class UI {
    private static Scanner scanner = new Scanner(System.in);
    private static boolean running = true;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static boolean runLedger;
    private static boolean runReport;

    static boolean homeScreen(float balance) {
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
                AccountingLedger.addTransaction(true);
                break;
            case "P":
                AccountingLedger.addTransaction(false);
                break;
            case "L":
                runLedger = true;
                while (runLedger) {
                    ledgerScreen(AccountingLedger.getLedger());
                }
                break;
            case "X":
                System.out.println("Thank you for using our app!");
                running = false;
                return running;
            default:
                System.out.println("Invalid option. Please select a valid option from the menu.");
        }
        buffer();
        return running;
    }

    static void ledgerScreen(ArrayList<Transaction> ledger) {
        ledger.sort(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getTime).reversed());
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
                System.out.println("All Transactions\n" +
                        "========================================================================================================================");
                display(AccountingLedger.getLedger());
                graph(AccountingLedger.getLedger());
                break;
            case "D":
                System.out.println("Deposits\n" +
                        "========================================================================================================================");
                ArrayList<Transaction> deposits = Filter.filterTransactions(true);
                display(deposits);
                graph(deposits);
                break;
            case "P":
                System.out.println("Payments\n" +
                        "========================================================================================================================");
                ArrayList<Transaction> payments = Filter.filterTransactions(false);
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
                ArrayList<Transaction> monthToDateReport = Filter.filterSinceDate(monthToDate);
                System.out.println("Month to Date\n" +
                        "========================================================================================================================");
                display(monthToDateReport);
                graph(monthToDateReport);
                break;
            case "2":
                ArrayList<Transaction> previousMonth = Filter.filterPrevious(false);
                System.out.println("Previous Month\n" +
                        "========================================================================================================================");
                display(previousMonth);
                graph(previousMonth);
                break;
            case "3":
                LocalDate yearToDate = LocalDate.now().minusDays(LocalDate.now().getDayOfYear());
                ArrayList<Transaction> yearToDateReport = Filter.filterSinceDate(yearToDate);
                System.out.println("Year to Date\n" +
                        "========================================================================================================================");
                display(yearToDateReport);
                graph(yearToDateReport);
                break;
            case "4":
                ArrayList<Transaction> previousYear = Filter.filterPrevious(true);
                System.out.println("Previous Year\n" +
                        "========================================================================================================================");
                display(previousYear);
                graph(previousYear);
                break;
            case "5":
                String vendor = enterInput("Enter the vendor name that you would like to search for.\n");
                ArrayList<Transaction> filteredByVendor = Filter.filterVendor(vendor, AccountingLedger.getLedger());
                System.out.println("Search by Vendor\n" +
                        "========================================================================================================================");
                display(filteredByVendor);
                break;
            case "6":
                ArrayList<Transaction> search = Filter.customSearch();
                System.out.println("Custom Search\n" +
                        "========================================================================================================================");
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

    static String enterInput(String message) {
        System.out.println(message);
        return scanner.nextLine();
    }

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


}
