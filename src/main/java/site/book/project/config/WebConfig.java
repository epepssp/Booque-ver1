package site.book.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${profiles.absolute.path}")    // (예진) 절대 경로(application.properties에 설정한 외부 경로) 값 주입
    private String uploadPath;
    
//    @Value("${upload.path}")    // (예진) 절대 경로(application.properties에 설정한 외부 경로) 값 주입
//    private String upload;

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    
        registry.addResourceHandler("/show/**")
        .addResourceLocations(uploadPath);      
        
}
}
