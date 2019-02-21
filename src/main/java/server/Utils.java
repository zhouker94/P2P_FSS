package server;

import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.json.simple.parser.ParseException;


public class Utils {

    public enum Status {
        START, STOP, JOIN
    }

    public static void printHelpInfo() {
        System.out.println("usage:Server [-advertisedhostname <arg>][-debug][-connectionintervallimit <arg>]");
        System.out.println("[-exchangeinterval <arg>][-port <arg>][-secret <arg>]");
        System.out.println("Server for Unimelb COMP90015");
        System.out.println();
        System.out.println("-advertisedhostname <arg> advertised hostname");
        System.out.println("-connectionintervallimit <arg> connection interval limit in seconds");
        System.out.println("-exchangeinterval <arg> exchange interval in seconds");
        System.out.println("-port <arg> server port, an integer");
        System.out.println("-secret <arg> secret");
        System.out.println("-debug print debug information");
        System.out.println("-sport <arg> secure port number");
    }

    public static String generateSecret() {
        StringBuilder secret = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 26; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            if ("char".equalsIgnoreCase(charOrNum)) {
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                secret.append((char) (random.nextInt(26) + temp));
            } else {
                secret.append(String.valueOf(random.nextInt(10)));
            }
        }
        return secret.toString();
    }

    public static JSONObject stringToJSON(String msg) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        jsonObject = (JSONObject) jsonParser.parse(msg);
        return jsonObject;
    }
}
