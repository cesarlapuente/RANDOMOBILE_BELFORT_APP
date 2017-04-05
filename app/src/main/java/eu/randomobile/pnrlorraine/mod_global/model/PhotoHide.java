package eu.randomobile.pnrlorraine.mod_global.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.util.Log;

import eu.randomobile.pnrlorraine.MainApp;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class PhotoHide {
	private String nid;
	private String title;
	private String body;
	private String image;
	private Poi poi;
	private ArrayList<ResponseOption> options;
	private String idAnswer;
	private int points;
	private boolean done;
	private int attempts;

	// Interface para comunicarse con las llamadas asíncronas
	public static PhotoHideInterface photoHideInterface;
	public static interface PhotoHideInterface {
		public void seCargoListaPhotoHides(ArrayList<PhotoHide> photoHides);
		public void producidoErrorAlCargarListaPhotoHides(String error);
		public void seCargoPhotoHide(PhotoHide photoHide);
		public void producidoErrorAlCargarPhotoHide(String error);
		public void seHaResueltoPhotoHide(boolean res, int pointsEarned);
		public void producidoErrorAlResolverPhotoHide(String error);
	}
	
	
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
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Poi getPoi() {
		return poi;
	}
	public void setPoi(Poi poi) {
		this.poi = poi;
	}
	public ArrayList<ResponseOption> getOptions() {
		return options;
	}
	public void setOptions(ArrayList<ResponseOption> options) {
		this.options = options;
	}
	public String getIdAnswer() {
		return idAnswer;
	}
	public void setIdAnswer(String idAnswer) {
		this.idAnswer = idAnswer;
	}
	public boolean isDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done = done;
	}
	public int getAttempts() {
		return attempts;
	}
	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}



	
	
	
	public static void cargarListaPhotoHides(Application application){

		HashMap<String, String> params = null;

		MainApp app = (MainApp)application;
		
		app.clienteDrupal.customMethodCallPost("photohide/get_list", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de cargar photo hides: " + response);
				
				ArrayList<PhotoHide> listaPhotoHides = null;
				
				if(response != null && !response.equals("")){
					
					try {
	                	JSONArray arrayRes = new JSONArray(response);
	                	if(arrayRes != null){
	                		if(arrayRes.length() > 0){
	                			Log.d("Milog", "array devuelto contiene al menos 1 elemento");
	                			listaPhotoHides = new ArrayList<PhotoHide>();
	                		}
	                		
	                		for(int i=0; i< arrayRes.length(); i++){
	                			Object recObj = arrayRes.get(i);
	                			if(recObj != null){
	                				if(recObj.getClass().getName().equals(JSONObject.class.getName())){
	                					JSONObject recDic = (JSONObject)recObj;
	                					String nid = recDic.getString("nid");
	                					String title = recDic.getString("title");
	                					String image = recDic.getString("image");
	                					boolean done = recDic.getBoolean("done");
	                					int attempts = recDic.getInt("attempts");
	                					PhotoHide item = new PhotoHide();
	                					item.setNid(nid);
	                					item.setTitle(title);
	                					item.setImage(image);
	                					item.setDone(done);
	                					item.setAttempts(attempts);
	                					listaPhotoHides.add(item);
	                					
	                				}
	                			}
	                		}
	                		
	                		// Informar al delegate
	                		if(PhotoHide.photoHideInterface != null){
	                			PhotoHide.photoHideInterface.seCargoListaPhotoHides(listaPhotoHides);
	                			return;
	                		}
	                		
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion en photo hides: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(PhotoHide.photoHideInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			PhotoHide.photoHideInterface.producidoErrorAlCargarListaPhotoHides("Error al cargar lista de photo hides");
	    		}
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(PhotoHide.photoHideInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error: " + error.toString());
	    			PhotoHide.photoHideInterface.producidoErrorAlCargarListaPhotoHides(error.toString());
	    		}
			}
		}, 
		params);
		
	}
	
	
	
	public static void cargarPhotoHide(Application application, String nid){

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nid", nid);
		
		MainApp app = (MainApp)application;
		
		app.clienteDrupal.customMethodCallPost("photohide/get_item", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de cargar un photo hide: " + response);
				
				PhotoHide photoHide = null;
				
				if(response != null && !response.equals("")){
					
					try {
	                	JSONObject dicRes = new JSONObject(response);
	                	if(dicRes != null){
	                		
	                		photoHide = new PhotoHide();
	                		
	                		String nid = dicRes.getString("nid");
	                		String body = dicRes.getString("body");
	                		String image = dicRes.getString("image");
	                		boolean done = dicRes.getBoolean("done");
	                		int attempts = dicRes.getInt("attempts");
	                		int points = dicRes.getInt("points");
	                		
	                		Object objOptions = dicRes.get("options");
	                		if(objOptions != null && objOptions.getClass().getName().equals(JSONArray.class.getName())){
	                			JSONArray arrayOptions = (JSONArray)objOptions;
	                			ArrayList<ResponseOption> opcionesRespuesta = new ArrayList<ResponseOption>();
	                			for(int i=0; i<arrayOptions.length(); i++){
	                				Object objOption = arrayOptions.get(i);
	                				if(objOption != null && objOption.getClass().getName().equals(JSONObject.class.getName())){
	                					JSONObject dicOption = (JSONObject)objOption;
	                					ResponseOption opcion = new ResponseOption();
	                					opcion.setId(dicOption.getString("id"));
	                					opcion.setTexto(dicOption.getString("text"));
	                					opcionesRespuesta.add(opcion);
	                				}
	                			}
	                			photoHide.setOptions(opcionesRespuesta);
	                		}
	                		
	                		photoHide.setNid(nid);
	                		photoHide.setBody(body);
	                		photoHide.setImage(image);
	                		photoHide.setDone(done);
	                		photoHide.setAttempts(attempts);
	                		photoHide.setPoints(points);

	                		// Informar al delegate
	                		if(PhotoHide.photoHideInterface != null){
	                			PhotoHide.photoHideInterface.seCargoPhotoHide(photoHide);
	                			return;
	                		}
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion get photo hide: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(PhotoHide.photoHideInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			PhotoHide.photoHideInterface.producidoErrorAlCargarPhotoHide("Error al cargar photo hide");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(PhotoHide.photoHideInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			PhotoHide.photoHideInterface.producidoErrorAlCargarPhotoHide(error.toString());
	    		}
			}
		}, 
		params);
		
	}
	
	
	
	public static void resolverPhotoHide(Application application, String nidPhotoHide, String idRespuesta){

		MainApp app = (MainApp)application;
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nid", nidPhotoHide);
		params.put("response", idRespuesta);
		params.put("key", app.drupalSecurity.encrypt(nidPhotoHide));
		
		

		app.clienteDrupal.customMethodCallPost("photohide/solve", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de resolver photohide: " + response);
				if(response != null && !response.equals("")){
					
					try {
	                	JSONObject dicRes = new JSONObject(response);
	                	if(dicRes != null){
	                		
	                		boolean success = dicRes.getBoolean("success");
	                		int error = dicRes.getInt("error");
	                		int points = dicRes.getInt("points");
	                		
	                		if(error == 0){
	                			// Si error == 0, todo ha ido de puta madre
	                			if(success){
		                			// Esto es que ha respondido todo bien
	                				// Informar al delegate
	    	                		if(PhotoHide.photoHideInterface != null){
	    	                			PhotoHide.photoHideInterface.seHaResueltoPhotoHide(true, points);
	    	                			return;
	    	                		}
		                		}else{
		                			// Esto es que ha respondido muy mal
	                				// Informar al delegate
	    	                		if(PhotoHide.photoHideInterface != null){
	    	                			PhotoHide.photoHideInterface.seHaResueltoPhotoHide(false, 0);
	    	                			return;
	    	                		}
		                		}
	                		}else{
	                			// Informar al delegate
	            	    		if(PhotoHide.photoHideInterface != null){
	            	    			Log.d("Milog", "Antes de informar al delegate de un error: " + error);
	            	    			PhotoHide.photoHideInterface.producidoErrorAlResolverPhotoHide("No se puede procesar la respuesta en el servidor");
	            	    			return;
	            	    		}
	                		}
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion resolver photo hide: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(PhotoHide.photoHideInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			PhotoHide.photoHideInterface.producidoErrorAlResolverPhotoHide("Error al recoger respuesta");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(PhotoHide.photoHideInterface!= null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			PhotoHide.photoHideInterface.producidoErrorAlResolverPhotoHide(error.toString());
	    		}
			}
		}, 
		params);
		
	}
	
	
	
}
