package eu.randomobile.pnrlorraine.mod_offline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.environment.GPS;
import eu.randomobile.pnrlorraine.mod_global.libraries.download.DownloadUrl;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_global.model.ResourceFile;
import eu.randomobile.pnrlorraine.mod_global.model.Route;
import eu.randomobile.pnrlorraine.mod_global.model.Vote;
import eu.randomobile.pnrlorraine.mod_global.model.Poi.PoisInterface;
import eu.randomobile.pnrlorraine.mod_global.model.Route.RoutesInterface;
import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.RouteCategoryTerm;
import eu.randomobile.pnrlorraine.utils.JSONManager;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;

// Clase para la funcionalidad Offline
public class Offline {
	private static final String dbName = "Data_Pnrlorraine";
	private static final String poisTable = "Pois";
	private static final String routesTable = "Routes";
	private static final String listsTable = "Lists";
	private static final int idPoi = 0;
	private static final int idRoute = 1;
	

	
	
	public static boolean createDB(Application application) {
		MainApp app = (MainApp)application;
		SQLiteDatabase myDB= null;
		boolean result = false;
		//File ficheroDB = new File(Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/" + dbName);
		String ficheroDB = Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/" + dbName; 
		try {
			   myDB = app.openOrCreateDatabase(ficheroDB, Context.MODE_PRIVATE, null);
			 
			   /* Create a Table in the Database. */
			   myDB.execSQL("CREATE TABLE IF NOT EXISTS "
			     + poisTable
			     + " (nid INTEGER PRIMARY KEY, jsonString VARCHAR);");

			   /* Create a Table in the Database. */
			   myDB.execSQL("CREATE TABLE IF NOT EXISTS "
			     + routesTable
			     + " (nid INTEGER PRIMARY KEY, jsonString VARCHAR);");
			   
			   /* Create a Table in the Database. */
			   /* Almacena los strings sqlite para las rutas o para los pois */
			   myDB.execSQL("CREATE TABLE IF NOT EXISTS "
			     + listsTable
			     + " (id INTEGER PRIMARY KEY, jsonString VARCHAR);");
			   result = true;
		}
	    catch(Exception e) {
		   Log.e("Error", "Error", e);
		  } finally {
		   if (myDB != null)
		    myDB.close();
		  }
		return result;
	}

	public static void insertJsonItem(Application application, String typeData, int nid, String jsonString) {
		MainApp app = (MainApp)application;
		SQLiteDatabase myDB = null;
		List<String> imageUrls = null;
		ContentValues insertValues = new ContentValues();
		String ficheroDB = Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/" + dbName;
		if (extractJsonItem (app, typeData, nid)!=null) {
			updateJsonItem(app, typeData, nid, jsonString);
			return;
		}
		try {
			myDB = app.openOrCreateDatabase(ficheroDB, Context.MODE_PRIVATE, null);
			if (typeData.equals(app.DRUPAL_TYPE_POI)) {
				insertValues.put("nid", nid);
				insertValues.put("jsonString", jsonString);
				myDB.insert(poisTable, null, insertValues);
				imageUrls = getFilesRouteList(jsonString, app);
//				myDB.execSQL("INSERT INTO "
//					     + poisTable
//					     + " (nid, jsonString)"
//					     + " VALUES (nid, jsonString);");
			}
			else if (typeData.equals(app.DRUPAL_TYPE_ROUTE)) {
				insertValues.put("nid", nid);
				insertValues.put("jsonString", jsonString);
				myDB.insert(routesTable, null, insertValues);
				imageUrls = getFilesRouteList(jsonString, app);
			}
			//Descargar ahora las urls de la lista...
			DownloadUrl downloadUrl= new DownloadUrl(app);
			String[] stringsUrls = new String[imageUrls.size()];
			stringsUrls = imageUrls.toArray(stringsUrls);//now strings is the resulting array
			downloadUrl.execute(stringsUrls);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			   if (myDB != null)
			    myDB.close();
			  }
			
	}

	public static void insertJsonList(Application application, String typeData, String jsonString) {

		MainApp app = (MainApp)application;
		SQLiteDatabase myDB = null;
		String ficheroDB = Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/" + dbName;
		if (extractJsonList(app,typeData) != null) {
			updateJsonList(app,typeData,jsonString);
			//getFilesRouteList(jsonString, app);
			return;
		}
		ContentValues insertValues = new ContentValues();
		try {
			myDB = app.openOrCreateDatabase(ficheroDB, Context.MODE_PRIVATE, null);
			if (typeData.equals(app.DRUPAL_TYPE_POI)) {
				insertValues.put("id", idPoi);
				insertValues.put("jsonString", jsonString);
				myDB.insert(listsTable, null, insertValues);

			}
			else if (typeData.equals(app.DRUPAL_TYPE_ROUTE)) {
				insertValues.put("id", idRoute);
				insertValues.put("jsonString", jsonString);
				myDB.insert(listsTable, null, insertValues);
				//getFilesRouteList(jsonString, app);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			   if (myDB != null)
			    myDB.close();
			  }

	}

	// Actualiza una ruta o poi concreto
	public static void updateJsonItem(Application application, String typeData, int nid, String jsonString) {

		MainApp app = (MainApp)application;
		SQLiteDatabase myDB = null;
		String where = "nid=" + Integer.toString(nid);
		ContentValues values = new ContentValues();
		String ficheroDB = Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/" + dbName;
		List<String> imageUrls = null;
		try {
			myDB = app.openOrCreateDatabase(ficheroDB, Context.MODE_PRIVATE, null);
			if (typeData.equals(app.DRUPAL_TYPE_POI)) {
				values.put("nid", nid);
				values.put("jsonString", jsonString);
				myDB.update(poisTable, values, where, null);
				imageUrls = getFilesRouteList(jsonString, app);
			}
			else if (typeData.equals(app.DRUPAL_TYPE_ROUTE)) {
				values.put("nid", nid);
				values.put("jsonString", jsonString);
				myDB.update(routesTable, values, where, null);
				imageUrls = getFilesRouteList(jsonString, app);
			}
			//Descargar ahora las urls de la lista...
			DownloadUrl downloadUrl= new DownloadUrl(app);
			String[] stringsUrls = new String[imageUrls.size()];
			stringsUrls = imageUrls.toArray(stringsUrls);//now strings is the resulting array
			downloadUrl.execute(stringsUrls);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			   if (myDB != null)
			    myDB.close();
			  }

	}

	// Actualiza la lista (ya sea de rutas o pois)
	public static void updateJsonList (Application application, String typeData, String jsonString) {

		MainApp app = (MainApp)application;
		SQLiteDatabase myDB = null;
		int idTable =-1;
		String where = null;
		String ficheroDB = Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/" + dbName;
		ContentValues values = new ContentValues();
		
		if (typeData.equals(app.DRUPAL_TYPE_POI)) {
			idTable = idPoi;
		}
		else if (typeData.equals(app.DRUPAL_TYPE_ROUTE)) {
			idTable = idRoute;
		}
		where = "id=" + Integer.toString(idTable);
		try {
			myDB = app.openOrCreateDatabase(ficheroDB, Context.MODE_PRIVATE, null);
			values.put("id", idTable);
			values.put("jsonString", jsonString);
			myDB.update(listsTable, values, where, null);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			   if (myDB != null)
			    myDB.close();
			  }

	}
	// Extrae la lista (de pois o rutas indicadas en typeData) en formato json
	public static String extractJsonList (Application application, String typeData) {
		String jsonString = null;
		MainApp app = (MainApp)application;
		SQLiteDatabase myDB = null;
		String where = null;
		String selectQuery = null;
		Cursor c = null;
		String ficheroDB = Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/" + dbName;
		
		if (typeData.equals(app.DRUPAL_TYPE_POI)) {
			where = " WHERE id=" + Integer.toString(idPoi);
		}
		else if (typeData.equals(app.DRUPAL_TYPE_ROUTE)) {
			where = " WHERE id=" + Integer.toString(idRoute);
		}
		
		selectQuery = "SELECT jsonString FROM " + listsTable + where;
		try {
			myDB = app.openOrCreateDatabase(ficheroDB, Context.MODE_PRIVATE, null);

			c = myDB.rawQuery(selectQuery, null);
			if (c.moveToFirst()) {
			    jsonString = c.getString(c.getColumnIndex("jsonString"));
			}
			c.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			   if (myDB != null)
			    myDB.close();
			  }
		return jsonString;
	}
	
	// Extrae un item (ya sea poi o ruta) identificado por su nid.
	// La cadena de salida esta en formato Json
	public static String extractJsonItem (Application application, String typeData, int nid) {
		String jsonString = null;
		MainApp app = (MainApp)application;
		SQLiteDatabase myDB = null;
		String where = null;
		String selectQuery = null;
		Cursor c = null;
		String ficheroDB = Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/" + dbName;
		
		where = " WHERE nid=" + Integer.toString(nid);
		
		try {
			myDB = app.openOrCreateDatabase(ficheroDB, Context.MODE_PRIVATE, null);
			if (typeData.equals(app.DRUPAL_TYPE_POI)) {
				selectQuery = "SELECT jsonString FROM " + poisTable + where;
			}
			else if (typeData.equals(app.DRUPAL_TYPE_ROUTE)) {
				selectQuery = "SELECT jsonString FROM " + routesTable + where;
			}
			c = myDB.rawQuery(selectQuery, null);
			if (c.moveToFirst()) {
			    jsonString = c.getString(c.getColumnIndex("jsonString"));
			}
			c.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			   if (myDB != null)
			    myDB.close();
			  }

		return jsonString;
	}

	// Borra los contenidos de las bases de datos y la crea de nuevo
	public static boolean resetDB(Application application) {
		
		MainApp app = (MainApp)application;
		boolean result = false;
		String ficheroDB = Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/" + dbName;
		result = app.deleteDatabase(ficheroDB); 
		if (result) 
			result =createDB(app);
		return result;
	}
	
	public static boolean resetTable (Application application, String typeData) {
		MainApp app = (MainApp)application;
		boolean result = false;
		SQLiteDatabase myDB = null;
		String ficheroDB = Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/" + dbName;
		try {
			myDB = app.openOrCreateDatabase(ficheroDB, Context.MODE_PRIVATE, null);
			if (typeData.equals(app.DRUPAL_TYPE_POI)) {
			   myDB.execSQL("DROP TABLE IF EXISTS " + poisTable);
			   /* Create a Table in the Database. */
			   myDB.execSQL("CREATE TABLE IF NOT EXISTS "
			     + poisTable
			     + " (nid INTEGER PRIMARY KEY, jsonString VARCHAR);");
			}
			else if (typeData.equals(app.DRUPAL_TYPE_ROUTE)) {
				myDB.execSQL("DROP TABLE IF EXISTS " + routesTable);
				myDB.execSQL("CREATE TABLE IF NOT EXISTS "
						     + routesTable
						     + " (nid INTEGER PRIMARY KEY, jsonString VARCHAR);");
			}
			result = true;
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			   if (myDB != null)
			    myDB.close();
	   }
		
		return result;
	}
	

	
	private static Boolean checkInVariable (String val, int max) {
		if (val.equals("null"))
			return true;
		if (Integer.parseInt(val) < max)
			return true;
		else 
			return false;
	}
	
	private static Boolean checkKeyword (String keyword, String title, String body) {
		
		// Locale de Francia
		Locale locale = new Locale("fr", "FR");
		Boolean ok = true;
		if ((title != null) && ! title.toLowerCase(locale).contains(keyword.toLowerCase(locale)))
			ok &= false;
		if ((body != null) && ! body.toLowerCase(locale).contains(keyword.toLowerCase(locale)))
			ok &= false;
		return ok;
	}
	
	// Filtra las rutas que esten en la BDD con los criterios establecidos
	// como parametros
	public static String filterRoute (Application application, int max_distance_route, int max_duration, int max_difficulty, int max_distance, String keyword) {
		MainApp app = (MainApp)application;
		String jsonString = null;
		JSONArray dicInput; 
		JSONArray dicOutput;
		// Primero obtenemos el json de las rutas
		jsonString = extractJsonList (app, app.DRUPAL_TYPE_ROUTE);
		
		try {
        	dicInput = new JSONArray(jsonString);
        	dicOutput = new JSONArray ();
        	if (dicInput == null)
        		return null;
        	for(int i=0; i< dicInput.length(); i++){
    			Object recObj = dicInput.get(i);
    			boolean ok = true;
    			if(recObj != null){
    				if(recObj.getClass().getName().equals(JSONObject.class.getName())){
    					JSONObject recDic = (JSONObject)recObj;
    					String distance_route = recDic.getString("distance_route");
    					ok &= checkInVariable (distance_route, max_distance_route);
    					String duration = recDic.getString ("duration");
    					ok &= checkInVariable (duration, max_duration);
    					String distance = recDic.getString("distance");
    					ok &= checkInVariable (distance, max_distance);
    					String difficulty = recDic.getString("difficulty");
    					ok &= checkInVariable (difficulty, max_difficulty);
    					String title = recDic.getString("title");
    					String body = recDic.getString("body");
    					ok &= checkKeyword (keyword, title, body);
    					
    					if (ok==true)
    						dicOutput.put(recObj);
    				}
    			}
        	}
        	jsonString = dicOutput.toString();
        } 
		catch (Exception e) {
			Log.d("Milog", "Excepcion cargar route: " + e.toString());
		}
		
		return jsonString;
	}
	
	public static void downloadListPhotos (Application application, String typeData, String jsonString) {
		MainApp app = (MainApp)application;
		if (typeData.equals(app.DRUPAL_TYPE_POI)) {
			
		}
		else if (typeData.equals(app.DRUPAL_TYPE_ROUTE)) {
			getFilesRouteList(jsonString, app);
		}
	}
	
	private static List<String> getFilesRouteList (String response, Application application) {
		MainApp app = (MainApp) application;
		List<String> mStrings = new ArrayList<String>();
		try {
        			JSONObject recObj = new JSONObject(response);
        			if(recObj != null){
        				Object objImages = recObj.get("images");
        				if(objImages != null && objImages.getClass().getName().equals(JSONArray.class.getName())){
        					JSONArray array = (JSONArray)objImages;
        					for(int i=0; i<array.length(); i++){
        						Object obj = array.get(i);
        						if(obj != null && obj.getClass().getName().equals(JSONObject.class.getName())){
        							JSONObject dic = (JSONObject)obj;
        							String url = dic.getString("url");
        							mStrings.add(url);
        						}
        					}
        				}
        				//Anyadir ahora la url de mapas (para el caso de rutas)
        				String url_map = recObj.getString("map");
        				if (url_map != null)
        					mStrings.add(url_map);
        			} 		        		
        } catch (Exception e) {
			Log.d("Milog", "Excepcion en lista rutas: " + e.toString());
		}
		return mStrings;
	}
	
	
	
	
	// Nos dice si la ruta esta descargada ya
	public static boolean isNidInDB (Application application, String typeData, int nid) {
		boolean result = false;
		String jsonString = null;
		MainApp app = (MainApp)application;
		SQLiteDatabase myDB = null;
		String where = null;
		String selectQuery = null;
		Cursor c = null;
		String ficheroDB = Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/" + dbName;
		
		where = " WHERE nid=" + Integer.toString(nid);
		
		try {
			myDB = app.openOrCreateDatabase(ficheroDB, Context.MODE_PRIVATE, null);
			if (typeData.equals(app.DRUPAL_TYPE_POI)) {
				selectQuery = "SELECT jsonString FROM " + poisTable + where;
			}
			else if (typeData.equals(app.DRUPAL_TYPE_ROUTE)) {
				selectQuery = "SELECT jsonString FROM " + routesTable + where;
			}
			c = myDB.rawQuery(selectQuery, null);
			if (c.moveToFirst()) {
			    jsonString = c.getString(c.getColumnIndex("jsonString"));
			}
			c.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			   if (myDB != null)
			    myDB.close();
			  }
		result = (jsonString != null);
		return result;
	}

}
