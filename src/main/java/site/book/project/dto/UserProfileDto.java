package site.book.project.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class UserProfileDto {

    private Integer id;   // userId
    private String userImage;
    private String postIntro;
    private String nickName;
    private String filePath;
    private String fileName;
   // private MultipartFile file;
}
