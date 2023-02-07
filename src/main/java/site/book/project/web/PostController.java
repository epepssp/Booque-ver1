package site.book.project.web;

import java.io.FileNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.engine.AttributeName;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Book;
import site.book.project.domain.Post;
import site.book.project.domain.User;
import site.book.project.dto.PostCreateDto;
import site.book.project.dto.PostListDto;
import site.book.project.dto.PostReadDto;
import site.book.project.dto.PostUpdateDto;

import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.UserRepository;
import site.book.project.service.BookService;
import site.book.project.service.PostService;
import site.book.project.service.ReplyService;
import site.book.project.service.UserService;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final BookService bookService;
    private final UserService userService;
    private final ReplyService replyService;
    private final UserRepository userRepository;
       
    
    @Transactional(readOnly = true)
    @GetMapping("/list")
    public String list(@AuthenticationPrincipal UserSecurityDto userSecurityDto, String postWriter, Model model) {
        log.info("list()");
//        bookService.readPostCountByAllBookId();

        User user = null; 
        List<PostListDto> postList = new ArrayList<>();
        
        if(userSecurityDto == null) {
            user = userService.read(postWriter);
            Integer userId = user.getId();
            
           postList = postService.postDtoList(userId);
            
               
        } else if (postWriter == null) {
            
            Integer userId = userSecurityDto.getId();
            user = userService.read(userId);
            log.info("id= {}",userId);
        
            postList = postService.postDtoList(userId);
        
              
        }  else if(userSecurityDto.getUsername().equals(postWriter)) {
            
            Integer userId = userSecurityDto.getId();
             user = userService.read(userId);
            log.info("id= {}",userId);
        
            postList = postService.postDtoList(userId);
        
        
        } else if(!userSecurityDto.getUsername().equals(postWriter)) {
            user = userService.read(postWriter);
            Integer userId = user.getId();
            
             postList = postService.postDtoList(userId);
      } 
        
       
       // 포스트 create 날짜랑 오늘 날짜랑 같으면 new 하려고
        LocalDate now = LocalDate.now();
        String day= now.toString().substring(8);
        
        // (하은) post에 있는 bookId로 책 정보 넘기기
        List<Book> books = new ArrayList<>();
        
        for ( PostListDto p : postList) {
            
            Book book = bookService.read(p.getBookId());
            books.add(book);
        }
            
        
            model.addAttribute("day", day);
            model.addAttribute("user", user);      
            model.addAttribute("list", postList);
            model.addAttribute("books", books);
                
        return "/post/list";
    }


   
    
    @PostMapping("/create")
    public String create(PostCreateDto dto, RedirectAttributes attrs) {
        log.info("create(dto ={})", dto);   
      
        Post entity = postService.create(dto); 
        
        // (홍찬) 리뷰순에서 사용할 것 - 글이 등록되기 전에
        // BookID에 해당하는 포스트 글이 1 증가시켜주기
        postService.countUpPostByBookId(dto.getBookId());
        
        attrs.addFlashAttribute("createdPostId", entity.getPostId());
        attrs.addFlashAttribute("userId", dto.getUserId());
        return "redirect:/post/list";
    }
    
    @Transactional(readOnly = true)
    @GetMapping({ "/detail", "/modify" })
    public void detail(@AuthenticationPrincipal UserSecurityDto userDto,
            Integer postId, String username ,Integer bookId, Model model) {
        log.info("detail(postId= {}, bookId={}, postWriter={})", postId, bookId, username);
        
        List<PostReadDto> recomList = postService.postRecomm(userDto.getUsername(), bookId);  // 1)
        
        Post p = postService.read(postId);
        Book b = bookService.read(bookId);
        

        
        if (username == null || userDto == null) { // 글 작성자와 유저가 다른 경우
            User u = userService.read(p.getUser().getId());
            Post entity = postService.read(postId); // 그 글의 조회수를 1올려줌.
            entity.update(postId, entity.getHit()+1);
            int hitCount = entity.getHit();
            model.addAttribute("hitCount", hitCount);
            model.addAttribute("user", u);
            
        } else { // 글 작성자와 유저가 같은경우
            User u = userService.read(username);
            int hitCount = postService.read(postId).getHit();
            model.addAttribute("hitCount", hitCount);
            model.addAttribute("user", u);
        }
        
         model.addAttribute("recomList",recomList );    // 2)
         model.addAttribute("post", p);
         model.addAttribute("book", b);
       
        
    }
   
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/delete")
    public String delete(Integer postId, RedirectAttributes attrs) {
        log.info("delete(postId={})",postId);
       
        replyService.deletePostIdWithAllReply(postId);
        postService.delete(postId);
        attrs.addFlashAttribute("deletedPostId", postId);
       
        // 삭제 완료 후에는 목록 페이지로 이동(redirect) - PRG 패턴
        return "redirect:/post/list";
    }
   
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/update")
    public String update(PostUpdateDto dto) {
        log.info("update(dto={})", dto);
       
        postService.update(dto);
       
        // 포스트 수정 성공 후에는 상세 페이지로 이동(redirect)
        return "redirect:/post/detail?postId=" + dto.getPostId()+"&bookId="+ dto.getBookId();
    }
   
    @GetMapping("/search")
    public String search(String type, String keyword, Model model) {
        log.info("search(type={}, keyword={})", type, keyword);
       
        List<Post> list = postService.search(type, keyword);
        model.addAttribute("list", list);
       
        return "/post/list"; // list.html 파일
    }

    
//    @PostMapping("/profile/imageInsert")    // create profileImage 
//    public String writepro(@AuthenticationPrincipal UserSecurityDto userSecurityDto, Model model, MultipartFile file) throws Exception{
//      
//      User user = userService.read(userSecurityDto.getId());
//      userService.write(user, file);
//      
//      model.addAttribute("message", "프로필 이미지 업로드 완료");
//      model.addAttribute("profileUrl", "/post/list");
//      
//      return "message";
//    }
    
    @Value("${site.book.upload.path}") // (예진) 절대 경로(외부 경로) 값 주입
    private String imageFilePath;
    
    // (예진) 프로필 이미지 업로드
    @PostMapping("/profile/imageUpdate")  
    public String profileImageUpdate(Integer id, MultipartFile file, HttpServletRequest request) throws Exception{
        
        String referer = request.getHeader("referer");  // 현재 페이지 주소
        log.info("CurrentUrl ={}", referer);
        String urlTemp = referer.toString().substring(21);  // localhost:8888 뒷 부분만 잘라냄
        log.info("urlTemp ={}", urlTemp);  


        userService.write(id, file);
        
        return "redirect:"+urlTemp;  // 현재 페이지로 리다이렉트 
    }
    
    @PostMapping("/postIntroUpdate")  // (예진) postIntro 수정
    public String postIntroUpdate(Integer id, String postIntro, HttpServletRequest request) {
         log.info("포스트인트로!={} : {}",id,postIntro);
        
         String referer = request.getHeader("referer");  
         String urlTemp = referer.toString().substring(21);  
        
        User user = userService.read(id);
        log.info("변경 전: 포스트인트로 ={}", user.getPostIntro());
        user.setPostIntro(postIntro);
        log.info("변경 후: 포스트인트로 ={}", user.getPostIntro());
        
        userRepository.save(user);
        
        return "redirect:"+urlTemp;
    }
   
   
   
}