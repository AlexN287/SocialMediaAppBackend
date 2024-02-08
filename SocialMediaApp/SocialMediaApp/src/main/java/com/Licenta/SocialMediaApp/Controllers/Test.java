package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Config.AwsS3.S3Service;
import com.Licenta.SocialMediaApp.Model.User;
import com.Licenta.SocialMediaApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping
public class Test {
    @Autowired
    UserRepository userRepository;

    @Autowired
    S3Service s3Service;

    @GetMapping
    public List<User> getTest()
    {
        return userRepository.findAll();
    }

    /*@PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("key") String key) {
        try {
            byte[] fileContent = file.getBytes();
            s3Service.putObject("social-media-bucket1",key, fileContent);
            return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }*/

/*    @GetMapping("/upload")
    public List<User> uploadFile(){
        return userRepository.findAll();
    }*/
}
