package com.example.secureapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import software.amazon.awssdk.core.sync.RequestBody;

import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${bucket.name}")
    private String bucketName;

    public S3Service(

            @Value("${aws.accessKeyId}") String accessKey,

            @Value("${aws.secretAccessKey}") String secretKey,

            @Value("${aws.region}") String region,

            @Value("${aws.s3.endpoint}") String endpoint

    ) {

        this.s3Client = S3Client.builder()

                .endpointOverride(URI.create(endpoint))

                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        accessKey,
                                        secretKey
                                )
                        )
                )

                .region(Region.of(region))

                .forcePathStyle(true)

                .build();
    }

    public List<String> listFiles() {

        ListObjectsV2Response response =
                s3Client.listObjectsV2(builder ->
                        builder.bucket(bucketName)
                );

        return response.contents()
                .stream()
                .map(object -> object.key())
                .toList();
    }

    public void uploadFile(MultipartFile file) throws IOException {

        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(file.getOriginalFilename())
                        .checksumAlgorithm((String) null)
                        .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(file.getBytes())
        );
    }
}