package org.tudelft.blockchain.booking.otawebapp.util;

import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

@Component
public class Util {

    private static InputStream isKey;
    private static BufferedReader brKey;

    /**
     * Parse a private key file using EC (Elliptic Curve) algorithm
     * @param keyFolderPath path to the containing folder
     * @param keyFileName filename
     * @return
     * @throws IOException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public static PrivateKey parsePrivateKey(String keyFolderPath, String keyFileName)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        try {

            isKey = new FileInputStream(keyFolderPath + File.separator + keyFileName);
            brKey = new BufferedReader(new InputStreamReader(isKey));
            StringBuilder keyBuilder = new StringBuilder();

            for (String line = brKey.readLine(); line != null; line = brKey.readLine()) {
                if (line.indexOf("PRIVATE") == -1) {
                    keyBuilder.append(line);
                }
            }

            byte[] encoded = DatatypeConverter.parseBase64Binary(keyBuilder.toString());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("EC");

            return kf.generatePrivate(keySpec);
        } finally {
            isKey.close();
            brKey.close();
        }
    }

    /**
     * Parse a PEM signed certificate file
     * @param certFolderPath path to the containing folder
     * @param certFileName filename
     * @return
     * @throws IOException
     */
    public static String parseCertificate(String certFolderPath, String certFileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(certFolderPath, certFileName)));
    }

//    public static CAEnrollment getEnrollment(String keyFolderPath,  String keyFileName,  String certFolderPath, String certFileName)
//            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//        PrivateKey key = null;
//        String certificate = null;
////        InputStream isKey = null;
////        BufferedReader brKey = null;
//
//        try {
//
//            isKey = new FileInputStream(keyFolderPath + File.separator + keyFileName);
//            brKey = new BufferedReader(new InputStreamReader(isKey));
//            StringBuilder keyBuilder = new StringBuilder();
//
//            for (String line = brKey.readLine(); line != null; line = brKey.readLine()) {
//                if (line.indexOf("PRIVATE") == -1) {
//                    keyBuilder.append(line);
//                }
//            }
//
//            certificate = new String(Files.readAllBytes(Paths.get(certFolderPath, certFileName)));
//
//            byte[] encoded = DatatypeConverter.parseBase64Binary(keyBuilder.toString());
//            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
//            KeyFactory kf = KeyFactory.getInstance("EC");
//            key = kf.generatePrivate(keySpec);
//        } finally {
//            isKey.close();
//            brKey.close();
//        }
//
//        CAEnrollment enrollment = new CAEnrollment(key, certificate);
//        return enrollment;
//    }
    
}
