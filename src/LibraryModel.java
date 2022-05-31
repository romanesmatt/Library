/*
 * LibraryModel.java
 * Author: Matt Romanes (300492211)
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
//            URL for running project on uni lab machines
            String urlUni = "jdbc:postgresql://db.ecs.vuw.ac.nz" + userId + "_jdbc";

//            URL for running the project locally, as well as username and password
            String urlLocal = "jdbc:postgresql://localhost:5432/matt.romanes";
            String userLocal = "matt.romanes";
            String passwordLocal = "romanematt";
            connection = DriverManager.getConnection(urlLocal, userLocal, passwordLocal);
            connection.setAutoCommit(false);

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
        String authorSurname = "";
        String authorFirstName = "";
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
                authorFirstName = result.getString("name");
                authorSurname = result.getString("surname");
                title = result.getString("Title");
            }
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Book Lookup: \n     " + isbn + ": " + title + "\n   Edition: " + edition +
                " \n    Number of copies: " + nCopies + " \n    Copies left: " + copiesLeft + "\n 	Authors: " + authorSurname.replaceAll("\\s+", " ") + ", " + authorFirstName.replaceAll("\\s+", " ");
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
        StringBuilder output = new StringBuilder("Show Loaned Books: \n 	");

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
                output.append(bookLookup(isbn)).append("\n \n ");
            }
            statement.close();

            if (!isLoaned) return output + "(No Loaned Books)";

        } catch (SQLException e) {
            return "ERROR accessing books.";
        }

        return output.toString();
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
        StringBuilder book = new StringBuilder();
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
                output = "	" + authorID + " - " + result.getString("name").
                        replaceAll("\\s+", "") + " " + result.getString("surname").
                        replaceAll("\\s+", "") + "\n";

                book.append("\n	").
                        append(result.getInt("isbn")).
                        append(" - ").
                        append(result.getString("title"));
            }

            //Book does not exist/have been written.
            if (ID == 0) {
                title = "(no books written)";
            } else {
                title = "Book written:";
            }

            statement.close();

        } catch (SQLException e) {
            return "No such Author ID: ";
        }

        return "Show Author: \n" + output + title + book + " \n";
    }

    /**
     * Searches and returns all authors.
     *
     * @return
     */
    public String showAllAuthors() {
        StringBuilder allAuthors = new StringBuilder("Showing Authors: \n");

        try {
            String select = "SELECT * FROM author;";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(select);

            while (result.next()) {
                allAuthors.append(" 	").
                        append(result.getInt("authorid")).
                        append(" - ").
                        append(result.getString("name").
                                replaceAll("\\s+", "")).
                        append(", ").
                        append(result.getString("surname")).
                        append("\n");
            }

        } catch (SQLException e) {
            System.out.println("ERROR Showing Authors");
        }

        return allAuthors.toString();
    }

    /**
     * Searches and returns a given customer.
     *
     * @param customerID
     * @return
     */
    public String showCustomer(int customerID) {
        StringBuilder cust = new StringBuilder("Show Customer: \n");
        StringBuilder book = new StringBuilder();
        String borrowed = "";

        try {
            int customer = 0;
            Statement statement = connection.createStatement();
            String select = "SELECT * FROM  Customer WHERE (customerId = " + customerID + ");";
            ResultSet result = statement.executeQuery(select);

            while (result.next()) {
                customer++;
            }
            if (customer == 0) {
                return "No customer with that ID in the database.";
            }

            try {
                select = "SELECT * FROM Customer WHERE (customerId = " + customerID + ");";
                result = statement.executeQuery(select);

                while (result.next()) {
                    cust.append(" 	").append(result.getInt("customerid")).append(": ").append(result.getString("l_name").replaceAll("\\s+", "")).append(", ").append(result.getString("f_name").replaceAll("\\s+ ", "")).append(" - ").append(result.getString("city")).append("\n");
                }

                select = "SELECT * FROM Cust_book NATURAL JOIN book WHERE (customerId = " + customerID + ");";
                result = statement.executeQuery(select);

                while (result.next()) {
                    book.append(" 	\n ").
                            append(result.getInt("isbn")).
                            append(" - ").
                            append(result.getString("title"));
                    customer++;
                }

                if (customer == 0) {
                    borrowed = "\n (No books borrowed)";
                } else {
                    borrowed = " 	Books Borrowed: " + customer;
                }

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
     *
     * @return
     */
    public String showAllCustomers() {
        StringBuilder allCustomers = new StringBuilder("Show all customers: \n");

        try {
            String select = "SELECT * FROM customer;";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(select);

            while (result.next()) {
                allCustomers.append(result.getInt("customerid")).
                        append(": ").
                        append(result.getString("l_name").
                                replaceAll("\\s+", "")).
                        append(", ").
                        append(result.getString("f_name").
                                replaceAll("\\s+ ", ""))
                        .append(" - ").
                        append(result.getString("city")).
                        append(" \n");
            }

        } catch (SQLException e) {
            System.out.println("ERROR getting all customers");
            e.printStackTrace();
        }

        return allCustomers.toString();
    }

    /**
     * Allows the customer to borrow a book
     *
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
        String message = "Customer is unable to borrow book.";
        StringBuilder finalResult = new StringBuilder();

        try {
            Statement statementOne = connection.createStatement();
            Statement statementTwo = connection.createStatement();
            Statement statementThree = connection.createStatement();

            String selectOne = "SELECT numLeft FROM book WHERE (isbn = " + isbn + ") AND (numLeft > 0);";
            ResultSet resultOne = statementOne.executeQuery(selectOne);

            if (resultOne.next()) {
                String selectTwo = "SELECT customerid FROM cust_book WHERE (isbn = " + isbn + ") AND (customerid = " + customerID + ");";
                ResultSet resultTwo = statementTwo.executeQuery(selectTwo);

                //checks if the result from the query of the isbn entered
                if (resultTwo.next())
                    message = "Customer already borrowed this book: " + isbn;
                else {
                    message = "Borrow book successful.";
                    updateBook(isbn, customerID, day, month, year);
                }
            } else message = "There are no copies left of this book: " + isbn;

            String selectThree = "SELECT customerid FROM cust_book WHERE (isbn = " + isbn + ") AND (customerid = " + customerID + ");";
            ResultSet resultThree = statementThree.executeQuery(selectThree);
            resultThree = statementThree.executeQuery("SELECT * FROM cust_book WHERE (customerid = " + customerID + ");");

            while (resultThree.next()) {
                finalResult.append("\n ").append(resultThree.getInt("isbn")).
                        append(" - ").append(resultThree.getString("title")).
                        append("\nLoaned to: ").append(resultThree.getInt("customerid")).
                        append(": ").
                        append(resultThree.getString("l_name").
                                replaceAll("\\s+", "")).
                        append(", ").
                        append(resultThree.getString("f_name").
                                replaceAll("\\s+ ", "")).
                        append("\nDue Date: ").append(resultThree.
                                getDate(2));
            }
        } catch (SQLException e) {
            System.out.println("ERROR borrowing book");
            e.printStackTrace();
        }

        return borrow + message + "\n 	Book: " + isbn + finalResult;
    }

    /**
     * Helper method for the updateBook method.
     * Updates the values from the queries and returns it.
     *
     * @param isbn, customerID, day, month, year
     */
    public void updateBook(int isbn, int customerID, int day, int month, int year) {
        try {
            LocalDate date = LocalDate.of(year, month, day);

            Statement statementOne = connection.createStatement();
            Statement statementTwo = connection.createStatement();
            String selectOne = "INSERT INTO cust_book VALUES('" + isbn + "','" + date + "','" + customerID + "');";
            String selectTwo = "UPDATE book SET numleft = numleft-1 WHERE (isbn = " + isbn + ");";

            int resultOne = statementOne.executeUpdate(selectOne);
            int resultTwo = statementTwo.executeUpdate(selectTwo);

            System.out.println("Book updated.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gives the customer the opportunity to return a given book.
     *
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
            String selectOne = "SELECT * FROM customer WHERE (customerid = " + customerid + ");";
            result = statement.executeQuery(selectOne);

            if (result.next()) {
                if (result.next()) output = "ISBN does not exist.";

                else {
                    String selectTwo = "SELECT * FROM book WHERE (isbn = " + isbn + ");";
                    result = statement.executeQuery(selectTwo);

                    String selectThree = "DELETE FROM cust_book WHERE (customerid = " + customerid + ");";
                    statement.executeUpdate(selectThree);

                    String selectFour = "UPDATE book SET numleft = numleft+1 WHERE (isbn = " + isbn + ");";
                    statement.executeUpdate(selectFour);
                }
            } else output = "No more remaining copies of the book.";

            String selectFive = "SELECT * FROM cust_book WHERE (customerid = " + customerid + ");";
            result = statement.executeQuery(selectFive);

            connection.commit();
        } catch (SQLException e) {
            System.out.println("ERROR occured");
            e.printStackTrace();
        }

        return returnBook + output + " \n" + " 	Book has been returned.";
    }

    /**
     * Disconnects the connection between program and database.
     */
    public void closeDBConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Connection cannot be closed.");
        }
    }

    public String deleteCus(int customerID) {
        String deleteCustomer = "Delete Customer: \n \n";
        String message = "";

        try {
            Statement statement = connection.createStatement();
            String selectOne = "SELECT * FROM cust_book WHERE (customerid = " + customerID + ");";
            ResultSet result = statement.executeQuery(selectOne);

            if (!result.next()) {
                message = " The Customer does not exist within this library.";
            } else {
                Statement statementTwo = connection.createStatement();
                String selectTwo = "DELETE  FROM customer WHERE (customerid = " + customerID + ");";
                int resultTwo = statementTwo.executeUpdate(selectTwo);

                message = "The Customer has been removed from the database.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deleteCustomer + message;
    }

    public String deleteAuthor(int authorID) {
        String deleteAuthor = "Delete Author: \n \n";
        String message = "";

        try {
            Statement statementOne = connection.createStatement();
            String selectOne = "SELECT * FROM author WHERE (authorid = " + authorID + ");";
            ResultSet resultOne = statementOne.executeQuery(selectOne);

            if (!resultOne.next()) {
                message = "The Author does not exist within this library.";
            } else {
                Statement statementTwo = connection.createStatement();
                String selectTwo = "DELETE FROM author WHERE (authorid = " + authorID + ");";
                int resultTwo = statementTwo.executeUpdate(selectTwo);

                message = "The Author has been removed from the database.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return deleteAuthor + message;
    }

    public String deleteBook(int isbn) {
        return "Delete Book: \n \n";
    }
}