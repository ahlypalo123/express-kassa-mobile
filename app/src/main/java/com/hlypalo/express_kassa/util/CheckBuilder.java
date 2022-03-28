package com.hlypalo.express_kassa.util;

public class CheckBuilder {

    public static String build() {

        String BILL = "";

        BILL = "                XXXX MART  \n" +
               "                 XX.AA.BB.CC.  \n " +
               "                NO 25 ABC ABCDE    \n" +
               "                 XXXXX YYYYYY      \n" +
               "                 MMM 590019091      \n";
        BILL = BILL
                + "--------------------------------\n";


        BILL = BILL + String.format("%1$10s %2$4s %3$4s %4$4s", "Item", "Qty", "Rate", "Total");
        BILL = BILL + "\n";
        BILL = BILL
                + "--------------------------------";

        BILL = BILL + "\n " + String.format("%1$10s %2$4s %3$4s %4$4s", "item-001", "5", "10", "50.00");
        BILL = BILL + "\n " + String.format("%1$10s %2$4s %3$4s %4$4s", "item-002", "10", "5", "50.00");
        BILL = BILL + "\n " + String.format("%1$10s %2$4s %3$4s %4$4s", "item-003", "20", "10", "200.00");
        BILL = BILL + "\n " + String.format("%1$10s %2$4s %3$4s %4$4s", "item-004", "50", "10", "500.00");

        BILL = BILL
                + "\n--------------------------------";
        BILL = BILL + "\n\n ";

        BILL = BILL + "    Total Qty:" + "       " + "85" + "\n";
        BILL = BILL + "     Total Value:" + "    " + "700.00" + "\n";

        BILL = BILL
                + "--------------------------------\n";
        BILL = BILL + "\n\n ";
        return BILL;

    }

}
