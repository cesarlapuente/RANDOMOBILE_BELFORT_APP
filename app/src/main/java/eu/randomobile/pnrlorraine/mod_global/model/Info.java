package eu.randomobile.pnrlorraine.mod_global.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import eu.randomobile.pnrlorraine.MainApp;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.Application;
import android.util.Log;


public class Info {

	private String id;
	private String nombre;
	private String descricpion;
	private String imagenDestacada;
	

	public Info(){
		
	}
	public Info(String id, String nombre, String descripcion, String imagenDestacada){
		this.id = id;
		this.nombre = nombre;
		this.descricpion = descripcion;
		this.imagenDestacada = imagenDestacada;
	}
	
	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id = id;
	}
	
	public String getNombre(){
		return nombre;
	}
	public void setNombre(String nombre){
		this.nombre = nombre;
	}

	public String getDescripcion(){
		return descricpion;
	}
	public void setDescripcion(String descripcion){
		this.descricpion = descripcion;
	}

	public String getImagenDestacada(){
		return imagenDestacada;	
	}
	public void setImagenDestacada(String imagenDestacada){
		this.imagenDestacada = imagenDestacada;
	}
	
	
	// Interface para comunicarse con las llamadas asíncronas
	public static InfosInterface infosInterface;
	public static interface InfosInterface {
		public void seCargoListaInfos(ArrayList<Info> infos);
		public void producidoErrorAlCargarListaInfos(String error);
		public void seCargoInfo(Info info);
		public void producidoErrorAlCargarInfo(String error);
	}
	
	public static void cargarListaInfo(Application application){

		HashMap<String, String> params = null;
		
		MainApp app = (MainApp)application;
		
		app.clienteDrupal.customMethodCallPost("info/get_list", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de cargar infos: " + response);
				
				ArrayList<Info> listaInfos = null;
				
				if(response != null && !response.equals("")){
					
					try {
	                	JSONArray arrayRes = new JSONArray(response);
	                	if(arrayRes != null){
	                		if(arrayRes.length() > 0){
	                			Log.d("Milog", "array devuelto contiene al menos 1 elemento");
	                			listaInfos = new ArrayList<Info>();
	                		}
	                		
	                		for(int i=0; i< arrayRes.length(); i++){
	                			Object recObj = arrayRes.get(i);
	                			if(recObj != null){
	                				if(recObj.getClass().getName().equals(JSONObject.class.getName())){
	                					JSONObject recDic = (JSONObject)recObj;
	                					String nid = recDic.getString("nid");
	                					String nombre = recDic.getString("title");
	                					String descripcion = recDic.getString("body");
	                					String imagenDestacada = recDic.getString("image");
	 
	                					Info item = new Info();
	                					item.setId(nid);
	                					item.setNombre(nombre);
	                					item.setDescripcion(descripcion);
	                					item.setImagenDestacada(imagenDestacada);
	                					listaInfos.add(item);
	                					
	                				}
	                			}
	                		}
	                		
	                		// Informar al delegate
	                		if(Info.infosInterface != null){
	                			Info.infosInterface.seCargoListaInfos(listaInfos);
	                			return;
	                		}
	                		
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion en infos: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(Info.infosInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Info.infosInterface.producidoErrorAlCargarListaInfos("Error al cargar lista de infos");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Info.infosInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error: " + error.toString());
	    			Info.infosInterface.producidoErrorAlCargarListaInfos(error.toString());
	    		}
			}
		}, 
		params);
		
	}
	
	
	public static void cargarListaInfoPastoral(Application application){

		HashMap<String, String> params = null;

		
		MainApp app = (MainApp)application;
		
		app.clienteDrupal.customMethodCallPost("info/get_pastoral_list", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de cargar infos pastoral: " + response);
				
				ArrayList<Info> listaInfos = null;
				
				if(response != null && !response.equals("")){
					
					try {
	                	JSONArray arrayRes = new JSONArray(response);
	                	if(arrayRes != null){
	                		if(arrayRes.length() > 0){
	                			Log.d("Milog", "array devuelto contiene al menos 1 elemento");
	                			listaInfos = new ArrayList<Info>();
	                		}
	                		
	                		for(int i=0; i< arrayRes.length(); i++){
	                			Object recObj = arrayRes.get(i);
	                			if(recObj != null){
	                				if(recObj.getClass().getName().equals(JSONObject.class.getName())){
	                					JSONObject recDic = (JSONObject)recObj;
	                					String nid = recDic.getString("nid");
	                					String nombre = recDic.getString("title");
	                					String descripcion = recDic.getString("body");
	                					String imagenDestacada = recDic.getString("image");
	 
	                					Info item = new Info();
	                					item.setId(nid);
	                					item.setNombre(nombre);
	                					item.setDescripcion(descripcion);
	                					item.setImagenDestacada(imagenDestacada);
	                					listaInfos.add(item);
	                					
	                				}
	                			}
	                		}
	                		
	                		// Informar al delegate
	                		if(Info.infosInterface != null){
	                			Info.infosInterface.seCargoListaInfos(listaInfos);
	                			return;
	                		}
	                		
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion en infos pastoral: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(Info.infosInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Info.infosInterface.producidoErrorAlCargarListaInfos("Error al cargar lista de infos pastoral");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Info.infosInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error en pastoral: " + error.toString());
	    			Info.infosInterface.producidoErrorAlCargarListaInfos(error.toString());
	    		}
			}
		}, 
		params);
		
	}
	
	
	
	
	public static void cargarInfo(Application application, final String nid){

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nid", nid);
		
		MainApp app = (MainApp)application;
		
		app.clienteDrupal.customMethodCallPost("info/get_item", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de cargar un info: " + response);
				
				Info info = null;
				
				if(response != null && !response.equals("")){
					
					try {
	                	JSONObject dicRes = new JSONObject(response);
	                	if(dicRes != null){
	                		
	                		info = new Info();
	                		
	                		info.setId(nid);
	                		info.setNombre(dicRes.getString("title"));
	                		info.setDescripcion(dicRes.getString("body"));
	                		info.setImagenDestacada(dicRes.getString("image"));

	                		// Informar al delegate
	                		if(Info.infosInterface != null){
	                			Info.infosInterface.seCargoInfo(info);
	                			return;
	                		}
	                		
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion get info: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(Info.infosInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Info.infosInterface.producidoErrorAlCargarInfo("Error al cargar info");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Info.infosInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Info.infosInterface.producidoErrorAlCargarInfo(error.toString());
	    		}
			}
		}, 
		params);
		
	}
	
}