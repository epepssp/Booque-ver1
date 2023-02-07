package site.book.project.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Notices;
import site.book.project.domain.PostReply;
import site.book.project.dto.NoticeDto;
import site.book.project.dto.ReplyReadDto;
import site.book.project.service.NoticeService;
import site.book.project.service.ReplyService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class NoticeController {

    private final NoticeService noticeService;
    private final ReplyService replyService;
    
    // (예진) 어떤 포스트 글에 새 댓글이 달릴 때 알림(notice) 만들어짐
    // notice create
    @PostMapping("/notice")
    public ResponseEntity<Integer> newNotice(@RequestBody NoticeDto dto){
        log.info("registe노티스={}", dto);
        
       Integer noticeId = noticeService.create(dto);
       
       return ResponseEntity.ok(noticeId);
    }
    
    // (예진) userId(postWriter) 기준 알림 리스트(notice list) 불러오기
    @GetMapping("/showNotice/{userId}")
    public ResponseEntity<List<NoticeDto>> showAllNotices(@PathVariable Integer userId) {
        log.info("쇼All노티스(userId={})", userId);
        
        List<NoticeDto> list =noticeService.readNotices(userId);
       
        
        
        return ResponseEntity.ok(list);
    }
}
