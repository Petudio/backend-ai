package petudio.petudioai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import petudio.petudioai.etc.callback.CheckedExceptionConverterTemplate;
import petudio.petudioai.service.AiCopyService;
import petudio.petudioai.service.dto.BeforePictureDto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/copy")
public class AiCopyController {

    private final AiCopyService aiCopyService;
    private final CheckedExceptionConverterTemplate template;
    @Value("${local.repository.baseurl}")
    private String baseurl;

    @Autowired
    public AiCopyController(AiCopyService aiCopyService) {
        this.aiCopyService = aiCopyService;
        template = new CheckedExceptionConverterTemplate();
    }




    @PostMapping("/upload")
    public String beforePictures(@RequestParam("beforePictures") List<MultipartFile> beforePictures, @RequestParam("bundleId") Long bundleId) {
        List<BeforePictureDto> beforePictureDtoList = beforePictures.stream()
                .map(beforePicture -> new BeforePictureDto(beforePicture.getOriginalFilename(), template.execute(beforePicture::getBytes)))
                .collect(Collectors.toList());

        aiCopyService.createCopyPicture(beforePictureDtoList, bundleId);
        return "ok";
    }
}
