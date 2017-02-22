package eu.randomobile.pnrlorraine.mod_global.model.taxonomy;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import eu.randomobile.pnrlorraine.MainApp;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.Application;
import android.graphics.Color;
import android.util.Log;

public class RouteCategoryTerm extends Term {
	
	private Color color;

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	
	public static RouteCategoriesInterface routeCategoriesInterface;
	public static interface RouteCategoriesInterface {
		public void seCargoListaCategoriasRutas(ArrayList<RouteCategoryTerm> routeCategories);
		public void producidoErrorAlCargarListaCategoriasRutas(String error);
	}
	
	
	public static void cargarListaCategoriasRutas(Application application){

		HashMap<String, String> params = null;
		
		MainApp app = (MainApp)application;
		
		app.clienteDrupal.customMethodCallPost("taxonomy/routes", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de cargar categorias routes: " + response);
				
				ArrayList<RouteCategoryTerm> listaCategorias = null;
				
				if(response != null && !response.equals("")){
					
					try {
	                	JSONArray arrayRes = new JSONArray(response);
	                	if(arrayRes != null){
	                		if(arrayRes.length() > 0){
	                			Log.d("Milog", "array devuelto contiene al menos 1 elemento");
	                			listaCategorias = new ArrayList<RouteCategoryTerm>();
	                		}
	                		
	                		for(int i=0; i< arrayRes.length(); i++){
	                			Object recObj = arrayRes.get(i);
	                			if(recObj != null){
	                				if(recObj.getClass().getName().equals(JSONObject.class.getName())){
	                					JSONObject recDic = (JSONObject)recObj;
	                					String tid = recDic.getString("tid");
	                					String title = recDic.getString("title");
	                					
	                					RouteCategoryTerm item = new RouteCategoryTerm();
	                					item.setTid(tid);
	                					item.setName(title);
	                					listaCategorias.add(item);
	                				}
	                			}
	                		}

	                		// Informar al delegate
	                		if(RouteCategoryTerm.routeCategoriesInterface != null){
	                			RouteCategoryTerm.routeCategoriesInterface.seCargoListaCategoriasRutas(listaCategorias);
	                			return;
	                		}
	                		
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion en lista categorias rutas: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(RouteCategoryTerm.routeCategoriesInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			RouteCategoryTerm.routeCategoriesInterface.producidoErrorAlCargarListaCategoriasRutas("Error al cargar lista de pois");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(RouteCategoryTerm.routeCategoriesInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error: " + error.toString());
	    			RouteCategoryTerm.routeCategoriesInterface.producidoErrorAlCargarListaCategoriasRutas(error.toString());
	    		}
			}
		}, 
		params);
		
	}
	
	
	
}
