package eu.randomobile.pnrlorraine.mod_global.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.PoiCategoryTerm;
import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.TagTerm;
//import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.utils.JSONManager;

public class Poi {

	private String nid;
	private String title;
	private String body;
	private double distanceMeters;
	private PoiCategoryTerm category;
	private GeoPoint coordinates;
	private String mainImage;
	private ArrayList<ResourceFile> images;
	private ArrayList<ResourceFile> videos;
	private ArrayList<ResourceFile> audios;
	private ArrayList<ResourceLink> enlaces;
	private ArrayList<TagTerm> tags;
	private Vote vote;
	public static PoisInterface poisInterface;

	public static interface PoisInterface {
		public void seCargoListaPois(ArrayList<Poi> pois);
		public void producidoErrorAlCargarListaPois(String error);
		public void seCargoPoi(Poi poi);
		public void producidoErrorAlCargarPoi(String error);
	}
	
	public static void cargarListaPoisOrdenadosDistancia(Application application, double lat, double lon, int radio, int num, int pag, String catTid, String searchTxt){

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("lat", String.valueOf(lat));
		params.put("lon", String.valueOf(lon));
		if(radio > 0){
			params.put("radio", String.valueOf(radio));
		}
		if(num > 0){
			params.put("num", String.valueOf(num));
		}
		if(pag > 0){
			params.put("pag", String.valueOf(pag));
		}
		if(catTid != null && !catTid.equals("")){
			params.put("cat", catTid);
		}
		if(searchTxt != null && !searchTxt.equals("")){
			params.put("search", searchTxt);
		}
		
		Log.d("Milog", "Parametros enviados a poi/get_list_distance: " + params.toString());

		MainApp app = (MainApp)application;
		
		app.clienteDrupal.customMethodCallPost("poi/get_list_distance", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de cargar pois distance: " + response);
				
				ArrayList<Poi> listaPois = null;
				
				if(response != null && !response.equals("")){
					
					listaPois = fillPoiList(response);
				}
				
				
				// Informar al delegate
	    		if(Poi.poisInterface != null){
	        		// Informar al delegate
	        		if(listaPois != null)
	        			Poi.poisInterface.seCargoListaPois(listaPois);
	        		else {
		    			Log.d("Milog", "Antes de informar al delegate de un error");
		    			Poi.poisInterface.producidoErrorAlCargarListaPois("Error al cargar lista de pois");
		    		}
	    		}
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Poi.poisInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error: " + error.toString());
	    			Poi.poisInterface.producidoErrorAlCargarListaPois(error.toString());
	    		}
			}
		}, 
		params);
		
	}

	public static void cargarPoi(Application application, String nid) {

		MainApp app = (MainApp) application;
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nid", nid);

		app.clienteDrupal.customMethodCallPost("poi/get_item",
				new AsyncHttpResponseHandler() {
					public void onSuccess(String response) {
						Log.d("Milog", "Exito al cargar poi: " + response);

						if(response != null && !response.equals("")){
						    Poi poi = fillPoiItem(response);	
			        		// Informar al delegate
			        		if((poi != null) && (Poi.poisInterface != null)){
			        			Poi.poisInterface.seCargoPoi(poi);
			        			return;
			        		}
						}
						// Informar al delegate
			    		if(Poi.poisInterface != null){
			    			Log.d("Milog", "Antes de informar al delegate de un error");
			    			Poi.poisInterface.producidoErrorAlCargarPoi("Error al cargar poi");
			    		}
						
					}
					public void onFailure(Throwable error) {
						// Informar al delegate
						if (Poi.poisInterface != null) {
							Log.d("Milog",
									"Antes de informar al delegate de un error: " + error.toString());
							Poi.poisInterface
									.producidoErrorAlCargarPoi(error
											.toString());
						}
					}
				}, params);

	}

	public static ArrayList<Poi> fillPoiList(String response) {
		ArrayList<Poi> listaPois = null;
		try {
        	JSONArray arrayRes = new JSONArray(response);
        	if(arrayRes != null){
        		if(arrayRes.length() > 0){
        			Log.d("Milog", "array devuelto contiene al menos 1 elemento");
        			listaPois = new ArrayList<Poi>();
        		}
        		
        		for(int i=0; i< arrayRes.length(); i++){
        			Object recObj = arrayRes.get(i);
        			if(recObj != null){
        				if(recObj.getClass().getName().equals(JSONObject.class.getName())){
        					JSONObject recDic = (JSONObject)recObj;
        					String nid = recDic.getString("nid");
        					String title = recDic.getString("title");
        					String body = recDic.getString("body");
        					String lat = recDic.getString("lat");
        					String lon = recDic.getString("lon");
        					String alt = recDic.getString("altitude");
        					String distance = recDic.getString("distance");
        					String image = recDic.getString("image");
        					
        					Poi item = new Poi();
        					item.setNid(nid);
        					item.setTitle(title);
        					item.setBody(body);
        					
        					double distanceKMDouble = Double.valueOf(distance);
        					double distanceMDouble = distanceKMDouble * 1000;
        					item.setDistanceMeters(distanceMDouble);
        					
        					Object objCat = recDic.get("cat");
	                		if(objCat != null && objCat.getClass().getName().equals(JSONObject.class.getName())){
	                			JSONObject dicCat = (JSONObject)objCat;
	                			String tid = dicCat.getString("tid");
	                			String name = dicCat.getString("name");
	                			String icon = dicCat.getString("image");
	                			PoiCategoryTerm poiCatTerm = new PoiCategoryTerm();
	                			poiCatTerm.setTid(tid);
	                			poiCatTerm.setName(name);
	                			poiCatTerm.setIcon(icon);
	                			item.setCategory(poiCatTerm);
	                		}
	                		
	                		Object objRate = recDic.get("rate");
	                		if(objRate != null && objRate.getClass().getName().equals(JSONObject.class.getName())){
	                			JSONObject dicRate = (JSONObject)objRate;
	                			String numVotosStr = dicRate.getString("count");
	                			String resultsAvgStr = dicRate.getString("results");
	                			int numVotos = 0;
	                			int resultsAvg = 0;
	                			if(numVotosStr != null && !numVotosStr.equals("") && !numVotosStr.equals("null")){
	                				numVotos = (int) Float.parseFloat( numVotosStr );
	                			}
	                			if(resultsAvgStr != null && !resultsAvgStr.equals("") && !resultsAvgStr.equals("null")){
	                				resultsAvg = (int) Float.parseFloat( resultsAvgStr );
	                			}
		                		Vote vote = new Vote();
		                		vote.setEntity_id(nid);
		                		vote.setNumVotes(numVotos);
		                		vote.setValue(resultsAvg);
		                		item.setVote(vote);
	                		}

        					if(image != null && ( image.equals("") || image.equals("null") ) ){
        						item.setMainImage(null);
        					}else{
        						item.setMainImage(image);
        					}
        					
        					GeoPoint gp = new GeoPoint();
        					
        					if(lat != null && !lat.equals("") && !lat.equals("null")){
        						gp.setLatitude( Double.parseDouble(lat) );
        					}
        					
        					if(lon != null && !lon.equals("") && !lon.equals("null")){
        						gp.setLongitude( Double.parseDouble(lon) );
        					}
        					
        					if(alt != null && !alt.equals("") && !alt.equals("null")){
        						gp.setAltitude( Double.parseDouble(alt) );
        					}
        					item.setCoordinates(gp);
        					
        					listaPois.add(item);
        				}
        			}
        		} //for	
        	} //arrayRes
        	

        } catch (Exception e) {
			Log.d("Milog", "Excepcion en lista pois: " + e.toString());
			listaPois = null;
		}
		return listaPois;
	}
	
	public static Poi fillPoiItem (String response) {
		Poi poi = null;
		try {
        	JSONObject dicRes = new JSONObject(response);
        	if(dicRes != null){
        		String nid = dicRes.getString("nid");
        		String title = dicRes.getString("title");
        		String body = dicRes.getString("body");
        		String lat = dicRes.getString("lat");
        		String lon = dicRes.getString("lon");
        		String altitude = dicRes.getString("altitude");
        		
        		
        		
        		poi = new Poi();
        		poi.setNid(nid);
        		poi.setTitle(title);
        		poi.setBody(body);
        		
        		
        		double latDouble = Double.parseDouble(lat);
        		double lonDouble = Double.parseDouble(lon);
        		double altDouble = Double.parseDouble(altitude);
        		GeoPoint gp = new GeoPoint();
        		gp.setLatitude(latDouble);
        		gp.setLongitude(lonDouble);
        		gp.setAltitude(altDouble);
        		poi.setCoordinates(gp);
        		
        		Object objCat = dicRes.get("type");
        		if(objCat != null && objCat.getClass().getName().equals(JSONObject.class.getName())){
        			JSONObject dicCat = (JSONObject)objCat;
        			String tid = dicCat.getString("tid");
        			String name = dicCat.getString("name");
        			String icon = dicCat.getString("image");
        			PoiCategoryTerm poiCatTerm = new PoiCategoryTerm();
        			poiCatTerm.setTid(tid);
        			poiCatTerm.setName(name);
        			poiCatTerm.setIcon(icon);
        			poi.setCategory(poiCatTerm);
        		}
        		
        		
        		ArrayList<ResourceFile> arrayResourceImages = new ArrayList<ResourceFile>();
        		Object objImages = dicRes.get("images");
        		if(objImages != null && objImages.getClass().getName().equals(JSONArray.class.getName())){
        			JSONArray array = (JSONArray)objImages;
        			for(int i=0; i<array.length(); i++){
        				Object obj = array.get(i);
        				if(obj != null && obj.getClass().getName().equals(JSONObject.class.getName())){
        					JSONObject dic = (JSONObject)obj;
        					String name = dic.getString("name");
        					String url = dic.getString("url");
        					ResourceFile rf = new ResourceFile();
        					rf.setFileName(name);
        					rf.setFileUrl(url);
        					rf.setFileTitle(JSONManager.getString(dic, "title"));
        					rf.setFileBody(JSONManager.getString(dic, "body"));
        					rf.setCopyright(JSONManager.getString(dic, "copyright"));
        					arrayResourceImages.add(rf);
        				}
        			}
        		}
        		poi.setImages(arrayResourceImages);
        		
        		ArrayList<ResourceFile> arrayResourceAudios = new ArrayList<ResourceFile>();
        		Object objAudios = dicRes.get("audios");
        		if(objAudios != null && objAudios.getClass().getName().equals(JSONArray.class.getName())){
        			JSONArray array = (JSONArray)objAudios;
        			for(int i=0; i<array.length(); i++){
        				Object obj = array.get(i);
        				if(obj != null && obj.getClass().getName().equals(JSONObject.class.getName())){
        					JSONObject dic = (JSONObject)obj;
        					String name = dic.getString("name");
        					String url = dic.getString("url");
        					ResourceFile rf = new ResourceFile();
        					rf.setFileName(name);
        					rf.setFileUrl(url);
        					arrayResourceAudios.add(rf);
        				}
        			}
        		}
        		poi.setAudios(arrayResourceAudios);
        		
        		ArrayList<ResourceFile> arrayResourceVideos = new ArrayList<ResourceFile>();
        		Object objVideos = dicRes.get("videos");
        		if(objVideos != null && objVideos.getClass().getName().equals(JSONArray.class.getName())){
        			JSONArray array = (JSONArray)objVideos;
        			for(int i=0; i<array.length(); i++){
        				Object obj = array.get(i);
        				if(obj != null && obj.getClass().getName().equals(JSONObject.class.getName())){
        					JSONObject dic = (JSONObject)obj;
        					String name = dic.getString("name");
        					String url = dic.getString("url");
        					ResourceFile rf = new ResourceFile();
        					rf.setFileName(name);
        					rf.setFileUrl(url);
        					arrayResourceVideos.add(rf);
        				}
        			}
        		}
        		poi.setVideos(arrayResourceVideos);
        		
        		ArrayList<ResourceLink> arrayResourceLinks = new ArrayList<ResourceLink>();
        		Object objLinks = dicRes.get("links");
        		if(objLinks != null && objLinks.getClass().getName().equals(JSONArray.class.getName())){
        			JSONArray array = (JSONArray)objLinks;
        			for(int i=0; i<array.length(); i++){
        				Object obj = array.get(i);
        				if(obj != null && obj.getClass().getName().equals(JSONObject.class.getName())){
        					JSONObject dic = (JSONObject)obj;
        					String name = dic.getString("name");
        					String url = dic.getString("url");
        					ResourceLink rl = new ResourceLink();
        					rl.setTitle(name);
        					rl.setUrl(url);
        					arrayResourceLinks.add(rl);
        				}
        			}
        		}
        		poi.setEnlaces(arrayResourceLinks);
        	}
        	

        } catch (Exception e) {
			Log.d("Milog", "Excepcion cargar poi: " + e.toString());
			return null;
		}
		return poi;
	}
	public static int getResourceIconForPoiCategoryTid(String tid){
		if(tid != null){
			if(tid.equals("7")){
				// Commerce
				return R.drawable.btn_poi_comercio_normal;
			}else if(tid.equals("1")){
				// Courtaou
				return R.drawable.btn_poi_courtaou_normal;
			}else if(tid.equals("6")){
				// Hebergement
				return R.drawable.btn_poi_hotel_normal;
			}else if(tid.equals("8")){
				// Restaurant
				return R.drawable.btn_poi_restaurante_normal;
			}else if(tid.equals("4")){
				// Site culturel
				return R.drawable.btn_poi_cultural_normal;
			}else if(tid.equals("5")){
				// Site natural
				return R.drawable.btn_poi_naturaleza_normal;
			}else if(tid.equals("3")){
				// Village
				return R.drawable.btn_poi_pueblo_normal;
			}
		}
		
		
		return R.drawable.ic_launcher;
	}
	
	public static String getImageNameForCategoryTid(Application app, String tid){
		int resId = Poi.getResourceIconForPoiCategoryTid(tid);
		String imageName = app.getResources().getResourceEntryName(resId) + ".png";
		return imageName;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public PoiCategoryTerm getCategory() {
		return category;
	}

	public void setCategory(PoiCategoryTerm category) {
		this.category = category;
	}

	public ArrayList<ResourceFile> getImages() {
		return images;
	}

	public void setImages(ArrayList<ResourceFile> images) {
		this.images = images;
	}

	public ArrayList<ResourceFile> getVideos() {
		return videos;
	}

	public void setVideos(ArrayList<ResourceFile> videos) {
		this.videos = videos;
	}

	public ArrayList<ResourceFile> getAudios() {
		return audios;
	}

	public void setAudios(ArrayList<ResourceFile> audios) {
		this.audios = audios;
	}

	public ArrayList<ResourceLink> getEnlaces() {
		return enlaces;
	}

	public void setEnlaces(ArrayList<ResourceLink> enlaces) {
		this.enlaces = enlaces;
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

	public GeoPoint getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(GeoPoint coordinates) {
		this.coordinates = coordinates;
	}

	public ArrayList<TagTerm> getTags() {
		return tags;
	}

	public void setTags(ArrayList<TagTerm> tags) {
		this.tags = tags;
	}



	public double getDistanceMeters() {
		return distanceMeters;
	}



	public void setDistanceMeters(double distanceMeters) {
		this.distanceMeters = distanceMeters;
	}



	public String getMainImage() {
		return mainImage;
	}



	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
	}



	public Vote getVote() {
		return vote;
	}



	public void setVote(Vote vote) {
		this.vote = vote;
	}

}
