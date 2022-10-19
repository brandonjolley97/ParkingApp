import java.security.MessageDigest;

/**
 * Hashes a string based on the SHA256 algorithm. Use for QR code generation and authentication of users.
 */
public class SHA256Gen {

    public static void main(String[] args) {
        String user = "newUser123";
        String parkingSpaceHash = "pretend I am a hash";

        String data = user + parkingSpaceHash;
        SHA256Gen gen = new SHA256Gen();
        String hash = gen.getSHA256Hash(data);
        System.out.println(hash);

    }

    /**
     * Returns a hexadecimal encoded SHA-256 hash for the input String.
     * @param data Input string
     * @return SHA256 hash string
     */
    private String getSHA256Hash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            return bytesToHex(hash); // make it printable
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Converts byte array to a hexidecimal
     * @param bytes Input byte array
     * @return Hexidecimal value string
     */
    private String bytesToHex(byte[] bytes){
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(String.format("%02x", aByte));
        }
        return result.toString();
    }
}
