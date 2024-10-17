package com.pluralsight;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AccountingLedger {
    private static ArrayList<Transaction> ledger = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static float balance = 0f;
    private static boolean running = true;

    public static void main(String[] args) {
        balance = FileManager.loadLedger(ledger);
        while (running) {
            running = UI.homeScreen(balance);
        }
        scanner.close();
    }

    static void addTransaction(boolean deposit) {
        String action;
        if (deposit) {
            action = "deposit";
        } else {
            action = "payment";
        }

        String answer = UI.enterInput("Please enter the details for the " + action + "\n" +
                "Date and Time will default to current if not set.\n" +
                "Would you like to set a Date and Time?");

        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        if (answer.equalsIgnoreCase("yes")) {
            do {
                date = UI.validateDate("Please enter a date in format (YYYY-MM-DD): ");
            } while (date == null);
            time = UI.validateTime("Please enter a time in format (HH:MM:SS): ");
        }

        String description = UI.enterInput("Description: ");

        String vendor = UI.enterInput("Vendor name: ");

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
        FileManager.saveTransaction(t);
    }

    static ArrayList<Transaction> getLedger() {
        return ledger;
    }

    static void testLedger() {
        for (Transaction t : ledger) {
            System.out.println("Date: " + t.getDate() + " Time: " + t.getTime() + " Description: " + t.getDescription() + " Vendor: " + t.getVendor() + " Amount: " + t.getAmount());
        }
    }
}
