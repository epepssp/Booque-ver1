package site.book.project.web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.User;
import site.book.project.dto.ReplyUpdateDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.UserRepository;
import site.book.project.service.UserService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ImageUploadController {
    
    private final UserService userService;
    private final UserRepository userRepository;
 
    
    @Value("${site.book.upload.path}") // (예진) 이미지 저장할 로컬 폴더 
    private String imageFilePath; 
    
    @GetMapping("/tempView/{id}")
    public ResponseEntity<User> viewTempImage(@PathVariable Integer id){
 
        User u =userService.read(id);
       
        return ResponseEntity.ok(u);
    }
    
     
     
     @GetMapping("/view/{fileName}")  // 로컬 폴더 이미지 불러오기
     public ResponseEntity<Resource> viewUpdatedImage(@PathVariable String fileName) {
        
        File file = new File(imageFilePath, fileName);
        
        String contentType = null;
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            log.error("{} : {}", e.getCause(), e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", contentType);
        
        Resource resource = new FileSystemResource(file);
        
        log.info("헤더보여조={}", headers);
        log.info("리소스보여조={}", resource);
       
        return ResponseEntity.ok().headers(headers).body(resource);
    }
  
    
}

