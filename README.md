# CoSign

Capstone: A consignment system for small businesses

# Description
This project aims to build a system for sellers and suppliers to aid them in managing consignment transactions. It addresses the gaps concerning operational inefficiencies (i.e. manual auditing, expiration and revenue tracking, etc.). It also targets small businesses who do not have the resources to market their products to bigger consignment services.

# The Classes 

## Entity
Entity is an abstract class and all entities have the following private fields: a name, and an ID. The name and ID must be declared as final and cannot be changed after construction. The ID will be a non-repeating, randomly generated sequence of numbers. There will also be a getter for both fields. 

## Consignor
The consignor is the supplier of the items sold by the consignee. It extends from the Entity therefore every consignor has a name and a unique non-repeating seven-digit ID. Additionally, this class has the following private fields: payable balance, status of activity, and a list of transaction history. 
There will be getters for all its fields, and a setter for payable balance. It has a method that changes the active state of a consignor and another that updates the payable balance. 
And lastly, a custom toCSV method that sets the information from the class to the format that it will be stored in the file.
 
## Consignee
The consignee serves as the administrator of the system, handling the details and any transactions between them and the consignor. 
The consignee extends from the entity giving it its own name and unique ID. Their name will also serve as their username and has these private fields: a final String password which serves as their way in logging in, 
a list of items that tracks what they have in supply to sell, and a list of consignors that tracks which suppliers they have and which provides which items making transfer of supplier share easily traceable. 
Each field has a getter and the consignee also has an add and remove method for their list of consignors and items, and a toCSV method that sets the information from the class to the format that it will be stored in the file.
 
## Item 
The item has the following private fields: a final String item ID to easily track the item, a final String item name, a final Consignor owner to know which supplier the product was from and preventing confusion, int quantity of how many of the product is present, 
final double selling price, commission rate which is the amount of money the seller earns if the item is sold, date when the item was received, and another date when the item must be returned if it has not sold after a period of time or has expired, lastly a status that checks if the item is still in stock or is expired. 
It has a getter for each field except for the status, a setter for the quantity and status, a method that calculates the owner’s share when an item is bought, and a toCSV method that sets the information from the class to the format that it will be stored in the file. 

## NonPerishable
NonPerishable class is a class for items that are non-perishable. Extends from Item and merely has a constructor and a toCSV method that overrides 
the method in its parent class.

## Perishable
Perishable class is a class for items that are perishable. Extends from Item and has a constructor, a method that checks if item is already expired, and a toCSV method that overrides
the method in its parent class.

## ItemComparator
This class is merely a class that arranges the consignee's list of items by different categories according to what is set or needed. It can be arranged
by name alphabetically, by item ID in increasing order, by date received which was first to arrive, by selling price from cheapest to most expensive,
and by expiry date the first being the soonest to expire while non-perishable items are last on the list and according to their return date.

## Payout
Payouts are the process of transferring of the consignor's share from the consignee's sale.
This class has the following private fields: a final String payout ID made up of the letter P and a unique seven-digit number, a consignor of which the payout was done for, a double amount paid representing the consignor's share or the amount transferred, 
and a date for when the payout was made. It has getters for all its fields and a toCSV method.

## Transaction 
A transaction occurs when the consignee is able to sell an item. 
The transaction class has the following fields: a final String transaction ID made up of the letter T and a unique seven-digit number, 
the item sold, the date the item was sold, the total amount of the sale, the store revenue or overall earning from the sale without deductions from consignor share, 
the consignor’s share of the transaction, and a boolean that checks if there has already been a payout for the transaction. 
This class has getters for all its fields, a method that checks if it is already a payout, and its own toCSV method.

## The Handlers
From the name itself the following classes under this handle significant operations correlating to the actively visible objects in our gui. 
They essentially serves as Data Access Object (DAO) used to separate low-level data accessing operations from the high-level business logic of our system.
It acts as a mediator between the logic behind the system and the data and files.

### SupplierHandler
It is responsible for handling all file input/output (I/O) operations, ensuring that the in-memory List<Consignor> remains synchronized with the persistent data stored in a CSV file. 
The Handler's internal list facilitates the rapid pushing of data needed by the dashboard and other GUI panels. 

### InventoryHandler
It is responsible for managing the persistent storage of inventory, handling the creation of different item types, and providing structured data for display in the application's user interface.
It ensures that any updates of changes done in the inventory gui is applied to CSV file when program is saved ensuring up-to-date information.

### PayoutsHandler
The PayoutsHandler class is responsible for managing the financial records of payments made from the Consignee (store owner) to the Consignors (suppliers). 
Its primary function is to track, persist, and process all finalized payouts based on completed sales transactions.

### Transaction Handler
The TransactionsHandler class serves as the dedicated Data Access Object (DAO) for all sales activities. 
Its core responsibility is to record, manage, and process every sale that occurs in the system, 
ensuring that financial data is accurately tracked and propagated to the relevant Consignor balances. 
And is vital for linking changes in inventory to financial updates.

## Style
The Style class is a utility container designed to hold static inner classes that extend standard Java Swing components. Its purpose is to encapsulate custom look-and-feel (L&F) logic, 
specifically to render standard components with rounded corners, improving the aesthetic design of the application's Graphical User Interface (GUI). 
This class uses the Template Method pattern by overriding the paintComponent(Graphics g) method of the parent Swing classes to insert custom drawing logic before or after the standard component rendering.

## Table Formatter
The TableFormatter class is a utility container designed to hold static inner classes that implement custom cell renderers for Java Swing JTable components. 
Its sole purpose is to enhance the appearance and readability of data displayed in tables by controlling the formatting, alignment, and padding of individual cells based on the data type or context. 
This class uses the Decorator pattern inherent in the DefaultTableCellRenderer class to customize how data is presented without altering the underlying data model.

## Text Field Filter
The TextFieldFilter class is a crucial UI utility container designed to hold static inner classes that extend the Java Swing DocumentFilter abstract class. 
Its primary role is to implement real-time input validation on text components (like JTextField and JPasswordField), ensuring that users can only type characters that conform to a specific data type (integers or doubles). 
By using a DocumentFilter, the class intercepts attempts to modify the text field's document before the modification occurs, allowing it to enforce strict input rules.
