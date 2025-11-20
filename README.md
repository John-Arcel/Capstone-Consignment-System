# Capstone-Consignment-System

Capstone: A consignment system for small businesses

Description
This project aims to build a system for sellers and suppliers to aid them in managing consignment transactions. It addresses the gaps concerning operational inefficiencies (i.e. manual auditing, expiration and revenue tracking, etc.). It also targets small businesses who do not have the resources to market their products to bigger consignment services.

The Classes (STC)

Entity
Entity is an abstract class and all entities have the following private fields: a name, an ID, and a contact number. The name and ID must be declared as final and cannot be changed after construction. The ID will be a non-repeating, randomly generated sequence of numbers. There will also be a getter for all the fields and only one setter for the contact number. 

Consignor
The consignor is the supplier of the items sold by the consignee. This class has the following private fields: payable balance, status of activity, a list of transaction history, and a location. There will be getters for all its fields. It also has a method that changes the location and the active state of a consignor.
 
Consignee
The consignee serves as the administrator of the system, handling the details. The consignee has these private fields: username, password, a list of items and a list of consignors. The username is a final field, as well as the password. They also have an add and remove method for their list of consignors and items. 
 
Item 
The item has the following private fields: item ID, item name, selling price, commission rate, date received, owner. It has a getter for the owner and the item name, a method for setting the selling price, a method that calculates the owner’s share when an item is bought, and a method that checks the expiration date of an item. 

Payout
This class has the following private fields: payout ID, a consignor, amount paid, payout date, and a list of covered items. The payout ID is a non-repeating string of numbers. It has getters for the payout Id, the consignor and the amount paid.

Transaction 
The transaction class has the following fields: transaction ID, the item sold, the date the item was sold, the total amount, the store revenue, and the consignor’s share of the transaction. This class has getters for the transaction ID, store revenue, and a getter for consignor share. It also has a method that generates a receipt for the transaction.
