package eu.randomobile.pnrlorraine.mod_global.model;

import java.util.HashMap;

import android.app.Application;
import android.util.Log;

import eu.randomobile.pnrlorraine.MainApp;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class Test {

	
	public static void test(Application application){

		MainApp app = (MainApp)application;
		
		String nid = "10";
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nid", nid);

		String keyGen = app.drupalSecurity.encrypt(nid);

		params.put("key", keyGen);
		
		

		app.clienteDrupal.customMethodCallPost("debug/capture", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				Log.d("Clave", "Respuesta test: " + response);
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				Log.d("Clave", "Antes de informar al delegate de un error: " + error.toString());
			}
		}, 
		params);
		
	}
	
	
}
