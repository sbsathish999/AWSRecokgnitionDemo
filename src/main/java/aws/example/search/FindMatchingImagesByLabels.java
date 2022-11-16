package aws.example.search;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.List;

/* This program to find matching images with its matching labels with query string using Detect Labels API from AWS Recokgnition services*/

public class FindMatchingImagesByLabels {

    public static void main(String args[]) {
        String bucket = "1-bucket-rekognition-demo-1";
        String queryString = "head";
        Region region = Region.US_EAST_1;
        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();
        S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        searchImagesByLabels(s3, rekClient, bucket, queryString);
        rekClient.close();
    }
    public static void searchImagesByLabels(S3Client s3, RekognitionClient rekClient,
                                                      String bucketName, String queryString) {
        List<software.amazon.awssdk.services.s3.model.S3Object> objectListFromBucket = listBucketObjects(s3, bucketName);
        for(software.amazon.awssdk.services.s3.model.S3Object imageObject : objectListFromBucket) {
            S3Object s3Object = S3Object.builder()
                    .bucket(bucketName)
                    .name(imageObject.key())
                    .build();
            List<Label> labelsFromImage = getLabelsFromImage(s3Object, rekClient);
            if(isMatchingLabelFound(queryString, labelsFromImage)) {
                System.out.println("Image object : " + imageObject + ", \n Labels : " + labelsFromImage);
            }
        }
    }

    public static Boolean isMatchingLabelFound(String queryString, List<Label> labels) {
        for(Label label : labels) {
            if(label.name().toLowerCase().equalsIgnoreCase(queryString)
                || queryString.toLowerCase().contains(label.name().toLowerCase())
                || label.name().toLowerCase().contains(queryString.toLowerCase())) {
                return true;
            }
            if(label.parents() != null && label.parents().size() > 0) {
                Boolean isFound = label.parents()
                                        .stream()
                                        .anyMatch( e -> e.name().equalsIgnoreCase(queryString)
                                                    || e.name().toLowerCase().contains(queryString.toLowerCase())
                                                    || queryString.toLowerCase().contains(e.name().toLowerCase()));
                if(isFound){
                    return true;
                }
            }
            if(label.categories() != null && label.categories().size() > 0) {
                Boolean isFound = label.categories()
                                        .stream()
                                        .anyMatch( e -> e.name().equalsIgnoreCase(queryString)
                                                    || e.name().toLowerCase().contains(queryString.toLowerCase())
                                                    || queryString.toLowerCase().contains(e.name().toLowerCase()));
                if(isFound){
                    return true;
                }
            }
            if(label.aliases() != null && label.aliases().size() > 0) {
                Boolean isFound = label.aliases()
                                        .stream()
                                        .anyMatch( e -> e.name().equalsIgnoreCase(queryString)
                                                    || e.name().toLowerCase().contains(queryString.toLowerCase())
                                                    || queryString.toLowerCase().contains(e.name().toLowerCase()));
                if(isFound){
                    return true;
                }
            }
        }
        return false;
    }

    public static List<software.amazon.awssdk.services.s3.model.S3Object> listBucketObjects(S3Client s3,
                                                                                            String bucketName) {

        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            return res.contents();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<Label> getLabelsFromImage(S3Object s3Object, RekognitionClient rekClient) {
        try {
            Image myImage = Image.builder()
                    .s3Object(s3Object)
                    .build();

            DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                    .image(myImage)
                    .maxLabels(25)
                    .build();

            DetectLabelsResponse labelsResponse = rekClient.detectLabels(detectLabelsRequest);
            return labelsResponse.labels();
        } catch (RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
}
