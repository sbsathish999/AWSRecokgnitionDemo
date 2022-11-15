package aws.example.s3.objects;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

public class ListObjects {

    public static void main(String[] args) {
        try {
            String bucketName = "1-bucket-rekognition-demo-1";
            ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
            Region region = Region.US_EAST_1;
            S3Client s3 = S3Client.builder()
                    .region(region)
                    .credentialsProvider(credentialsProvider)
                    .build();

            listBucketObjects(s3, bucketName);
            s3.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // snippet-start:[s3.java2.list_objects.main]
    public static void listBucketObjects(S3Client s3,
                                         String bucketName) {

        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            for (S3Object myValue : objects) {
                System.out.print("\n " + myValue.toString());
                System.out.print("\n The name of the key is " + myValue.key());
                System.out.print("\n The object is " + calKb(myValue.size()) + " KBs");
                System.out.print("\n The owner is " + myValue.owner());
            }

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //convert bytes to kbs.
    private static long calKb(Long val) {
        return val / 1024;
    }
}
