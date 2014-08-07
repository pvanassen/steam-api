package nl.pvanassen.steam.store.login;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RSACrypto {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Cipher cipher;
    private final RSAPublicKey RSAkey;

    RSACrypto(BigInteger mod, BigInteger exp, boolean oaep) {
        try {
            final RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(mod, exp);

            final KeyFactory factory = KeyFactory.getInstance("RSA");
            RSAkey = (RSAPublicKey) factory.generatePublic(publicKeySpec);

            Security.addProvider(new BouncyCastleProvider());
            if (oaep) {
                cipher = Cipher.getInstance("RSA/None/OAEPWithSHA1AndMGF1Padding", "BC");
            }
            else {
                cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");
            }
            cipher.init(Cipher.ENCRYPT_MODE, RSAkey);
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException
                | InvalidKeySpecException e) {
            logger.error("Error initializing encryption", e);
            throw new RuntimeException("Error initializing encryption", e);
        }

    }

    byte[] encrypt(byte[] input) {
        try {
            return cipher.doFinal(input);
        }
        catch (IllegalBlockSizeException | BadPaddingException e) {
            logger.error("Error performing encryption", e);
            throw new RuntimeException("Error performing encryption", e);
        }
    }
}
