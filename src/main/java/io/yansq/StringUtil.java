package io.yansq;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.TrustAnchor;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author yansq
 * @version V1.0
 * @package io.yansq
 * @date 2024/2/6 14:06
 */
public class StringUtil {
    /**
     * Apply SHA256 to string.
     *
     * @param input input
     * @return SHA256 in String
     */
    public static String applySha256(String input){
        try {
            MessageDigest digest= MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte bt : hash) {
                String hex = Integer.toHexString(0xff & bt);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Apply ECDSA signature and returns the result(as bytes)
     *
     * @param privateKey
     * @param input
     * @return
     */
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output;
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            output = dsa.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }


    /**
     * verify a string signature
     *
     * @param publicKey
     * @param data
     * @param signatrue
     * @return
     */
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signatrue) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signatrue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param key
     * @return
     */
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String getMerkleRoot(List<Transaction> transactions) {
        int count = transactions.size();
        List<String> previousTreeLayer = new ArrayList<>();
        for (Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.getTransactionId());
        }
        List<String> treeLayer = previousTreeLayer;
        while (count > 1) {
            treeLayer = new ArrayList<>();
            for (int i = 1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(applySha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }
}
