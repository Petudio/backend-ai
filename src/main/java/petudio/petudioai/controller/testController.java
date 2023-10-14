package petudio.petudioai.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RestController
public class testController {

    @GetMapping("/hello")
    public String Hello() throws IOException {
        String pythonPath = "src/main/java/petudio/petudioai/python/test.py";
        ProcessBuilder processBuilder = new ProcessBuilder("python", pythonPath);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        return "hello";
    }

    @PostMapping("/upload")
    public ResponseEntity<String> receiveFile(@RequestParam("file") MultipartFile file) {
        System.out.println("file.getOriginalFilename() = " + file.getOriginalFilename());
        // 파일을 저장하거나 처리하는 코드 작성
        // ...

        return ResponseEntity.ok("File received successfully.");
    }
}
