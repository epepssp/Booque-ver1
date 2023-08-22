# 📚 Booque ver1
<p align="center"><img width="300" alt="booque_logo" src="https://github.com/epepssp/Booque-ver1/assets/118948099/c3f7fd4a-37ec-417d-ab0f-361c3f9aee0b"></p>
<p align="center"><img width="300"  alt="부끄1" src="https://github.com/epepssp/Booque-ver1/assets/118948099/8a1b6368-f53d-4896-b42a-3f63fd08c2fd"></p>
   <br>
   
## 개요
**인원** 아이티윌 자바 134기 "하찮은 진정혜" 조 6인이 함께한 첫번째 팀 프로젝트<br>
**일정** 개발기간: 2022년 11월 21일 ~ 2022년 12월 23일<br>

## 프로젝트 소개

팀원들의 공통 관심사를 바탕으로 선택하게 된 "책" 이라는 주제<br>
리뷰와 개인 블로그를 통해 책과 유저 그리고 유저와 유저를 이어주는 도서 판매 웹 사이트 구현 (예: yes24, 교보문고)
<br>

## 사용 기술 및 개발환경
+ Java
+ Spring Boot
+ HTML
+ CSS
+ JavaScript

## 주요기능 소개
- 로그인 
- 메인
  - 책 분류, 책 추천
  - 서치, 페이징, 조회수
- 판매
  - 책 상세 페이지, 위시리스트 등록, 장바구니 담기, 주문, 결제
- 개인 블로그(리뷰)
   - 프로필, 리뷰 작성, 다른 사람의 블로그 방문, 리뷰 보기, 댓글 작성
- 마이페이지
  -    

<br>

## 나의 구현 기능
+ ### 블로그(Post) CRUD 기능
  + #### Create 

  > BookDetailController.java 일부

  ```java
     @GetMapping("/post/create")  // 책 상세보기 페이지에 리뷰(Post) 작성 버튼 
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

        if( book.getBookScore() == null) { // 글 작성과 동시에 별점 평균 계산하여 반영
        	book.update(25);
        } else {
        	Integer score = book.getBookScore() + dto.getMyScore()*10;
        	book.update(score/2);
        }
        
        Post entity = postRepository.save(dto.toEntity(book,user));
        return entity;
     }
  ```
  + #### Detail
       
  > PostController.java 일부
  
  ```java
     
        @Transactional(readOnly = true)
        @GetMapping({ "/detail", "/modify" })
        public void detail(@AuthenticationPrincipal UserSecurityDto userDto,
                 Integer postId, Integer bookId, Model model) {
  
             Post post = postService.read(postId);
        
             // 그 책(같은 책)에 대한 다른 유저들의 리뷰들 보여주기 위한 리스트 
             // 해당 bookId의 모든 리뷰 중에서, 현재 detail 페이지 글 빼고 다른 글들 보여줌 
             List<PostReadDto> recomList = postService.postRecomm(post.getUser().getUsername(), bookId);
        
              int hitCount = 0; // 조회수
              if (userDto != null && post.getPostWriter().equals(userDto.getUsername())) { // 글 작성자와 유저가 같으면 조회수 이미 카운트 된 상태 -> 중복 카운트 X
                  hitCount = postService.read(postId).getHit();
            
              } else { // 글 작성자와 유저가 다르면
                  post.update(postId, post.getHit()+1);  // 그 글의 조회수를 +1 올려줌
                  hitCount = post.getHit();
              }
        
                 model.addAttribute("post", post);
                 model.addAttribute("user", post.getUser());
                 model.addAttribute("book", bookService.read(bookId));
                 model.addAttribute("recomList",recomList); 
                 model.addAttribute("hitCount", hitCount);
          
        }
   ```
  
   + #### Update/Delete

   > PostController.java 일부

   ```java
      @PostMapping("/update")
      public String update(PostUpdateDto dto) {
           postService.update(dto);
           return "redirect:/post/detail?postId=" + dto.getPostId()+"&bookId="+ dto.getBookId();
      }

      @PostMapping("/delete")
      public String delete(Integer postId, RedirectAttributes attrs) {
           replyService.deletePostIdWithAllReply(postId);  // 리플 먼저 삭제 후
           postService.delete(postId);  // 리뷰(포스트) 삭제
           attrs.addFlashAttribute("deletedPostId", postId);
           return "redirect:/post/list";
      }
   ```
  <br>
  
+ ### 게시판 API(Summernote) 이용한 다양한 Editor 기능 ⭐
<br>
<div align="center"><img src="https://github.com/epepssp/test/assets/118948099/0cf55421-b7b9-49db-be7a-aeaa80965347" width="700" height="450" alt="뿌끄1에디터15"></div>
<br>

  > create.html

  ```create.html

       <!-- summernote API -->
       <head>
          <link
           href="https://cdn.jsdelivr.net/npm/summernote@0.8.18/dist/summernote.min.css"
           rel="stylesheet">
          <script
           src="https://cdn.jsdelivr.net/npm/summernote@0.8.18/dist/summernote.min.js"></script>
       </head>
    

       <!-- 글 작성 form -->
       <form method="post">
          <textarea class="summernote" id="postContent" name="postContent"></textarea>
       </form>


       <!-- 하단 script 영역 -->
       <script>
          $('.summernote').summernote({
              height: 600,
              lang: "ko-KR"
          });
       </script>
  
   ```
   <br>

  + ### post - create.html / detail.html / modify.html / list.html 작성

  + #### html 왼쪽 프로필 영역 - 모두 공통 
  ```html

         <div class="col-2"><!-- 메인 왼쪽: 프로필 영역 start -->
           <div class="card mx-2 my-2" style="width: 20rem; height:auto;">    
               <a th:href="@{ /myPage }"> <!-- 프로필 사진 클릭하면 마이페이지로 이동 -->
                  <img th:src="${ user.userImage }" width="220px" height="280" class="img-fluid rounded-start">
               </a>       
           </div>
         
           <div class="card-body" style="text-align: left;">
               <div class="border-bottom mb-2 border-dark">
                    <span class="card-title" th:text="${ '&nbsp;&nbsp;'+ user.nickName }" style="font-weight: bold;"></span>
                    <span style="font-size: 16px;" th:text="${ '('+ user.username +')' }"></span>
                </div>
                <div class="mb-4">
                    <span th:text="${user.postIntro}" ></span>
                </div>
           </div>
        </div><!-- 프로필 영역 end -->
    
        <button onclick="location.href='/post/list'" type="button" class="w3-button w3-grey border rounded">▶ 전 체 목 록 보 기</button>
   ```

   > detail.html

   ```html

          <!-- detail.html 왼쪽 공통 프로필 영역 밑에 같은 책에 대한 다른 유저들의 리뷰 보여주는 리스트 추가  -->
          <div class="fw-bold" style="text-align: left; font-size: small;" th:text="${ book.bookName +', ' }"></div>
          <div class="fw-bold mb-1" style="text-align: left; color:gray; font-size: 12px;">다른 부끄들은 이런 리뷰를 남겼어요!</div>

          <table class="table-hover">
             <tbody>
               <tr th:each=" postReadDto : ${ recomList }"
                   style="cursor: pointer;"
                   th:onclick="|location.href='@{ /post/detail?postId={postId}&bookId={bookId} (postId = ${ postReadDto.postId }, bookId = ${ postReadDto.bookId } )}'|">
                   <td><a
                        th:href="@{ /post/detail?postId={postId}&bookId={bookId} (postId = ${ postReadDto.postId }, bookId = ${ postReadDto.bookId } )}">
                            <img class="m-2" style="width: 35px; height: 35px;" alt="" th:src="${postReadDto.userImage}">
                       </a> 
                    </td>
                    <td>
                        <div><small th:text="${ postReadDto.writer }" style="color:gray;"></small></div>
                        <small style="font-weight: bold;" th:text="${ postReadDto.title } + '(별점 ' + ${ postReadDto.myScore } + ')'"></small>
                     </td>
                  </tr>
               </tbody>
            </table>


          <!-- 맨 오른쪽: 도서 정보 블록 start-->
          <div class="card mx-2 my-2 sticky" style="width: 30rem;">    
              <a th:href="@{ /detail?id={bookId} (bookId = ${ book.bookId })}">
                <img th:src="${ book.bookImage }" class="card-img-top" />
              </a>
              <div class="card-body" style="text-align: left;">
                  <div class="my-2">
                      <small class="d-inline-flex px-2 border border-1 rounded text-secondary">
                          <span th:text="${ book.bookgroup }"></span><span> / </span><span th:text="${ book.category }"></span>
                      </small>
                  </div>
                  <h4 class="card-title" th:text="${ book.bookName }" style="font-weight: bold;"></h4>
                  <div th:text="${ book.author }"></div>
                  <div th:text="${ book.publisher }"></div>
                  <div th:text="${ book.publishedDate }"></div>
                  <div th:text="|${#numbers.formatInteger(book.prices, 0, 'COMMA')}원|"></div>
                  <div><a>별점</a> <span id="score" th:text="${ book.bookScore/10 }"></span>
                       <span th:text="${ book.bookScore/10 }"></span>
                  </div>
                  <div><a>내 별점</a> <span id="scoreM" th:text="${ post.myScore }"></span>
                       <span th:text="${ post.myScore }"></span>
                  </div>
                  <div>오늘 주문하면 내일 도착</div>
                  <button type="button" class="w-50 btn btn-dark" id="btnGoCart">장바구니</button>
              </div>
          </div>
          <!-- 도서 정보 블록 end -->
   ```
   <br>

  + ### 블로그 방문 가능한 경우의 수 - 총 4가지  ⭐ 
<br>
<div align="center">    
<img style="display: inline-block;" width="380" alt="경우1" src="https://github.com/epepssp/test/assets/118948099/d5549c0b-a3b1-43a0-9cbc-85a10d905593">
<img style="display: inline-block;" width="382" alt="경우2" src="https://github.com/epepssp/test/assets/118948099/5b7e1f45-5469-4bb5-9144-1fc4c574e276">
<img style="display: inline-block;" width="380" alt="경우3" src="https://github.com/epepssp/test/assets/118948099/da184d66-4924-4fb2-9a13-1c94e2232c15">
<img style="display: inline-block;" width="380" alt="경우4" src="https://github.com/epepssp/test/assets/118948099/a088a2c0-6c55-4412-a262-44857722209e">
</div>
<div align="center" style="font-size:8px;">클릭시 커짐</div>
<br>

  ```
     1. 로그인 하지 않아도 리뷰 클릭해서 다른 유저 블로그 방문 가능
     2. 로그인 한 상태에서 리뷰 클릭해서 다른 유저 블로그 방문 가능
     3. 내가 작성한 리뷰 클릭해서 나의 블로그 방문 가능
     4. 상단바 메뉴 > 내 블로그 > 내 블로그 방문 가능

     4가지 경우에 따라, 현재 로그인 한 유저(상단바)와 누구의 블로그인지(블로그 프로필에 그릴 유저)는 다를 수 있음을 고려

  ```
  
  > PostController.java 일부 - 수정한 코드

  ```java

        @Transactional(readOnly = true)
        @GetMapping("/list")
        public String list(@AuthenticationPrincipal UserSecurityDto userSecurityDto, String postWriter, Model model) {
                        // @AuthenticationPrincipal 애너테이션 사용하여 현재 로그인 상태인 유저 정보 찾을 수 있음

           List<PostListDto> postList = new ArrayList<>();
        
           if (postWriter == null) {  // 1, 2, 3 경우
               model.addAttribute("user", userService.read(userSecurityDto.getId()));  
               postList = postService.postDtoList(userSecurityDto.getId());
     
           } else {  // 4 경우
               model.addAttribute("user", userService.read(postWriter));  
               postList = postService.postDtoList(userService.read(postWriter).getId());
           }
        }
     
   ```
   
   <br>

   + ### 아이콘 추가 + Blink Effect
   + #### NEW 아이콘 추가 

   > PostController.java  
   ```java

        // 포스트 create 날짜랑 오늘 날짜랑 같으면 새로 작성된 글 - new 아이콘 
        LocalDate now = LocalDate.now();
        String day= now.toString().substring(8);
        
        model.addAttribute("day", day);
   ```

   + #### HOT 아이콘 추가
   > PostService
 
   ```service

       @Transactional(readOnly = true)
       public List<PostListDto> postDtoList(Integer userId) {
          List<PostListDto> dtoList = new ArrayList<>();
          PostListDto dto = null;

          List<Post> list = postRepository.findByUserIdOrderByCreatedTimeDesc(userId);
          for (Post post : list) {
              Post p = post;

            // HOT 아이콘 구현 위해 포스트 리플 정보 추가
            // 해당 Post의 리플 목록 구하고 
            List<ReplyReadDto> rpiList = replyService.readReplies(p.getPostId());
             
              dto = PostListDto.builder()
                            .userId(p.getUser().getId()).postId(p.getPostId())
                            .title(p.getTitle()).postWriter(p.getPostWriter())
                            .postContent(p.getPostContent()).hit(p.getHit())
                            .bookId(p.getBook().getBookId()).bookImage(p.getBook().getBookImage())
                            .modifiedTime(p.getModifiedTime()).createdTime(p.getCreatedTime())

                             // 리플 갯수 Count 한다 
                            .replyCount(rpiList.size())
                            .build();
            
               dtoList.add(dto);           
           }  
            return dtoList;
        }
   ```

   + #### Blink Effect
   > list.html
   ```html

           <style>  
              @keyframes  blink-effect {
                   50% {
                   opacity: 0;
             }
            }

             .blink {
                animation: blink-effect 1s step-end infinite;
                animation-timing-function: step-start, step-end;
             }
           </style>

           <!-- 오늘 작성한 글 new 아이콘 보여주기 -->
           <span th:if="${ day } == ${ #temporals.format(post.createdTime, 'dd') }">
               <span><img class="blink m-1" src="/images/new.png" width="38" height="38" ></span>
           </span>
                     
           <!-- 댓글 수 10개 이상인 글 hot 아이콘 보여주기 -->
           <span th:if="${ post.replyCount } > 9 ">
               <span><img class="blink m-1" src="/images/promotional.png" width="30" height="30" ></span>
           </span>
    ```
