package site.book.project.web;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.User;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.UserRepository;
import site.book.project.service.UserService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ImageUploadController {
    
    private final UserService userService;
    private final UserRepository userRepository;
    
    @GetMapping("/updateImage/{id}")
    public ResponseEntity<User> viewUpdatedImage(@PathVariable Integer id){
        log.info("viewUpdatedImage@@@@@(id={})", id);
        
        User u =userService.read(id);
       
        return ResponseEntity.ok(u);
    }
    
//    @Value("${profiles.absolute.path}")    // (예진) 절대 경로(application.properties에 설정한 외부 경로) 값 주입
//    private String uploadPath;
//
//    
//    @PostMapping("/upload")
//    public ResponseEntity<Integer> saveImage(@RequestParam("id") Integer id,@RequestParam("file") MultipartFile file) throws IllegalStateException, IOException{
//        
//        
//        log.info("컨트롤러 오ㅏㅆ니={},{}",id, file);
//        log.info("uploadPath={}",uploadPath);
//        
//        // 이미지 파일 저장 경로 설정
////      String projectPath = System.getProperty(profilePath);
////      log.info("projectPath={}",projectPath);
//        
//        UUID uuid = UUID.randomUUID();  // 식별자
//        String fileName = uuid + "_" + file.getOriginalFilename();
//        log.info("!#fileName={}", fileName);
//        
//        File saveFile=new File(uploadPath, fileName); // saveFile: 파일 껍데기(객체) 생성 -> 경로+파일이름을 저장
//        file.transferTo(saveFile);
//        
//        User userTemp = userService.read(id);  // 현재 로그인 한 유저
//        
//        userTemp.setFileName(fileName);
//        userTemp.setUserImage("/profiles/"+file.getOriginalFilename());
//        //userTemp.setFilePath("C:\\profiles\\" + fileName);
//        userTemp.setFilePath(uploadPath + fileName);
//        
//        userRepository.save(userTemp);
//        Integer result = userTemp.getId();
//        
//        log.info("uploadPath={}", uploadPath);
//        log.info("fileName={}", fileName);
//        log.info("filePath={}", uploadPath  + fileName);
//        // log.info("user.getUserImage ={}", userTemp.getUserImage());
//        
//       
//        return ResponseEntity.ok(result);
//    }
//    
//  
//    @GetMapping("/show/{id}")
//    public ResponseEntity<String> viewImage(Integer id){
//        
//        User userTemp = userService.read(id);
//        String imagePath = userTemp.getFilePath();
//        
////        File file = new File(profilePath, fileName);
////        String contentType = null;
////        
////        try {
////           contentType = Files.probeContentType(file.toPath());
////        } catch (IOException e) {
////            log.error("{} : {}", e.getCause(), e.getMessage());
////            e.printStackTrace();
////        }
////        
////        HttpHeaders headers = new HttpHeaders();
////        headers.add("content-Type", contentType);
////        
////        Resource resource = new FileSystemResource(file);
//        return ResponseEntity.ok(imagePath);
//    }
//    
//    
//
}
