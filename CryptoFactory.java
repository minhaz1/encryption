package com.minhaz.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

public final class CryptoFactory {

	private CryptoFactory(){
		// does nothing. don't want this class to be instantiable
	}


	/** This function returns key based on the pass code
	 * 
	 * @param pass - the passcode to use for encryption
	 * @return byte array that is used as the key for the encryption
	 * @throws Exception
	 */
	private static byte[] generateKey(String pass) throws Exception {
		byte[] key = (pass).getBytes("UTF-8");
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 32); // use only first 256 bit
		return key;
	}

	public static void encrypt(File source, FileOutputStream fos, String pass) throws Exception {
		FileInputStream fis = new FileInputStream(source);

		SecretKeySpec sks = new SecretKeySpec(generateKey(pass), "AES");

		// Create cipher
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, sks);

		byte[] iv = cipher.getIV();
		fos.write(iv);

		String test = null; 
		for(byte b: iv){
			test += (char)b;
		}
		
		// Wrap the output stream
		CipherOutputStream cos = new CipherOutputStream(fos, cipher);
		// Write bytes
		int b;
		byte[] d = new byte[8];
		while((b = fis.read(d)) != -1) {
			cos.write(d, 0, b);
		}
		// Flush and close streams.
		cos.flush();
		cos.close();
		fis.close();
		fos.close();
	}

	public static void decrypt(FileInputStream fis, File destination, String pass) throws Exception {	

		FileOutputStream fos = new FileOutputStream(destination);
		byte[] vector = new byte[16];
		fis.read(vector);

		String test = null; 
		for(byte b: vector){
			test += (char)b;
		}
		
		SecretKeySpec sks = new SecretKeySpec(generateKey(pass), "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(vector));
		CipherInputStream cis = new CipherInputStream(fis, cipher);

		int b;
		byte[] d = new byte[8];
		while((b = cis.read(d)) != -1) {
			fos.write(d, 0, b);
		}
		fos.flush();
		fos.close();
		cis.close();
	}


}
