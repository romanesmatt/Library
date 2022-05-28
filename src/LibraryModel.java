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

//        Declaring variables for accessibility throughout method
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
            ResultSet resultSet = statement.executeQuery(query);

            //Searching for the result then adding it to the output.
            while (resultSet.next()) {
                edition = resultSet.getString("edition_no");
                nCopies = resultSet.getString("numofcop");
                copiesLeft = resultSet.getString("numleft");
                author = resultSet.getString("surname") + ",";
                title = resultSet.getString("Title");
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
            Statement statemnt = connection.createStatement();
            ResultSet resultSet = statemnt.executeQuery(query);

            while(resultSet.next()){
                int isbn = resultSet.getInt("isbn");

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
     * @return
     */
    public String showLoanedBooks() {
        String output = "Show Loaned Books: \n 	";

        try {
            int isbn = 0;
            //if no books are loaned, the boolean will return false
            boolean isLoaned = false;

            String query = "SELECT * FROM Book WHERE (numofcop > numLeft) ORDER BY isbn ASC;";
            Statement statemnt = connection.createStatement();
            ResultSet result = statemnt.executeQuery(query);

            //the book has been succesfully loaned (if any)
            while(result.next()){
                isLoaned= true;
                isbn = result.getInt("isbn");
                output += bookLookup(isbn) + "\n \n ";
            }
            statemnt.close();

            if(isLoaned == false) return output +  "(No Loaned Books)";

        } catch (SQLException e) {
            return "ERROR accessing books.";
        }

        return output;
    }

    public String showAuthor(int authorID) {
        return "Show Author Stub";
    }

    public String showAllAuthors() {
        return "Show All Authors Stub";
    }

    public String showCustomer(int customerID) {
        return "Show Customer Stub";
    }

    public String showAllCustomers() {
        return "Show All Customers Stub";
    }

    public String borrowBook(int isbn, int customerID,
                             int day, int month, int year) {
        return "Borrow Book Stub";
    }

    public String returnBook(int isbn, int customerid) {
        return "Return Book Stub";
    }

    public void closeDBConnection() {
    }

    public String deleteCus(int customerID) {
        return "Delete Customer";
    }

    public String deleteAuthor(int authorID) {
        return "Delete Author";
    }

    public String deleteBook(int isbn) {
        return "Delete Book";
    }
}