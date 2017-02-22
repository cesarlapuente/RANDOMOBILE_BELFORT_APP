package eu.randomobile.pnrlorraine.mod_global.model;

import java.util.Date;

import org.json.JSONObject;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import eu.randomobile.pnrlorraine.MainApp;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class Sesion {
	
	// Interface para comunicarse con las llamadas as’ncronas del objeto Sesion
	public static SesionInterface sesionInterface;
		public static interface SesionInterface {
			public void seComproboLaSesion(boolean sigueActiva);
			public void producidoErrorAlComprobarSesion(String error);
	}

		
	public static void comprobarSesionActiva(Application application) {
		final MainApp app = (MainApp)application;
		app.clienteDrupal.systemConnect(new AsyncHttpResponseHandler(){
			
			public void onSuccess(String response) {
				
				Log.d("Milog", "Response: " + response);
				

				boolean sesionActiva = false;
				
				SharedPreferences.Editor editor = app.preferencias.edit();
				editor.putLong(app.COOKIE_KEY_TIMESTAMP_SESSID, new Date().getTime() / 100);
				editor.commit();
				
				if(response != null && !response.equals("")){
	                try {
						
	                	JSONObject resObj = new JSONObject(response);
	                	Object userObj = resObj.get("user");
	                	if(userObj.getClass().getName().equals(JSONObject.class.getName())){
	                		JSONObject userDic = (JSONObject)userObj;
	                		Object userUid = userDic.get("uid");
	                		
	                		
	                		
	                		if(userUid.getClass().getName().equals(String.class.getName())){
	                			String uidStr = (String)userUid;
	                			
	                			if(uidStr.equals("0")){
	                				Log.d("Milog", "Sesion no activa");
	                				sesionActiva = false;
	                			}else{
	                				Log.d("Milog", "Sesion activa");
	                				sesionActiva = true;
	                			}
	                			
	                		}else{
	                			Log.d("Milog", "Sesion no activa");
	                			sesionActiva = false;
	                		}
	                		
	                	}

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion en comprobarSesionActiva: " + e.toString());
					}
				}
				
				
				if(Sesion.sesionInterface != null){
					Sesion.sesionInterface.seComproboLaSesion(sesionActiva);
				}
				
				
				
				

			}
			
			public void onFailure(Throwable error) {
				if(Sesion.sesionInterface != null){
					Sesion.sesionInterface.producidoErrorAlComprobarSesion(error.toString());
				}
			}
		
		});
	}
	
	
	
	
	public static void borrarCookiesSesion(Application application){
		MainApp app = (MainApp)application;
		SharedPreferences.Editor editor = app.preferencias.edit();
		editor.remove(app.COOKIE_KEY_ID_USUARIO_LOGUEADO);
		editor.remove(app.COOKIE_KEY_ID_SESION_USUARIO_LOGUEADO);
		editor.remove(app.COOKIE_KEY_EMAIL_USUARIO_LOGUEADO);
		editor.remove(app.COOKIE_KEY_NICK_USUARIO_LOGUEADO);
		editor.remove(app.COOKIE_KEY_NOMBRE_USUARIO_LOGUEADO);
		editor.remove(app.COOKIE_KEY_APELLIDOS_USUARIO_LOGUEADO);
		editor.commit();
	}
	
	
	
	
}
