package petudio.petudioai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import petudio.petudioai.etc.callback.CheckedExceptionConverterTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@Service
@Slf4j
public class AiCopyService {

    @Value("${local.repository.baseurl}")
    private String baseUrl;

    @Async
    public void createCopyPicture(MultipartFile beforePicture, Long bundleId) {
        CheckedExceptionConverterTemplate template = new CheckedExceptionConverterTemplate();

        byte[] beforePictureBytes = template.execute(beforePicture::getBytes);

        template.execute(() -> {Thread.sleep(1000 * 10); return null;});

        //TODO restTemplate to backend
    }


    private String createOriginalNameAfter(String originalName) {
        int pos = originalName.lastIndexOf(".");
        if (pos == -1) {
            return originalName + "_after";
        }
        String prefix = originalName.substring(0, pos);
        String ext = originalName.substring(pos + 1);
        return prefix + "_after" + "." + ext;
    }
}
