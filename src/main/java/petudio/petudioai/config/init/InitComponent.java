package petudio.petudioai.config.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

@Component
@Slf4j
public class InitComponent {

    @Value("${local.repository.baseurl}")
    private String baseUrl;

    @PostConstruct
    public void initLocalStorage() {
        File folder = new File(baseUrl);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
}
