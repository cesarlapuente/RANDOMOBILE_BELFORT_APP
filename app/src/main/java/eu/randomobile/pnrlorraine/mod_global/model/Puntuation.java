package eu.randomobile.pnrlorraine.mod_global.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.util.Log;

import eu.randomobile.pnrlorraine.MainApp;
import com.loopj.android.http.AsyncHttpResponseHandler;


public class Puntuation {

	private String descripcion;
	private int puntos;
	private String date;
	
	


	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	public Puntuation(){
		
	}
	public Puntuation(String descripcion, int puntos){
		this.setDescripcion(descripcion);
		this.puntos = puntos;
	}


	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public int getPuntos(){
		return puntos;
	}
	public void setPuntos(int puntos){
		this.puntos = puntos;
	}
	
	// Interface para comunicarse con las llamadas asíncronas
	public static PuntuacionInterface puntuacionInterface;
	public static interface PuntuacionInterface {
		public void seCargoListaPuntuaciones(ArrayList<Puntuation> puntuaciones);
		public void producidoErrorAlCargarListaPuntuaciones(String error);
		
		public void seCargoPuntuacionUsuario(int puntuacion);
		public void producidoErrorAlCargarPuntuacionUsuario(String error);
	}
	
	public static void cargarListaPuntuaciones(Application application, String uid){

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", uid);
		
		Log.d("Milog", "Llamada al servicio");
		
		MainApp app = (MainApp)application;
		
		app.clienteDrupal.customMethodCallGet("points_log", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de cargar puntos: " + response);
				
				ArrayList<Puntuation> listaPuntos = null;
				
				if(response != null && !response.equals("")){
					
					try {
	                	JSONArray arrayRes = new JSONArray(response);
	                	if(arrayRes != null){
	                		if(arrayRes.length() > 0){
	                			Log.d("Milog", "array devuelto contiene al menos 1 elemento");
	                			listaPuntos = new ArrayList<Puntuation>();
	                		}
	                		
	                		for(int i=0; i< arrayRes.length(); i++){
	                			Object recObj = arrayRes.get(i);
	                			if(recObj != null){
	                				if(recObj.getClass().getName().equals(JSONObject.class.getName())){
	                					JSONObject recDic = (JSONObject)recObj;
	                					
	                					
	                					
	                					int puntos = recDic.getInt("points");
	                					String descripcion = "";
	                					if(!recDic.isNull("description")){
	                						descripcion = recDic.getString("description");
	                					}
	
	                					String date = recDic.getString("thedate");

	                					Puntuation item = new Puntuation();
	                					item.setDescripcion(descripcion);
	                					item.setPuntos(puntos);
	                					item.setDate(date);
	                					
	                					
	                					
	                					listaPuntos.add(item);
	                					
	                				}
	                			}
	                		}
	                		
	                		// Informar al delegate
	                		if(Puntuation.puntuacionInterface != null){
	                			Puntuation.puntuacionInterface.seCargoListaPuntuaciones(listaPuntos);
	                			return;
	                		}
	                		
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion en puntuaciones: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(Puntuation.puntuacionInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Puntuation.puntuacionInterface.producidoErrorAlCargarListaPuntuaciones("Error al cargar lista de puntuaciones");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Puntuation.puntuacionInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error: " + error.toString());
	    			Puntuation.puntuacionInterface.producidoErrorAlCargarListaPuntuaciones(error.toString());
	    		}
			}
		}, 
		params);
		
	}
	
	
	
	
	public static void cargarPuntuacionUsuario(Application application){

		MainApp app = (MainApp)application;
		
		app.clienteDrupal.customMethodCallPost("basic/passport", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				Log.d("Milog", "Respuesta de cargar puntucion: " + response);
				if(response != null && !response.equals("")){
					
					try {
	                	JSONObject dicRes = new JSONObject(response);
	                	if(dicRes != null){

	                		int puntuacion = dicRes.getInt("points");

	                		// Informar al delegate
	                		if(Puntuation.puntuacionInterface != null){
	                			Puntuation.puntuacionInterface.seCargoPuntuacionUsuario(puntuacion);
	                			return;
	                		}
	                		
	                	}
	                } catch (Exception e) {
						Log.d("Milog", "Excepcion get puntuacion: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(Puntuation.puntuacionInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Puntuation.puntuacionInterface.producidoErrorAlCargarPuntuacionUsuario("Error al cargar puntuacion");
	    		}
			}

			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Puntuation.puntuacionInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Puntuation.puntuacionInterface.producidoErrorAlCargarPuntuacionUsuario(error.toString());
	    		}
			}
		}, 
		null);
		
	}
	
	
	
}
