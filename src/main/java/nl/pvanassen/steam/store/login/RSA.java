package nl.pvanassen.steam.store.login;

import java.math.BigInteger;

import org.apache.commons.codec.binary.Base64;

public class RSA {
	private final BigInteger modulus;
	private final BigInteger exponent;
	public RSA(String modHex, String expHex) {
		modulus = new BigInteger(modHex, 16);
		exponent = new BigInteger(expHex, 16);
	}
	
	public String encrypt(String password) {
		BigInteger data = pkcs1pad2(password.getBytes(), (modulus.bitLength() + 7 ) >> 3);
		BigInteger d2 = data.modPow(exponent, modulus);
		String dataHex = d2.toString(16);
		byte[] encrypted = hexStringToByteArray(dataHex);
		return Base64.encodeBase64String(encrypted);
	}

	private BigInteger pkcs1pad2(byte[] data, int n) {
		byte[] bytes = new byte[n];
		int i = data.length - 1;
		while (i >= 0 && n > 11) {
			bytes[--n] = data[i--];
		}
		bytes[--n] = 0;

		while (n > 2) {
			bytes[--n] = 0x01;
		}

		bytes[--n] = 0x2;
		bytes[--n] = 0;

		return new BigInteger(bytes);
	}
	
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }	
}
