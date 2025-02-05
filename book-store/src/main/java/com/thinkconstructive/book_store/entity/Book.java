package com.thinkconstructive.book_store.entity;

public record Book(String bookId, String name,
                   String price, String author, String description) {
}
