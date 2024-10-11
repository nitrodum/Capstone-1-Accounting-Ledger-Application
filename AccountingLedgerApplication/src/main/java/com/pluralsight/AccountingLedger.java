package com.pluralsight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        scanner.close();
    }

    static void homeScreen() {
        System.out.println(LocalDate.now());
        System.out.println(LocalTime.now());
        testLedger();
        System.out.println(
                "*******************************************************************************\n" +
                "Welcome to your account!\n" +
                "Please enter the letter corresponding to the command you would like to perform.\n" +
                "D) Add Deposit\n" +
                "P) Make Payment\n" +
                "L) View Ledger\n" +
                "X) Exit\n" +
                "*******************************************************************************"
        );

        String input = scanner.nextLine().toUpperCase();

        switch (input) {
            case "D" :
                addTransaction(true);
                break;
            case "P":
                addTransaction(false);
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

    static void addTransaction(boolean deposit) {
        String action;
        if (deposit) {
            action = "deposit";
        } else {
            action = "payment";
        }

        System.out.println("Please enter the details for the " + action + "\n" +
                "Description: \n");
        String description = scanner.nextLine();

        System.out.println("Vendor name: ");
        String vendor = scanner.nextLine();

        System.out.println("Amount: ");
        float amount = scanner.nextFloat();
        scanner.nextLine();

        if (!deposit) {
            amount *= -1;
        }

        Transaction t = new Transaction(LocalDate.now(), LocalTime.now(), description, vendor, amount);
        ledger.add(t);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

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

    static void testLedger() {
        for (Transaction t : ledger) {
            System.out.println("Date: " + t.getDate() + " Time: " + t.getTime() + " Description: " + t.getDescription() + " Vendor: " + t.getVendor() + " Amount: " + t.getAmount());
        }
    }
}
