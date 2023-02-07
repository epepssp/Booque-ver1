package site.book.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Notices;
import site.book.project.domain.PostReply;
import site.book.project.domain.User;
import site.book.project.dto.NoticeDto;
import site.book.project.repository.NoticeRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ReplyService replyService;
    
    // 새 댓글 달리면 노티스 등록
    public Integer create(NoticeDto dto) {
        
        Notices notice = Notices.builder()
                .userId(dto.getUserId())
                .bookId(dto.getBookId())
                .postId(dto.getPostId())
                .replyId(dto.getReplyId()).build();
        
        Notices n= noticeRepository.save(notice);
        return n.getNoticeId();
    }

   
    public List<NoticeDto> readNotices(Integer userId) {
        log.info("read노티스(유저Id={})", userId);
        
        List<Notices> list = noticeRepository.findByUserIdOrderByNoticeIdDesc(userId);
        List<NoticeDto> noticeList = new ArrayList<>();
        PostReply r = null;
        NoticeDto dto = null;
        for (Notices n : list) {
            r = replyService.readRep(n.getReplyId());
           
             dto= NoticeDto.builder().noticeId(n.getNoticeId())
            .postId(n.getPostId())
            .bookId(n.getBookId())
            .userId(n.getUserId())
            .replyId(n.getReplyId())
            .replyWriter(r.getReplyWriter())
            .userImage(r.getUser().getUserImage())
            .nickName(r.getUser().getNickName())
            .build();           
            
            noticeList.add(dto);
        }
        
        log.info("노티스디티오리스트={}",noticeList);
        
        return noticeList;
    }

}
