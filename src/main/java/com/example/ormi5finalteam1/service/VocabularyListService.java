package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import com.example.ormi5finalteam1.domain.vocabulary.VocabularyList;
import com.example.ormi5finalteam1.repository.VocabularyListRepository;
import com.example.ormi5finalteam1.repository.VocabularyListVocabularyRepository;
import com.example.ormi5finalteam1.repository.VocabularyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VocabularyListService {

    private final VocabularyListRepository vocabularyListRepository;
    private final VocabularyListVocabularyRepository vocabularyListVocabularyRepository;
    private final VocabularyRepository vocabularyRepository;

    public void create(Provider provider) {
        vocabularyListRepository.save(new VocabularyList(new User(provider.id())));
    }

    @Transactional
    public void addVocabulary(Provider provider) {

        VocabularyList myVocabularyList = getMyVocabularyList(provider);
        Long lastVocabularyId = getLastVocabularyId(provider, myVocabularyList);
        List<Vocabulary> newVocabularies = getNewVocabularies(provider, lastVocabularyId);

        myVocabularyList.addVocabularies(newVocabularies);
    }

    //내 단어장 가져오기
    public VocabularyList getMyVocabularyList(Provider provider) {
        return vocabularyListRepository.findByUserId(provider.id())
            .orElseThrow(() -> new BusinessException(ErrorCode.VOCABULARY_LIST_NOT_FOUND));
    }

    //브릿지 테이블에서 마지막 단어 id 가져오기
    private Long getLastVocabularyId(Provider provider, VocabularyList myVocabularyList) {
        return vocabularyListVocabularyRepository.findMaxVocabularyIdByVocabularyListIdAndGrade(
            myVocabularyList.getId(), provider.grade()).orElse(0L);
    }

    //마지막 단어 id 보다 큰 값중에서 조건에 맞는 단어 가지고오기
    private List<Vocabulary> getNewVocabularies(Provider provider, Long lastVocabularyId) {
        List<Vocabulary> newVocabularies = vocabularyRepository
            .findTop10ByGradeAndIdGreaterThanOrderById(provider.grade(), lastVocabularyId);
        if (newVocabularies.isEmpty()) {
            throw new BusinessException(ErrorCode.NEW_VOCABULARIES_NOT_FOUND);
        }
        return newVocabularies;
    }

}
