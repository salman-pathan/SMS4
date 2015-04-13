package SMS4;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class SMS4Demo {

	private static int[] convert(byte buf[]) {
		int intArr[] = new int[buf.length / 4];
		int offset = 0;
		for(int i = 0; i < intArr.length; i++) {
			intArr[i] = (buf[3 + offset] & 0xFF) | ((buf[2 + offset] & 0xFF) << 8) |
					((buf[1 + offset] & 0xFF) << 16) | ((buf[0 + offset] & 0xFF) << 24);  
			offset += 4;
		}
		return intArr;
	}

	public static byte[] convertIntToByteArray(int intArray[]) {

		int[] data = intArray;

		ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(data);

		byte[] array = byteBuffer.array();
		return array;
	}
	
	private static void enDecImage()
	{
		Path path = Paths.get("/Users/salmankhan/Desktop/image.jpg");
		int[] encryptionKey = {0x01234567, 0x89abcdef, 0xfedcba98, 0x76543210};
		Path encryptedPath = Paths.get("/Users/salmankhan/Desktop/encryptImage");
		Path decryptedPath = Paths.get("/Users/salmankhan/Desktop/decrypt.jpg");
		
		try {
			byte[] data = Files.readAllBytes(path);
			int[] encryptedData = SMS4.encrypt(convert(data), encryptionKey);
			byte[] enryptedDataInByte = convertIntToByteArray(encryptedData);
			
			java.nio.file.Files.write(encryptedPath, 
									  enryptedDataInByte, 
									  StandardOpenOption.CREATE, 
									  StandardOpenOption.TRUNCATE_EXISTING);
			
			int[] decryptedData = SMS4.decrypt(convert(enryptedDataInByte), encryptionKey);
			byte[] decryptedDataInByte = convertIntToByteArray(decryptedData);
			
			java.nio.file.Files.write(decryptedPath, 
									  decryptedDataInByte, 
									  StandardOpenOption.CREATE, 
									  StandardOpenOption.TRUNCATE_EXISTING);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void enDecText()
	{
		Path path = Paths.get("/Users/salmankhan/Desktop/input.txt");
		int[] encryptionKey = {0x01234567, 0x89abcdef, 0xfedcba98,0x76543210};
		Path encryptedPath = Paths.get("/Users/salmankhan/Desktop/encrypt.txt");
		Path decryptedPath = Paths.get("/Users/salmankhan/Desktop/decrypt.txt");
		
		try {
			byte[] data = Files.readAllBytes(path);
			int[] encryptedData = SMS4.encrypt(convert(data), encryptionKey);
			byte[] enryptedDataInByte = convertIntToByteArray(encryptedData);
			
			java.nio.file.Files.write(encryptedPath, 
									  enryptedDataInByte, 
									  StandardOpenOption.CREATE, 
									  StandardOpenOption.TRUNCATE_EXISTING);
			
			int[] decryptedData = SMS4.decrypt(convert(enryptedDataInByte), encryptionKey);
			byte[] decryptedDataInByte = convertIntToByteArray(decryptedData);
			
			java.nio.file.Files.write(decryptedPath, 
									  decryptedDataInByte, 
									  StandardOpenOption.CREATE, 
									  StandardOpenOption.TRUNCATE_EXISTING);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Path enpath          = Paths.get("/Users/salmankhan/Desktop/Encrypted-2.txt");
		Path path          = Paths.get("/Users/salmankhan/Desktop/input.txt");
		Path encryptedPath = Paths.get("/Users/salmankhan/Desktop/encrypt.txt");
		Path decryptedPath = Paths.get("/Users/salmankhan/Desktop/decrypt.txt");
		
		int[] encryptionKey = {0x01234567, 0x89abcdef, 0xfedcba98, 0x76543210};
		
		try {
			byte[] data = Files.readAllBytes(path);
			//byte[] paddedData = SMS4.padData(data);
			
			byte[] encryptedData = SMS4.cipher(data, encryptionKey);
			//byte[] enryptedDataInByte = convertIntToByteArray(encryptedData);
			
			java.nio.file.Files.write(encryptedPath, 
									  encryptedData, 
									  StandardOpenOption.CREATE, 
									  StandardOpenOption.TRUNCATE_EXISTING);
			
			byte[] endata = Files.readAllBytes(enpath);
			byte[] decryptedData = SMS4.decipher(endata, encryptionKey);
			//byte[] decryptedDataInByte = convertIntToByteArray(decryptedData);
			//byte[] unpaddedData = SMS4.unPadData(decryptedData);
			
			java.nio.file.Files.write(decryptedPath, 
									  decryptedData, 
									  StandardOpenOption.CREATE, 
									  StandardOpenOption.TRUNCATE_EXISTING);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		//enDecImage();
		//enDecText();
	}

}
