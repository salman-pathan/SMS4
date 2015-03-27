package sms4java;

import java.awt.image.ConvolveOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class SMS$JavaExample {

	public static int[] convert(byte buf[]) {
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
	
	public static void main(String[] args) throws IOException {
		
		int[] key={0x01234567, 0x89abcdef, 0xfedcba98,0x76543210};
		String currentDirectory = System.getProperty("user.dir");
		String fileUrl = currentDirectory+"/src/sms4java/input.txt";
		File file = new File(fileUrl);
		
		FileInputStream in = null;
	    FileOutputStream out = null;
	   
	      try {
	         in = new FileInputStream(file);
	         byte fileContent[] = new byte[(int)file.length()];
	         in.read(fileContent);
	         int[] data = convert(fileContent);
	         
	         for (int i = 0; i < data.length; i++) {
	        	 System.out.println("Data = "+data[i]);
			}
	         //System.out.println("Data = "+data.toString());
	         
	         SMS4 smsAlgorithm = new SMS4();
	         int[] encryptedData = smsAlgorithm.encrypt(data, key);
	         
	         for (int i = 0; i < encryptedData.length; i++) {
	        	 System.out.println("Encrypted Data = "+encryptedData[i]);
			 }
	         
	         out = new FileOutputStream("encrypted");
	         out.write(convertIntToByteArray(encryptedData));
	         
	         
	         int[] decryptedData = smsAlgorithm.decrypt(encryptedData, key);
	         for (int i = 0; i < decryptedData.length; i++) {
	        	 System.out.println("Decrypted Data = "+decryptedData[i]);
			 }
	         
	         out = new FileOutputStream("decrypted");
	         out.write(convertIntToByteArray(decryptedData));
	         
	      }finally {
	         if (in != null) {
	            in.close();
	         }
	         if (out != null) {
	        	 out.close();
	         }
	      }

	}

}
