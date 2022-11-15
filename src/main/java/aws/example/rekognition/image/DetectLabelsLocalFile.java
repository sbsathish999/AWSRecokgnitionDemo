package aws.example.rekognition.image;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class DetectLabelsLocalFile {
    public static void main(String[] args) {

//        final String usage = "\n" +
//                "Usage: " +
//                "   <sourceImage>\n\n" +
//                "Where:\n" +
//                "   sourceImage - The path to the image (for example, C:\\AWS\\pic1.png). \n\n";
//
//        if (args.length != 1) {
//            System.out.println(usage);
//            System.exit(1);
//        }

        try {
            String sourceImage = "unknown2.jpg";
            Region region = Region.US_EAST_1;
            RekognitionClient rekClient = RekognitionClient.builder()
                    .region(region)
                    .credentialsProvider(ProfileCredentialsProvider.create())
                    .build();

            detectImageLabels(rekClient, sourceImage);
            rekClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // snippet-start:[rekognition.java2.detect_labels.main]
    public static void detectImageLabels(RekognitionClient rekClient, String sourceImage) {

        try {
            InputStream sourceStream = new FileInputStream(sourceImage);
            SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

            // Create an Image object for the source image.
            Image souImage = Image.builder()
                    .bytes(sourceBytes)
                    .build();

            DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                    .image(souImage)
                    .maxLabels(30)
                    .build();

            DetectLabelsResponse labelsResponse = rekClient.detectLabels(detectLabelsRequest);
            List<Label> labels = labelsResponse.labels();
            System.out.println("Detected labels for the given photo");
            int i = 1;
            for (Label label : labels) {
                System.out.println(i + " - " + label.name() + ": " + label.confidence().toString());
                i++;
            }

        } catch (RekognitionException | FileNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
