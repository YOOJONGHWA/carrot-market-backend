package com.example.carrotmarketbackend.S3;

import com.example.carrotmarketbackend.Exception.S3ExceptionWrapper;
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
            throw new S3ExceptionWrapper(S3statusEnum.UPLOAD_FAIILE, e);
        } catch (SdkClientException e) {
            throw new S3ExceptionWrapper(S3statusEnum.INTERNAL_SERER_ERROR, e);
        }
    }

    public String createPresignedUrlForUpdate(String oldPath, String newPath) {
        try {
            // 새로운 파일을 업로드할 서명된 URL 생성
            String uploadUrl = createPresignedUrlForUpload(newPath);

            // 기존 파일을 삭제
            createPresignedUrlForDelete(oldPath);

            return uploadUrl;
        } catch (Exception e) {
            throw new S3ExceptionWrapper(S3statusEnum.INTERNAL_SERER_ERROR, e);
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
            System.out.println("Successfully deleted object: " + imagePath);
        } catch (S3Exception e) {
            throw new S3ExceptionWrapper(S3statusEnum.INTERNAL_SERER_ERROR, e);
        }
    }

    private String extractKeyFromUrl(String url) {
        // URL에서 경로 부분만 추출
        try {
            return new URL(url).getPath().substring(1); // URL에서 '/'를 제거하여 키를 추출
        } catch (MalformedURLException e) {
            throw new S3ExceptionWrapper(S3statusEnum.INVAILD_URL, e);
        }
    }
}
