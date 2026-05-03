package com.example.library.service;

import com.example.library.dto.request.AuthorRequestDTO;
import com.example.library.dto.request.AuthorUpdateDTO;
import com.example.library.dto.response.AuthorResponseDTO;
import com.example.library.entity.Author;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.mapper.AuthorMapper;
import com.example.library.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Transactional
    public AuthorResponseDTO create(AuthorRequestDTO dto) {
        Author author = AuthorMapper.toEntity(dto);
        Author saved = authorRepository.save(author);
        return AuthorMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<AuthorResponseDTO> findAll() {
        List<AuthorResponseDTO> result = new ArrayList<>();
        for (Author author : authorRepository.findAll()) {
            result.add(AuthorMapper.toResponseDTO(author));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public AuthorResponseDTO findById(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author not found with id: " + id);
        }
        Author author = authorRepository.findById(id).get();
        return AuthorMapper.toResponseDTO(author);
    }

    @Transactional
    public AuthorResponseDTO update(Long id, AuthorUpdateDTO dto) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author not found with id: " + id);
        }
        Author author = authorRepository.findById(id).get();
        AuthorMapper.updateEntityFromDTO(author, dto);
        Author saved = authorRepository.save(author);
        return AuthorMapper.toResponseDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
    }
}
