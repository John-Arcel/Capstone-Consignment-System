package handlers;

import classes.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionsHandler {
    private String path;
    private List<Transaction> transaction_list;

    public TransactionsHandler(String entityID){
        path = "data/" + entityID + "/transactions.csv";
        transaction_list = new ArrayList<>();

        loadTransactions();
    }

    private void loadTransactions() {

    }
}
