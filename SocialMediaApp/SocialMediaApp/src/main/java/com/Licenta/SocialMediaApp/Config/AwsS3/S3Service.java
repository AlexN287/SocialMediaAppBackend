package com.Licenta.SocialMediaApp.Config.AwsS3;

import com.Licenta.SocialMediaApp.Model.S3Model.S3ContentType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
public class S3Service {
    private final S3Client s3Client;
    private final S3Bucket s3Bucket;

    public S3Service(S3Client s3Client, S3Bucket s3Bucket){
        this.s3Client = s3Client;
        this.s3Bucket= s3Bucket;
    }

    public void putObject(String key, byte[] file){
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(s3Bucket.getBucket())
                .key(key)
                .build();
        s3Client.putObject(objectRequest, RequestBody.fromBytes(file));
    }

    public byte[] getObject(String buketName, String key) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(buketName)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);

        try {
            byte[] bytes = response.readAllBytes();
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String generateProfileImageKey(int userId, MultipartFile file)
    {
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.lastIndexOf('.') != -1) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }

        String key = "users/" + userId + "/" + S3ContentType.PROFILE_IMAGE + "/profileImage" + extension;

        return key;
    }

    public String generateGroupImageKey(int conversationId, MultipartFile file)
    {
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.lastIndexOf('.') != -1) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }

        String key = "conversations/" + conversationId + "/" + S3ContentType.CONVERSATION + extension;

        return key;
    }
}
