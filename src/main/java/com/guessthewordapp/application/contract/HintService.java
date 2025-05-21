package com.guessthewordapp.application.contract;

import com.guessthewordapp.application.contract.dto.HintDTO;
import java.util.List;

public interface HintService {
    List<HintDTO> getHintsForWord(Long wordId);
    HintDTO saveHint(HintDTO hintDTO);
}