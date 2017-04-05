package eu.randomobile.pnrlorraine.mod_global.model;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Application;
import android.util.Log;

import eu.randomobile.pnrlorraine.MainApp;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class Tip {

	private String id;
	private String descricpion;
	private String imagenDestacada;
	private int puntos;
	private String cupon;
	private boolean hecho;

	public Tip(){
		
	}
	public Tip(String id, String descripcion, String imagenDestacada, int puntos, String cupon, boolean hecho){
		this.id = id;
		this.descricpion = descripcion;
		this.imagenDestacada = imagenDestacada;
		this.puntos = puntos;
		this.cupon = cupon;
		this.hecho = hecho;
	}
	
	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id = id;
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
	
	public int getPuntos(){
		return puntos;
	}
	public void setPuntos(int puntos){
		this.puntos = puntos;
	}

	public String getCupon(){
		return cupon;
	}
	public void setCupon(String cupon){
		this.cupon = cupon;
	}
	
	
	public boolean getHecho(){
		return hecho;
	}
	public void setHecho(boolean hecho){
		this.hecho = hecho;
	}


	// Interface para comunicarse con las llamadas asíncronas
	public static TipsInterface tipsInterface;
	public static interface TipsInterface {
		public void seCargoTip(Tip tip);
		public void producidoErrorAlCargarTip(String error);
		
		public void seHaMarcadoTipComoDone(boolean res);
		public void producidoErrorAlMarcarTipDone(String error);
	}
	
	public static void cargarTip(Application application, final String nid){

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nid", nid);
		
		Log.d("Milog", "Llamada al servicio");
		
		MainApp app = (MainApp)application;
		
		app.clienteDrupal.customMethodCallPost("tip/get_item", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de cargar un tip: " + response);
				
				Tip tip = null;
				
				if(response != null && !response.equals("")){
					
					try {
	                	JSONObject dicRes = new JSONObject(response);
	                	if(dicRes != null){
	                		
	                		tip = new Tip();
	                		
	                		tip.setId(nid);
	                		Log.d("Milog", "NID: " + tip.getId());
	                		tip.setDescripcion(dicRes.getString("body"));
	                		Log.d("Milog", "DESC: " + tip.getDescripcion());
	                		tip.setImagenDestacada(dicRes.getString("image"));
	                		Log.d("Milog", "IMAGEN: " + tip.getImagenDestacada());
	                		tip.setPuntos(dicRes.getInt("points"));
	                		Log.d("Milog", "PUNTOS: " + tip.getPuntos());
	                		tip.setCupon(dicRes.getString("coupon"));
	                		Log.d("Milog", "CUPON: " + tip.getCupon());
	                		tip.setHecho(dicRes.getBoolean("done"));
	                		Log.d("Milog", "HECHO: " + tip.getHecho());

	                		Log.d("Milog", "VALORES DEL ITEM TIP CORRECTOS!!!");
	                		
	                		
	                		// Informar al delegate
	                		if(Tip.tipsInterface != null){
	                			Tip.tipsInterface.seCargoTip(tip);
	                			return;
	                		}
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion get tip: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(Tip.tipsInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Tip.tipsInterface.producidoErrorAlCargarTip("Error al cargar tip");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Tip.tipsInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Tip.tipsInterface.producidoErrorAlCargarTip(error.toString());
	    		}
			}
		}, 
		params);
		
	}
	
	
	
	
	public static void marcarTipDone(Application application, String nidTip, String codeCoupon){

		MainApp app = (MainApp)application;
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nid", nidTip);
		
		String keyFull = app.drupalSecurity.encrypt(nidTip);
		
		if(codeCoupon == null || codeCoupon.equals("")){
			params.put("response", keyFull);
		}else{
			params.put("response", codeCoupon);
		}

		params.put("key", keyFull);
		
		
		

		app.clienteDrupal.customMethodCallPost("tip/solve", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de resolver tip: " + response);
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
	    	                		if(Tip.tipsInterface != null){
	    	                			Tip.tipsInterface.seHaMarcadoTipComoDone(true);
	    	                			return;
	    	                		}
		                		}else{
		                			// Esto es que ha respondido muy mal
	                				// Informar al delegate
	    	                		if(Tip.tipsInterface != null){
	    	                			Tip.tipsInterface.seHaMarcadoTipComoDone(false);
	    	                			return;
	    	                		}
		                		}
	                		}else{
	                			// Informar al delegate
	            	    		if(Tip.tipsInterface != null){
	            	    			Log.d("Milog", "Antes de informar al delegate de un error: " + error);
	            	    			Tip.tipsInterface.producidoErrorAlMarcarTipDone("No se puede procesar la respuesta en el servidor");
	            	    			return;
	            	    		}
	                		}
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion resolver enigma: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(Tip.tipsInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Tip.tipsInterface.producidoErrorAlMarcarTipDone("Error al recoger respuesta");
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Tip.tipsInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Tip.tipsInterface.producidoErrorAlMarcarTipDone(error.toString());
	    		}
			}
		}, 
		params);
		
	}
	
	
	
	
}