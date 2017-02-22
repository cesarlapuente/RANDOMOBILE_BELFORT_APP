package eu.randomobile.pnrlorraine.mod_global.libraries.mcrypt;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

public class MCrypt {

		private String SecretKey;	// SecretKey
        private String iv;			// IV
        
        
        
        private IvParameterSpec ivspec;
        private SecretKeySpec keyspec;
        private Cipher cipher;
        
        
        
        public MCrypt(String _secretKey, String _iv)
        {
        		// Put the key and iv
        	SecretKey = _secretKey;
        	iv = _iv;
        	
        	
                ivspec = new IvParameterSpec(iv.getBytes());

                keyspec = new SecretKeySpec(SecretKey.getBytes(), "AES");
                
                try {
                        cipher = Cipher.getInstance("AES/CBC/NoPadding");
                } catch (NoSuchAlgorithmException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }
        
        public byte[] encrypt(String text) throws Exception
        {
                if(text == null || text.length() == 0)
                        throw new Exception("Empty string");
                
                byte[] encrypted = null;

                try {
                        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

                        encrypted = cipher.doFinal(padString(text).getBytes());
                } catch (Exception e)
                {                       
                        throw new Exception("[encrypt] " + e.getMessage());
                }
                
                return encrypted;
        }
        
        public byte[] decrypt(String code) throws Exception
        {
                if(code == null || code.length() == 0)
                        throw new Exception("Empty string");
                
                byte[] decrypted = null;

                try {
                        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
                        
                        decrypted = cipher.doFinal(hexToBytes(code));
                        
                        //Remove trailing zeroes
                        if( decrypted.length > 0)
                        {
                            int trim = 0;
                            for( int i = decrypted.length - 1; i >= 0; i-- ) if( decrypted[i] == 0 ) trim++;

                            if( trim > 0 )
                            {
                                byte[] newArray = new byte[decrypted.length - trim];
                                System.arraycopy(decrypted, 0, newArray, 0, decrypted.length - trim);
                                decrypted = newArray;
                            }
                        }
                        
                        
                } catch (Exception e)
                {
                        throw new Exception("[decrypt] " + e.getMessage());
                }
                return decrypted;
        }
        

        
        public static String bytesToHex(byte[] data)
        {
                if (data==null)
                {
                        return null;
                }
                
                int len = data.length;
                String str = "";
                for (int i=0; i<len; i++) {
                        if ((data[i]&0xFF)<16)
                                str = str + "0" + java.lang.Integer.toHexString(data[i]&0xFF);
                        else
                                str = str + java.lang.Integer.toHexString(data[i]&0xFF);
                }
                return str;
        }
        
                
        public static byte[] hexToBytes(String str) {
                if (str==null) {
                        return null;
                } else if (str.length() < 2) {
                        return null;
                } else {
                        int len = str.length() / 2;
                        byte[] buffer = new byte[len];
                        for (int i=0; i<len; i++) {
                                buffer[i] = (byte) Integer.parseInt(str.substring(i*2,i*2+2),16);
                        }
                        return buffer;
                }
        }
        
        
        public static String hexToString(String hex){

      	  StringBuilder sb = new StringBuilder();
      	  StringBuilder temp = new StringBuilder();

      	  for( int i=0; i<hex.length()-1; i+=2 ){

      	      //grab the hex in pairs
      	      String output = hex.substring(i, (i + 2));
      	      //convert hex to decimal
      	      int decimal = Integer.parseInt(output, 16);
      	      //convert the decimal to character
      	      sb.append((char)decimal);

      	      temp.append(decimal);
      	  }
      	  Log.d("Milog", "Decimal : " + temp.toString());
      	  return sb.toString();
      	}
        

        private static String padString(String source)
        {
          char paddingChar = ' ';
          int size = 16;
          int x = source.length() % size;
          int padLength = size - x;

          for (int i = 0; i < padLength; i++)
          {
                  source += paddingChar;
          }

          return source;
        }
}
