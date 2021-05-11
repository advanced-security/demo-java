package com.github.hackathon.advancedsecurityjava;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.hackathon.advancedsecurityjava.Models.Book;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpringBootApplication
public class Application {

	public static String connectionString = "jdbc:sqlite:database.sqlite";

	public static final Logger logger = LogManager.getLogger();
  

	public static void main(String[] args) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException error) {
		error.printStackTrace();
		System.exit(1);
		}
	
		createDatabase();
	
		List<Book> books = new ArrayList<Book>();
	
		books.add(new Book("The Hobbit", "JRR Tolkien", true));
		books.add(new Book("The Fellowship of the Ring", "JRR Tolkien", true));
		books.add(new Book("The Eye of the World", "Robert Jordan"));
		books.add(new Book("A Game of Thrones", "George R. R. Martin", true));
		books.add(new Book("The Way of Kings", "Brandon Sanderson"));
	
		// Create database entries
		createDatabaseEntries(books);

		SpringApplication.run(Application.class, args);
	}


	public static void createDatabase() {
		try (Connection connection = DriverManager.getConnection(connectionString);
			Statement stmt = connection.createStatement()) {
	
		  // Create tables if they don't exist
		  stmt.execute(
			  "CREATE TABLE IF NOT EXISTS Books (id INTEGER PRIMARY KEY, name TEXT NOT NULL, author TEXT NOT NULL, read INTEGER, UNIQUE(name))");
	
		} catch (SQLException error) {
		  error.printStackTrace();
		  System.exit(1);
		}
	
	  }
	
	  public static void createDatabaseEntries(List<Book> books) {
	
		try (Connection connection = DriverManager.getConnection(connectionString)) {
		  String query = "INSERT INTO Books (name, author, read) VALUES(?, ?, ?)";
	
		  for (Book book : books) {
			try (PreparedStatement prepStmt = connection.prepareStatement(query);) {
			  prepStmt.setString(1, book.name);
			  prepStmt.setString(2, book.author);
			  prepStmt.setInt(3, book.read? 1 : 0);
	
			  prepStmt.executeUpdate();
	
			} catch (SQLException error) {
			  logger.warn("Failed to create book (already exists?) :: " + book.name);
			}
	
		  }
		} catch (SQLException error) {
		  error.printStackTrace();
		  System.exit(2);
		}
	  }

}
