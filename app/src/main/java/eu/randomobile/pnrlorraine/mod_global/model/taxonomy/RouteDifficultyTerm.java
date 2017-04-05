package eu.randomobile.pnrlorraine.mod_global.model.taxonomy;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.util.Log;

import eu.randomobile.pnrlorraine.MainApp;
import com.loopj.android.http.AsyncHttpResponseHandler;


public class RouteDifficultyTerm extends Term {
	
	public static RouteDifficultiesInterface routeDifficultiesInterface;
	public static interface RouteDifficultiesInterface {
		public void seCargoListaDificultadesRutas(ArrayList<RouteDifficultyTerm> routeDifficulties);
		public void producidoErrorAlCargarListaDificultadesRutas(String error);
	}
	
	
	public static void cargarListaDificultadesRutas(Application application){

		HashMap<String, String> params = null;
		
		MainApp app = (MainApp)application;
		
		app.clienteDrupal.customMethodCallPost("taxonomy/difficulty", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de cargar dificultades routes: " + response);
				
				ArrayList<RouteDifficultyTerm> listaDificultades = null;
				
				if(response != null && !response.equals("")){
					try {
	                	JSONArray arrayRes = new JSONArray(response);
	                	if(arrayRes != null){
	                		if(arrayRes.length() > 0){
	                			Log.d("Milog", "array devuelto contiene al menos 1 elemento");
	                			listaDificultades = new ArrayList<RouteDifficultyTerm>();
	                		}
	                		
	                		for(int i=0; i< arrayRes.length(); i++){
	                			Object recObj = arrayRes.get(i);
	                			if(recObj != null){
	                				if(recObj.getClass().getName().equals(JSONObject.class.getName())){
	                					JSONObject recDic = (JSONObject)recObj;
	                					String tid = recDic.getString("tid");
	                					String title = recDic.getString("title");
	                					
	                					RouteDifficultyTerm item = new RouteDifficultyTerm();
	                					item.setTid(tid);
	                					item.setName(title);
	                					listaDificultades.add(item);
	                				}
	                			}
	                		}

	                		// Informar al delegate
	                		if(RouteDifficultyTerm.routeDifficultiesInterface != null){
	                			RouteDifficultyTerm.routeDifficultiesInterface.seCargoListaDificultadesRutas(listaDificultades);
	                			return;
	                		}
	                		
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion en lista dificultades rutas: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(RouteDifficultyTerm.routeDifficultiesInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			RouteDifficultyTerm.routeDifficultiesInterface.producidoErrorAlCargarListaDificultadesRutas("Error al cargar lista de pois");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(RouteDifficultyTerm.routeDifficultiesInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error: " + error.toString());
	    			RouteDifficultyTerm.routeDifficultiesInterface.producidoErrorAlCargarListaDificultadesRutas(error.toString());
	    		}
			}
		}, 
		params);
		
	}
	
	

}
