/**
 * 
 */
window.addEventListener('DOMContentLoaded', () => {
    
     // (예진) 이미지 업데이트 
     
     tempImage();
    
     function tempImage() {
        
        const id = document.querySelector('#id').value;
        console.log("tempImage: id", id);
        
        axios
        .get('/tempView/' + id)  
        .then(response => { viewImage(response.data) } )
        .catch(err => { console.log(err) })

    };
       
       function viewImage(data) {

        const divProfileImage = document.querySelector('#divProfileImage');
            
        axios
        .get('/view/'+ data.fileName)  
        .then(response => { console.log('성공!!') } )
        .catch(err => { console.log(err) })
        
        let img ='';
        if(data.fileName) {
           img +=  `<img src="/view/${data.fileName}" width=200px />`;
        } else {
           // 설정한 프사 없을 경우 디폴트 이미지
           img +=  `<img src="/view/10.png" width=200px />`;
        }
        divProfileImage.innerHTML = img;
       
    };     
   
    // profile form HTML 요소를 찾음.
    const profileForm = document.querySelector('#profileForm');

    // 프로필 이미지 변경 버튼 찾아서 이벤트 리스너 등록
    const btnProfileUpdate = document.querySelector('#btnProfileUpdate');
    btnProfileUpdate.addEventListener('click', submitForm);
    
    function submitForm(event){
        event.preventDefault();
        
        const result = confirm('프로필 사진을 변경하시겠습니까?');
        if(result) {
         
            profileForm.action = '/post/profile/imageUpdate';
            profileForm.method= 'post';
            profileForm.submit();
        }
        
    tempImage();
       
    }


  
});