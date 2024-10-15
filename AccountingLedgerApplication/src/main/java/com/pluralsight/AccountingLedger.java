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
                break;
            default:
                System.out.println("Invalid Option");
        }
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
                displayLedger(ledger);
                break;
            case "D":
                ArrayList<Transaction> deposits = filterTransactions(true);
                displayLedger(deposits);
                break;
            case "P":
                ArrayList<Transaction> payments = filterTransactions(false);
                displayLedger(payments);
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
                break;
            default:
                System.out.println("Invalid Option");
        }
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
                        "0) Return to Ledger\n" +
                        "*******************************************************************************\n");

        switch (input) {
            case "1":
                LocalDate monthToDate = LocalDate.now().minusMonths(1);
                ArrayList<Transaction> monthToDateReport = filterToDate(monthToDate);
                displayLedger(monthToDateReport);
                break;
            case "2":
                filterPrevious(false);
                break;
            case "3":
                LocalDate yearToDate = LocalDate.now().minusYears(1);
                ArrayList<Transaction> yearToDateReport = filterToDate(yearToDate);
                displayLedger(yearToDateReport);
                break;
            case "4":
                filterPrevious(true);
                break;
            case "5":
                System.out.println("Search by vendor has not been implemented yet!");
                break;
            case "0":
                System.out.println("Returning to Ledger!");
                runReport = false;
                break;
            default:
                System.out.println("Invalid Option");
        }
    }

    static ArrayList<Transaction> filterToDate(LocalDate limit) {
        ArrayList<Transaction> filteredToDate = new ArrayList<>();
        for (Transaction t : ledger) {
            if (t.getDate().isAfter(limit)) {
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
        displayLedger(filteredPreviousMonth);
    }

    static void displayLedger(ArrayList<Transaction> toDisplay) {
        for (Transaction t : toDisplay) {
            System.out.println("Date: " + t.getDate() + " Time: " + t.getTime().format(formatter) + " Description: " + t.getDescription() + " Vendor: " + t.getVendor() + " Amount: " + t.getAmount());
        }
    }

    static ArrayList<Transaction> filterTransactions(boolean deposit) {
        ArrayList<Transaction> filteredTransactions = new ArrayList<>();
            for (Transaction t : ledger) {
                if ((t.getAmount() > 0 && deposit) || (t.getAmount() < 0 && !deposit)) {
                    filteredTransactions.add(t);
                }
            }
            return filteredTransactions;
    }

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
