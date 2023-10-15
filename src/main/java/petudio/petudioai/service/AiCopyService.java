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
import petudio.petudioai.service.dto.BeforePictureDto;

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
    @Value("${python.command}")
    private String pythonCommand;
    @Value("${python.script.baseurl}")
    private String pythonScriptBaseUrl;
    private final CheckedExceptionConverterTemplate template;

    @Autowired
    public AiCopyService() {
        template = new CheckedExceptionConverterTemplate();
    }

    //@Async 사용시 파일 관리에 이상한게 생김, ex : readAllBytes에서 UncheckedIoException, cannot delete exeption 발생
    //https://stackoverflow.com/questions/36565597/spring-async-file-upload-and-processing
    //이유 : 
    //multi part file의 관리 : file을 input으로 받음 -> 내 로컬환경의 임시저장소에 파일 형태로 저장해놓음 -> 컨트롤러가 종료될때(서블릿이 종료될때) 해당 파일을 delete
    //따라서 async service에서 multipartfile을 인자로 받게되면 컨트롤러가 종료되면서 해당 multipart file을 삭제하려고 하는데 service에서는 해당 multipart file.getBytes를 하게 되면서 race condition 발생
    //그러므로 controller단에서 byteArray로 전부 변환한 후에 async service에 넘겨줘야 한다.
    @Async
    public void createCopyPicture(List<BeforePictureDto> beforePictures, Long bundleId){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = createBody(beforePictures, bundleId);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        //sleep 10 secs using python
        String pythonPath = pythonScriptBaseUrl + "/sleep.py";
        ProcessBuilder processBuilder = new ProcessBuilder(pythonCommand, pythonPath);
        template.execute(() -> {
            Process process = processBuilder.start();
            int i = process.waitFor();
            log.info("exit code = {}", i);
            return null;
        });


        ResponseEntity<String> response = restTemplate.exchange(
                mainServerBaseUrl + "/api/callback/addAfter",
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        log.info("response = {}", response);
    }

    private MultiValueMap<String, Object> createBody(List<BeforePictureDto> beforePictures, Long bundleId) {
        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("bundleId", bundleId);
        beforePictures
                .forEach(beforePicture -> {
                    String afterName = createOriginalNameAfter(beforePicture.getOriginalPictureName());
                    ByteArrayResource byteArrayResource = new ByteArrayResource(beforePicture.getByteArray()) {
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
