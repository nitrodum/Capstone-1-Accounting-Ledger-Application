package com.pluralsight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class AccountingLedger {
    private static ArrayList<Transaction> ledger = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static boolean running = true;

    public static void main(String[] args) {
        loadLedger();
        while (running) {
            homeScreen();
        }
    }

    static void homeScreen() {
        System.out.println("Welcome to your account!\n" +
                "Please enter the letter corresponding to the command you would like to perform.\n" +
                "D) Add Deposit\n" +
                "P) Make Payment\n" +
                "L) View Ledger\n" +
                "X) Exit"
        );

        String input = scanner.nextLine().toUpperCase();

        switch (input) {
            case "D" :
                System.out.println("Deposit not implemented yet!");
                break;
            case "P":
                System.out.println("Payment not implemented yet!");
                break;
            case "L":
                System.out.println("Ledger Screen not implemented yet!");
                break;
            case "X":
                System.out.println("Thank you for using our app!");
                running = false;
                break;
            default:
                System.out.println("Invalid Option");
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

    static void testLedger() {
        for (Transaction t : ledger) {
            System.out.println("Date: " + t.getDate() + " Time: " + t.getTime() + " Description: " + t.getDescription() + " Vendor: " + t.getVendor() + " Amount: " + t.getAmount());
        }
    }
}
