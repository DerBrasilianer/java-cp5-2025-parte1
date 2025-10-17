package br.com.fiap.rsa;

import java.math.BigInteger;

public class RSAUtil {

    public static BigInteger encryptChar(char ch, BigInteger e, BigInteger n) {
        BigInteger m = BigInteger.valueOf((int) ch);
        return m.modPow(e, n);
    }

    public static char decryptChar(BigInteger cipher, BigInteger d, BigInteger n) {
        BigInteger m = cipher.modPow(d, n);
        int val = m.intValue();
        return (char) val;
    }

    public static String encryptString(String plain, BigInteger e, BigInteger n) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (char ch : plain.toCharArray()) {
            BigInteger c = encryptChar(ch, e, n);
            if (!first) {
                sb.append(",");
            }
            sb.append(c.toString());
            first = false;
        }
        return sb.toString();
    }

    public static String decryptString(String cipherList, BigInteger d, BigInteger n) {
        if (cipherList == null || cipherList.trim().isEmpty()) {
            return "";
        }
        String[] parts = cipherList.split(",");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.trim().isEmpty()) {
                continue;
            }
            BigInteger c = new BigInteger(part.trim());
            char ch = decryptChar(c, d, n);
            sb.append(ch);
        }
        return sb.toString();
    }

}
