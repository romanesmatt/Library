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
        String result = "";
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
                String Author = "\n Author: " + rs.getString("name");
                if (result.equals("")) {
                    result += "\n \n isbn: " + isbn + title + edition + noOfCopies + numLeft + Author;
                } else {
                    result += Author;
                }
            }
            if (!bookExists) {
                result = "There are no records of this book in the database";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
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
        String result = "";
        try {
            s = connection.createStatement();
            rs = s.executeQuery("SELECT * FROM book ORDER BY isbn ASC");
            while (rs.next()) {
                String isbn = "ISBN: " + rs.getString("isbn");
                String title = "\n Title: " + rs.getString("Title");
                String edition = "\n Edition No.: " + rs.getString("edition_no");
                String noOfCopies = "\n Number of Copies: " + rs.getString("numofcop");
                String numLeft = "\n Number left: " + rs.getString("numleft");
                result += "\n \n" + isbn + title + edition + noOfCopies + numLeft;
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
        String result = "";
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
                result += "\n \n " + isbn + title + edition + noOfCopies + numLeft;
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
        String result = "";
        boolean authorExists = false;
        try {
            s = connection.createStatement();
            rs = s.executeQuery("SELECT * FROM Author WHERE authorid=" + authorID);
            while (rs.next()) {
                authorExists = true;
                String authorFirstName = "\n Author's first name: " + rs.getString("name");
                String authorSurname = "\n Author's surname: " + rs.getString("surname");
                result += "\n \n Author ID: " + authorID + authorFirstName + authorSurname;
            }
            if (!authorExists) {
                result = "There are no records of this author in the database";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Searches and returns all authors.
     *
     * @return
     */
    public String showAllAuthors() {
        Statement s = null;
        ResultSet rs = null;
        String result = "";
        try {
            s = connection.createStatement();
            rs = s.executeQuery("SELECT * FROM Author");
            while (rs.next()) {
                String authorFirstName = "\n Author first name: " + rs.getString("name");
                String authorSurname = "\n Author surname: " + rs.getString("surname");
                String authorId = "\n Author ID: " + rs.getString("authorid");
                result += authorId + authorFirstName + authorSurname;
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
        String result = "";
        boolean customerExists = false;
        try {
            s = connection.createStatement();
            rs = s.executeQuery("SELECT * FROM Customer WHERE customerid=" + customerID);
            while (rs.next()) {
                customerExists = true;
                String customerName = "\n Customer Name: " + rs.getString("F_Name") + rs.getString("L_Name");
                String city = "\n City: " + rs.getString("city");
                result += "\n \n Customer ID: " + customerID + customerName + city;
            }
            if (!customerExists) {
                result = "There are no records of this customer in the database;";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Searches and returns all customers.
     *
     * @return
     */
    public String showAllCustomers() {
        Statement s = null;
        ResultSet rs = null;
        String result = "";
        try {
            s = connection.createStatement();
            rs = s.executeQuery("SELECT * FROM Customer");
            while (rs.next()) {
                String customerName = "\n Customer Name: " + rs.getString("F_Name") + rs.getString("L_Name");
                String city = "\n City: " + rs.getString("city");
                String customerId = "\n Customer ID: " + rs.getString("customerId");
                result += "\n \n" + customerId + customerName + city;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
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
                ResultSet rsbook = s.executeQuery("SELECT * FROM book WHERE isbn=" + isbn);
                if (rsbook.next()) {
                    String bookleft = rsbook.getString("numLeft");
                    if (Integer.parseInt(bookleft) > 0) {
                        s.execute("LOCK book IN ROW SHARE MODE;");

                        JFrame f = new JFrame();
                        int a = JOptionPane.showConfirmDialog(f, "Are you sure that you want to borrow this book?");
                        if (a == JOptionPane.YES_OPTION) {
                            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            LocalDate date = LocalDate.of(year, month, day);
                            s.executeUpdate("INSERT INTO cust_book VALUES('" + isbn + "','" + date + "','" + customerID
                                    + "');");
                            s.executeUpdate("UPDATE book SET numleft = numleft-1 WHERE isbn =" + isbn + " ;");
                            s.execute("commit;");
                            result = "book borrowed ";
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
            // check if customer exist
            ResultSet rsCustomer = s.executeQuery("SELECT * FROM Customer WHERE customerid=" + customerid);
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

    public String deleteCus(int customerID) {
        Statement s = null;
        String result = "";
        try {
            s = connection.createStatement();
            // check if customer exist
            ResultSet rsCustomer = s.executeQuery("SELECT * FROM Customer WHERE customerid=" + customerID);
            if (rsCustomer.next()) {
                ResultSet rsCustCustomer = s.executeQuery("SELECT * FROM cust_book WHERE customerid=" + customerID);
                if (rsCustCustomer.next()) {
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

    public String deleteAuthor(int authorID) {
        Statement s = null;
        String result = "";
        try {
            s = connection.createStatement();
            // check if customer exist
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

    public String deleteBook(int isbn) {
        Statement s = null;
        String result = "";
        try {
            s = connection.createStatement();
            // check if customer exist
            ResultSet rsBook = s.executeQuery("SELECT * FROM book WHERE isbn=" + isbn);
            if (rsBook.next()) {
                ResultSet rsCustBook = s.executeQuery("SELECT * FROM cust_book WHERE isbn=" + isbn);
                if (rsCustBook.next()) {
                    s = connection.createStatement();
                    s.execute("BEGIN");
                    result = "The book with ISBN(" + isbn
                            + ") still has loaned copies. \n Therefore, it cannot be deleted.";
                } else {
                    ResultSet rsauBook = s.executeQuery("SELECT * FROM Book_Author WHERE isbn=" + isbn);
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