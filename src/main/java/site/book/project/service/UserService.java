package site.book.project.service;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.User;
import site.book.project.dto.UserModifyDto;
import site.book.project.dto.UserProfileDto;
import site.book.project.dto.UserRegisterDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.dto.UserSigninDto;
import site.book.project.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    

    
    
    
    
    
    public String checkUsername(String username) {
        log.info("checkUsername(username = {})", username);
        
        Optional<User> result = userRepository.findByUsername(username);
        if (result.isPresent()) {
                return "namenok";
            } else {
                return "nameok"; 
            }
    }
    
    public String checkNickname(String nickname) {
        log.info("checkNickname(nickname = {})", nickname);
        
        Optional<User> result = userRepository.findByNickName(nickname);
        if (result.isPresent()) {
            return "nicknok";
        } else {
            return "nickok";
        }
    }

    public User registerUser(UserRegisterDto dto) {

        log.info("registerMember(dto = {})", dto);
        
        // 로그인 비밀번호를 암호화한 후 DB에 insert
        dto.setSignupPassword(passwordEncoder.encode(dto.getSignupPassword()));
        User entity = userRepository.save(dto.toEntity());
        log.info("entity = {}", entity);
        
        return entity;
    }
    
    public User read(Integer userId) {
        return userRepository.findById(userId).get();
    }
    
    public User read(String username) {
        return userRepository.findByUsername(username).get();
    }

    public String checkEmail(String email) {

        Optional<User> result = userRepository.findByEmail(email);
        if (result.isPresent() ) {
            return "emailnok";
        } else {
            return "emailok";
        }
    }

    public String signIn(String username, String password) {
        log.info("checkPw userid = {} password = {}", username, password);
        User user = userRepository.findByUsername(username).get();
        log.info("checkPassword user = {}", user);
        String encodingPw = user.getPassword();
        log.info(encodingPw);
        Boolean confirm = confirm(password, encodingPw);
        log.info("confirm = {}", confirm);
        
        if (confirm == true) {
            return "ok";
        } else {
            return "nok";
        }
    }
    
    public User signinUser(UserSigninDto dto) {
        User user = userRepository.findByUsername(dto.getUsername()).get();
        log.info("find in signinUser user = {}", user);
        return user;
    }
    
    

    private Boolean confirm(String password, String password2) {
        return passwordEncoder.matches(password, password2);
    }
    
    public Optional<User> getUserBySigninId(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> read() {
       
        return userRepository.findAll();
    }

    // (하은) 포인트 적립
    public void update(Integer point, Integer id) {
        User user = userRepository.findById(id).get();
        user.update(point + user.getPoint());
        userRepository.save(user);
    }
    
//    @Transactional
//    public void modifyUserImage(Integer id, UserProfileDto dto, MultipartFile file) throws IllegalStateException, IOException {
//      String projectPath=System.getProperty("user.dir")+"\\src\\main\\resources\\static\\images";
//      
//      log.info("projectPath={}",projectPath);
//    
//      String fileName = file.getOriginalFilename();
//      File saveFile=new File(projectPath, fileName);
//      file.transferTo(saveFile);
// //     freeSharePost.setFileName(fileName);        //생성한 파일이름을 저장해줌.
//      System.out.println(fileName);
////      System.out.println(freeSharePost.toString());
// //     freeSharePost.setFilePath("/files/" + fileName);
//      User user = userRepository.findById(id).get();
//    user.updateProfileImage(dto);
//    log.info("i,!!!!mages + fileName={}","/images/" + fileName);
//   
//      user.updateImage(fileName, "/images/" + fileName);
//      
//    }


    @Transactional
    public Integer modify(UserModifyDto userModifyDto, UserSecurityDto u) {
        // 중복검사
        User user = userRepository.findById(u.getId()).get();
        user.updateProfile(userModifyDto);
        
        return 0;
    }
    
// // (예진) 유저 프로필 이미지 변경
//    @Transactional
//   public void updateProfileImage(UserProfileDto dto) {
//        log.info("userProfileDto={}", dto);
//       log.info("changeProfileImage(userId={}, userImage={})", dto.getId(), dto.getUserImage());
//
//        User entity = userRepository.findById(dto.getId()).get();
//        entity.updateProfileImage(dto);
//
//   }


    public void write(Integer id, MultipartFile file) throws IllegalStateException, IOException{
    	
    	log.info("!##id={}",id);
    	
    	// 이미지 파일 저장 경로 설정
    	String projectPath = System.getProperty("user.dir")+"\\src\\main\\resources\\static\\files";
    	
    	UUID uuid = UUID.randomUUID();  // 식별자
    	
    	String fileName = uuid + "_" + file.getOriginalFilename();
    	log.info("!#fileName={}", fileName);
    	File saveFile=new File(projectPath, fileName); // saveFile: 파일 껍데기(객체) 생성 -> 경로+파일이름을 저장
    	file.transferTo(saveFile);
    	
    	User user = userRepository.findById(id).get();
    	//user.updateImage(fileName, "/files/" + fileName);
    	
    	user.setFileName(fileName);
    	user.setFilePath("/files/" + fileName);
    	
    	userRepository.save(user);
    	log.info("fileName={}", fileName);
    	log.info("filePath={}", "/files/" + fileName);
    	log.info("user.getUserImage ={}", user.getUserImage());
    }

}
