package petudio.petudioai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import petudio.petudioai.etc.Pair;
import petudio.petudioai.etc.callback.CheckedExceptionConverterTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AiCopyService {

    @Value("${local.repository.baseurl}")
    private String localStorageUrl;
    @Value("${petudio.main.server.url}")
    private String mainServerBaseUrl;
    private final CheckedExceptionConverterTemplate template;

    @Autowired
    public AiCopyService() {
        template = new CheckedExceptionConverterTemplate();
    }

    @Async
    public void createCopyPicture(List<MultipartFile> beforePictures, Long bundleId){

//        template.execute(() -> {
//            Thread.sleep(1000 * 10);
//            return null;
//        });

        File bundleFolder = new File(localStorageUrl + "/" + bundleId);
        bundleFolder.mkdirs();

        //before file, after file, multipart file
        List<Pair<Pair<File, File>, MultipartFile>> fileContainerList = beforePictures.stream()
                .map(beforePicture -> new Pair<>(
                        new Pair<>(
                                new File(localStorageUrl + "/" + bundleId + "/" + beforePicture.getOriginalFilename()),
                                new File(localStorageUrl + "/" + bundleId + "/" + createOriginalNameAfter(beforePicture.getOriginalFilename()))
                        ),
                        beforePicture
                )).toList();

        fileContainerList
                .forEach(fileContainer -> template.execute(() -> {
                    fileContainer.getFirst().getFirst().createNewFile();
                    fileContainer.getFirst().getSecond().createNewFile();
                    return null;
                }));


//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        MultiValueMap<String, Object> body = createBody(beforePictures, bundleId);
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                mainServerBaseUrl + "/api/callback",
//                HttpMethod.POST,
//                requestEntity,
//                String.class
//        );
//        log.info("response = {}", response);
    }

    private MultiValueMap<String, Object> createBody(List<MultipartFile> beforePictures, Long bundleId) {
        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("bundleId", bundleId);
        beforePictures
                .forEach(beforePicture -> {
                    InputStream is = template.execute(() -> beforePicture.getInputStream());
                    byte[] byteArray = template.execute(is::readAllBytes);
                    template.execute(() ->{is.close(); return null;});
                    String afterName = createOriginalNameAfter(beforePicture.getOriginalFilename());
                    ByteArrayResource byteArrayResource = new ByteArrayResource(byteArray) {
                        @Override
                        public String getFilename() {
                            return afterName;
                        }
                    };
                    body.add("afterPictures", byteArrayResource);
                });
        return body;
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
