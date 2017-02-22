package eu.randomobile.pnrlorraine.mod_global.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.util.Log;

import eu.randomobile.pnrlorraine.MainApp;
import com.loopj.android.http.AsyncHttpResponseHandler;

import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.PoiCategoryTerm;

public class Enigma {
	
	private String nid;
	private String title;
	private String body;
	private String image;
	private Poi poi;
	private ArrayList<ResponseOption> options;
	private String idAnswer;
	private int points;
	private ArrayList<Tip> tips;
	private boolean done;
	private Badge badge;
	
	
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



	public int getPoints() {
		return points;
	}



	public void setPoints(int points) {
		this.points = points;
	}



	public ArrayList<Tip> getTips() {
		return tips;
	}



	public void setTips(ArrayList<Tip> tips) {
		this.tips = tips;
	}



	public boolean isDone() {
		return done;
	}



	public void setDone(boolean done) {
		this.done = done;
	}



	public Badge getBadge() {
		return badge;
	}



	public void setBadge(Badge badge) {
		this.badge = badge;
	}



	public static EnigmasInterface getEnigmasInterface() {
		return enigmasInterface;
	}



	public static void setEnigmasInterface(EnigmasInterface enigmasInterface) {
		Enigma.enigmasInterface = enigmasInterface;
	}



	// Interface para comunicarse con las llamadas asíncronas
	public static EnigmasInterface enigmasInterface;
	public static interface EnigmasInterface {
		public void seCargoEnigma(Enigma enigma);
		public void producidoErrorAlCargarEnigma(String error);
		
		public void seHaResueltoEnigma(boolean res);
		public void producidoErrorAlResolverEnigma(String error);
	}

	
	
	public static void cargarEnigma(Application application, final String nid){

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nid", nid);
		
		Log.d("Milog", "NID pasado para cargar engima: " + nid);
		
		MainApp app = (MainApp)application;
		
		app.clienteDrupal.customMethodCallPost("enigma/get_item", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de cargar un enigma: " + response);
				
				Enigma enigma = null;
				
				if(response != null && !response.equals("")){
					
					try {
	                	JSONObject dicRes = new JSONObject(response);
	                	if(dicRes != null){
	                		enigma = new Enigma();
	                		
	                		enigma.setNid(nid);
	                		enigma.setTitle( dicRes.getString("title") );
	                		enigma.setBody( dicRes.getString("body") );
	                		enigma.setImage( dicRes.getString("image") );
	                		
	                		Object objPoi = dicRes.get("poi");
	                		if(objPoi != null && objPoi.getClass().getName().equals(JSONObject.class.getName())){
	                			JSONObject dicPoi = (JSONObject)objPoi;
	                			Poi poi = new Poi();
	                			poi.setNid(dicPoi.getString("nid"));
	                			poi.setTitle(dicPoi.getString("title"));
	                			
	                			GeoPoint gp = new GeoPoint();
	                			gp.setLatitude( dicPoi.getDouble("lat") );
	                			gp.setLongitude( dicPoi.getDouble("lon") );
	                			poi.setCoordinates(gp);
	                			
	                			Object objCatPoi = dicPoi.get("cat");
	                			if(objCatPoi != null && objCatPoi.getClass().getName().equals(JSONObject.class.getName())){
	                				JSONObject dicCatPoi = (JSONObject)objCatPoi;
	                				PoiCategoryTerm poiCat = new PoiCategoryTerm();
	                				poiCat.setTid( dicCatPoi.getString("tid") );
	                				poiCat.setName( dicCatPoi.getString("name") );
	                				poiCat.setIcon( dicCatPoi.getString("image") );
	                				poi.setCategory( poiCat );
	                			}
	                			
	                			
	                			enigma.setPoi(poi);
	                		}
	                		
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
	                			enigma.setOptions(opcionesRespuesta);
	                		}
	                		
	                		enigma.setIdAnswer(dicRes.getString("answer"));
	                		
	                		if(!dicRes.isNull("points")){
	                			String pointsStr = dicRes.getString("points");
	                			enigma.setPoints( Integer.parseInt(pointsStr) );
	                		}
	                		
	                		

	                		Object objTips = dicRes.get("tips");
	                		if(objTips != null && objTips.getClass().getName().equals(JSONArray.class.getName())){
	                			JSONArray arrayTips = (JSONArray)objTips;
	                			ArrayList<Tip> tips = new ArrayList<Tip>();
	                			for(int i=0; i<arrayTips.length(); i++){
	                				Object objTip = arrayTips.get(i);
	                				if(objTip != null && objTip.getClass().getName().equals(JSONObject.class.getName())){
	                					JSONObject dicTip = (JSONObject)objTip;
	                					Tip tip = new Tip();
	                					tip.setId(dicTip.getString("id"));
	                					tip.setPuntos(dicTip.getInt("points"));
	                					tip.setHecho(dicTip.getBoolean("done"));

	                					tips.add(tip);
	                				}
	                			}
	                			enigma.setTips(tips);
	                		}
	                		
	                		enigma.setDone(dicRes.getBoolean("done"));
	                		
	                		Object objBadge = dicRes.get("badge");
	                		if(objBadge != null && objBadge.getClass().getName().equals(JSONObject.class.getName())){
	                			JSONObject dicBadge = (JSONObject)objBadge;
	                			Badge badge = new Badge();
	                			badge.setNid(dicBadge.getString("nid"));
	                			badge.setTitle(dicBadge.getString("title"));
	                			badge.setImage(dicBadge.getString("image"));
	                			enigma.setBadge(badge);
	                		}

	                		// Informar al delegate
	                		if(Enigma.enigmasInterface != null){
	                			Enigma.enigmasInterface.seCargoEnigma(enigma);
	                			return;
	                		}
	                		
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion get enigma: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(Enigma.enigmasInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Enigma.enigmasInterface.producidoErrorAlCargarEnigma("Error al cargar enigma");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Enigma.enigmasInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Enigma.enigmasInterface.producidoErrorAlCargarEnigma(error.toString());
	    		}
			}
		}, 
		params);
		
	}
	
	
	
	
	public static void resolverEnigma(Application application, String nidEnigma, String idRespuesta){

		MainApp app = (MainApp)application;
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nid", nidEnigma);
		params.put("response", idRespuesta);
		params.put("key", app.drupalSecurity.encrypt(nidEnigma));
		
		

		app.clienteDrupal.customMethodCallPost("enigma/solve", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de resolver enigma: " + response);
				if(response != null && !response.equals("")){
					
					try {
	                	JSONObject dicRes = new JSONObject(response);
	                	if(dicRes != null){
	                		
	                		boolean success = dicRes.getBoolean("success");
	                		int error = dicRes.getInt("error");
	                		
	                		if(error == 0){
	                			// Si error == 0, todo ha ido de puta madre
	                			if(success){
		                			// Esto es que ha respondido todo bien
	                				// Informar al delegate
	    	                		if(Enigma.enigmasInterface != null){
	    	                			Enigma.enigmasInterface.seHaResueltoEnigma(true);
	    	                			return;
	    	                		}
		                		}else{
		                			// Esto es que ha respondido muy mal
	                				// Informar al delegate
	    	                		if(Enigma.enigmasInterface != null){
	    	                			Enigma.enigmasInterface.seHaResueltoEnigma(false);
	    	                			return;
	    	                		}
		                		}
	                		}else{
	                			// Informar al delegate
	            	    		if(Enigma.enigmasInterface != null){
	            	    			Log.d("Milog", "Antes de informar al delegate de un error: " + error);
	            	    			Enigma.enigmasInterface.producidoErrorAlResolverEnigma("No se puede procesar la respuesta en el servidor");
	            	    			return;
	            	    		}
	                		}
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion resolver enigma: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(Enigma.enigmasInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Enigma.enigmasInterface.producidoErrorAlResolverEnigma("Error al recoger respuesta");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Enigma.enigmasInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Enigma.enigmasInterface.producidoErrorAlResolverEnigma(error.toString());
	    		}
			}
		}, 
		params);
		
	}
	
	
	
	
	
}
