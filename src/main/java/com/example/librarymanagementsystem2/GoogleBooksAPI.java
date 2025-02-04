package com.example.librarymanagementsystem2;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GoogleBooksAPI {
    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public static JsonArray getBookData(String query) throws IOException {
        String url = API_URL + query;
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
            if (jsonObject.has("error") || !jsonObject.has("items")) {
                return new JsonArray(); // Trả về mảng rỗng nếu có lỗi hoặc không có sách
            }
            return jsonObject.getAsJsonArray("items");
        }
    }
    public static String getTitle(JsonObject volumeInfo) {
        return volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "";
    }

    public static String getAuthors(JsonObject volumeInfo) {
        if (volumeInfo.has("authors")) {
            JsonArray authorsArray = volumeInfo.getAsJsonArray("authors");
            StringBuilder authors = new StringBuilder();
            for (JsonElement author : authorsArray) {
                authors.append(author.getAsString()).append(", ");
            }
            if (authors.length() > 2) {
                authors.delete(authors.length() - 2, authors.length()); // Xóa ", " cuối cùng
            }
            return authors.toString();
        } else {
            return "";
        }
    }
    public static String getPublishedDate(JsonObject volumeInfo) {
        return volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").getAsString() : "";
    }
    public static String getISBN10(JsonObject volumeInfo) {
        return getISBN(volumeInfo, "ISBN_10");
    }
    public static String getISBN13(JsonObject volumeInfo) {
        return getISBN(volumeInfo, "ISBN_13");
    }
    private static String getISBN(JsonObject volumeInfo, String isbnType) {
        if (volumeInfo.has("industryIdentifiers")) {
            JsonArray identifiers = volumeInfo.getAsJsonArray("industryIdentifiers");
            for (JsonElement identifier : identifiers) {
                JsonObject idObject = identifier.getAsJsonObject();
                if (idObject.get("type").getAsString().equals(isbnType)) {
                    return idObject.get("identifier").getAsString();
                }
            }
        }
        return "";
    }
    public static String getCategories(JsonObject volumeInfo) {
        if (volumeInfo.has("categories")) {
            JsonArray categoriesArray = volumeInfo.getAsJsonArray("categories");
            StringBuilder categories = new StringBuilder();
            for (JsonElement category : categoriesArray) {
                categories.append(category.getAsString()).append(", ");
            }
            if (categories.length() > 2) {
                categories.delete(categories.length() - 2, categories.length());
            }
            return categories.toString();
        } else {
            return "";
        }
    }
    public static String getBookImage(JsonObject volumeInfo) {
        if (volumeInfo.has("imageLinks")) {
            JsonObject imageLinks = volumeInfo.getAsJsonObject("imageLinks");
            if (imageLinks.has("thumbnail")) {
                return imageLinks.get("thumbnail").getAsString();
            } else if (imageLinks.has("smallThumbnail")) {
                return imageLinks.get("smallThumbnail").getAsString();
            }
        }
        return "";
    }
}