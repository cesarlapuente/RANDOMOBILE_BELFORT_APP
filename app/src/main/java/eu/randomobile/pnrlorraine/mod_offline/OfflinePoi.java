package eu.randomobile.pnrlorraine.mod_offline;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_global.environment.GPS;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_global.model.Route;
import eu.randomobile.pnrlorraine.mod_notification.Cache;


public class OfflinePoi {
	public static PoisModeOfflineInterface poisInterface;
	public static interface PoisModeOfflineInterface {
		public void seCargoListaPoisOffline(ArrayList<Poi> pois);
		public void producidoErrorAlCargarListaPoisOffline(String error);
		public void seCargoPoiOffline(Poi poi);
		public void producidoErrorAlCargarPoiOffline(String error);
	}

	public static void fillPoisTable (Application application) {
		final MainApp app = (MainApp)application;
		HashMap<String,String> params = new HashMap<String, String>();
		double [] coords = GPS.getGPS(app.getApplicationContext());
		params.put("lat", String.valueOf(coords[0]));
		params.put("lon", String.valueOf(coords[1]));
		
		app.clienteDrupal.customMethodCallPost("poi/get_list_distance", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de cargar pois list distance: " + response);
				
				if(response != null && !response.equals("")){
					Offline.insertJsonList(app,app.DRUPAL_TYPE_POI,response);
	
				}
				if (OfflinePoi.poisInterface != null) {
					OfflinePoi.poisInterface.seCargoListaPoisOffline(null);
				}
			}
			
			public void onFailure(Throwable error) {
				Log.d("Milog", error.toString());
				if (OfflinePoi.poisInterface != null) {
					Log.d("Milog",
							"Antes de informar al delegate de un error: " + error.toString());
					OfflinePoi.poisInterface
							.producidoErrorAlCargarListaPoisOffline(error
									.toString());
				}
			}
		}, 
		params);
	}
	public static void fillPoisItem (Application application, final String nid) {
		final MainApp app = (MainApp)application;
		HashMap<String,String> params = new HashMap<String, String>();
		params.put("nid", nid);
		
		app.clienteDrupal.customMethodCallPost("poi/get_item", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de cargar pois list distance: " + response);
				
				if(response != null && !response.equals("")){
					Offline.insertJsonItem(app,app.DRUPAL_TYPE_POI, Integer.parseInt(nid), response);
				}
				if (OfflinePoi.poisInterface != null) {
					OfflinePoi.poisInterface.seCargoPoiOffline(null);
				}
			}
			
			public void onFailure(Throwable error) {
				Log.d("Milog", error.toString());
				if (OfflinePoi.poisInterface != null) {
					Log.d("Milog",
							"Antes de informar al delegate de un error: " + error.toString());
					OfflinePoi.poisInterface
							.producidoErrorAlCargarPoiOffline(error
									.toString());
				}
			}
		}, 
		params);
	}
	
	public static void cargaListaPoisOffline (Application application) {
		final MainApp app = (MainApp) application;
		String jsonRes = Offline.extractJsonList(app, app.DRUPAL_TYPE_POI);
		ArrayList<Poi> listaPois = Poi.fillPoiList(jsonRes);
		if (OfflinePoi.poisInterface != null) {
			if (listaPois != null)
				OfflinePoi.poisInterface.seCargoListaPoisOffline(listaPois);
			else {
				Log.d("Milog",
						"error cargaListaPoisOffline");
				OfflinePoi.poisInterface
						.producidoErrorAlCargarListaPoisOffline("cargaListaPoisOffline");				
			}
		}
	}
	
	public static void cargarPoiOffline (Application application, String nid) {
		final MainApp app = (MainApp) application;
		String jsonRes = Offline.extractJsonItem(app, app.DRUPAL_TYPE_POI, Integer.valueOf(nid));
		Poi item = Poi.fillPoiItem(jsonRes);
		if (OfflinePoi.poisInterface != null) {
			if (item != null)
				OfflinePoi.poisInterface.seCargoPoiOffline(item);
			else if (Cache.hashMapPois.containsKey(nid)) {
				OfflinePoi.poisInterface.seCargoPoiOffline(Cache.arrayPois.get(Cache.hashMapPois.get(nid)));
			}
			else {
				Log.d("Milog",
						"error cargarRouteOffline");
				OfflinePoi.poisInterface
						.producidoErrorAlCargarPoiOffline("cargarPoiOffline: nid= " + nid);
			}
		}
	}
	
}
