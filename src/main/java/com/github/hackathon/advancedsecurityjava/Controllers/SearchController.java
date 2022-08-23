package com.github.hackathon.advancedsecurityjava.Controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.github.hackathon.advancedsecurityjava.Application;
import com.github.hackathon.advancedsecurityjava.Models.Book;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class SearchController {

    @GetMapping("/search")
    @ResponseBody
    public List<Book> searchBooks(@RequestParam(name = "q", required = true) String search) {
        List<Book> books = new ArrayList<Book>();

        Application.logger.info("Search query: {}", search);

        String query = "SELECT * FROM Books WHERE name LIKE '%" + search + "%' OR author LIKE '%" + search + "%'";
        try(Connection connection = DriverManager.getConnection(Application.connectionString))
        {
            try (Statement statement = connection.createStatement()) {
                ResultSet results = statement.executeQuery(query);

                while (results.next()) {
                    Book book = new Book(results.getString("name"), results.getString("author"), (results.getInt("read") == 1));

                    books.add(book);
                }
            } catch (SQLException error) {
                error.printStackTrace();
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }

        return books;
    }
}