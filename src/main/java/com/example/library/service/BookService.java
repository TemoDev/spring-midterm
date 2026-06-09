package com.example.library.service;

import com.example.library.dto.request.BookRequestDTO;
import com.example.library.dto.request.BookUpdateDTO;
import com.example.library.dto.response.BookResponseDTO;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.mapper.BookMapper;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Transactional
    public BookResponseDTO create(BookRequestDTO dto) {
        if (!authorRepository.existsById(dto.getAuthorId())) {
            throw new ResourceNotFoundException("Author not found with id: " + dto.getAuthorId());
        }
        Author author = authorRepository.findById(dto.getAuthorId()).get();
        Book book = BookMapper.toEntity(dto, author);
        Book saved = bookRepository.save(book);
        return BookMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<BookResponseDTO> findAll() {
        List<BookResponseDTO> result = new ArrayList<>();
        for (Book book : bookRepository.findAll()) {
            result.add(BookMapper.toResponseDTO(book));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public BookResponseDTO findById(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        Book book = bookRepository.findById(id).get();
        return BookMapper.toResponseDTO(book);
    }

    @Transactional
    public BookResponseDTO update(Long id, BookUpdateDTO dto) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        if (!authorRepository.existsById(dto.getAuthorId())) {
            throw new ResourceNotFoundException("Author not found with id: " + dto.getAuthorId());
        }
        Book book = bookRepository.findById(id).get();
        Author author = authorRepository.findById(dto.getAuthorId()).get();
        BookMapper.updateEntityFromDTO(book, dto, author);
        Book saved = bookRepository.save(book);
        return BookMapper.toResponseDTO(saved);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }
}
