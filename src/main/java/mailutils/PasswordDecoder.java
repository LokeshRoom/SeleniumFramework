package mailutils;
import java.util.Base64;

public class PasswordDecoder {
    public static String decoder(String password){
        Base64.Decoder decoder = Base64.getDecoder();
        return (new String(decoder.decode(password)));
    }
    private static String encode(String password){
        Base64.Encoder encoder=Base64.getEncoder();
        return (encoder.encodeToString(password.getBytes()));
    }

    public static void main(String[] args) {
        System.out.println(encode("Sfdcjul#2020"));
    }

}
