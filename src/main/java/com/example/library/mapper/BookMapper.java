package com.example.library.mapper;

import com.example.library.dto.request.BookRequestDTO;
import com.example.library.dto.request.BookUpdateDTO;
import com.example.library.dto.response.BookResponseDTO;
import com.example.library.entity.Author;
import com.example.library.entity.Book;

public class BookMapper {

    private BookMapper() {}

    public static Book toEntity(BookRequestDTO dto, Author author) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setPublicationYear(dto.getPublicationYear());
        book.setAuthor(author);
        return book;
    }

    public static BookResponseDTO toResponseDTO(Book book) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setPublicationYear(book.getPublicationYear());
        dto.setAuthorId(book.getAuthor().getId());
        dto.setAuthorName(book.getAuthor().getName());
        return dto;
    }

    public static void updateEntityFromDTO(Book book, BookUpdateDTO dto, Author author) {
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setPublicationYear(dto.getPublicationYear());
        book.setAuthor(author);
    }
}
