package SMS4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author salmankhan
 *
 */
public class SMS4 {

	/**
	 * Number of Rounds to perform bit manipulation.
	 */
	private final static int ROUND = 32;

	/**
	 * S-Box for bit shifting.
	 */
	private static short sbox[] = { 
		0xd6, 0x90, 0xe9, 0xfe, 0xcc, 0xe1, 0x3d, 0xb7, 0x16, 0xb6, 0x14, 0xc2, 0x28, 0xfb, 0x2c, 0x05,
		0x2b, 0x67, 0x9a, 0x76, 0x2a, 0xbe, 0x04, 0xc3, 0xaa, 0x44, 0x13, 0x26, 0x49, 0x86, 0x06, 0x99, 
		0x9c, 0x42, 0x50, 0xf4, 0x91, 0xef, 0x98, 0x7a, 0x33, 0x54, 0x0b, 0x43, 0xed, 0xcf, 0xac, 0x62, 
		0xe4, 0xb3, 0x1c, 0xa9, 0xc9, 0x08, 0xe8, 0x95, 0x80, 0xdf, 0x94, 0xfa, 0x75, 0x8f, 0x3f, 0xa6, 
		0x47, 0x07, 0xa7, 0xfc, 0xf3, 0x73, 0x17, 0xba, 0x83, 0x59, 0x3c, 0x19, 0xe6, 0x85, 0x4f, 0xa8, 
		0x68, 0x6b, 0x81, 0xb2, 0x71, 0x64, 0xda, 0x8b, 0xf8, 0xeb, 0x0f, 0x4b, 0x70, 0x56, 0x9d, 0x35, 
		0x1e, 0x24, 0x0e, 0x5e, 0x63, 0x58, 0xd1, 0xa2, 0x25, 0x22, 0x7c, 0x3b, 0x01, 0x21, 0x78, 0x87, 
		0xd4, 0x00, 0x46, 0x57, 0x9f, 0xd3, 0x27, 0x52, 0x4c, 0x36, 0x02, 0xe7, 0xa0, 0xc4, 0xc8, 0x9e, 
		0xea, 0xbf, 0x8a, 0xd2, 0x40, 0xc7, 0x38, 0xb5, 0xa3, 0xf7, 0xf2, 0xce, 0xf9, 0x61, 0x15, 0xa1, 
		0xe0, 0xae, 0x5d, 0xa4, 0x9b, 0x34, 0x1a, 0x55, 0xad, 0x93, 0x32, 0x30, 0xf5, 0x8c, 0xb1, 0xe3, 
		0x1d, 0xf6, 0xe2, 0x2e, 0x82, 0x66, 0xca, 0x60, 0xc0, 0x29, 0x23, 0xab, 0x0d, 0x53, 0x4e, 0x6f, 
		0xd5, 0xdb, 0x37, 0x45, 0xde, 0xfd, 0x8e, 0x2f, 0x03, 0xff, 0x6a, 0x72, 0x6d, 0x6c, 0x5b, 0x51, 
		0x8d, 0x1b, 0xaf, 0x92, 0xbb, 0xdd, 0xbc, 0x7f, 0x11, 0xd9, 0x5c, 0x41, 0x1f, 0x10, 0x5a, 0xd8, 
		0x0a, 0xc1, 0x31, 0x88, 0xa5, 0xcd, 0x7b, 0xbd, 0x2d, 0x74, 0xd0, 0x12, 0xb8, 0xe5, 0xb4, 0xb0, 
		0x89, 0x69, 0x97, 0x4a, 0x0c, 0x96, 0x77, 0x7e, 0x65, 0xb9, 0xf1, 0x09, 0xc5, 0x6e, 0xc6, 0x84, 
		0x18, 0xf0, 0x7d, 0xec, 0x3a, 0xdc, 0x4d, 0x20, 0x79, 0xee, 0x5f, 0x3e, 0xd7, 0xcb, 0x39, 0x48 
	};

	/**
	 *	System Key.
	 */
	private static int fk[] = { 0xa3b1bac6, 0x56aa3350, 0x677d9197, 0xb27022dc };

	/**
	 *	Circular Key.
	 */
	private static int ck[] = { 
		0x00070e15, 0x1c232a31, 0x383f464d, 0x545b6269,
		0x70777e85, 0x8c939aa1, 0xa8afb6bd, 0xc4cbd2d9, 
		0xe0e7eef5, 0xfc030a11,	0x181f262d, 0x343b4249, 
		0x50575e65, 0x6c737a81, 0x888f969d, 0xa4abb2b9,
		0xc0c7ced5, 0xdce3eaf1, 0xf8ff060d, 0x141b2229, 
		0x30373e45, 0x4c535a61,	0x686f767d, 0x848b9299, 
		0xa0a7aeb5, 0xbcc3cad1, 0xd8dfe6ed, 0xf4fb0209,
		0x10171e25, 0x2c333a41, 0x484f565d, 0x646b7279 
	};


	private static int rotateLeft(int x, int n) {
		return ((x << n) | x >>> (32 - n));
	}

	private static int ltrans(int b) {
		return b ^ (rotateLeft(b, 2)) ^ (rotateLeft(b, 10)) ^ (rotateLeft(b, 18))	^ (rotateLeft(b, 24));
	}

	private static int keyLtrans(int b) {
		return (b ^ rotateLeft(b, 13) ^ rotateLeft(b, 23));
	}

	private static int substitute(int x) {
		return (sbox[(x >>> 24) & 0xff] << 24)
				^ (sbox[(x >>> 16) & 0xff] << 16)
				^ (sbox[(x >>> 8) & 0xff] << 8)
				^ (sbox[(x) & 0xff]);
	}

	private static int ttrans(int x) {
		return ltrans(substitute(x));
	}

	private static int keyTtrans(int x) {
		return keyLtrans(substitute(x));
	}

	/**
	 *	Process a word (128 bits).
	 */
	private static void processWord(int[] input, int[] rk,int idx) {

		// Buffer array to store 36 values. 
		int[] buf = new int[36];

		// Position Variables.
		int first  = idx;
		int second = first  + 1;
		int third  = second + 1;
		int forth  = third  + 1;

		//	Store values in buffer according to the supplied position.
		buf[0] = input[first];
		buf[1] = input[second];
		buf[2] = input[third];
		buf[3] = input[forth];

		//	Loops 32 times and stores the XOR expression after every 4 position.
		for (int position = 0; position < ROUND; position++) {
			buf[position+4] = buf[position] ^ ttrans(buf[position+1] ^ buf[position+2] ^ buf[position+3] ^ rk[position]); 			
		}

		//	Stores the reverse value.
		input[first]  = buf[35];
		input[second] = buf[34];
		input[third]  = buf[33];
		input[forth]  = buf[32];
	}

	/**
	 *	Calculates Round key.
	 */
	private static int[] calculateRoundKey(int[] mainKey) {

		// Round Key.
		int roundKey[] = { 0, 0, 0, 0, 0, 0, 0, 0,
						   0, 0, 0, 0, 0, 0, 0, 0,
						   0, 0, 0, 0, 0, 0, 0, 0,
						   0, 0, 0, 0, 0, 0, 0, 0 
		};

		//	Buffer array.
		int[] key = new int[4];
		key[0]    = mainKey[0];
		key[1]    = mainKey[1];
		key[2]    = mainKey[2];
		key[3]    = mainKey[3];

		//	XORing the buffer array with system key.
		key[0] ^= fk[0];
		key[1] ^= fk[1];
		key[2] ^= fk[2];
		key[3] ^= fk[3];

		for (int i = 0; i < ROUND; i++) {
			roundKey[i]  = key[0] ^ keyTtrans(key[1] ^ key[2] ^ key[3] ^ ck[i]);
			key[0] = key[1];
			key[1] = key[2];
			key[2] = key[3];
			key[3] = roundKey[i];
		}
		return roundKey;
	}

	/**
	 *	This method takes data in byte array format and ensures that each block of data is of 16 bits and 
	 *	appends padding data if last block is smaller then 16 bits and returns the new array.
	 *
	 *	@param : Data in byte array.
	 *	@return: Data in byte array with appended padding data if last block is less than 16 bits.
	 */
	static byte[] padData(byte[] data)
	{
		int wordSize = 4; //  bytes
		int blocklen = 4; // size		
		int blockSize = wordSize * blocklen; // 4*4 bytes

		int wordMod = data.length % blockSize;

		if(wordMod > 0) {
			int padNumber = (blockSize - wordMod);
			byte[] dataList = new byte[padNumber];
			for(int i=0; i< padNumber; i++){
				dataList[i] = (byte)padNumber;
			}

			byte[] dataBuf = new byte[dataList.length+data.length];
			java.lang.System.arraycopy(data, 0, dataBuf, 0, data.length);
			java.lang.System.arraycopy(dataList, 0, dataBuf, dataBuf.length-dataList.length, dataList.length);

			return dataBuf;
		} else {
			return data;
		}
	}
	
	/**
	 *	This method returns the number of padding data to add in last block of data array.
	 *
	 *	@param : Data in byte array.
	 *	@return: Number of padding data to add in last block of data array.
	 */
	static int dataToPad(byte[] data) {

		int wordSize = 4; //  bytes
		int blocklen = 4; // size		
		int blockSize = wordSize * blocklen; // 4*4 bytes		
		int wordMod = data.length % blockSize;

		if(wordMod > 0) {
			int padNumber = (blockSize - wordMod);
			return padNumber;
		}
		return wordMod;
	}

	/**
	 *	This method removes the padding data from last block of data array.
	 *
	 *	@param : Data in byte array.
	 *	@return: Byte array with no padding data.
	 */
	static byte[] unPadData(byte[] data)
	{		
		byte lastWord = data[data.length - 1];
		int wordSize = 4; //  bytes
		int blocklen = 4; // size		
		int blockSize = wordSize * blocklen; // 4*4 bytes
		if(lastWord < blockSize){
			byte[] paddata = new byte[lastWord];
			java.lang.System.arraycopy(data, data.length-lastWord, paddata, 0, lastWord);
			boolean padFlag = true;
			for(int index = 0; index<lastWord; index++){
				if(padFlag){
					padFlag = paddata[index] == lastWord;
				} else {
					break;
				}
			}
			if(padFlag){
				data = Arrays.copyOf(data, data.length - lastWord);
			}

		}
		return data;

	}

	/**
	 *	Encrypts data of type Integer array with a specific encryption key.
	 *
	 *	@param	data	Data to be encrypted.
	 *	@param	encryptionKey	Key used to encrypt data.
	 *	@return	Encrypted data in integer array format.
	 */
	public static int[] encrypt(int[] data, int[] encryptionKey) {

		int rk[] = calculateRoundKey(encryptionKey);

		for (int i = 0; i < data.length; i=i+4) {

			processWord(data, rk,i);
		}
		return data;
	}

	/**
	 *	Decrypts an encrypted data with a specific encryption key.
	 *
	 *	@param	encryptedData	Encrypted data which needs to be decrypted.
	 *	@param  encryptionKey	Key for decrypting data.
	 *	@return	Decrypted data in integer array format.
	 */
	public static int[] decrypt(int[] encryptedData, int[] encryptionKey) {

		int rk[] = calculateRoundKey(encryptionKey);
		int drk[] = new int[ROUND];

		int wordLength = 4;
		int wordCount = encryptedData.length/wordLength;
		int wordMod = encryptedData.length % wordLength;
		wordCount += wordMod > 0 ? 1 : 0;

		int dataSize = wordCount*wordLength;
		int[] datBuff = new int[dataSize];

		java.lang.System.arraycopy(encryptedData, 0, datBuff, 0, encryptedData.length);

		for (int position = 0; position < ROUND; position++) {
			drk[position]=rk[31-position];
		}

		for (int index = 0; index < datBuff.length; index=index+4) {
			processWord(datBuff, drk, index);
		}

		return datBuff;
	}

	/**
	 *	This method checks the last block of the data array and adds padding data if last block is less than 16 bits.
	 *	It saves the record of this padding data in a byte and appends it to the data after encrypting the data.
	 *
	 *	@param	byteData	Data array in byte format.
	 *	@param	encryptionKey	Key to encrypt data.
	 *	@return	Encrypted data with appended meta data.
	 */
	public static byte[] cipher(byte[] byteData, int[] encryptionKey) {		

		//	Number of data to pad.
		int noOfDataToPad = dataToPad(byteData);

		//	Metadata to append in the encrypted data.
		byte metaData = (byte)noOfDataToPad;

		// Pad byte data.
		byte[] paddedData = padData(byteData);

		//	Convert byte data to int data.
		int[] dataForEncryption = convertByteArraytoIntArray(paddedData);			

		//	Encrypt the data.
		int[] cipherData = encrypt(dataForEncryption, encryptionKey);

		// Conver int data to byte data		
		byte[] byteBuffer = convertIntArrayToByteArray(cipherData);
		byte[] encryptedData = new byte[byteBuffer.length+1];
		java.lang.System.arraycopy(byteBuffer, 0, encryptedData, 0, byteBuffer.length);  

		int metaDataPosition = byteBuffer.length; 

		//	Append Meta Data.
		encryptedData[metaDataPosition] = metaData;

		return encryptedData;		
	} 

	/**
	 *	This method checks the metadata in the last block of the data array. It further removes the appended metadata 
	 *	and interprets the number of byte to remove from the last block of data array and return the decrypted data.
	 *
	 *	@param	encryptedData	Encrypted data in byte array format.
	 *	@param	encryptionKey	Key to decrypt data.
	 *	@return	Returns the 
	 */
	public static byte[] decipher(byte[] encryptedData, int[] encryptionKey) {

		int metaDataPosition = encryptedData.length - 1;

		//	Gets metadata from the data.
		int metaData = (int)encryptedData[metaDataPosition];

		//	Removes metadata byte from encrypted data.
		byte[] plainEncryptedData =  ArrayUtils.remove(encryptedData, metaDataPosition);

		//	Decrypt the data.
		byte[] decryptedData = convertIntArrayToByteArray(decrypt(convertByteArraytoIntArray(plainEncryptedData), encryptionKey));

		if(metaData > 0) {
			List<Byte> dataList = Arrays.asList(ArrayUtils.toObject(decryptedData));
			int size = dataList.size();
			for (int i = size; i < size - metaData; i--) {
				dataList.remove(i);
			}
			Byte[] dataArray = dataList.toArray(new Byte[dataList.size()]);
			return ArrayUtils.toPrimitive(dataArray);
		} else {
			return decryptedData;
		}		
	}
	
	/**
	 *	Converts Byte Array to Integer Array.
	 *
	 *	@param byteArray Byte Array.
	 *	@return Integer Array.
	 */
	private static int[] convertByteArraytoIntArray(byte byteArray[]) {
		int intArr[] = new int[byteArray.length / 4];
		int offset = 0;
		for(int i = 0; i < intArr.length; i++) {
			intArr[i] = (byteArray[3 + offset] & 0xFF) | ((byteArray[2 + offset] & 0xFF) << 8) |
					((byteArray[1 + offset] & 0xFF) << 16) | ((byteArray[0 + offset] & 0xFF) << 24);  
			offset += 4;
		}
		return intArr;
	}

	/**
	 *	Converts Integer Array to Byte Array
	 *
	 *	@param intArray Integer Array.
	 *	@return Byte Array.
	 */
	private static byte[] convertIntArrayToByteArray(int intArray[]) {

		int[] data = intArray;

		ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(data);

		byte[] array = byteBuffer.array();
		return array;
	}
}

