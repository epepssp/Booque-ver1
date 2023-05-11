# Booque-ver1
<p align="center"><img width="350" alt="booque_logo" src="https://github.com/epepssp/MyGit/assets/118948099/0ad8d0b0-6d41-4c17-aaa0-923289eb4728"></p>
<p align="center"><img width="350" alt="logo-1" src="https://github.com/epepssp/MyGit/assets/118948099/185aeec0-2bdc-4f79-8e0f-f9378d41c1c8"></p>

## 개요
> #### 아이티윌 자바 134기 하찮은 진정혜 조 6인이 함께한 팀 프로젝트
> #### 개발기간: 2022년 11월 21일 ~ 2022년 12월 23일<br>

## 프로젝트 소개

뭐라고 적지 책과 유저 유저와 유저를 이어주는 블로그기능..


## 사용 기술 및 개발환경
+ Java
+ Spring Boot
+ HTML
+ CSS
+ JavaScript


## 주요기능 소개

- 로그인


- 

- 개인 블로그


- 장바구니 결제


- 마이페이지





## 구현 기능
### 저는 블로그와 Post를 담당했습니다.

```
  프로젝트 내 유일한 포스트 팀(1명)의 팀원으로
  블로그와 Post 기능을 처음부터 끝까지, 프론트엔드부터 백엔드까지 전적으로 담당하여 구현했습니다.

```  


+ 블로그 리뷰 게시글 CRUD 기능
    Create 

> BookDetailController.java 일부

```java
  @GetMapping("/post/create")
    public String create(@AuthenticationPrincipal UserSecurityDto userSecurityDto, Integer id, Model model) {

          User user = userService.read(userSecurityDto.getId()); // 현재 로그인 유저
          model.addAttribute("user", user);
          
          Book book = bookService.read(id);
          model.addAttribute("book", book);
     
        return "post/create";
    }
```

> PostController.java 일부

```java
 @PostMapping("/create")
    public String create(PostCreateDto dto, RedirectAttributes attrs) {
        log.info("create(dto ={})", dto);   
      
        Post entity = postService.create(dto); 
        
        // 리뷰순에서 사용할 것 - 글이 등록되기 전에
        // BookID에 해당하는 포스트 글이 1 증가시켜주기
        postService.countUpPostByBookId(dto.getBookId());
        
        attrs.addFlashAttribute("createdPostId", entity.getPostId());
        attrs.addFlashAttribute("userId", dto.getUserId());
        return "redirect:/post/list";
    }
```

> PostService.java 일부

```java
 @Transactional
    public Post create(PostCreateDto dto) {
        Book book = bookRepository.findById(dto.getBookId()).get();
        User user = userRepository.findById(dto.getUserId()).get();

        if( book.getBookScore() == null) { //별점 계산
        	book.update(25);
        } else {
        	Integer score = book.getBookScore() + dto.getMyScore()*10;
        	book.update(score/2);
        }
        
        Post entity = postRepository.save(dto.toEntity(book,user));
        return entity;
    }
```

  - Read

> PostController.java 일부

```java
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
             model.addAttribute("user", u);
             
            Post entity = postService.read(postId); // 그 글의 조회수를 1올려줌.
            entity.update(postId, entity.getHit()+1);
            int hitCount = entity.getHit();
            model.addAttribute("hitCount", hitCount);
                      
        } else { // 글 작성자와 유저가 같은경우
            User u = userService.read(username);      
            model.addAttribute("user", u);
            
            int hitCount = postService.read(postId).getHit();
            model.addAttribute("hitCount", hitCount);
        }
        
         model.addAttribute("recomList",recomList );    // 2) 다른 유저 리뷰글 추천 리스트
         model.addAttribute("post", p);
         model.addAttribute("book", b);     
        
    }
```

> PostService.java 일부

```java
@Transactional(readOnly = true)
    public Post read(Integer postId) {
        log.info("read(postId = {})", postId);
        
        return postRepository.findById(postId).get();
    }
```


3. Update/Delete

> PostController.java 일부

```java
   @PostMapping("/update")
    public String update(PostUpdateDto dto) {

        postService.update(dto);
       
        // 포스트 수정 성공 후에는 상세 페이지로 이동(redirect)
        return "redirect:/post/detail?postId=" + dto.getPostId()+"&bookId="+ dto.getBookId();
    }

   @PostMapping("/delete")
    public String delete(Integer postId, RedirectAttributes attrs) {

        replyService.deletePostIdWithAllReply(postId);
        postService.delete(postId);
        attrs.addFlashAttribute("deletedPostId", postId);
       
        // 삭제 완료 후에는 목록 페이지로 이동(redirect) - PRG 패턴
        return "redirect:/post/list";
    }
```

> PostService.java 일부

```java
 @Transactional // readOnly = false(기본값)
    public void update(PostUpdateDto dto) {
       
        Post entity = postRepository.findById(dto.getPostId()).get(); // (1)
        entity.update(dto.getTitle(), dto.getPostContent()); // (2)
   }

   public void delete(Integer postId) {
     
        postRepository.deleteById(postId);
       
    }
```

---

+ 블로그 전체 글 리스트

> PostController.java 일부

```java
 @Transactional(readOnly = true)
    @GetMapping("/list")
    public String list(@AuthenticationPrincipal UserSecurityDto userSecurityDto, String postWriter, Model model) {
        log.info("list()");

        User user = null; 
        List<PostListDto> postList = new ArrayList<>();
        
        if (postWriter == null) { // 로그인 한 유저가 홈에서 자신의 블르그 접근시     
            
            user = userService.read(userSecurityDto.getId());        
            postList = postService.postDtoList(userSecurityDto.getId());
            
        } else if (postWriter != null) { // 그 외의 모든 루트로 접근시. ex) 다른 유저 블로그 접근, 로그인 안 한 상태에서 다른 유저 블로그 방문 등
            
            user = userService.read(postWriter);
            postList = postService.postDtoList(user.getId());
            
            Integer uId2 = userSecurityDto.getId();
            model.addAttribute("uId2", uId2);
            model.addAttribute("nick2", userSecurityDto.getNickName());
        }
       
        // (예진) 포스트 create 날짜랑 오늘 날짜랑 같으면 new 아이콘 띄우기 = 새 글!
        LocalDate now = LocalDate.now();
        String day= now.toString().substring(8);
        
        model.addAttribute("day", day);
        model.addAttribute("user", user);      
        model.addAttribute("list", postList);
        model.addAttribute("books", books);
                
        return "/post/list";
    }
```

> PostService.java 일부

```java
 @Transactional(readOnly = true)
    public List<PostListDto> postDtoList(Integer userId) {
        List<Post> list = postRepository.findByUserIdOrderByCreatedTimeDesc(userId);
         
        List<PostListDto> dtoList = new ArrayList<>();
        PostListDto dto = null;
        
        for (Post post : list) {
            Post p = post;
            List<ReplyReadDto> rpiList = replyService.readReplies(p.getPostId());
             
            dto = PostListDto.builder()
            .userId(p.getUser().getId())
            .postId(p.getPostId())
            .title(p.getTitle())
            .postWriter(p.getPostWriter())
            .postContent(p.getPostContent())
            .bookId(p.getBook().getBookId())
            .bookImage(p.getBook().getBookImage()).modifiedTime(p.getModifiedTime())
            .createdTime(p.getCreatedTime())
            .replyCount(rpiList.size())
            .hit(p.getHit())
            .build();
      
            dtoList.add(dto);           
        }
   
      return dtoList;
    }
```

> list.html 일부

```html
 <!-- 예진: 작성한 리뷰가 없을 때.  -->   
 <div th:if="${ #lists.isEmpty(list) }">작성한 글이 없습니다.<span style="font-weight: bold; font-size: 20px;">첫 리뷰를 작성해보세요!!</span></div>
 
 <!-- 예진: 작성한 리뷰가 있을 때. -->
 <div th:if="${ !#lists.isEmpty(list) }">
 
    <!-- 사용자 작성 리뷰 리스트(테이블) -->
    <table class="table">
    <thead>
        <tr class="border-bottom border-4 border-dark">
            <td class="col-9">책 리뷰 목록</td>
            <td></td>
        </tr>
    </thead>
    <tbody>
        <tr th:each="post : ${ list }">
            <td class="py-5" style="cursor: pointer;" th:onclick="|location.href='@{ /post/detail?postId={postId}&bookId={bookId} (postId = ${ post.postId }, bookId = ${ post.bookId } )}'|">
             
                <div>
                    <span th:text="${ post.postId }"></span>
                    <strong><a th:href="@{ /post/detail?postId={postId}&bookId={bookId} (postId = ${ post.postId }, bookId = ${ post.bookId } )}" th:text="${ post.title }" th:postId="${post.postId}" th:username="${#authentication.name}" onclick="postHitCountUp(this.getAttribute('postId'), this.getAttribute('username'));"></a></strong>
                    <!-- 예진:  오늘 작성한 글 new 아이콘 보여주기 -->
                    <span th:if="${ day } == ${ #temporals.format(post.createdTime, 'dd') }">
                        <span><img class="blink" src="/images/new.png" width="50" height="50" ></span>
                    </span>
                     
                     <!-- 예진: 댓글 수 10개 이상인 글 hot 아이콘 보여주기 -->
                    <span th:if="${ post.replyCount } > 9 ">
                        <span><img class="blink" src="/images/promotional.png" width="50" height="40" ></span>
                    </span>
                </div>
                
                <div class="box">
                <small class="w-100 postContent" id="postContent" th:text="${ post.postContent }"></small>
                </div>
                
                <div>
                        <small th:text="${ #temporals.format(post.modifiedTime, 'yyyy-MM-dd HH:mm:ss') }" style="color:gray;"></small>
                        <small>조회수 <span style="color:blue; font-weight: bold;" th:text="${post.hit}"></span></small>
                        <small>댓글수 <span style="color:red; font-weight: bold;"th:text="${ post.replyCount }"></span></small>                     
                </div>
            </td>
            <td style="text-align: center;">
                <a style="margin-left: 18px;" th:href="@{ /detail?id={bookId} (bookId = ${ post.bookId })}" th:bookId="${post.bookId}" th:username="${#authentication.name}" onclick="viewHitUp(this.getAttribute('bookId'),this.getAttribute('username'));">
                    <img th:src="${ post.bookImage }" class="img-fluid rounded-start" style="width:15rem">
                </a>
            </td>
       </tr>
    
    </tbody>
    </table>
    </div>
```

---
+ 리뷰 글 또는 댓글 작성자 클릭 시 해당 유저 블로그로 이동

---
+ 아이콘 추가
1. 새 게시글 ‘NEW’ 아이콘 추가

> PostController.java 일부

```java
```

> list.html 일부

```html
         <!-- 예진:  오늘 작성한 글 new 아이콘 보여주기 -->
          <span th:if="${ day } == ${ #temporals.format(post.createdTime, 'dd') }">
               <span><img class="blink" src="/images/new.png" width="50" height="50" ></span>
          </span>
```

2. 댓글 10 이상 게시글에 ‘HOT’ 아이콘 추가
> list.html 일부

```html
         <!-- 예진: 댓글 수 10개 이상인 글 hot 아이콘 보여주기 -->
         <span th:if="${ post.replyCount } > 9 ">
              <span><img class="blink" src="/images/promotional.png" width="50" height="40" ></span>
         </span>
```
