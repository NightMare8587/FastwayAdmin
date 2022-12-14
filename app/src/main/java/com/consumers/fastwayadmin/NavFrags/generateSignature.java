package com.consumers.fastwayadmin.NavFrags;

import org.apache.commons.codec.binary.Hex;

import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class generateSignature {
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    String orderID,razorpayID,sign,secret;

    public generateSignature(String orderID, String razorpayID, String sign, String secret) throws SignatureException {
        this.orderID = orderID;
        this.razorpayID = razorpayID;
        this.sign = sign;
        this.secret  =secret;

//        calculateRFC2104HMAC(orderID + "|" + razorpayID,secret);
    }

    public static String calculateRFC2104HMAC(String data, String secret)
            throws SignatureException
    {
        String result;
        try {

            // get an hmac_sha256 key from the raw secret bytes
            SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), HMAC_SHA256_ALGORITHM);

            // get an hmac_sha256 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);

            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes());

            // base64-encode the hmac
//            result = DatatypeConverter.printHexBinary(rawHmac).toLowerCase();
//            result = new String(Base64.encodeBase64(rawHmac));
            result = new String(Hex.encodeHex(rawHmac));

        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }
}
