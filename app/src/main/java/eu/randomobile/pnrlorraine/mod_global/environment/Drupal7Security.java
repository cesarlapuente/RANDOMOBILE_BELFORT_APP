package eu.randomobile.pnrlorraine.mod_global.environment;

import java.util.ArrayList;
import java.util.Random;

import android.util.Log;

import eu.randomobile.pnrlorraine.mod_global.libraries.mcrypt.MCrypt;

public class Drupal7Security {
	
	protected static final String secretKey = "G4U8r48G3dj6a4wL";				// SecretKey
	protected static final String iv 		= "GHSkhnI4783f72D9";				// IV

	
	private MCrypt mcrypt;
	private Random randomGenerator;
    private ArrayList<String> keys;

    public Drupal7Security () { 
    	keys = new ArrayList<String>();
    	keys.add("X339w64o");
    	keys.add("f51y5nEh");
    	keys.add("P048s9FE");
    	keys.add("k6bSL8w2");
    	keys.add("7a6U185c");
    	keys.add("2q3I3f8t");
    	keys.add("tWQhk8bh");
    	keys.add("V3V141aW");
    	keys.add("WN33kdvi");
    	keys.add("z3I9Wrl9");

    	mcrypt = new MCrypt(secretKey, iv);

        randomGenerator = new Random();
    }

    // Get a random key
    public String getRandomKey()  {
        int index = randomGenerator.nextInt(keys.size());
        String item = keys.get(index);
        return item;
    }
    
    // Get a random key crypted
    public String getCryptedRandomKey(){
    	return encrypt(getRandomKey());
    }

    // Encriptar
    public String encrypt(String plainTxt){
    	
    	String keyFull = getRandomKey() + plainTxt;

    	String encrypted = null;
    	/* Encrypt */
    	try {
    		encrypted = MCrypt.bytesToHex( mcrypt.encrypt(keyFull) );
    		
    		Log.d("Milog", "Datos encriptados: " + encrypted);
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return encrypted;
    }
    
    // Desencriptar
    public String decrypt(String cryptedTxt){
    	
    	String decrypted = null;
    	String clave = null;
		String cadena = null;
    	
    	/* Decrypt */
    	try {
    		byte[] byteArrayDecrypt = mcrypt.decrypt( cryptedTxt );
			decrypted = new String( byteArrayDecrypt);
			
			clave = decrypted.substring(0, 8);
			cadena = decrypted.substring(8);
			
			Log.d("Milog", "Decripted: " + decrypted);
			Log.d("Milog", "Clave: " + clave);
			Log.d("Milog", "Cadena: " + cadena + " Longitud cadena: " + cadena.length());
			
			if( !keys.contains(clave) ) {
				cadena = "";
			}
			
			
		} catch (Exception e) {
			Log.d("Milog", "Excepcion al desencriptar: " + e.toString());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return cadena;
    }
}
