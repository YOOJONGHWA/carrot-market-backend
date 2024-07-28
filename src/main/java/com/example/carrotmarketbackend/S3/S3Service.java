package com.example.carrotmarketbackend.S3;

import com.example.carrotmarketbackend.Enum.S3statusEnum;
import com.example.carrotmarketbackend.Exception.S3ExceptionHandler;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;
import software.amazon.awssdk.core.exception.SdkClientException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;
    private final S3Presigner s3Presigner;
    private final software.amazon.awssdk.services.s3.S3Client s3Client; // S3 클라이언트 추가

    public String createPresignedUrlForUpload(String path) {
        try {
            var putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(path)
                    .build();
            var preSignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(3))
                    .putObjectRequest(putObjectRequest)
                    .build();
            return s3Presigner.presignPutObject(preSignRequest).url().toString();
        } catch (S3Exception e) {
            throw new S3ExceptionHandler(S3statusEnum.UPLOAD_FAIL, e);
        } catch (SdkClientException e) {
            throw new S3ExceptionHandler(S3statusEnum.INTERNAL_SERVER_ERROR, e);
        }
    }

    public void createPresignedUrlForUpdate(String oldPath, String newPath) {
        try {
            // 새로운 파일을 업로드할 서명된 URL 생성
            String uploadUrl = createPresignedUrlForUpload(newPath);

            // 기존 파일을 삭제
            createPresignedUrlForDelete(oldPath);

        } catch (S3Exception e) {
            throw new S3ExceptionHandler(S3statusEnum.INTERNAL_SERVER_ERROR, e);
        }
    }

    public void createPresignedUrlForDelete(String path) {
        String imagePath = extractKeyFromUrl(path);
        try {
            var deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(imagePath)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new S3ExceptionHandler(S3statusEnum.INTERNAL_SERVER_ERROR, e);
        }
    }

    private String extractKeyFromUrl(String url) {
        // URL에서 경로 부분만 추출
        try {
            return new URL(url).getPath().substring(1); // URL에서 '/'를 제거하여 키를 추출
        } catch (MalformedURLException e) {
            throw new S3ExceptionHandler(S3statusEnum.INVALID_URL, e);
        }
    }
}
