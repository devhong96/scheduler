package com.attendance.scheduler.board.controller;

import com.attendance.scheduler.board.application.BoardService;
import com.attendance.scheduler.board.dto.BoardRequest;
import com.attendance.scheduler.board.dto.BoardResponse;
import com.attendance.scheduler.board.dto.Condition;
import com.attendance.scheduler.board.dto.NoticeDetailResponse;
import com.attendance.scheduler.board.dto.NoticePageResponse;
import com.attendance.scheduler.comment.application.CommentService;
import com.attendance.scheduler.comment.dto.CommentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 공지사항(board) 도메인 REST API.
 * 목록/상세 조회는 공개, 작성/수정/삭제는 ROLE_ADMIN (SecurityConfig 에서 제어).
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardApiController {

    private final BoardService boardService;
    private final CommentService commentService;

    /** 공지 목록 (검색 + 페이징). */
    @GetMapping
    public ResponseEntity<NoticePageResponse> list(@ModelAttribute Condition condition, Pageable pageable) {
        Page<BoardResponse> page = boardService.pageNoticeList(condition, pageable);
        return ResponseEntity.ok(NoticePageResponse.of(page));
    }

    /** 공지 상세 + 댓글 목록. */
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        BoardResponse notice = boardService.findNoticeById(id);
        if (notice == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "존재하지 않는 공지입니다."));
        }
        List<CommentResponse> comments = commentService.getCommentList(id);
        return ResponseEntity.ok(new NoticeDetailResponse(notice, comments));
    }

    /** 공지 작성 (관리자). 작성자명은 인증 principal 에서. */
    @PostMapping
    public ResponseEntity<?> write(@RequestBody BoardRequest request, Authentication authentication) {
        boardService.writeNotice(request.withName(authentication.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "등록되었습니다."));
    }

    /** 공지 수정 (관리자). */
    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable Long id, @RequestBody BoardRequest request) {
        boardService.editNotice(request.withId(id));
        return ResponseEntity.ok(Map.of("message", "수정되었습니다."));
    }

    /** 공지 삭제 (관리자). */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boardService.deleteNotice(id);
        return ResponseEntity.ok(Map.of("message", "삭제되었습니다."));
    }
}
