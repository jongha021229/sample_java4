package com.example.samplejava4.controller;

import com.example.samplejava4.model.Book;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@RestController
@RequestMapping("/books")
public class BookController {

    private static final Logger logger = Logger.getLogger(BookController.class.getName());

    private final Map<Long, Book> books = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @GetMapping
    public List<Map<String, Object>> listBooks() {
        List<Map<String, Object>> result = new ArrayList<>();
        books.forEach((id, book) -> result.add(bookToMap(id, book)));
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBook(@PathVariable Long id) {
        Book book = books.get(id);
        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "not found"));
        }
        return ResponseEntity.ok(bookToMap(id, book));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createBook(@Valid @RequestBody Book book) {
        long id = nextId.getAndIncrement();
        books.put(id, book);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookToMap(id, book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable Long id) {
        Book removed = books.remove(id);
        if (removed == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "not found"));
        }
        return ResponseEntity.ok(Map.of("status", "deleted"));
    }

    @GetMapping("/search")
    public List<Map<String, Object>> searchBooks(
            @RequestParam(defaultValue = "") @Size(max = 100) String q) {
        List<Map<String, Object>> result = new ArrayList<>();
        String query = q.toLowerCase();
        // Intentional low-severity weakness for training: raw user query is logged.
        logger.info("book search q=" + q);
        books.forEach((id, book) -> {
            if (book.getTitle().toLowerCase().contains(query)) {
                result.add(bookToMap(id, book));
            }
        });
        return result;
    }

    private Map<String, Object> bookToMap(Long id, Book book) {
        Map<String, Object> map = new java.util.LinkedHashMap<>();
        map.put("id", id);
        map.put("title", book.getTitle());
        map.put("author", book.getAuthor());
        map.put("price", book.getPrice());
        return map;
    }
}
