import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtility {


    public static void main(String[] args) throws Exception {

        if (args[0].equals("encrypt")) {
            if (args.length < 2) {
                System.out.println("usage: encrypt [argument]");
                return;
            }
            SecureRandom rnd = new SecureRandom();
            IvParameterSpec iv = new IvParameterSpec(rnd.generateSeed(16));

            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);
            SecretKey k = generator.generateKey();

            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, k, iv);

            byte[] bytes = c.doFinal(args[1].getBytes("UTF-8"));
            File currentDirectory = new File("output.encrypted");
            try (FileOutputStream fos = new FileOutputStream(currentDirectory)) {
                fos.write(iv.getIV());
                fos.write(bytes);
                System.out.println("Current directory: " + new File(".").getCanonicalPath());
            }
            try (FileOutputStream fos = new FileOutputStream(new File("output.key"))) {
                fos.write(k.getEncoded());
            }
        }
        else if (args[0].equals("decrypt")) {
            try (FileInputStream fis = new FileInputStream(new File("output.encrypted"));
                    FileInputStream keyFis = new FileInputStream(new File("output.key"))) {
                System.out.println("Current directory: " + new File(".").getCanonicalPath());
                byte[] iv = new byte[16];
                fis.read(iv);
                byte[] bytes = new byte[2056];
                int len = fis.read(bytes);
                byte[] sliced = new byte[len];
                for (int i=0; i < len; i++) {
                    sliced[i] = bytes[i];
                }
                byte[] key = new byte[32];
                keyFis.read(key);

                SecretKey k = new SecretKeySpec(key, "AES");

                Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
                c.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));

                System.out.println("decrypted: " + new String(c.doFinal(sliced)));
            }

        } else if (args[0].equals("testrun")) {
            if (args.length < 2) {
                System.out.println("usage: testrun [argument]");
                return;
            }

            SecureRandom rnd = new SecureRandom();
            IvParameterSpec iv = new IvParameterSpec(rnd.generateSeed(16));

            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);
            SecretKey k = generator.generateKey();
            byte[] keyBytes = k.getEncoded();

            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, k, iv);

            byte[] bytes = c.doFinal(args[1].getBytes());
            System.out.println("bytes " + new String(bytes));

            c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            Key rebuiltKey = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec rebuiltIv = new IvParameterSpec(iv.getIV());
            c.init(Cipher.DECRYPT_MODE, rebuiltKey, rebuiltIv);
            System.out.println("decrypted: " + new String(c.doFinal(bytes)));
        } else {
            System.out.println("usage: encrypt [argument] | decrypt");
        }
    }
}
