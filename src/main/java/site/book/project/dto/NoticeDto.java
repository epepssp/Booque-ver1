package site.book.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Notices;
import site.book.project.domain.PostReply;
import site.book.project.service.ReplyService;

@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Data
public class NoticeDto {

   
    
    private Integer noticeId;
    private Integer userId;  // postWriter id;
    private Integer postId;
    private Integer bookId;
    
    private PostReply reply;
    private Integer replyId;
    private String replyWriter;
    private String nickName;  // replyWriter의 닉네임
    private String userImage; // replyWriter의 유저이미지
    
//    public NoticeDto fromEntity(Notices entity) {
//        
// 
//        return NoticeDto.builder().
//    }
    
}
