package eu.randomobile.pnrlorraine.mod_global.environment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import eu.randomobile.pnrlorraine.MainApp;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Drupal7RESTClient {
	public HttpGet mSERVERGET;
	public HttpPost mSERVERPOST;
	public String url;
	public static String mDOMAIN;
	AsyncHttpClient client;
	private Context mCtx;
	private final String mPREFS_AUTH;
	public static Long mSESSION_LIFETIME;
	
	MainApp app;
	
	/* Constructor */
	public Drupal7RESTClient(Application application, String _prefs_auth, String _server,	String _domain, Long _session_lifetime) {
		mPREFS_AUTH = _prefs_auth;
		mDOMAIN = _domain;
		url = _server;
		mCtx = application.getApplicationContext();
		mSESSION_LIFETIME = _session_lifetime;
		app = (MainApp)application;
		
		client = new AsyncHttpClient();
		client.setTimeout( mSESSION_LIFETIME.intValue() );
	}
	
	
	/* Devuelve el id de la sesi�n actual */
	private String getSessionID() {
		SharedPreferences auth = mCtx.getSharedPreferences(mPREFS_AUTH, 0);
		Long timestamp = auth.getLong(app.COOKIE_KEY_TIMESTAMP_SESSID, 0);
		Long currenttime = new Date().getTime() / 100;
		String sessionid = auth.getString(app.COOKIE_KEY_ID_SESION_USUARIO_LOGUEADO, null);
		if (sessionid == null || (currenttime - timestamp) >= mSESSION_LIFETIME) {
			return sessionid;
		} else{
			return sessionid;
		}
	}
	
	
	/* System.connect */
	public void systemConnect(AsyncHttpResponseHandler responseHandler) {
		String uri = url + "system/connect";

		client.post(uri, responseHandler);
	}
	
	
	
	
	/* Llamada a services a trav�s de POST */
	private void callPost(final AsyncHttpResponseHandler responseHandler, final String uri, final RequestParams parametros){
		Log.d("JmLog","URL POST :"+uri+" parametros"+parametros);
		Log.d("Milog", "Entra en callPost");
		final String sessid = this.getSessionID();

		Long timestamp = app.preferencias.getLong(app.COOKIE_KEY_TIMESTAMP_SESSID, 0);
		
		Long currenttime = new Date().getTime() / 100;
		
		Log.d("Milog", "currenttime = " + currenttime + "  timestamp = " + timestamp + " mSESSION_LIFETIME = " + mSESSION_LIFETIME);
		
		Log.d("Milog", "" + (currenttime - timestamp) + " // " + mSESSION_LIFETIME);

		if (sessid == null || (currenttime - timestamp) >= mSESSION_LIFETIME) {
			
			Log.d("Milog", "Sessid == null || (currenttime - timestamp) >= mSession_LIFETIME");
			
			systemConnect(new AsyncHttpResponseHandler(){
				public void onSuccess(String response) {
					Log.d("JmLog","system connect ! ");
					JSONObject resObj;
					try {
						resObj = new JSONObject(response);
					
						String newSessid = resObj.getString("sessid");
	
						SharedPreferences.Editor editor = app.preferencias.edit();
						editor.putLong(app.COOKIE_KEY_TIMESTAMP_SESSID, new Date().getTime() / 100);
						editor.putString(app.COOKIE_KEY_ID_SESION_USUARIO_LOGUEADO, newSessid);
						editor.commit();

						final Long timestamp = new Date().getTime() / 100;
						final String time = timestamp.toString();
						parametros.put("domain_time_stamp", time);
						parametros.put("sessid", newSessid);

						client.post(uri, parametros, responseHandler);
					
					} catch (JSONException e) {
						Log.d("Milog","Excepcion en callPost: " + e.toString());
					}
				}
				
				public void onFailure(Throwable error) {
					Log.d("Milog","Error en callPost: " + error.toString());
				}
				
			});
			
			
		} else{
			
			Log.d("Milog", "El sessid est� relleno, y tiene de valor = " + sessid);

			final String time = timestamp.toString();
			parametros.put("domain_time_stamp", time);
			parametros.put("sessid", sessid);

			client.post(uri, parametros, responseHandler);
			
		}
	}
	
	
	/* Llamada a servicios a trav�s de GET */
	private void callGet(AsyncHttpResponseHandler responseHandler, final String url){
		client.get(url, responseHandler);
	}
	
	
	
	
	
	
	
	
	/* Login de usuario */
	public void userLogin(AsyncHttpResponseHandler responseHandler, String user, String password){
		String uri = url + "user/login";
		RequestParams rm = new RequestParams();
		rm.put("username", user);
		rm.put("password", password);
		
		callPost(responseHandler, uri, rm);
	}

	/* Login de usuario personalizado con contrase�a encriptada */
	public void basicLogin(AsyncHttpResponseHandler responseHandler, String mail, String passwordCrypted){
		String uri = url + "basic/login";
		RequestParams rm = new RequestParams();
		rm.put("mail", mail);
		rm.put("password", passwordCrypted);
		
		callPost(responseHandler, uri, rm);
	}
	
	/* Logout de usuarios */
	public void userLogout(AsyncHttpResponseHandler responseHandler, String sessionID){

		String uri = url + "user/logout";

		RequestParams rm = new RequestParams();
		Log.d("Jmlog","URL SERV => "+uri+" rm ->"+rm+" responseHandler -> "+responseHandler);
		callPost(responseHandler, uri, rm);
	}
	
	
	/* Registro de usuarios */
	public void userRegister(AsyncHttpResponseHandler responseHandler, HashMap<String, String> user){
		Log.d("Milog", "Entra en el metodo userRegister");
		String uri = url + "user/register";
		
		RequestParams rm = new RequestParams();
		
		// Recorrer el hashmap que nos pasan
		Iterator<Entry<String, String>> iterator = user.entrySet().iterator();
		Map.Entry<String,String> item;
		while (iterator.hasNext()) {
			item = (Map.Entry<String,String>)iterator.next();
			rm.put(item.getKey(), item.getValue());
		}

		callPost(responseHandler, uri, rm);
	}
	
	
	/* Devolver lista de t�rminos de un vocabulario */
	public void getTreeWithVid(AsyncHttpResponseHandler responseHandler, String vid, String parent, String maxDepth){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("vid", vid);
		params.put("parent", parent);
		params.put("max_depth", maxDepth);
		getTreeWithParams(responseHandler, params);
	}
	
	/* Devolver lista de t�rminos de un vocabulario */
	public void getTreeWithParams(AsyncHttpResponseHandler responseHandler, HashMap<String, String> params){
		String uri = url + "taxonomy_vocabulary/getTree";
		RequestParams rm = new RequestParams();
		
		// Recorrer el hashmap que nos pasan
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		Map.Entry<String,String> item;
		while (iterator.hasNext()) {
			item = (Map.Entry<String,String>)iterator.next();
			rm.put(item.getKey(), item.getValue());
		}
		
		callPost(responseHandler, uri, rm);
	}
	
	/* Devolver un t�rmino a partir de su tid */
	public void getTermWithTid(AsyncHttpResponseHandler responseHandler, String tid){
		String uri = url + "taxonomy_term/" + tid;
		callGet(responseHandler, uri);
	}
	
	
	/* Llamar a una vista */
	public void viewsGet(AsyncHttpResponseHandler responseHandler, String viewName, String displayId, ArrayList<String> params){
		String uri = url + "views/" + viewName + "?display_id=" + displayId;
		if(params != null){
			
			if(params.size() > 0){
				uri = uri + "&";
			}
			
			for(int i=0; i<params.size(); i++){
				uri = uri + "&args[" + i + "]=" + params.get(i);
			}
		}
		
		callGet(responseHandler, uri);
	}
	
	
	/* Obtener un nodo */
	public void nodeGet(AsyncHttpResponseHandler responseHandler, String nid){
		String uri = url + "node/" + nid;
		callGet(responseHandler, uri);
	}
	
	
	
	
	
	public void customMethodCallPost(String serviceURLPart, AsyncHttpResponseHandler responseHandler, HashMap<String, String> params){
		String uri = url + serviceURLPart;
		
		RequestParams rm = new RequestParams();
		
		if(params != null){
			// Recorrer el hashmap que nos pasan
			Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
			Map.Entry<String, String> item;
			while (iterator.hasNext()) {
				item = (Map.Entry<String, String>)iterator.next();
				rm.put(item.getKey(), item.getValue());
			}
		}
		

		callPost(responseHandler, uri, rm);
	}
	
	public void customMethodCallGet(String serviceURLPart, AsyncHttpResponseHandler responseHandler, HashMap<String, String> params){
		String uri = url + serviceURLPart;
		if(params != null){
			Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
			Map.Entry<String, String> item;
			while (iterator.hasNext()) {
				item = (Map.Entry<String, String>)iterator.next();
				uri = uri + "?" + item.getKey() + "=" + item.getValue();
			}
		}
		callGet(responseHandler, uri);
	}


	
}
