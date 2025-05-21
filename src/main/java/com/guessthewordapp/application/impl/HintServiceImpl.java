package com.guessthewordapp.application.impl;

import com.guessthewordapp.application.contract.HintService;
import com.guessthewordapp.application.contract.dto.HintDTO;
import com.guessthewordapp.domain.enteties.Hint;
import com.guessthewordapp.infrastructure.persistence.contract.HintRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HintServiceImpl implements HintService {

    private final HintRepository hintRepository;

    public HintServiceImpl(HintRepository hintRepository) {
        this.hintRepository = hintRepository;
    }

    @Override
    public List<HintDTO> getHintsForWord(Long wordId) {
        return hintRepository.findByWordId(wordId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public HintDTO saveHint(HintDTO hintDTO) {
        Hint hint = mapToEntity(hintDTO);
        Hint savedHint = hintRepository.save(hint);
        return mapToDTO(savedHint);
    }

    private Hint mapToEntity(HintDTO dto) {
        return new Hint(
            dto.id(),
            dto.wordId(),
            dto.text()
        );
    }

    private HintDTO mapToDTO(Hint entity) {
        return new HintDTO(
            entity.getId(),
            entity.getWordId(),
            entity.getText()
        );
    }
}