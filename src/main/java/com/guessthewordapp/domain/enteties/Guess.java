package com.guessthewordapp.domain.enteties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Guess {
    private Long id;
    private Long sessionId;
    private Long wordId;
    private String guessedText;
    private boolean correct;
}