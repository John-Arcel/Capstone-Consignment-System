import java.util.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {

    public void main(String[] args) {
        Scanner scn = new Scanner(System.in);

        List<Consignee> consigneeAccounts = new ArrayList<Consignee>();
        Consignee currentConsignee = null;

        while (true) {
            // register/login page
            boolean isLoggedIn = false;
            if (!isLoggedIn) {
                System.out.println("\n===== REGISTER/LOGIN PAGE =====");
                System.out.println("[1] Login");
                System.out.println("[2] Register");
                System.out.println();
                System.out.println("[0] Exit");
                System.out.print("Enter option: ");
                int c = scn.nextInt();

                String name, contactNumber, username, password;

                switch (c) {
                    case 1:
                        System.out.println("\n===== ACCOUNT PAGE =====");
                        System.out.println("Login");
                        scn.nextLine();
                        System.out.print("Enter username: ");
                        username = scn.nextLine();
                        System.out.print("Enter password: ");
                        password = scn.nextLine();
                        if (consigneeAccounts.isEmpty()) {
                            System.out.println("No account found");
                            textBuffer(scn);
                            break;
                        }

                        for (Consignee user : consigneeAccounts) {
                            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                                System.out.println("Login successful");
                                currentConsignee = user;
                                isLoggedIn = true;
                                textBuffer(scn);
                                break;
                            }
                        }
                        if(!isLoggedIn) {
                            System.out.println("Invalid credentials");
                            textBuffer(scn);
                        }
                        break;
                    case 2:
                        // create a consignee account
                        System.out.println("\n===== ACCOUNT PAGE =====");
                        scn.nextLine();
                        System.out.println("Create an account");
                        System.out.print("Enter username: ");
                        username = scn.nextLine();
                        System.out.print("Enter password: ");
                        password = scn.nextLine();

                        System.out.println("\n===== WELCOME PAGE =====");
                        System.out.print("Enter name: ");
                        name = scn.nextLine();
                        System.out.print("Enter contact number: ");
                        contactNumber = scn.nextLine();

                        Consignee consignee = new Consignee(name, contactNumber, username, password);
                        consigneeAccounts.add(consignee);

                        isLoggedIn = true;
                        currentConsignee = consignee;
                        break;
                    case 0:
                        System.out.println("Exiting program...");
                        return;
                    default:
                        System.out.println("Invalid input");
                        textBuffer(scn);
                        break;
                }
            }

            if(!isLoggedIn) continue;

            // main menu
            boolean isMainMenuRunning = true;
            while (isMainMenuRunning) {
                System.out.println("\n===== MAIN MENU =====");
                System.out.println("Welcome Consignee: " + currentConsignee.getName() + "!");
                System.out.println("\nWhat do you want to do? ");
                System.out.println("Consignors options");
                System.out.println("[1] add consignor");
                System.out.println("[2] remove consignor");
                System.out.println("[3] display consignors");
                System.out.println("Item options");
                System.out.println("[4] add item");
                System.out.println("[5] remove item");
                System.out.println("[6] return an item");
                System.out.println("[7] display items");
                System.out.println();
                System.out.println("[0] logout");
                int c = scn.nextInt();
                switch (c) {
                    case 1: // add consignor
                        currentConsignee.addConsignor(addConsignor(scn, currentConsignee.getConsignorList().size()));
                        textBuffer(scn);
                        break;
                    case 2: // remove consignor
                        currentConsignee.removeConsignor(removeConsignor(scn, currentConsignee.getConsignorList()));
                        textBuffer(scn);
                        break;
                    case 3:
                        // display consignors
                        displayConsignors(currentConsignee.getConsignorList());
                        scn.nextLine();
                        textBuffer(scn);
                        break;
                    case 4: // add item
                        currentConsignee.addItem(addItem(scn, currentConsignee.getItemList().size(), currentConsignee));
                        textBuffer(scn);
                        break;
                    case 5: // remove item
                        currentConsignee.removeItem(removeItem(scn, currentConsignee.getItemList()));
                        textBuffer(scn);
                        break;
                    case 6:
                        // return item
                        currentConsignee.removeItem(returnItem(scn, currentConsignee.getItemList()));
                        textBuffer(scn);
                        break;
                    case 7:
                        // display items
                        displayItems(scn, currentConsignee.getItemList());
                        scn.nextLine();
                        textBuffer(scn);
                        break;
                    case 0: // logout
                        System.out.println("Logging out...");
                        isMainMenuRunning = false;
                        textBuffer(scn);
                        break;
                    default:
                        System.out.println("Invalid input");
                        textBuffer(scn);
                        break;
                }
            }

        }
    }


    private Item returnItem(Scanner scn, List<Item> items) {
        boolean running = true;
        while(running) {
            System.out.println("===== Returning an item from inventory =====");
            System.out.println("Items in inventory: " + items.size());

            String dateToday = getTodayDate();

            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                if (item instanceof NonPerishable)
                    System.out.println((i + 1) + ". " + item.getName() +
                            " | id: " + item.getItemID() +
                            " | price: Php" + String.format("%.2f", item.getSellingPrice()) +
                            " | date received: " + item.getDateReceived() +
                            " | perishable: false" +
                            " | date expiry: N/A" +
                            " | isExpired: N/A");
                else
                    System.out.println((i + 1) + ". " + item.getName() +
                            " | id: " + item.getItemID() +
                            " | price: Php" + String.format("%.2f", item.getSellingPrice()) +
                            " | date received: " + item.getDateReceived() +
                            " | perishable: true" +
                            " | date expiry: " + ((Perishable)item).getExpiryDate() +
                            " | isExpired: " + ((Perishable) item).isExpired(dateToday));
            }
            int c;
            System.out.println("\nOperations");
            System.out.println("[1] Return");
            System.out.println("\nSort by: ");
            System.out.println("[2] Name");
            System.out.println("[3] Expiry");
            System.out.println("[4] Date Received");
            System.out.println("[5] ItemID");
            System.out.println("[6] Selling Price");
            System.out.println();
            System.out.println("[0] Cancel");
            System.out.print("Enter option: "); c = scn.nextInt();
            switch(c) {
                case 1:
                    running = false;
                    break;
                case 2:
                    items.sort(ItemComparators.BY_NAME);
                    break;
                case 3:
                    items.sort(ItemComparators.BY_EXPIRY);
                    break;
                case 4:
                    items.sort(ItemComparators.BY_DATE_RECEIVED);
                    break;
                case 5:
                    items.sort(ItemComparators.BY_ITEM_ID);
                    break;
                case 6:
                    items.sort(ItemComparators.BY_SELLING_PRICE);
                    break;
                case 0:
                    System.out.println("Cancelled return item");
                    return null;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }

        int c;
        System.out.print("Enter item number to return or 0 to cancel: "); c = scn.nextInt();
        if(c == 0) {
            System.out.println("Cancelled return item");
        }
        else if(c > 0 && c <= items.size()) {
            System.out.println("Returning item: " + items.get(c-1).getName() + " (id: " + items.get(c-1).getItemID() + ")");
            return items.get(c-1);
        }
        else {
            System.out.println("Invalid input");
        }
        return null;
    }

    private void displayItems(Scanner scn, List<Item> items) {
        boolean isMainMenuRunning = true;
        while(isMainMenuRunning) {
            System.out.println("===== Displaying list of items in inventory =====");
            System.out.println("Items in inventory: " + items.size());

            String dateToday = getTodayDate();

            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                if (item instanceof NonPerishable)
                    System.out.println((i + 1) + ". " + item.getName() +
                            " | id: " + item.getItemID() +
                            " | price: Php" + String.format("%.2f", item.getSellingPrice()) +
                            " | date received: " + item.getDateReceived() +
                            " | perishable: false" +
                            " | date expiry: N/A" +
                            " | isExpired: N/A");
                else
                    System.out.println((i + 1) + ". " + item.getName() +
                            " | id: " + item.getItemID() +
                            " | price: Php" + String.format("%.2f", item.getSellingPrice()) +
                            " | date received: " + item.getDateReceived() +
                            " | perishable: true" +
                            " | date expiry: " + ((Perishable)item).getExpiryDate() +
                            " | isExpired: " + ((Perishable) item).isExpired(dateToday));
            }
            int c;
            System.out.println("Sort by: ");
            System.out.println("[1] Name");
            System.out.println("[2] Expiry");
            System.out.println("[3] Date Received");
            System.out.println("[4] ItemID");
            System.out.println("[5] Selling Price");
            System.out.println();
            System.out.println("[0] Return to menu");
            System.out.print("Enter option: "); c = scn.nextInt();
            switch(c) {
                case 1:
                    items.sort(ItemComparators.BY_NAME);
                    break;
                case 2:
                    items.sort(ItemComparators.BY_EXPIRY);
                    break;
                case 3:
                    items.sort(ItemComparators.BY_DATE_RECEIVED);
                    break;
                case 4:
                    items.sort(ItemComparators.BY_ITEM_ID);
                    break;
                case 5:
                    items.sort(ItemComparators.BY_SELLING_PRICE);
                    break;
                case 0:
                    System.out.println("Returning to menu...");
                    isMainMenuRunning = false;
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
    }

    private void displayConsignors(List<Consignor> consignors) {
        System.out.println("===== Displaying partnered consignors =====");
        System.out.println("Consignors partnered: " + consignors.size());
        for(int i = 0; i < consignors.size(); i++) {
            Consignor c = consignors.get(i);
            System.out.println((i+1) + ". " + c.getName() + " | #: " + c.getContactNumber() + " | loc: " + c.getLocation());
        }
    }


    private Consignor addConsignor(Scanner scn, int consignorAmount) {
        String name, contactNumber, location;
        System.out.println("===== Add new consignor =====");
        System.out.println("You are currently partnered with " + consignorAmount + " consignors");
        scn.nextLine();
        System.out.print("Enter consignor name: "); name = scn.nextLine();
        System.out.print("Enter " + name + "'s contact number: "); contactNumber = scn.nextLine();
        System.out.print("Enter " + name + "'s location: "); location = scn.nextLine();

        Consignor consignor = new Consignor(name, contactNumber, location);
        System.out.println("New consignor: " + name + " added");
        return consignor;
    }

    private Consignor removeConsignor(Scanner scn, List<Consignor> consignors) {
        System.out.println("===== Remove a consignor =====");
        System.out.println("List of partnered consignors");
        for(int i = 0; i < consignors.size(); i++) {
            Consignor c = consignors.get(i);
            System.out.println("[" + (i+1) + "] " + c.getName());
        }
        System.out.println("\n[0] Cancel");
        System.out.print("Enter consignor number or 0 to cancel: "); int c = scn.nextInt();
        if(c == 0) {
            System.out.println("Cancelled removing a consignor");
            return null;
        }
        else if(c <= consignors.size()) {
            System.out.println("Removing consignor: " + consignors.get(c-1).getName());
            return consignors.get(c-1);
        }
        else {
            System.out.println("Invalid input");
        }
        return null;
    }

    private Item addItem(Scanner scn, int itemAmount, Consignee owner) {
        System.out.println("===== Add a new item =====");
        System.out.println("You currently have " + itemAmount + " items");
        scn.nextLine();

        System.out.print("Is this item perishable? (y/n): "); String c = scn.nextLine();


        String name, dateReceived;
        double sellingPrice;
        String expiryDate;
        System.out.print("Enter name of the item: "); name = scn.nextLine();
        System.out.print("Enter date of item received: "); dateReceived = scn.nextLine();
        System.out.print("Enter selling price of the item: "); sellingPrice = scn.nextDouble();
        scn.nextLine();

        if(c.equalsIgnoreCase("y")) { // item is perishable
            System.out.print("Enter expiry date of the item: "); expiryDate = scn.nextLine();
            return new Perishable(name, sellingPrice, dateReceived, owner, expiryDate);
        }
        else { // item is non-perishable
            return new NonPerishable(name, sellingPrice, dateReceived, owner);
        }
    }

    private Item removeItem(Scanner scn, List<Item> items) {
        System.out.println("===== Remove an item =====");
        for(int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            System.out.println("[" + i + "] " + item.getName() + " | id: " + item.getItemID());
        }
        System.out.println("\n[0] Cancel");
        System.out.print("Enter item number or 0 to cancel: "); int c = scn.nextInt();
        if(c == 0) {
            System.out.println("Cancelled remove an item");
            return null;
        }
        else if(c > items.size()) {
            System.out.println("Removing item: " + items.get(c-1).getName() + " with id: " + items.get(c-1).getItemID());
            return items.get(c-1);
        }
        else {
            System.out.println("Invalid input");
        }
        return null;
    }

// ================================= HELPER FUNCTIONS =======================================================

    public static String getTodayDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");
        return LocalDate.now().format(formatter);
    }

    private void textBuffer(Scanner scn) {
        System.out.println();
        System.out.println("Press Enter to continue...");
        scn.nextLine();
    }


}