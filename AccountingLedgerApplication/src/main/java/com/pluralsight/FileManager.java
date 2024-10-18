package com.pluralsight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FileManager {
    private static final String readFile = "transactions.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static float loadLedger(ArrayList<Transaction> ledger) {
        float balance = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(readFile));
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
        return balance;
    }

    public static void saveTransaction(Transaction t) {
        try {
            FileWriter writer = new FileWriter(readFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.newLine();
            bufferedWriter.write(t.getDate().toString() + "|" + t.getTime().format(formatter) + "|" + t.getDescription() + "|" + t.getVendor() + "|" + t.getAmount());
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println("Error writing to file!");
            e.printStackTrace();
        }
        System.out.println("Transaction added successfully!");
    }
}