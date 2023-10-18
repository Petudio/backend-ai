package petudio.petudioai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import petudio.petudioai.service.dto.BundleServiceDto;
import petudio.petudioai.service.dto.PictureServiceDto;

import java.util.List;

@Service
@Slf4j
public class MainServerCallService {

    @Value("${petudio.main.server.url}")
    private String mainServerBaseUrl;

    public ResponseEntity<String> sendAfterPicturesToMainServer(BundleServiceDto afterBundle) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = createBody(afterBundle);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
            mainServerBaseUrl + "/api/callback/addAfter",
                HttpMethod.POST,
                requestEntity,
                String.class
        );
    }

    private MultiValueMap<String, Object> createBody(BundleServiceDto afterBundle) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("bundleId", afterBundle.getBundleId());
        List<PictureServiceDto> afterPictureList = afterBundle.getPictures();
        afterPictureList
                .forEach(afterPicture -> {
                    ByteArrayResource byteArrayResource = new ByteArrayResource(afterPicture.getByteArray()) {
                        @Override
                        public String getFilename() {
                            return afterPicture.getOriginalPictureName();
                        }
                    };
                    body.add("afterPictures", byteArrayResource);
                });
        return body;
    }
}
