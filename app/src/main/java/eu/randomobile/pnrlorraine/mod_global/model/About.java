package eu.randomobile.pnrlorraine.mod_global.model;

import java.util.HashMap;

import org.json.JSONObject;

import eu.randomobile.pnrlorraine.MainApp;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.Application;
import android.util.Log;


public class About {

	private String id;
	private String nombre;
	private String descricpion;
	private String imagenDestacada;
	

	public About(){
		
	}
	public About(String id, String nombre, String descripcion, String imagenDestacada){
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
	public static AboutInterface aboutInterface;
	public static interface AboutInterface {
		public void seCargoAbout(About about);
		public void producidoErrorAlCargarAbout(String error);
	}
	
	
	public static void cargarAbout(Application application){

		HashMap<String, String> params = new HashMap<String, String>();
		MainApp app = (MainApp)application;
		
		app.clienteDrupal.customMethodCallPost("info/about", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de cargar un info: " + response);
				
				About about = null;
				
				if(response != null && !response.equals("")){
					
					try {
	                	JSONObject dicRes = new JSONObject(response);
	                	if(dicRes != null){
	                		
	                		about = new About();
	                		
	                		//info.setId(nid);
	                		about.setNombre(dicRes.getString("title"));
	                		about.setDescripcion(dicRes.getString("body"));
	                		about.setImagenDestacada(dicRes.getString("image"));

	                		// Informar al delegate
	                		if(About.aboutInterface != null){
	                			About.aboutInterface.seCargoAbout(about);
	                			return;
	                		}
	                		
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion get about: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(About.aboutInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			About.aboutInterface.producidoErrorAlCargarAbout("Error al cargar info");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(About.aboutInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			About.aboutInterface.producidoErrorAlCargarAbout(error.toString());
	    		}
			}
		}, 
		params);
		
	}
	
}