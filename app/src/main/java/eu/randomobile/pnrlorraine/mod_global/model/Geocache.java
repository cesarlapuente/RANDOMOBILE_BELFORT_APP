package eu.randomobile.pnrlorraine.mod_global.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import eu.randomobile.pnrlorraine.MainApp;

public class Geocache implements Parcelable {

    public static final Parcelable.Creator<Geocache> CREATOR = new Parcelable.Creator<Geocache>() {

        public Geocache createFromParcel(Parcel in) {
            Geocache complaint = new Geocache();
            complaint.setNid(in.readString());
            complaint.setTitle(in.readString());
            complaint.setBody(in.readString());
            complaint.setDistanceMeters(in.readDouble());
            complaint.setCoordinates((GeoPoint) in.readParcelable(GeoPoint.class.getClassLoader()));
            return complaint;
        }

        @Override
        public Geocache[] newArray(int size) {
            return new Geocache[size];
        }
    };
    // Interface para comunicarse con las llamadas asincronas
    public static GeocachingInterface geocachingInterface;
    private String nid;
	private String title;
	private String body;
	private double distanceMeters;
	private GeoPoint coordinates;
	private boolean done;

	public static void cargarListaGeocachesCercanos(Application application, double lat, double lon, int radio, int num, int pag){

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

		

		MainApp app = (MainApp)application;
		
		app.clienteDrupal.customMethodCallPost("geocache/get_list", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {


                ArrayList<Geocache> listaGeocaches = null;
				
				if(response != null && !response.equals("")){
					
					try {
	                	JSONArray arrayRes = new JSONArray(response);
	                	if(arrayRes != null){
	                		if(arrayRes.length() > 0){
	                			listaGeocaches = new ArrayList<Geocache>();
	                		}
	                		
	                		for(int i=0; i< arrayRes.length(); i++){
	                			Object recObj = arrayRes.get(i);
	                			if(recObj != null){
	                				if(recObj.getClass().getName().equals(JSONObject.class.getName())){
	                					JSONObject recDic = (JSONObject)recObj;
	                					String nid = recDic.getString("nid");
	                					String title = recDic.getString("title");

	                					String lat = recDic.getString("lat");
	                					String lon = recDic.getString("lon");
	                					String alt = recDic.getString("altitude");
	                					String distance = recDic.getString("distance");
	                					boolean done = recDic.getBoolean("done");
	                					
	                					Geocache item = new Geocache();
	                					item.setNid(nid);
	                					item.setTitle(title);
	                					item.setDone(done);
	                					
	                					double distanceKMDouble = Double.valueOf(distance);
	                					double distanceMDouble = distanceKMDouble * 1000;
	                					item.setDistanceMeters(distanceMDouble);

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
	                					
	                					listaGeocaches.add(item);
	                				}
	                			}
	                		}

	                		// Informar al delegate
	                		if(Geocache.geocachingInterface != null){
	                			Geocache.geocachingInterface.seCargoListaGeocachesCercanos(listaGeocaches);
	                			return;
	                		}
	                		
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion en lista geocaches: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(Geocache.geocachingInterface != null){
	    			Geocache.geocachingInterface.producidoErrorAlCargarListaGeocachesCercanos("Error al cargar lista de geocaches");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Geocache.geocachingInterface != null){
	    			Geocache.geocachingInterface.producidoErrorAlCargarListaGeocachesCercanos(error.toString());
	    		}
			}
		}, 
		params);
		
	}
	
	
	
	
	public static void capturarGeocache(Application application, String nidGeocache){

		MainApp app = (MainApp)application;
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nid", nidGeocache);
		params.put("key", app.drupalSecurity.encrypt(nidGeocache));
	
		
		

		app.clienteDrupal.customMethodCallPost("geocache/capture", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
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
	    	                		if(Geocache.geocachingInterface != null){
	    	                			Geocache.geocachingInterface.seHaCapturadoGeocache(true);
	    	                			return;
	    	                		}
		                		}else{
		                			// Esto es que ha respondido muy mal
	                				// Informar al delegate
	    	                		if(Geocache.geocachingInterface != null){
	    	                			Geocache.geocachingInterface.seHaCapturadoGeocache(false);
	    	                			return;
	    	                		}
		                		}
	                		}else{
	                			// Informar al delegate
	            	    		if(Geocache.geocachingInterface != null){
	            	    			Geocache.geocachingInterface.producidoErrorAlCapturarGeocache("No se puede procesar la respuesta en el servidor");
	            	    			return;
	            	    		}
	                		}
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion capturar geocache: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(Geocache.geocachingInterface != null){
	    			Geocache.geocachingInterface.producidoErrorAlCapturarGeocache("Error al recoger respuesta");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Geocache.geocachingInterface != null){
	    			Geocache.geocachingInterface.producidoErrorAlCapturarGeocache(error.toString());
	    		}
			}
		}, 
		params);
		
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


	public double getDistanceMeters() {
		return distanceMeters;
	}


	public void setDistanceMeters(double distanceMeters) {
		this.distanceMeters = distanceMeters;
	}


	public GeoPoint getCoordinates() {
		return coordinates;
	}


	public void setCoordinates(GeoPoint coordinates) {
		this.coordinates = coordinates;
	}
	
	
	
	
	
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(nid);
		dest.writeString(title);
		dest.writeString(body);
		dest.writeDouble(distanceMeters);
		dest.writeParcelable(coordinates, flags);
	}

	public boolean isDone() {
		return done;
	}




	public void setDone(boolean done) {
		this.done = done;
	}

    public interface GeocachingInterface {
        void seCargoListaGeocachesCercanos(ArrayList<Geocache> geocaches);

        void producidoErrorAlCargarListaGeocachesCercanos(String error);

        void seHaCapturadoGeocache(boolean res);

        void producidoErrorAlCapturarGeocache(String error);
    }


}
