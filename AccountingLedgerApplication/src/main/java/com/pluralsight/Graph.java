package com.pluralsight;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Graph {
    public static void drawGraph(ArrayList<Transaction> ledger) {
        Map<LocalDate, Float> plots = new TreeMap<>();
        ledger.sort(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getTime));
        float total = 0;
        float max = 0;

        for (Transaction t : ledger) {
            total += t.getAmount();
            max = Math.max(max, Math.abs(total));
            plots.put(t.getDate(), total);
        }

        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        for (LocalDate key : plots.keySet()) {
            float amount = plots.get(key);

            int plot;
            if (max == 0) {
                plot = 0;
            } else {
            plot = (int) ((Math.abs(amount) / max) * 100);
            }
            String line;
            if (amount > 0) {
                line = "*".repeat(plot);
            } else {
                line = "-".repeat(plot);
            }

            System.out.printf("%s | %s (%.2f)\n", key.toString(), line, amount);
        }
    }
}
