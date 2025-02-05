package com.thinkconstructive.book_store.dto;

public record BookDto(String bookId, String name,
                      String price, String author, String description) {
}
