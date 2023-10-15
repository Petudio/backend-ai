package petudio.petudioai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import petudio.petudioai.service.AiCopyService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/copy")
@RequiredArgsConstructor
public class AiCopyController {

    private final AiCopyService aiCopyService;
    @Value("${local.repository.baseurl}")
    private String baseurl;

    @PostMapping("/upload")
    public String beforePictures(@RequestParam("beforePictures") List<MultipartFile> beforePictures, @RequestParam("bundleId") Long bundleId) {
        aiCopyService.createCopyPicture(beforePictures, bundleId);
        return "ok";
    }
}
