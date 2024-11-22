import org.json.JSONObject;
import org.json.JSONArray;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class DestinationHashGenerator {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <roll_number> <json_file_path>");
            return;
        }

        String rollNumber = args[0].toLowerCase().replaceAll("\\s", ""); // Ensure lowercase and no spaces
        String jsonFilePath = args[1];

        try {
            // Read and parse the JSON file
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONObject jsonObject = new JSONObject(jsonContent);

            // Find the "destination" key's value
            String destinationValue = findDestination(jsonObject);

            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                return;
            }

            // Generate a random 8-character alphanumeric string
            String randomString = generateRandomString(8);

            // Concatenate Roll Number, Destination Value, and Random String
            String concatenatedString = rollNumber + destinationValue + randomString;

            // Generate MD5 hash
            String hashValue = generateMD5Hash(concatenatedString);

            // Print the result in the format: <hash>;<random_string>
            System.out.println(hashValue + ";" + randomString);

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to recursively find the first occurrence of the "destination" key
    private static String findDestination(Object json) {
        if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;

            for (String key : jsonObject.keySet()) {
                if (key.equals("destination")) {
                    return jsonObject.getString(key);
                } else {
                    Object child = jsonObject.get(key);
                    String result = findDestination(child);
                    if (result != null) {
                        return result;
                    }
                }
            }
        } else if (json instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) json;

            for (int i = 0; i < jsonArray.length(); i++) {
                Object child = jsonArray.get(i);
                String result = findDestination(child);
                if (result != null) {
                    return result;
                }
            }
        }

        return null; // Key "destination" not found
    }

    // Method to generate an MD5 hash
    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();

        for (byte b : digest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    // Method to generate a random alphanumeric string
    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }

        return result.toString();
    }
}
