package petudio.petudioai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
public class testController {

    @GetMapping("/hello")
    public String Hello() throws IOException {
        String pythonPath = "/Users/dongyullee/IntelliJ/petudioai/src/main/java/petudio/petudioai/python/test.py";
        ProcessBuilder processBuilder = new ProcessBuilder("python", pythonPath);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        return "hello";
    }

    @GetMapping("/wow")
    public String Wow() {
        return "wow";
    }
}
