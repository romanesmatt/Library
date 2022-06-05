/*
 * LibraryModel.java
 * Author: Matt Romanes (300492211)
 * Created on: (Began) on 28/5/2022
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
            String urlUni = "jdbc:postgresql://db.ecs.vuw.ac.nz/" + userId + "_jdbc";

//            URL for running the project locally, as well as username and password
            String urlLocal = "jdbc:postgresql://localhost:5432/matt.romanes";
            connection = DriverManager.getConnection(urlUni, userId, password);
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
        Statement s = null;
        ResultSet rs = null;
        StringBuilder result = new StringBuilder();
        boolean bookExists = false;
        try {
            s = connection.createStatement();
            rs = s.executeQuery("SELECT * FROM Book NATURAL JOIN Book_Author NATURAL JOIN AUTHOR  WHERE ISBN=" + isbn
                    + "ORDER BY AuthorSeqNo ASC");
            while (rs.next()) {
                bookExists = true;
                String title = "\n Title: " + rs.getString("Title");
                String edition = "\n Edition No.: " + rs.getString("edition_no");
                String noOfCopies = "\n Number of Copies: " + rs.getString("numofcop");
                String numLeft = "\n Number left: " + rs.getString("numleft");
                String author = "\n Author: " + rs.getString("name");
                if (result.toString().equals("")) {
                    result.append("\n \n ISBN: ").append(isbn).append(title).append(edition).append(noOfCopies).append(numLeft).append(author);
                } else {
                    result.append(author);
                }
            }
            if (!bookExists) {
                result = new StringBuilder("There are no records of this book in the database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * Returns all books looked up in the catalogue.
     * Uses bookLookup to search books and output them.
     *
     * @return
     */
    public String showCatalogue() {
        Statement s = null;
        ResultSet rs = null;
        StringBuilder result = new StringBuilder();
        try {
            s = connection.createStatement();
            rs = s.executeQuery("SELECT * FROM book ORDER BY isbn ASC");
            while (rs.next()) {
                String isbn = "ISBN: " + rs.getString("isbn");
                String title = "\n Title: " + rs.getString("Title");
                String edition = "\n Edition No.: " + rs.getString("edition_no");
                String noOfCopies = "\n Number of Copies: " + rs.getString("numofcop");
                String numLeft = "\n Number left: " + rs.getString("numleft");
                result.append("\n \n").append(isbn).append(title).append(edition).append(noOfCopies).append(numLeft);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Show Catalogue: \n" + result;

    }

    /**
     * Shows all loaned books, if there are any.
     *
     * @return
     */
    public String showLoanedBooks() {
        Statement s = null;
        ResultSet rs = null;
        StringBuilder result = new StringBuilder();
        String loanedBooks = "";
        boolean loaned = false;
        try {
            s = connection.createStatement();
            rs = s.executeQuery("SELECT * FROM book WHERE numofcop > numLeft ORDER BY isbn ASC");
            while (rs.next()) {
                loaned = true;
                loanedBooks = "Loaned books : \n";
                String isbn = "ISBN: " + rs.getString("isbn");
                String title = "\n Title: " + rs.getString("Title");
                String edition = "\n Edition No.: " + rs.getString("edition_no");
                String noOfCopies = "\n Number of Copies: " + rs.getString("numofcop");
                String numLeft = "\n Number left: " + rs.getString("numleft");
                result.append("\n \n ").append(isbn).append(title).append(edition).append(noOfCopies).append(numLeft);
            }
            if (!loaned) {
                loanedBooks = "There are currently no loaned books.";

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loanedBooks + result;
    }

    /**
     * Searches for a given author and outputs it.
     *
     * @param authorID
     * @return
     */
    public String showAuthor(int authorID) {
        Statement s = null;
        ResultSet rs = null;
        StringBuilder result = new StringBuilder();
        boolean authorExists = false;
        try {
            s = connection.createStatement();
            rs = s.executeQuery("SELECT * FROM Author WHERE authorid=" + authorID);
            while (rs.next()) {
                authorExists = true;
                String authorFirstName = "\n Author's first name: " + rs.getString("name");
                String authorSurname = "\n Author's surname: " + rs.getString("surname");
                result.append("\n \n Author ID: ").append(authorID).append(authorFirstName).append(authorSurname);
            }
            if (!authorExists) {
                result = new StringBuilder("There are no records of this author in the database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * Searches and returns all authors.
     *
     * @return
     */
    public String showAllAuthors() {
        Statement s = null;
        ResultSet rs = null;
        StringBuilder result = new StringBuilder();
        try {
            s = connection.createStatement();
            rs = s.executeQuery("SELECT * FROM Author");
            while (rs.next()) {
                String authorFirstName = "\n Author first name: " + rs.getString("name");
                String authorSurname = "\n Author surname: " + rs.getString("surname");
                String authorId = "\n Author ID: " + rs.getString("authorid");
                result.append(authorId).append(authorFirstName).append(authorSurname).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Showing all authors: \n" + result;
    }

    /**
     * Searches and returns a given customer.
     *
     * @param customerID
     * @return
     */
    public String showCustomer(int customerID) {
        Statement s = null;
        ResultSet rs = null;
        StringBuilder result = new StringBuilder();
        boolean customerExists = false;
        try {
            s = connection.createStatement();
            rs = s.executeQuery("SELECT * FROM Customer WHERE customerid=" + customerID);
            while (rs.next()) {
                customerExists = true;
                String customerName = "\n Customer Name: " + rs.getString("F_Name") + rs.getString("L_Name");
                String city = "\n City: " + rs.getString("city");
                result.append("\n \n Customer ID: ").append(customerID).append(customerName).append(city);
            }
            if (!customerExists) {
                result = new StringBuilder("There are no records of this customer in the database;");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * Searches and returns all customers.
     *
     * @return
     */
    public String showAllCustomers() {
        Statement s = null;
        ResultSet rs = null;
        StringBuilder result = new StringBuilder();
        try {
            s = connection.createStatement();
            rs = s.executeQuery("SELECT * FROM Customer");
            while (rs.next()) {
                String customerName = "\n Customer Name: " + rs.getString("F_Name") + rs.getString("L_Name");
                String city = "\n City: " + rs.getString("city");
                String customerId = "\n Customer ID: " + rs.getString("customerId");
                result.append(customerId).append(customerName).append(city).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Showing all customers: \n" + result.toString();
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
        Statement s = null;
        String result = "";
        try {
            s = connection.createStatement();
            // check if customer exist
            ResultSet rsCustomer = s.executeQuery("SELECT * FROM Customer WHERE customerid=" + customerID);
            s = connection.createStatement();
            // check if customer exist

            if (rsCustomer.next()) {
                s = connection.createStatement();
                s.execute("BEGIN");
                s.execute("LOCK Customer IN ROW SHARE MODE;");
                ResultSet rsBook = s.executeQuery("SELECT * FROM book WHERE isbn=" + isbn);
                if (rsBook.next()) {
                    String bookLeft = rsBook.getString("numLeft");
                    if (Integer.parseInt(bookLeft) > 0) {
                        s.execute("LOCK book IN ROW SHARE MODE;");

                        //Additional dialog to lock in user's choice
                        JFrame f = new JFrame();
                        int a = JOptionPane.showConfirmDialog(f, "Are you sure that you want to borrow this book?");
                        if (a == JOptionPane.YES_OPTION) {
                            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            LocalDate date = LocalDate.of(year, month, day);
                            s.executeUpdate("INSERT INTO cust_book VALUES('" + isbn + "','" + date + "','" + customerID
                                    + "');");
                            s.executeUpdate("UPDATE book SET numleft = numleft-1 WHERE isbn =" + isbn + " ;");
                            s.execute("commit;");

                            result = "Book has been borrowed ";
                        }

                        f.setSize(300, 300);
                        f.setLayout(null);
                    }
                } else {
                    result = "There are no books left.";
                }
            } else {
                result = "There are no records of this customer in the database";
            }
        }

        catch (SQLException e) {
            e.printStackTrace();
        }
        return result;

    }

    /**
     * Gives the customer the opportunity to return a given book.
     *
     * @param isbn
     * @param customerid
     * @return
     */
    public String returnBook(int isbn, int customerid) {
        Statement s = null;
        String result = "";
        try {
            s = connection.createStatement();
            // check if the customer exists
            ResultSet rsCustomer = s.executeQuery("SELECT * FROM Customer WHERE customerid=" + customerid);
            s = connection.createStatement();
            // check if the customer exists

            if (rsCustomer.next()) {
                s = connection.createStatement();
                s.execute("BEGIN");
                s.execute("LOCK Customer IN ROW SHARE MODE;");
                ResultSet rsBook = s.executeQuery("SELECT * FROM book WHERE isbn=" + isbn);
                if (rsBook.next()) {
                    String bookLeft = rsBook.getString("numLeft");
                    if (Integer.parseInt(bookLeft) > 0) {
                        s.execute("LOCK book IN ROW SHARE MODE;");


                        //Additional dialog to lock in user's choice
                        JFrame f = new JFrame();
                        int a = JOptionPane.showConfirmDialog(f, "Are you sure that you want to return this book?");
                        if (a == JOptionPane.YES_OPTION) {
                            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            s.executeUpdate("DELETE FROM cust_book WHERE customerid =" + customerid + ";");
                            s.executeUpdate("UPDATE book SET numleft = numleft+1 WHERE isbn =" + isbn + " ;");
                            s.execute("commit;");
                            result = "book returned. ";
                        }

                        f.setSize(300, 300);
                        f.setLayout(null);
                    }
                } else {
                    result = "There are no books left.";
                }
            } else {
                result = "There are no records of this customer in the database.";
            }
        }

        catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
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

    /**
     * Searches for and then deletes a customer based on
     * the given customer ID.
     * @param customerID
     * @return
     */
    public String deleteCus(int customerID) {
        Statement s = null;
        String result = "";
        try {
            s = connection.createStatement();
            // check if the customer exists
            ResultSet rsCustomer = s.executeQuery("SELECT * FROM Customer WHERE customerid=" + customerID);
            if (rsCustomer.next()) {
                ResultSet rsCustomerTwo = s.executeQuery("SELECT * FROM cust_book WHERE customerid=" + customerID);
                if (rsCustomerTwo.next()) {
                    s = connection.createStatement();
                    s.execute("BEGIN");
                    result = "The customer with customer ID(" + customerID
                            + ") still has loaned books. \n Therefore, their records cannot be deleted";
                } else {
                    s.executeUpdate("DELETE FROM customer WHERE customerid = " + customerID);
                    s.execute("commit;");
                    result = "Customer deleted.";
                }

            } else {
                result = "There are no records of this customer in the database";
            }
        }

        catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Searches for and then deletes an author based
     * on the given author ID.
     * @param authorID
     * @return
     */
    public String deleteAuthor(int authorID) {
        Statement s = null;
        String result = "";
        try {
            s = connection.createStatement();
            // check if the customer exists
            ResultSet rsAuthor = s.executeQuery("SELECT * FROM Author WHERE authorid=" + authorID);
            if (rsAuthor.next()) {
                s.executeUpdate("DELETE FROM Book_Author WHERE authorid = " + authorID);
                s.executeUpdate("DELETE FROM Author WHERE authorid = " + authorID);
                s.execute("commit;");
                result = "Author deleted.";

            } else {
                result = "There are no records of this author in the database.";
            }
        }

        catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Searches for and then deletes a book
     * based on the given ISBN.
     * @param isbn
     * @return
     */
    public String deleteBook(int isbn) {
        Statement s = null;
        String result = "";
        try {
            s = connection.createStatement();
            // check if the customer exists
            ResultSet rsBook = s.executeQuery("SELECT * FROM book WHERE isbn=" + isbn);
            if (rsBook.next()) {
                ResultSet rsCustomerBook = s.executeQuery("SELECT * FROM cust_book WHERE isbn=" + isbn);
                if (rsCustomerBook.next()) {
                    s = connection.createStatement();
                    s.execute("BEGIN");
                    result = "The book with ISBN(" + isbn
                            + ") still has loaned copies. \n Therefore, it cannot be deleted.";
                } else {
                    ResultSet rsAuthorBook = s.executeQuery("SELECT * FROM Book_Author WHERE isbn=" + isbn);
                    s.executeUpdate("DELETE FROM Book_Author WHERE isbn = " + isbn);
                    s.executeUpdate("DELETE FROM book WHERE isbn = " + isbn);

                    s.execute("commit;");
                    result = "Book deleted.";
                }

            } else {
                result = "There are no records of this book in the database";
            }
        }

        catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}