package com.pluralsight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class AccountingLedger {
    private static ArrayList<Transaction> ledger = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
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
                        "*******************************************************************************\n" +
                        "Welcome to your account!\n" +
                        "Please enter the letter corresponding to the command you would like to perform.\n" +
                        "D) Add Deposit\n" +
                        "P) Make Payment\n" +
                        "L) View Ledger\n" +
                        "X) Exit\n" +
                        "*******************************************************************************").toUpperCase();

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
                System.out.println("Invalid Option");
        }
        buffer();
    }

    static void ledgerScreen() {
        String input = enterInput(
                "*******************************************************************************\n" +
                "Welcome to your ledger!\n" +
                "Please enter the letter corresponding to the command you would like to perform.\n" +
                "A) Display All Transactions\n" +
                "D) Display All Deposits\n" +
                "P) Display All Payments\n" +
                "R) View Reports\n" +
                "H) Return to Home Screen\n" +
                "*******************************************************************************\n").toUpperCase();

        switch (input) {
            case "A":
                display(ledger);
                break;
            case "D":
                ArrayList<Transaction> deposits = filterTransactions(true);
                display(deposits);
                break;
            case "P":
                ArrayList<Transaction> payments = filterTransactions(false);
                display(payments);
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
                System.out.println("Invalid Option");
        }
        buffer();
    }

    static void reportScreen() {
        String input = enterInput(
                "*******************************************************************************\n" +
                        "Welcome to your reports!\n" +
                        "Please enter the number corresponding to the command you would like to perform.\n" +
                        "1) Month To Date\n" +
                        "2) Previous Month\n" +
                        "3) Year To Date\n" +
                        "4) Previous Year\n" +
                        "5) Search by Vendor\n" +
                        "6) Custom Search\n" +
                        "0) Return to Ledger\n" +
                        "*******************************************************************************\n");

        switch (input) {
            case "1":
                LocalDate monthToDate = LocalDate.now().minusMonths(1);
                ArrayList<Transaction> monthToDateReport = filterSinceDate(monthToDate);
                display(monthToDateReport);
                break;
            case "2":
                filterPrevious(false);
                break;
            case "3":
                LocalDate yearToDate = LocalDate.now().minusYears(1);
                ArrayList<Transaction> yearToDateReport = filterSinceDate(yearToDate);
                display(yearToDateReport);
                break;
            case "4":
                filterPrevious(true);
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
                System.out.println("Invalid Option");
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

        String message = "Please enter the details for the " + action + "\n" +
                "Description: \n";

        String description = enterInput(message);

        String vendor = enterInput("Vendor name: ");

        System.out.println("Amount: ");
        float amount = scanner.nextFloat();
        scanner.nextLine();

        if (!deposit) {
            amount *= -1;
        }

        Transaction t = new Transaction(LocalDate.now(), LocalTime.now(), description, vendor, amount);
        ledger.add(t);

        try {
            FileWriter writer = new FileWriter("transactions.csv", true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.newLine();
            bufferedWriter.write(LocalDate.now().toString() + "|" + LocalTime.now().format(formatter) + "|" + description + "|" + vendor + "|" + amount);
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println("Error writing to file!");
            e.printStackTrace();
        }
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
        ArrayList<Transaction> filteredToDate = new ArrayList<>();
        for (Transaction t : ledger) {
            if (t.getDate().isAfter(limit)) {
                filteredToDate.add(t);
            }
        }
        return filteredToDate;
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

    static void filterPrevious(boolean filterPreviousYear) {
        int previousMonth = LocalDate.now().minusMonths(1).getMonthValue();
        int yearComparison;
        if (filterPreviousYear) {
            yearComparison = LocalDate.now().getYear()-1;
        } else {
            yearComparison = LocalDate.now().getYear();
        }
        ArrayList<Transaction> filteredPreviousMonth = new ArrayList<>();
        for (Transaction t : ledger) {
            if (t.getDate().getYear() == yearComparison) {
                if (filterPreviousYear || (t.getDate().getMonthValue() == previousMonth)) {
                    filteredPreviousMonth.add(t);
                }
            }
        }
        display(filteredPreviousMonth);
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

        String startDate = enterInput("Enter the start date");
        if (!startDate.isEmpty()) {
            custom = filterSinceDate(LocalDate.parse(startDate));
        }

        String endDate = enterInput("Enter the end date");
        if (!endDate.isEmpty()) {
                custom = filterToDate(LocalDate.parse(endDate), ledger);
        }

        String description = enterInput("Enter the description");
        if (!description.isEmpty()) {
                custom = filterDescription(description, ledger);
        }

        String vendor = enterInput("Enter the vendor name");
        if (!vendor.isEmpty()) {
                custom = filterVendor(vendor, ledger);
        }

        String amount = enterInput("Enter the amount");
        if (!amount.isEmpty()) {
                custom = filterAmount(Float.parseFloat(amount), ledger);
        }

        return custom;
    }

    // Helper Methods
    static void display(ArrayList<Transaction> toDisplay) {
        for (Transaction t : toDisplay) {
            System.out.println("Date: " + t.getDate() + " Time: " + t.getTime().format(formatter) + " Description: " + t.getDescription() + " Vendor: " + t.getVendor() + " Amount: " + t.getAmount());
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
