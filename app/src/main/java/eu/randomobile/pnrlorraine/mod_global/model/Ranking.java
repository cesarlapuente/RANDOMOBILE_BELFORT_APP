package eu.randomobile.pnrlorraine.mod_global.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.util.Log;

import eu.randomobile.pnrlorraine.MainApp;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class Ranking {

	private String nombre;
	private int puntos;
	private String uid;


	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}

	public Ranking(){
		
	}
	public Ranking(String nombre, int puntos){
		this.nombre = nombre;
		this.puntos = puntos;
	}

	public String getNombre(){
		return nombre;
	}
	public void setNombre(String nombre){
		this.nombre = nombre;
	}

	public int getPuntos(){
		return puntos;
	}
	public void setPuntos(int puntos){
		this.puntos = puntos;
	}
	
	// Interface para comunicarse con las llamadas asíncronas
	public static RankingsInterface rankingsInterface;
	public static interface RankingsInterface {
		public void seCargoListaRankings(ArrayList<Ranking> rankings);
		public void producidoErrorAlCargarListaRankings(String error);
	}
	
	public static void cargarListaRanking(Application application){

		HashMap<String, String> params = null;
		Log.d("Milog", "Llamada al servicio");
		
		MainApp app = (MainApp)application;
		
		app.clienteDrupal.customMethodCallGet("ranking", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de cargar rankings: " + response);
				
				ArrayList<Ranking> listaRankings = null;
				
				if(response != null && !response.equals("")){
					
					try {
	                	JSONArray arrayRes = new JSONArray(response);
	                	if(arrayRes != null){
	                		if(arrayRes.length() > 0){
	                			Log.d("Milog", "array devuelto contiene al menos 1 elemento");
	                			listaRankings = new ArrayList<Ranking>();
	                		}
	                		
	                		for(int i=0; i< arrayRes.length(); i++){
	                			Object recObj = arrayRes.get(i);
	                			if(recObj != null){
	                				if(recObj.getClass().getName().equals(JSONObject.class.getName())){
	                					JSONObject recDic = (JSONObject)recObj;
	                					int puntos = recDic.getInt("points");
	                					String nombre = recDic.getString("name");
	                					String uid = recDic.getString("uid");

	                					Ranking item = new Ranking();
	                					item.setPuntos(puntos);
	                					item.setNombre(nombre);
	                					
	                					item.setUid(uid);
	                					
	                					listaRankings.add(item);
	                					
	                				}
	                			}
	                		}
	                		
	                		// Informar al delegate
	                		if(Ranking.rankingsInterface != null){
	                			Ranking.rankingsInterface.seCargoListaRankings(listaRankings);
	                			return;
	                		}
	                		
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion en rankings: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(Ranking.rankingsInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Ranking.rankingsInterface.producidoErrorAlCargarListaRankings("Error al cargar lista de rankings");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Ranking.rankingsInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error: " + error.toString());
	    			Ranking.rankingsInterface.producidoErrorAlCargarListaRankings(error.toString());
	    		}
			}
		}, 
		params);
		
	}
}