/**
 * 
 */

window.addEventListener('DOMContentLoaded', () => {
    // (예진)
    showImage();
    
     function showImage() {
        
        const id = document.querySelector('#id').value;
        
        axios
        .get('/updateImage/' + id)  
        .then(response => { viewUpdatedImage(response.data) } )
        .catch(err => { console.log(err) })
        
    };
   
   function viewUpdatedImage(data) {
       console.log(" viewUpdatedImage"+data);
       const divProfileImage = document.querySelector('#divProfileImage');
   
       let str='';

           str +=   `<img class="img-fluid rounded-start" src="${data.userImage}" id="profileImage" width="150px" height="200"/>`;
       divProfileImage.innerHTML = str;
   };
 
   
    // profile form HTML 요소를 찾음.
    const profileForm = document.querySelector('#profileForm');

    // 프로필 이미지 변경 버튼 찾아서 이벤트 리스너 등록
    const btnProfileUpdate = document.querySelector('#btnProfileUpdate');
    btnProfileUpdate.addEventListener('click',uup);
    
    function uup(){

        const result = confirm('프로필 사진을 변경하시겠습니까?');
        if(result) {
            console.log(profileImage)
            profileForm.action = '/post/profile/imageUpdate';
            profileForm.method= 'post';
            profileForm.submit();
        }
        
    showImage();
       
    }
    
    

  
  });