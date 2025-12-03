package classes;

import java.util.Comparator;

public class ItemComparators {

    public static final Comparator<Item> BY_NAME =
            Comparator.comparing(Item::getName);

    public static final Comparator<Item> BY_ITEM_ID =
            Comparator.comparing(Item::getItemID);

    public static final Comparator<Item> BY_DATE_RECEIVED =
            Comparator.comparing(Item::getDateReceived);

    public static final Comparator<Item> BY_SELLING_PRICE =
            Comparator.comparingDouble(Item::getSellingPrice);

    public static final Comparator<Item> BY_EXPIRY = (a, b) -> {
        boolean aPer = a instanceof Perishable;
        boolean bPer = b instanceof Perishable;

        if (aPer && bPer) {
            Perishable pa = (Perishable) a;
            Perishable pb = (Perishable) b;

            return pa.getReturnDate().compareTo(pb.getReturnDate());
        }

        // classes.NonPerishable goes last
        if (aPer && !bPer) return -1;
        if (!aPer && bPer) return 1;

        return 0;
    };
}
