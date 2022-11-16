package aws.example.search;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.util.stream.Collectors;

/* This is a program to find images from a bucket matching by their names
   with the contains of the letters from the query string by using only with S3 storage service
 */
public class FindMatchingImageNamesWithQueryString {
    public static void main(String[] args) {
        try {
            String bucketName = "1-bucket-rekognition-demo-1";
            String queryString = "known";
            ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
            Region region = Region.US_EAST_1;
            S3Client s3 = S3Client.builder()
                    .region(region)
                    .credentialsProvider(credentialsProvider)
                    .build();

            searchImagesByNameContainsWith(s3, bucketName, queryString);
            s3.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void searchImagesByNameContainsWith(S3Client s3,
                                                      String bucketName, String queryString) {
        List<S3Object> objectsFromS3Bucket = listBucketObjects(s3, bucketName);
        List<S3Object> matchingImagesList = objectsFromS3Bucket
                .stream()
                .filter(e -> e.key().equalsIgnoreCase(queryString)
                        || e.key().toLowerCase().contains(queryString.toLowerCase()))
                .sorted((e1, e2) -> e1.key().compareTo(e2.key()))
                .collect(Collectors.toList());
        System.out.println("Best matching image objects :");
        matchingImagesList.forEach(System.out::println);
    }

    public static List<S3Object> listBucketObjects(S3Client s3,
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
}
