import java.io.Console;

public class CryptoHelper {

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
	
	public static byte[] encrypt(File source, File destination, String pass) throws Exception {
	    // Here you read the cleartext.
	    FileInputStream fis = new FileInputStream(source);
	    // This stream write the encrypted text. This stream will be wrapped by another stream.
	    FileOutputStream fos = new FileOutputStream(destination);

	    // Length is 16 byte
	    SecretKeySpec sks = new SecretKeySpec(generateKey(pass), "AES");
	    // Create cipher
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    cipher.init(Cipher.ENCRYPT_MODE, sks);
	    
	    byte[] iv = cipher.getIV();
	    fos.write(iv);
	    
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
	    return iv;
	}

	/** Decrypt file
	 * 
	 * @param source
	 * @param destination
	 * @param pass
	 * @throws Exception
	 */
	public static void decrypt(File source, File destination, String pass) throws Exception {
	
		FileInputStream fis = new FileInputStream(source);
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
	
	
	public static void main(String[] args) {	
		
		if(args.length != 3){
			System.out.println("Error. Invalid arguments");
			System.out.println("Usage: \nEncrypt: java CryptoHelper -e path_to_input path_to_output");
			System.out.println("Decrypt: java CryptoHelper -d path_to_input path_to_output");
			System.exit(0);
		}
		
		
		String flag = args[0];
		String input = args[1];
		String output = args[2];
		
		if(!flag.equals("-e") && (!flag.equals("-d"))){
			System.out.println("Error: \nUsage: \nEncrypt: java CryptoHelper -e path_to_input path_to_output");
			System.out.println("Decrypt: java CryptoHelper -d path_to_input path_to_output");
			System.exit(0);
		}

		File in  = new File(input);
		File out = new File(output);
		Scanner reader = new Scanner(System.in);
		
		Console c = System.console();
		if (c == null) {
		    System.err.println("No console.");
		    System.exit(1);
		}


		try {
			
			if(flag.equals("-e")){
				System.out.println("Option: encrypt");
				System.out.println("Input file: " + input);
				System.out.println("Output file: " +  output);
				//System.out.println("Please enter a password for encryption: ");
				String pass = new String(c.readPassword("Please enter a password for encryption: "));
				//String pass = reader.nextLine();
				encrypt(in, out, pass);
			}
			else if(flag.equals("-d")){
				System.out.println("Option: decrypt");
				System.out.println("Input file: " + input);
				System.out.println("Output file: " + output);
				//System.out.println("Please enter your password: ");
				String pass = new String(c.readPassword("Please enter your password: "));
				//String pass = reader.nextLine();
				decrypt(in, out, pass);
			}

		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

}
