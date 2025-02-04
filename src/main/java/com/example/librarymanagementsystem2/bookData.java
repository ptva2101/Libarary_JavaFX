package com.example.librarymanagementsystem2;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class bookData {

    private String isbn;
    private String title;
    private String author;
    private String genre;
    private String image;
    private Date date;
    private int quantity;
    private int remain;
    private int issued;

    public bookData(String isbn, String title, String author, String genre, String image,
                    Date date, int quantity, int remain, int issued) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.image = image;
        this.date = date;
        this.quantity = quantity;
        this.remain = remain;
        this.issued = issued;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public String getImage() {
        return image;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getRemain() {
        return remain;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }

    public int getIssued() {
        return issued;
    }

    public void setIssued(int issued) {
        this.issued = issued;
    }

    public static List<bookData> searchBooks(String query) {
        List<bookData> bookList = new ArrayList<>();
        try {
            JsonArray booksData = GoogleBooksAPI.getBookData(query);

            for (JsonElement element : booksData) {
                JsonObject volume = element.getAsJsonObject();
                JsonObject volumeInfo = volume.getAsJsonObject("volumeInfo");

                String isbn10 = GoogleBooksAPI.getISBN10(volumeInfo);
                String isbn13 = GoogleBooksAPI.getISBN13(volumeInfo);
                String isbn = !isbn13.isEmpty() ? isbn13 : isbn10;

                Date publishDate = null;
                try {
                    String dateStr = GoogleBooksAPI.getPublishedDate(volumeInfo);

                    // Định dạng ngày tháng có thể là "yyyy-MM-dd" hoặc định dạng dài như "Wed Nov 20 00:00:00 ICT 2024"
                    DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");  // Định dạng như "Wed Nov 20 00:00:00 ICT 2024"

                    LocalDate localDate = null;

                    // Thử định dạng "yyyy-MM-dd"
                    try {
                        localDate = LocalDate.parse(dateStr, formatter1);
                    } catch (DateTimeParseException e1) {
                        // Nếu không parse được, thử định dạng dài
                        try {
                            // Parse chuỗi với định dạng dài
                            DateTimeFormatter formatterWithTime = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");
                            LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatterWithTime);
                            localDate = localDateTime.toLocalDate();  // Chỉ lấy ngày từ LocalDateTime
                        } catch (DateTimeParseException e2) {
                            // Nếu cả hai định dạng đều không hợp lệ, xử lý trường hợp này
                            System.out.println("Không thể parse ngày tháng từ chuỗi: " + dateStr);
                            e2.printStackTrace();
                        }
                    }

                    // Nếu có ngày hợp lệ thì chuyển sang Date
                    if (localDate != null) {
                        publishDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }



                bookData book = new bookData(
                        isbn,
                        GoogleBooksAPI.getTitle(volumeInfo),
                        GoogleBooksAPI.getAuthors(volumeInfo),
                        GoogleBooksAPI.getCategories(volumeInfo),
                        GoogleBooksAPI.getBookImage(volumeInfo),
                        publishDate,
                        0,
                        0,
                        0
                );

                bookList.add(book);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookList;
    }
}