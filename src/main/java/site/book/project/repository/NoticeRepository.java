package site.book.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import site.book.project.domain.Notices;
import site.book.project.domain.Post;

public interface NoticeRepository extends JpaRepository<Notices, Integer>{

    
    List<Notices> findByUserIdOrderByNoticeIdDesc(Integer userId);

}
