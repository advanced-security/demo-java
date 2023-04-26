package com.github.hackathon.advancedsecurityjava.Controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.github.hackathon.advancedsecurityjava.Application;
import com.github.hackathon.advancedsecurityjava.Models.Book;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

  private static Connection connection;

  @GetMapping("/")
  @ResponseBody
  public List<Book> getBooks(@RequestParam(name = "name", required = false) String bookname,
      @RequestParam(name = "author", required = false) String bookauthor,
      @RequestParam(name = "read", required = false) Boolean bookread) {
    List<Book> books = new ArrayList<Book>();

    PreparedStatement statement = null;
    List<String> parameters = new ArrayList<>();

    try {
      // Init connection to DB
      connection = DriverManager.getConnection(Application.connectionString);

      String query = null;

      if (bookname != null) {
        // Filter by book name
        query = "SELECT * FROM Books WHERE name LIKE ?";
        parameters.add("%" + bookname + "%");
      } else if (bookauthor != null) {
        // Filter by book author
        query = "SELECT * FROM Books WHERE author LIKE ?";
        parameters.add("%" + bookauthor + "%");
      } else if (bookread != null) {
        // Filter by if the book has been read or not
        Integer read = bookread ? 1 : 0;
        query = "SELECT * FROM Books WHERE read = ?";
        parameters.add(read.toString());
      } else {
        // All books
        query = "SELECT * FROM Books";
      }

      statement = connection.prepareStatement(query);
      int index = 1;
      for (String parameter : parameters) {
        statement.setString(index, parameter);
        index += 1;
      }

      ResultSet results = statement.executeQuery();

      while (results.next()) {
        Book book = new Book(results.getString("name"), results.getString("author"), (results.getInt("read") == 1));

        books.add(book);
      }

    } catch (SQLException error) {
      error.printStackTrace();
    } finally {
      try {
        if (connection != null) {
          connection.close();
        }
        if (statement != null) {
          statement.close();
        }
      } catch (SQLException error) {
        error.printStackTrace();
      }
    }
    return books;
  }
}