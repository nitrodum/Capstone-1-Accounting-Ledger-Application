package com.pluralsight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class AccountingLedger {
    private static ArrayList<Transaction> ledger = new ArrayList<>();

    public static void main(String[] args) {
        loadLedger();
    }

    static void loadLedger() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("transactions.csv"));
            String input;

            while ((input = bufferedReader.readLine()) != null) {
                String[] data = input.split("\\|");
                Transaction t = new Transaction(LocalDate.parse(data[0]), LocalTime.parse(data[1]), data[2], data[3], Float.parseFloat(data[4]));
                ledger.add(t);
            }
        }
        catch (Exception e) {
            System.out.println("Error reading file!");
            e.printStackTrace();
        }
    }
}
