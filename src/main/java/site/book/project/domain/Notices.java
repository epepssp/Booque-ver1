package site.book.project.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name ="NOTICES")
@SequenceGenerator(name = "NOTICES_SEQ_GEN", sequenceName = "NOTICES_SEQ", initialValue = 1, allocationSize = 1)
public class Notices {
    

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOTICES_SEQ_GEN")
    private Integer noticeId;
    
    private Integer userId;  // postWriter id;
    
    private Integer bookId;
    
    private Integer postId;
    
    private Integer replyId;
 
}
