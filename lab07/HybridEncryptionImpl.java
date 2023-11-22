package ch.zhaw.securitylab.slcrypt.encrypt;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import ch.zhaw.securitylab.slcrypt.FileHeader;
import ch.zhaw.securitylab.slcrypt.Helpers;

/**
 * A concrete implementation of the abstract class HybridEncryption.
 */
public class HybridEncryptionImpl extends HybridEncryption {

    /**
     * Creates a secret key.
     *
     * @param cipherAlgorithm The cipher algorithm to use
     * @param keyLength The key length in bits
     * @return The secret key
     */
    @Override
    protected byte[] generateSecretKey(
            String cipherAlgorithm, 
            int keyLength
    ) {
        System.out.println("=== generating secret key ===");
        
        try {
            KeyGenerator generator = KeyGenerator.getInstance(Helpers.getCipherName(cipherAlgorithm));
            generator.init(keyLength);
            SecretKey key = generator.generateKey();
            return key.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            System.err.println(cipherAlgorithm + " does not exist");
            System.exit(1);
        }
        return null;
    }

    /**
     * Encrypts the secret key with a public key.
     *
     * @param secretKey The secret key to encrypt
     * @param certificateEncrypt An input stream from which the certificate with
     * the public key for encryption can be read
     * @return The encrypted secret key
     */
    @Override
    protected byte[] encryptSecretKey(
            byte[] secretKey, 
            InputStream certificateEncrypt
    ) {
        System.out.println("=== encrypting secret key ===");
        
        try {
            CertificateFactory certificateReader = CertificateFactory.getInstance("X.509");
            Certificate certificate = certificateReader.generateCertificate(certificateEncrypt);
            Cipher RSACipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
            RSACipher.init(Cipher.ENCRYPT_MODE, certificate);
            return RSACipher.doFinal(secretKey);
        } catch (CertificateException e) {
            System.err.println("certicate error");
            System.exit(1);
        } catch (InvalidKeyException e) {
            System.err.println("public key invalid format");
            System.exit(1);
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            System.err.println("Unexpected error");
            System.err.println(e.toString());
            System.exit(1);
        }
        return null;
    }

    /**
     * Creates a file header object and fills it with the cipher algorithm name,
     * the authentication and integrity protection type and name, and the
     * encrypted secret key.
     *
     * @param cipherAlgorithm The cipher algorithm to use
     * @param authIntType The type to use for authentication and integrity
     * protection (M for MAC, S for signature, N for none)
     * @param authIntAlgorithm The algorithm to use for authentication and
     * integrity protection
     * @param certificateVerify An input stream from which the certificate for
     * signature verification can be read
     * @param encryptedSecretKey The encrypted secret key
     * @return The new file header object
     */
    @Override
    protected FileHeader generateFileHeader(
            String cipherAlgorithm, 
            char authIntType, 
            String authIntAlgorithm, 
            InputStream certificateVerify, 
            byte[] encryptedSecretKey
    ) {
        System.out.println("=== generating file header ===");
        
        FileHeader fileHeader = new FileHeader();
        fileHeader.setCipherAlgorithm(cipherAlgorithm);
        fileHeader.setAuthIntType(authIntType);
        fileHeader.setEncryptedSecretKey(encryptedSecretKey);
        
        if (authIntType == Helpers.NONE) {
            fileHeader.setAuthIntAlgorithm("");
        } else {
            fileHeader.setAuthIntAlgorithm(authIntAlgorithm);
        }
        
        if (authIntType == Helpers.SIGNATURE) {
            fileHeader.setCertificate(Helpers.inputStreamToByteArray(certificateVerify));
        } else {
            fileHeader.setCertificate(new byte[]{});
        }
        
        if (Helpers.hasIV(cipherAlgorithm)) {
            SecureRandom random = new SecureRandom();
            byte IV[] = new byte[Helpers.getIVLength(cipherAlgorithm)];
            random.nextBytes(IV);
            fileHeader.setIV(IV);
        } else {
            fileHeader.setIV(new byte[]{});
        }
        return fileHeader;
    }

    /**
     * Encrypts a document with a secret key. If GCM is used, the file header is
     * added as additionally encrypted data.
     *
     * @param document The document to encrypt
     * @param fileHeader The file header that contains information for
     * encryption
     * @param secretKey The secret key used for encryption
     * @return A byte array that contains the encrypted document
     */
    @Override
    protected byte[] encryptDocument(
            InputStream document,
            FileHeader fileHeader, 
            byte[] secretKey
    ) {
        System.out.println("=== encrypting document ===");
        
        String algorithmName = fileHeader.getCipherAlgorithm();
        Cipher cipher = null;
        
        try {
            cipher = Cipher.getInstance(algorithmName);
            SecretKeySpec key = new SecretKeySpec(secretKey, algorithmName);
            
            if (Helpers.isCBC(algorithmName) || Helpers.isCTR(algorithmName)) {
                cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(fileHeader.getIV()));
            } else if (Helpers.isCHACHA20(algorithmName)) {
                cipher.init(
                        Cipher.ENCRYPT_MODE,
                        key,
                        new ChaCha20ParameterSpec(fileHeader.getIV(), 1)
                );
            } else if (Helpers.isGCM(algorithmName)) {
                GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(Helpers.AUTH_TAG_LENGTH, fileHeader.getIV());
                cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);
                cipher.updateAAD(fileHeader.encode());
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            }
        } catch (NoSuchAlgorithmException e) {
            System.err.println(Helpers.getCipherName(algorithmName) + " does not exist");
            System.exit(1);
        } catch (NoSuchPaddingException e) {
            System.err.println("padding alogirithm does not exist");
            System.exit(1);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            System.err.println("unexpected error");
            System.err.println(e.toString());
            System.exit(1);
        }

        try (CipherInputStream chiperStream = new CipherInputStream(document, cipher)) {
            return chiperStream.readAllBytes();
        } catch (IOException e) {
            System.err.println("unexpected error");
            System.err.println(e.toString());
            System.exit(1);
        }

        return null;
    }

    /**
     * Computes the HMAC over a byte array.
     *
     * @param dataToProtect The input over which to compute the MAC
     * @param macAlgorithm The MAC algorithm to use
     * @param password The password to use for the MAC
     * @return The byte array that contains the MAC
     */
    @Override
    protected byte[] computeMAC(
            byte[] dataToProtect, 
            String macAlgorithm,
            byte[] password
    ) {
        System.out.println("=== computing MAC ===");

        try {
            SecretKeySpec keyGenerator = new SecretKeySpec(password, macAlgorithm);
            Mac macGenerator = Mac.getInstance(macAlgorithm);
            macGenerator.init(keyGenerator);
            return macGenerator.doFinal(dataToProtect);
        } catch (NoSuchAlgorithmException e) {
            System.err.println(macAlgorithm + "does not exist");
            System.exit(1);
        } catch (InvalidKeyException e) {
            System.err.println("unexpected error");
            System.err.println(e.toString());
            System.exit(1);
        }
        return null;
    }

    /**
     * Computes the signature over a byte array.
     *
     * @param dataToProtect The input over which to compute the signature
     * @param signatureAlgorithm The signature algorithm to use
     * @param privateKeySign An input stream from which the private key to sign
     * can be read
     * @return The byte array that contains the signature
     */
    @Override
    protected byte[] computeSignature(
            byte[] dataToProtect,
            String signatureAlgorithm, 
            InputStream privateKeySign
    ) {
        System.out.println("=== computing signature ===");
        
        try {
            Signature signatureGenerator = Signature.getInstance(signatureAlgorithm);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Helpers.inputStreamToByteArray(privateKeySign));
            KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
            signatureGenerator.initSign(rsaKeyFactory.generatePrivate(keySpec));
            signatureGenerator.update(dataToProtect);
            return signatureGenerator.sign();
        } catch (NoSuchAlgorithmException e) {
            System.err.println(signatureAlgorithm + "does not exist");
            System.exit(1);
        } catch (InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            System.err.println("unexpected error");
            System.err.println(e.toString());
            System.exit(1);
        }
        return null;
    }
}
