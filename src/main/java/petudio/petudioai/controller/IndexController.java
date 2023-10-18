package petudio.petudioai.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import petudio.petudioai.etc.callback.CheckedExceptionConverterTemplate;
import petudio.petudioai.service.AiCopyService;
import petudio.petudioai.service.MainServerCallService;
import petudio.petudioai.service.dto.BundleServiceDto;
import petudio.petudioai.service.dto.PictureServiceDto;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class IndexController {

    private final AiCopyService aiCopyService;
    private final MainServerCallService mainServerCallService;
    private final CheckedExceptionConverterTemplate template;
    @Value("${local.repository.baseurl}")
    private String baseurl;

    @Autowired
    public IndexController(AiCopyService aiCopyService, MainServerCallService mainServerCallService) {
        this.aiCopyService = aiCopyService;
        this.mainServerCallService = mainServerCallService;
        template = new CheckedExceptionConverterTemplate();
    }


    @PostMapping("/copy/upload")
    public String beforePictures(@RequestParam("bundleId") Long bundleId, @RequestParam("beforePictures") List<MultipartFile> beforePictures) {
        List<PictureServiceDto> beforePictureServiceDtoList = beforePictures.stream()
                .map(beforePicture -> new PictureServiceDto(beforePicture.getOriginalFilename(), template.execute(beforePicture::getBytes)))
                .collect(Collectors.toList());
        BundleServiceDto beforeBundle = new BundleServiceDto(bundleId, beforePictureServiceDtoList);

        //@Async, callback
        aiCopyService.generateAfterPicture(beforeBundle)
                .thenApply(mainServerCallService::sendAfterPicturesToMainServer)
                .thenAccept(responseEntity -> log.info("response from main server = {}", responseEntity));

        return "ok";
    }
}
