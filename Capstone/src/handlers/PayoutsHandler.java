package handlers;

import classes.Payout;

import java.util.ArrayList;
import java.util.List;

public class PayoutsHandler {
    private String path;
    private List<Payout> payout_list;

    public PayoutsHandler(String entityID){
        path = "data/" + entityID + "/payouts.csv";
        payout_list = new ArrayList<>();

        loadPayouts();
    }

    private void loadPayouts() {

    }

}
