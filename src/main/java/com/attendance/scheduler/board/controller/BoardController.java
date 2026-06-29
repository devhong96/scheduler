package com.attendance.scheduler.board.controller;

import com.attendance.scheduler.board.application.BoardService;
import com.attendance.scheduler.board.dto.BoardRequest;
import com.attendance.scheduler.board.dto.BoardResponse;
import com.attendance.scheduler.board.dto.Condition;
import com.attendance.scheduler.comment.application.CommentService;
import com.attendance.scheduler.comment.dto.CommentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    public final BoardService boardService;
    public final CommentService commentService;

    @GetMapping("")
    public String getNoticeList(Condition condition, Pageable pageable, Model model) {
        Page<BoardResponse> allBoardList = boardService.pageNoticeList(condition, pageable);
        model.addAttribute("noticeList", allBoardList);
        model.addAttribute("maxPage", 5);
        return "board/list";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/createNotice")
    public String writeNoticeForm(@ModelAttribute("noticeObject") BoardRequest boardRequest) {
        return "board/createNotice";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/write")
    public String writeNotice(BoardRequest boardRequest, Model model) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            boardRequest = boardRequest.withName(auth.getName());
            boardService.writeNotice(boardRequest);
            return "redirect:/board";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "board/createNotice";
        }
    }

    @GetMapping("/{id}")
    public String noticeForm(@PathVariable("id") Long id, Model model) {
        Optional<BoardResponse> noticeById = Optional.ofNullable(boardService.findNoticeById(id));
        List<CommentResponse> commentList = commentService.getCommentList(id);

        if (noticeById.isPresent()) {
            model.addAttribute("notice", noticeById.get());
            model.addAttribute("commentList", commentList);
            return "board/notice";
        }
        return "redirect:/board";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/edit/")
    public String editNoticeForm(@RequestParam(name = "id") Long id,
                                 @ModelAttribute("noticeObject") BoardRequest boardRequest, Model model) {

        Optional<BoardResponse> noticeById = Optional.ofNullable(boardService.editNoticeForm(id));
        if (noticeById.isPresent()) {
            model.addAttribute("notice", noticeById.get());
            return "board/editNotice";
        }
        return "redirect:/board";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/edit/")
    public String editNotice(@RequestParam(name = "id") Long id, BoardRequest boardRequest) {
        boardRequest = boardRequest.withId(id);
        boardService.editNotice(boardRequest);
        return "redirect:/board";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/")
    public String deleteNotice(@RequestParam(name = "id") Long id) {
        boardService.deleteNotice(id);
        return "redirect:/board";
    }
}
