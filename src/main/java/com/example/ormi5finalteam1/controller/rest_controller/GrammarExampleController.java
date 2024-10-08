package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.grammar_example.dto.GrammarExampleDto;
import com.example.ormi5finalteam1.domain.grammar_example.dto.GrammarExampleGradingDto;
import com.example.ormi5finalteam1.domain.grammar_example.dto.request.GradeGrammarExampleRequestDto;
import com.example.ormi5finalteam1.domain.grammar_example.dto.response.MultipleGrammarExampleResponseDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.service.GrammarExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GrammarExampleController {

  private final GrammarExampleService grammarExampleService;

  /** 문법 예문 조회 */
  @GetMapping("/me/grammar-examples")
  public ResponseEntity<MultipleGrammarExampleResponseDto> getGrammarExamples(
      @AuthenticationPrincipal Provider provider,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String keyword) {
    // 현재 로그인된 유저 정보에서 grade, grammarExampleCount를 추출하여 조회

    Grade grade = provider.grade();
    int grammarExampleCount = provider.grammarExampleCount();

    // 1페이지 외의 페이지 조회시
    if (page > 0) {
      if (grammarExampleCount % 10 != 0) {
        pageSize = grammarExampleCount % 10;
      }
    }
    PageRequest pageRequest = PageRequest.of(page, pageSize);

    List<GrammarExampleDto> grammarExamples =
        grammarExampleService.getGrammarExamples(grade, pageRequest, keyword);

    MultipleGrammarExampleResponseDto responseDto =
        new MultipleGrammarExampleResponseDto(grammarExamples, grammarExampleCount);

    return new ResponseEntity<>(responseDto, HttpStatusCode.valueOf(200));
  }

  /** 문법 예문 채점 */
  @PostMapping("/grammar-examples/{id}/grading")
  public ResponseEntity<GrammarExampleGradingDto> gradeGrammarExample(
      @PathVariable Long id,
      @RequestBody GradeGrammarExampleRequestDto requestDto,
      @AuthenticationPrincipal Provider provider) {
    Long userId = provider.id();
    String answer = requestDto.getAnswer();
    GrammarExampleGradingDto grammarExampleGradingDto =
        grammarExampleService.gradeGrammarExample(id, userId, answer);

    return new ResponseEntity<>(grammarExampleGradingDto, HttpStatusCode.valueOf(200));
  }

  /** 문법 예문 추가 */
  @PostMapping("/grammar-examples/more")
  public ResponseEntity<Void> createMoreGrammarExamples(
      @AuthenticationPrincipal Provider provider) {
    long userId = provider.id();
    grammarExampleService.createMoreGrammarExamples(userId);

    return new ResponseEntity<>(HttpStatusCode.valueOf(204));
  }
}
