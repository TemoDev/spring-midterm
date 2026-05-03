package com.example.library.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateDTO {

    @NotBlank
    @Size(min = 1, max = 200)
    private String title;

    @NotBlank
    @Size(min = 10, max = 20)
    private String isbn;

    @NotNull
    @Min(1000)
    @Max(2100)
    private Integer publicationYear;

    @NotNull
    private Long authorId;
}
