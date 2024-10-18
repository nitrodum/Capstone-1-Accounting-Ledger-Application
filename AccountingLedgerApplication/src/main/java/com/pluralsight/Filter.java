package com.pluralsight;

import java.time.LocalDate;
import java.util.ArrayList;

public class Filter {

    static ArrayList<Transaction> filterTransactions(boolean deposit) {
        ArrayList<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction t : AccountingLedger.getLedger()) {
            if ((t.getAmount() > 0 && deposit) || (t.getAmount() < 0 && !deposit)) {
                filteredTransactions.add(t);
            }
        }
        return filteredTransactions;
    }

    // Report Methods
    static ArrayList<Transaction> filterSinceDate(LocalDate limit, ArrayList<Transaction> toFilter) {
        ArrayList<Transaction> filteredSinceDate = new ArrayList<>();
        for (Transaction t : toFilter) {
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
        for (Transaction t : AccountingLedger.getLedger()) {
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
            if (t.getDescription().equalsIgnoreCase(description)) {
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
        ArrayList<Transaction> custom = new ArrayList<>(AccountingLedger.getLedger());

        LocalDate startDate = UI.validateDate("Enter the start date:");
        if (startDate != null) {
            custom = filterSinceDate(startDate, custom);
        }

        LocalDate endDate = UI.validateDate("Enter the end date:");
        if (endDate != null) {
            custom = filterToDate(endDate, custom);
        }

        String description = UI.enterInput("Enter the description:");
        if (!description.isEmpty()) {
            custom = filterDescription(description, custom);
        }

        String vendor = UI.enterInput("Enter the vendor name:");
        if (!vendor.isEmpty()) {
            custom = filterVendor(vendor, custom);
        }

        boolean valid = false;
        String amount;

        while (!valid) {
            amount = UI.enterInput("Enter the amount:");
            if (amount.isEmpty()) {
                valid = true;
            } else {
                try {
                    float parsedAmount = Float.parseFloat(amount);
                    custom = filterAmount(parsedAmount, custom);
                    valid = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid amount. Please enter a valid number.");
                }
            }
        }

        return custom;
    }
}
