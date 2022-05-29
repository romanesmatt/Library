/*
 * LibraryModel.java
 * Author:
 * Created on:
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;


import javax.swing.*;

/**
 * author matt.romanes
 */
public class LibraryModel {

    // For use in creating dialogs and making them modal
    private JFrame dialogParent;
    private Connection connection = null;

    public LibraryModel(JFrame parent, String userId, String password) throws SQLException {
        this.dialogParent = parent;
        try {
            Class.forName("org.postgresql.Driver");

            String url = "jdbc:postgresql://db.ecs.vuw.ac.nz" + userId + "_jdbc"; //URL for running project on uni lab machines
            connection = DriverManager.getConnection(url, userId, password);

            System.out.println("Connection established.");

        } catch (SQLException e) {
            System.out.println("Connection cannot be established.");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found.");
            e.printStackTrace();
        }
    }

    /**
     * Prints out the information of a given book by searching its ISBN.
     * Goes through the library of books using a query, and if it finds a match,
     * it will be addd onto the output and then printed.
     *
     * @param isbn
     * @return
     */
    public String bookLookup(int isbn) {
        String edition = "";
        String nCopies = "";
        String copiesLeft = "";
        String author = "";
        String title = "";


        try {
            String query = "SELECT * FROM Book NATURAL JOIN Book_Author NATURAL JOIN AUTHOR " +
                    "WHERE (isbn = " + isbn + ")" +
                    "ORDER BY AuthorSeqNo ASC;";

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);

            //Searching for the result then adding it to the output.
            while (result.next()) {
                edition = result.getString("edition_no");
                nCopies = result.getString("numofcop");
                copiesLeft = result.getString("numleft");
                author = result.getString("surname") + ",";
                title = result.getString("Title");
            }
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Book Lookup: \n 	" + isbn + ": " + title + "\n 	Edition: " + edition +
                " - Number of copies: " + nCopies + " - Copies Left: " + copiesLeft + "\n 	Authors: " + author.replaceAll("\\s+", " ");
    }

    /**
     * Returns all books looked up in the catalogue.
     * Uses bookLookup to search books and output them.
     *
     * @return
     */
    public String showCatalogue() {
        //        Declaring variables for accessibility throughout method
        String showCat = "Show catalogue: ";
        StringBuilder output = new StringBuilder();

        try {
            String query = "SELECT isbn FROM Book ORDER BY isbn ASC;";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);

            while (result.next()) {
                int isbn = result.getInt("isbn");

                //search the book with its isbn using method bookLookup.
                output.append("\n \n ").append(bookLookup(isbn));
            }

        } catch (SQLException e) {
            return "ERROR accessing catalogue.";
        }

        return showCat + output.toString();
    }

    /**
     * Shows all loaned books, if there are any.
     *
     * @return
     */
    public String showLoanedBooks() {
        String output = "Show Loaned Books: \n 	";

        try {
            int isbn = 0;
            //if no books are loaned, the boolean will return false
            boolean isLoaned = false;

            String query = "SELECT * FROM Book WHERE (numofcop > numLeft) ORDER BY isbn ASC;";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);

            //the book has been succesfully loaned (if any)
            while (result.next()) {
                isLoaned = true;
                isbn = result.getInt("isbn");
                output += bookLookup(isbn) + "\n \n ";
            }
            statement.close();

            if (!isLoaned) return output + "(No Loaned Books)";

        } catch (SQLException e) {
            return "ERROR accessing books.";
        }

        return output;
    }

    /**
     * Searches for a given author and outputs it.
     *
     * @param authorID
     * @return
     */
    public String showAuthor(int authorID) {
        String title = "";
        String output = "";
        String book = "";
        int ID = 0;

        try {
            String query = "SELECT * FROM Book NATURAL JOIN Book_Author NATURAL JOIN AUTHOR " +
                    "WHERE (AuthorId = " + authorID + ")" +
                    "ORDER BY AuthorSeqNo ASC;";

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);

            while (result.next()) {
                //iterates +1 book
                ID++;
                output = "	" + authorID + " - " + result.getString("name").replaceAll("\\s+", "") + " " + result.getString("surname").replaceAll("\\s+", "") + "\n";
                book += "\n	" + result.getInt("isbn") + " - " + result.getString("title");
            }

            //Book does not exist/have been written.
            if (ID == 0) {
                title = "(no books written)";
            } else {
                title = "	Book written:";
            }

            statement.close();

        } catch (SQLException e) {
            return "No such Author ID: ";
        }

        return "Show Author: \n" + output + title + book + " \n";
    }

    /**
     * Searches and returns all authors.
     * @return
     */
    public String showAllAuthors() {
        String authors = "Showing Authors: \n";

        try {
            String select = "SELECT * FROM author;";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(select);

            while(result.next()){
                authors += " 	" + result.getInt("AuthorId") + " - " + result.getString("name").replaceAll("\\s+","") + ", " + result.getString("surname") + "\n";
            }

        } catch (SQLException e) {
            System.out.println("ERROR Showing Authors");
        }

        return authors;
    }

    /**
     * Searches and returns a given customer.
     * @param customerID
     * @return
     */
    public String showCustomer(int customerID) {
        String cust = "Show Customer: \n" ;
        String book = "";
        String borrowed = "";

        try{
            int customer = 0;
            Statement statement = connection.createStatement();
            String select = "SELECT * FROM  Customer WHERE (customerId = " + customerID + ");";
            ResultSet result = statement.executeQuery(select);

            while(result.next()){ customer++; }
            if(customer == 0){ return "No such Customer ID"; }

            try {
                select = "SELECT * FROM Customer WHERE (customerId = " + customerID + ");";
                result = statement.executeQuery(select);

                while(result.next()){
                    cust += " 	" + result.getInt("customerid") + ": "+ result.getString("l_name").replaceAll("\\s+","") + ", " + result.getString("f_name").replaceAll("\\s+ ","")
                            + " - " + result.getString("city") + "\n";
                }

                select = "SELECT * FROM Cust_book NATURAL JOIN book WHERE (customerId = " + customerID +");";
                result = statement.executeQuery(select);

                while(result.next()){
                    book += " 	\n " + result.getInt("isbn") + " - " + result.getString("title");
                    customer++;
                }

                if(customer == 0){ borrowed = "\n (No books borrowed)"; }
                else{ borrowed = " 	Books Borrowed: " + customer; }

                statement.close();

            } catch (SQLException e) {
                System.out.println("ERROR cannot find books");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("ERROR cannot find books");
            e.printStackTrace();
        }

        return cust + borrowed + book + " \n";
    }

    /**
     * Searches and returns all customers.
     * @return
     */
    public String showAllCustomers() {
        String allCustomers = "Show all customers: \n";

        try {
            String select = "SELECT * FROM customer;";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(select);

            while(result.next()){
                allCustomers += "  	 " + result.getInt("customerid") + ": "+ result.getString("l_name").replaceAll("\\s+","") + ", " + result.getString("f_name").replaceAll("\\s+ ","")
                        + " - " + result.getString("city") + " \n";
            }

        } catch (SQLException e) {
            System.out.println("ERROR getting all customers");
            e.printStackTrace();
        }

        return allCustomers;
    }

    /**
     * Allows the customer to borrow a book
     * @param isbn
     * @param customerID
     * @param day
     * @param month
     * @param year
     * @return
     */
    public String borrowBook(int isbn, int customerID,
                             int day, int month, int year) {
        String borrow = "Borrow Book: \n";
        String message = " 	Customer unable to borrow book.";
        String finalResult = "";

        try {
            Statement statement1 = connection.createStatement();
            Statement statement2 = connection.createStatement();
            Statement statement3 = connection.createStatement();

            String select1 = "SELECT numLeft FROM book WHERE (isbn = " + isbn + ") AND (numLeft > 0);";
            ResultSet result1  = statement1.executeQuery(select1);

            if(result1.next()) {
                String select2 = "SELECT customerid FROM cust_book WHERE (isbn = " + isbn + ") AND (customerid = " + customerID + ");";
                ResultSet result2  = statement2.executeQuery(select2);

                //checks if the result from the query of the isbn entered
                if(result2.next())
                    message = " 	Customer already borrowed this book: " + isbn;
                else {
                    message = " 	Borrow book successful.";
                    updateBook(isbn, customerID, day, month, year);
                }
            }
            else message = " 	There are no copies left of this book: " + isbn;

            String select3 = "SELECT customerid FROM cust_book WHERE (isbn = " + isbn + ") AND (customerid = " + customerID + ");";
            ResultSet result3  = statement3.executeQuery(select3);
            result3 = statement3.executeQuery("SELECT * FROM cust_book WHERE (customerid = " + customerID + ");");

            while (result3.next()) {
                finalResult += "\n " + result3.getInt("isbn") + " - " + result3.getString("title") + "\nLoaned to: " + result3.getInt("customerid") + ": " +
                        result3.getString("l_name").replaceAll("\\s+","") + ", " + result3.getString("f_name").replaceAll("\\s+ ","") + "\nDue Date: " + result3.getDate(2);
            }
        }
        catch (SQLException e){
            System.out.println("ERROR borrowing book");
            e.printStackTrace();
        }

        return borrow + message + "\n 	Book: " + isbn + finalResult + "\n 	(borrow book information not shown for some reason)";
    }

    /**
     * Helper method for the updateBook method.
     * Updates the values from the queries and returns it.
     * @param isbn, customerID, day, month, year
     */
    public void updateBook(int isbn, int customerID, int day, int month, int year) {
        try {
            LocalDate date = LocalDate.of(year, month, day);

            Statement statement = connection.createStatement();
            Statement statementTwo = connection.createStatement();
            String select1 = "INSERT INTO cust_book VALUES('" + isbn + "','" + date + "','" + customerID + "');";
            String select2 = "UPDATE book SET numleft = numleft-1 WHERE (isbn = " + isbn + ");";

            int resultOne = statement.executeUpdate(select1);
            int resultTwo = statementTwo.executeUpdate(select2);

        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Gives the customer the opportunity to return a given book.
     * @param isbn
     * @param customerid
     * @return
     */
    public String returnBook(int isbn, int customerid) {
        String returnBook = "Return Book: \n";
        String output = "";
        ResultSet result = null;

        try {
            Statement statement = connection.createStatement();
            String select1 = "SELECT * FROM customer WHERE (customerid = " + customerid + ");";
            result = statement.executeQuery(select1);

            if(result.next()) {
                if(result.next()) output = " 	isbn does not exist.";

                else {
                    String select2 = "SELECT * FROM book WHERE (isbn = " + isbn + ");";
                    result = statement.executeQuery(select2);

                    String select3 = "DELETE FROM cust_book WHERE (customerid = " + customerid + ");";
                    statement.executeUpdate(select3);

                    String select4 = "UPDATE book SET numleft = numleft+1 WHERE (isbn = " + isbn + ");";
                    statement.executeUpdate(select4);
                }
            }
            else output = " 	No more remaining copies of the book.";

            String select5 = "SELECT * FROM cust_book WHERE (customerid = " + customerid + ");";
            result = statement.executeQuery(select5);

            connection.commit();
        }
        catch (SQLException e){
            System.out.println("ERROR occured");
            e.printStackTrace();
        }

        return returnBook + output + " \n" + " 	Book has been returned." ;
    }

    /**
     * Disconnects the connection between program and database.
     */
    public void closeDBConnection() {
        try {
            connection.close();
        }
        catch (SQLException e) {
            System.out.println("Connection cannot be closed.");
        }
    }

    public String deleteCus(int customerID) {
        String deleteCustomer = "Delete Customer: \n \n";
        String message = "";

        try {
            Statement statement = connection.createStatement();
            String select1 = "SELECT * FROM cust_book WHERE (customerid = " + customerID + ");";
            ResultSet result = statement.executeQuery(select1);

            if(result.next()) {
                message  = " 	The customer does not exist within this library.";
            }

            else {
                Statement statemnt2 = connection.createStatement();
                String select2 = "DELETE  FROM customer WHERE (customerid = " + customerID + ");";
                int result2 = statemnt2.executeUpdate(select2);

                message = " 	The Customer has been removed from the database.";
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return deleteCustomer + message;
    }

    public String deleteAuthor(int authorID) {
        return "Delete Author";
    }

    public String deleteBook(int isbn) {
        return "Delete Book";
    }
}