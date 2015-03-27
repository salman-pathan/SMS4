package sms4java;

/**
 * 
 */

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




/**
 * @author Salman
 * 
 */
public class SMS4 {

	private final static int ROUND = 32;
        private static short sbox[] = { 
		0xd6, 0x90, 0xe9, 0xfe, 0xcc, 0xe1, 0x3d,	0xb7, 0x16, 0xb6, 0x14, 0xc2, 0x28, 0xfb, 0x2c, 0x05, 
		 0x2b, 0x67, 0x9a,	0x76, 0x2a, 0xbe, 0x04, 0xc3, 0xaa, 0x44, 0x13, 0x26, 0x49, 0x86, 0x06,	0x99, 
		0x9c, 0x42, 0x50, 0xf4, 0x91, 0xef, 0x98, 0x7a, 0x33, 0x54, 0x0b,	0x43, 0xed, 0xcf, 0xac, 0x62, 
		0xe4, 0xb3, 0x1c, 0xa9, 0xc9, 0x08, 0xe8,	0x95, 0x80, 0xdf, 0x94, 0xfa, 0x75, 0x8f, 0x3f, 0xa6, 
 		0x47, 0x07, 0xa7,	0xfc, 0xf3, 0x73, 0x17, 0xba, 0x83, 0x59, 0x3c, 0x19, 0xe6, 0x85, 0x4f,	0xa8, 
		 0x68, 0x6b, 0x81, 0xb2, 0x71, 0x64, 0xda, 0x8b, 0xf8, 0xeb, 0x0f,	0x4b, 0x70, 0x56, 0x9d, 0x35, 
		0x1e, 0x24, 0x0e, 0x5e, 0x63, 0x58, 0xd1,	0xa2, 0x25, 0x22, 0x7c, 0x3b, 0x01, 0x21, 0x78, 0x87, 
		0xd4, 0x00, 0x46,	0x57, 0x9f, 0xd3, 0x27, 0x52, 0x4c, 0x36, 0x02, 0xe7, 0xa0, 0xc4, 0xc8,	0x9e, 
		 0xea, 0xbf, 0x8a, 0xd2, 0x40, 0xc7, 0x38, 0xb5, 0xa3, 0xf7, 0xf2,	0xce, 0xf9, 0x61, 0x15, 0xa1, 
		 0xe0, 0xae, 0x5d, 0xa4, 0x9b, 0x34, 0x1a,	0x55, 0xad, 0x93, 0x32, 0x30, 0xf5, 0x8c, 0xb1, 0xe3, 
		0x1d, 0xf6, 0xe2,	0x2e, 0x82, 0x66, 0xca, 0x60, 0xc0, 0x29, 0x23, 0xab, 0x0d, 0x53, 0x4e,	0x6f, 
		0xd5, 0xdb, 0x37, 0x45, 0xde, 0xfd, 0x8e, 0x2f, 0x03, 0xff, 0x6a,	0x72, 0x6d, 0x6c, 0x5b, 0x51, 
		0x8d, 0x1b, 0xaf, 0x92, 0xbb, 0xdd, 0xbc,	0x7f, 0x11, 0xd9, 0x5c, 0x41, 0x1f, 0x10, 0x5a, 0xd8, 
 		0x0a, 0xc1, 0x31,	0x88, 0xa5, 0xcd, 0x7b, 0xbd, 0x2d, 0x74, 0xd0, 0x12, 0xb8, 0xe5, 0xb4,	0xb0, 
		 0x89, 0x69, 0x97, 0x4a, 0x0c, 0x96, 0x77, 0x7e, 0x65, 0xb9, 0xf1,	0x09, 0xc5, 0x6e, 0xc6, 0x84, 
		0x18, 0xf0, 0x7d, 0xec, 0x3a, 0xdc, 0x4d,	0x20, 0x79, 0xee, 0x5f, 0x3e, 0xd7, 0xcb, 0x39, 0x48 
	};

	
	 private static int fk[] = { 0xa3b1bac6, 0x56aa3350, 0x677d9197, 0xb27022dc };

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

	 public static int rotateLeft(int x, int n) {
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
	 * 计算轮密钥rk
	 * @param input
	 * @param rk
	 */
 	private static void roundFun(int[] input, int rk,int idx) {
 		/*
 		int[] data = input;
 		for (int i = 0; i < data.length; i+=4) {
 			int tmp = input[i];
 	 		input[i] = input[i+1];
 			input[i+1] = input[i+2];
 	 		input[i+2] = input[i+3];
 			input[i+3] = tmp ^ ttrans(input[i] ^ input[i+1] ^ input[i+2] ^ rk);
		}*/
 		int first = idx;
 		int second = first+1;
 		int third = second+1;
 		int forth =third+1;
		int tmp = input[first];
 		input[first] = input[second];
		input[second] = input[third];
 		input[third] = input[forth];
		input[forth] = tmp ^ ttrans(input[first] ^ input[second] ^ input[third] ^ rk);
	 }

 	private static void roundBlock(int[] input, int[] rk,int idx) {
 		/*
 		int[] data = input;
 		for (int i = 0; i < data.length; i+=4) {
 			int tmp = input[i];
 	 		input[i] = input[i+1];
 			input[i+1] = input[i+2];
 	 		input[i+2] = input[i+3];
 			input[i+3] = tmp ^ ttrans(input[i] ^ input[i+1] ^ input[i+2] ^ rk);
		}*/
 		int[] buf = new int[36]; 		
 		int first = idx;
 		int second = first+1;
 		int third = second+1;
 		int forth =third+1;
 		
 		for (int j = 0; j < ROUND; j++) {
 			
 			buf[j] = input[first];
 			buf[j+1] = input[second];
 			buf[j+2] = input[third];
 			buf[j+3] = input[forth];
 			buf[j+4] = buf[j] ^ ttrans(buf[j+1] ^ buf[j+2] ^ buf[j+3] ^ rk[j]); 			
		}
 		int[] ebuf = new int[4]; 
 		ebuf[0] = buf[35];
 		ebuf[1] = buf[34];
 		ebuf[2] = buf[33];
 		ebuf[3] = buf[32];
 		input[first] = ebuf[0];
		input[second] = ebuf[1];
	 	input[third] = ebuf[2];
		input[forth] = ebuf[3];
	 }
 	
	 public static void reverse(int[] input, int idx) {
		 
		 int first = idx;
	 	 int second = first+1;
	 	 int third = second+1;
	 	 int forth =third+1;
		 int tmp;
		 tmp = input[first];	input[first] = input[forth];input[forth] = tmp;
		 tmp = input[second];	input[second] = input[third];input[third] = tmp;
 	}

 	public static int[] calcRk(int[] mainKey) {
		int rk[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] key=new int[4];
 		key[0] = mainKey[0];
		key[1] = mainKey[1];
		key[2] = mainKey[2];
 		key[3] = mainKey[3];
		
		key[0] ^= fk[0];
		key[1] ^= fk[1];
		key[2] ^= fk[2];
		key[3] ^= fk[3];
		 for (int i = 0; i < ROUND; i++) {
			 rk[i] = key[0] ^ keyTtrans(key[1] ^ key[2] ^ key[3] ^ ck[i]);
			key[0] = key[1];
			key[1] = key[2];
			 key[2] = key[3];
			key[3] = rk[i];
		 }
		 return rk;
	}

	/**
	 * 加密
	 * @param plain 明文
	 * @param key 密钥
	 * @return 密文
	 */
	public static int[] encrypt(int[] plain, int[] key) {
		int[] plainData=Arrays.copyOf(plain, plain.length);
		int rk[] = calcRk(key);
		
		for (int i = 0; i < plain.length; i=i+4) {
			roundBlock(plainData, rk,i);
		}
		
		return plainData;
	}
	
		
	/**
	 * 反复加密1000000次
	 * @param plain
	 * @param key
	 * @return
	 */
	public static int[] encryptMillion(int[] plain, int[] key) {
		 int[] plainData=Arrays.copyOf(plain, plain.length);
		int rk[] = calcRk(key);
		for(int j=0;j<1000000;j++) {
		   for (int i = 0; i < ROUND; i++) {
			 // roundFun(plainData, rk[i]);
		   }
		  //reverse(plainData);
 		}
		return plainData;
	}

	/**
	 * 解密
	 * @param cipher  密文
	 * @param key  密钥
	 * @return  明文
	 */
	public static int[] decrypt(int[] cipher, int[] key) {
		int[] cipherData=Arrays.copyOf(cipher, cipher.length);
		 int rk[] = calcRk(key);
		 int drk[] = new int[ROUND];
		 
		 for (int j = 0; j < ROUND; j++) {
			 drk[j]=rk[31-j];
		 }
		 
		 for (int i = 0; i < cipherData.length; i=i+4) {
				roundBlock(cipherData, drk,i);
			}
		 /*
		 for (int i = 0; i < cipher.length; i=i+4) {
			
			 for (int j = 0; j < ROUND; j++) {
					roundFun(cipherData, rk[31-j],i);
				reverse(cipherData,i);
				}
			}*/
		return cipherData;
	}
	
		
	
       public static int[] decryptMillion(int[] cipher, int[] key) {
 		int[] cipherData=Arrays.copyOf(cipher, cipher.length);
		int rk[] = calcRk(key);
		for(int j=0;j<1000000;j++) {
		   for (int i = 0; i < ROUND; i++) {
			  // roundFun(cipherData, rk[31-i]);
		   }
		   //reverse(cipherData);
 		}
 		return cipherData;
	 }
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		int[] plain={0x01234567, 0x89abcdef, 0xfedcba98,0x76543210};
		int[] key={0x01234567, 0x89abcdef, 0xfedcba98,0x76543210};
 		int[] cipher=encrypt(plain,key);
		 System.out.println("------------------");
 		System.out.println(Integer.toHexString(cipher[0]));
		 System.out.println(Integer.toHexString(cipher[1]));
		 System.out.println(Integer.toHexString(cipher[2]));
		 System.out.println(Integer.toHexString(cipher[3]));
		
		 int[] plain1=decrypt(cipher,key);
		 System.out.println("------------------");
 		System.out.println(Integer.toHexString(plain1[0]));
		 System.out.println(Integer.toHexString(plain1[1]));
		System.out.println(Integer.toHexString(plain1[2]));
		 System.out.println(Integer.toHexString(plain1[3]));
		System.out.println("------------------");
		
		int[] cipher1=encryptMillion(plain,key);
		System.out.println(Integer.toHexString(cipher1[0]));
		System.out.println(Integer.toHexString(cipher1[1]));
		System.out.println(Integer.toHexString(cipher1[2]));
		System.out.println(Integer.toHexString(cipher1[3]));
		System.out.println("------------------");
		int[] plain2=decryptMillion(cipher1,key);
		System.out.println(Integer.toHexString(plain2[0]));
		System.out.println(Integer.toHexString(plain2[1]));
		System.out.println(Integer.toHexString(plain2[2]));
		System.out.println(Integer.toHexString(plain2[3]));
		System.out.println("------------------");
			
	}

}
