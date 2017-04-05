package eu.randomobile.pnrlorraine.mod_global.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.util.Log;

import eu.randomobile.pnrlorraine.MainApp;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class Checkin {
	
	private String nid;
	private String title;
	private String body;
	private String image;
	private Poi poi;
	private GeoPoint checkinCoordinates;
	
	public String getNid() {
		return nid;
	}
	public void setNid(String nid) {
		this.nid = nid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Poi getPoi() {
		return poi;
	}
	public void setPoi(Poi poi) {
		this.poi = poi;
	}
	public GeoPoint getCheckinCoordinates() {
		return checkinCoordinates;
	}
	public void setCheckinCoordinates(GeoPoint checkinCoordinates) {
		this.checkinCoordinates = checkinCoordinates;
	}

	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}




	// Interface para comunicarse con las llamadas asincronas
	public static CheckinInterface checkinInterface;
	public static interface CheckinInterface {
		public void seCargoListaCheckins(ArrayList<Checkin> checkins);
		public void producidoErrorAlCargarListaCheckins(String error);
		public void seCargoCheckin(Checkin checkin);
		public void producidoErrorAlCargarCheckin(String error);
		public void seHaRealizadoCheckin(boolean res, String nidCheckin);
		public void producidoErrorAlRealizarCheckin(String strError, int errorCode);
	}
	
	
	
	
	public static void cargarListaCheckins(Application application, String nidPoi, String uidUsuario){

		HashMap<String, String> params = new HashMap<String, String>();
		if(nidPoi != null){
			params.put("nid", nidPoi);
		}
		if(uidUsuario != null){
			params.put("uid", uidUsuario);
		}

		
		Log.d("Milog", "Parametros enviados a checkin/get_list: " + params.toString());

		MainApp app = (MainApp)application;
		
		app.clienteDrupal.customMethodCallPost("checkin/get_list", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de cargar checkins: " + response);
				
				ArrayList<Checkin> listaCheckins = null;
				
				if(response != null && !response.equals("")){
					
					try {
	                	JSONArray arrayRes = new JSONArray(response);
	                	if(arrayRes != null){
	                		if(arrayRes.length() > 0){
	                			Log.d("Milog", "array devuelto contiene al menos 1 elemento");
	                			listaCheckins = new ArrayList<Checkin>();
	                		}
	                		
	                		for(int i=0; i< arrayRes.length(); i++){
	                			Object recObj = arrayRes.get(i);
	                			if(recObj != null){
	                				if(recObj.getClass().getName().equals(JSONObject.class.getName())){
	                					JSONObject recDic = (JSONObject)recObj;
	                					String nid = recDic.getString("nid");
	                					String title = recDic.getString("title");
	                					String body = recDic.getString("body");
	                					double lat = recDic.getDouble("lat");
	                					double lon = recDic.getDouble("lon");
	                					double alt = recDic.getDouble("altitude");
	                					String image = recDic.getString("image");
	                					String poiNid = recDic.getString("poi");
	                					
	                					Checkin checkin = new Checkin();
	                					checkin.setNid(nid);
	                					checkin.setTitle(title);
	                					checkin.setBody(body);
	                					
	                					GeoPoint gp = new GeoPoint();
	                					gp.setLatitude(lat);
	                					gp.setLongitude(lon);
	                					gp.setAltitude(alt);
	                					checkin.setCheckinCoordinates(gp);
	                					
	                					checkin.setImage(image);
	                					
	                					Poi poi = new Poi();
	                					poi.setNid(poiNid);
	                					checkin.setPoi(poi);
	                						
	                					
	                					
	                					listaCheckins.add(checkin);
	                				}
	                			}
	                		}

	                		// Informar al delegate
	                		if(Checkin.checkinInterface != null){
	                			Checkin.checkinInterface.seCargoListaCheckins(listaCheckins);
	                			return;
	                		}
	                		
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion en lista checkins: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(Checkin.checkinInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Checkin.checkinInterface.producidoErrorAlCargarListaCheckins("Error al cargar lista de checkins");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Checkin.checkinInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error: " + error.toString());
	    			Checkin.checkinInterface.producidoErrorAlCargarListaCheckins(error.toString());
	    		}
			}
		}, 
		params);
		
	}
	
	
	
	public static void cargarCheckin(Application application, String nid) {

		MainApp app = (MainApp) application;
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nid", nid);

		app.clienteDrupal.customMethodCallPost("checkin/get_item",
				new AsyncHttpResponseHandler() {
					public void onSuccess(String response) {
						Log.d("Milog", "Exito al cargar checkin: " + response);

						if(response != null && !response.equals("")){
							
							try {
			                	JSONObject dicRes = new JSONObject(response);
			                	if(dicRes != null){
			                		
			                		String nid = dicRes.getString("nid");
                					String title = dicRes.getString("title");
                					String body = dicRes.getString("body");
                					double lat = dicRes.getDouble("lat");
                					double lon = dicRes.getDouble("lon");
                					double alt = dicRes.getDouble("altitude");
                					String image = dicRes.getString("image");
                					String poiNid = dicRes.getString("poi");
                					
                					Checkin checkin = new Checkin();
                					checkin.setNid(nid);
                					checkin.setTitle(title);
                					checkin.setBody(body);
                					
                					GeoPoint gp = new GeoPoint();
                					gp.setLatitude(lat);
                					gp.setLongitude(lon);
                					gp.setAltitude(alt);
                					checkin.setCheckinCoordinates(gp);
                					
                					checkin.setImage(image);
                					
                					Poi poi = new Poi();
                					poi.setNid(poiNid);
                					checkin.setPoi(poi);
			                		
			                		// Informar al delegate
			                		if(Checkin.checkinInterface != null){
			                			Checkin.checkinInterface.seCargoCheckin(checkin);
			                			return;
			                		}
			                		
			                	}
			                	

			                } catch (Exception e) {
								Log.d("Milog", "Excepcion cargar checkin: " + e.toString());
							}
						}
						
						// Informar al delegate
			    		if(Checkin.checkinInterface != null){
			    			Log.d("Milog", "Antes de informar al delegate de un error");
			    			Checkin.checkinInterface.producidoErrorAlCargarCheckin("Error al cargar checkin");
			    		}
						
					}

					public void onFailure(Throwable error) {
						// Informar al delegate
						if (Checkin.checkinInterface != null) {
							Log.d("Milog",
									"Antes de informar al delegate de un error: " + error.toString());
							Checkin.checkinInterface
									.producidoErrorAlCargarCheckin(error
											.toString());
						}
					}
				}, params);

	}
	
	
	
	public static void realizarCheckin(Application application, String nidPoi, String lat, String lon, String alt, String body, String fidImage){

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("poi", nidPoi);
		params.put("lat", lat);
		params.put("lon", lon);
		params.put("alt", alt);
		
		if(body != null){
			params.put("body", body);
		}
		
		if(fidImage != null){
			params.put("image", fidImage);
		}
		
	
		
		MainApp app = (MainApp)application;

		app.clienteDrupal.customMethodCallPost("checkin/create", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de capturar geocache: " + response);
				if(response != null && !response.equals("")){
					
					try {
	                	JSONObject dicRes = new JSONObject(response);
	                	if(dicRes != null){
	                		
	                		boolean success = dicRes.getBoolean("success");
	                		int error = dicRes.getInt("error");
	                		String nid = dicRes.getString("nid");
	                		
	                		if(error == 0){
	                			// Si error == 0, todo ha ido de puta madre
	                			if(success){
		                			// Esto es que ha respondido todo bien
	                				// Informar al delegate
	    	                		if(Checkin.checkinInterface != null){
	    	                			Checkin.checkinInterface.seHaRealizadoCheckin(true, nid);
	    	                			return;
	    	                		}
		                		}else{
		                			// Esto es que ha respondido muy mal
	                				// Informar al delegate
	    	                		if(Checkin.checkinInterface != null){
	    	                			Checkin.checkinInterface.seHaRealizadoCheckin(false, null);
	    	                			return;
	    	                		}
		                		}
	                		}else{
	                			// Informar al delegate
	            	    		if(Checkin.checkinInterface != null){
	            	    			Log.d("Milog", "Antes de informar al delegate de un error: " + error);
	            	    			// Devuelve error = 1 si ya ha superado el l’mite de checkins
	            	    			Checkin.checkinInterface.producidoErrorAlRealizarCheckin("", error);
	            	    			return;
	            	    		}
	                		}
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion al hacer checkin: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(Checkin.checkinInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Checkin.checkinInterface.producidoErrorAlRealizarCheckin("Error al recoger respuesta", 0);
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Checkin.checkinInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Checkin.checkinInterface.producidoErrorAlRealizarCheckin(error.toString(), 0);
	    		}
			}
		}, 
		params);
		
	}
	
	
	
	
	
}
