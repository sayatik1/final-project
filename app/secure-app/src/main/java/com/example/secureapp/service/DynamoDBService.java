package com.example.secureapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class DynamoDBService {

    private final DynamoDbClient dynamoDbClient;

    public DynamoDBService(

            @Value("${aws.accessKeyId}") String accessKey,

            @Value("${aws.secretAccessKey}") String secretKey,

            @Value("${aws.region}") String region,

            @Value("${aws.s3.endpoint}") String endpoint

    ) {

        this.dynamoDbClient = DynamoDbClient.builder()

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

                .build();
    }

    public List<Map<String, String>> getUsers() {

        ScanRequest request = ScanRequest.builder()
                .tableName("users")
                .build();

        ScanResponse response = dynamoDbClient.scan(request);

        return response.items()
                .stream()
                .map(item -> Map.of(
                        "username", item.get("username").s()
                ))
                .toList();
    }
}
