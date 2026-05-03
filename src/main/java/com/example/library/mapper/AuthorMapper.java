package com.example.library.mapper;

import com.example.library.dto.request.AuthorRequestDTO;
import com.example.library.dto.request.AuthorUpdateDTO;
import com.example.library.dto.response.AuthorResponseDTO;
import com.example.library.entity.Author;

public class AuthorMapper {

    private AuthorMapper() {}

    public static Author toEntity(AuthorRequestDTO dto) {
        Author author = new Author();
        author.setName(dto.getName());
        author.setEmail(dto.getEmail());
        author.setBio(dto.getBio());
        return author;
    }

    public static AuthorResponseDTO toResponseDTO(Author author) {
        AuthorResponseDTO dto = new AuthorResponseDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setEmail(author.getEmail());
        dto.setBio(author.getBio());
        dto.setBookCount(author.getBooks().size());
        return dto;
    }

    public static void updateEntityFromDTO(Author author, AuthorUpdateDTO dto) {
        author.setName(dto.getName());
        author.setEmail(dto.getEmail());
        author.setBio(dto.getBio());
    }
}
