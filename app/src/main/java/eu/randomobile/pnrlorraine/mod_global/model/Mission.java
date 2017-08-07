package eu.randomobile.pnrlorraine.mod_global.model;

import android.app.Application;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import eu.randomobile.pnrlorraine.MainApp;

public class Mission {

    // Interface para comunicarse con las llamadas asíncronas
    public static MissionsInterface missionsInterface;
    private String id;
	private String nombre;
	private String descricpion;
	private String imagenDestacada;
	private int enigmas;
	private int resueltos;
    private float progreso;
    private String idEnigma;
	private boolean done;
	private Stamp stamp;

    public Mission() {

	}
	

	public Mission(String id, String nombre, String descripcion, String imagenDestacada, int enigmas, int resueltos, float progreso, String idEnigma){
		this.id = id;
		this.nombre = nombre;
		this.descricpion = descripcion;
		this.imagenDestacada = imagenDestacada;
		this.enigmas = enigmas;
		this.resueltos = resueltos;
		this.progreso = progreso;
		this.idEnigma = idEnigma;
	}

	public static void cargarListaMission(Application application){

		HashMap<String, String> params = null;

		MainApp app = (MainApp)application;

		app.clienteDrupal.customMethodCallPost("mission/get_list", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {


                ArrayList<Mission> listaMissions = null;

				if(response != null && !response.equals("")){

					try {
	                	JSONArray arrayRes = new JSONArray(response);
	                	if(arrayRes != null){
	                		if(arrayRes.length() > 0){
	                			listaMissions = new ArrayList<Mission>();
	                		}

	                		for(int i=0; i< arrayRes.length(); i++){
	                			Object recObj = arrayRes.get(i);
	                			if(recObj != null){
	                				if(recObj.getClass().getName().equals(JSONObject.class.getName())){
	                					JSONObject recDic = (JSONObject)recObj;
	                					String nid = recDic.getString("nid");
	                					String nombre = recDic.getString("title");
	                					String descripcion = recDic.getString("body");
	                					//String imagenDestacada = recDic.getString("image");
	                					float progreso = (float)recDic.getDouble("progress");

	                					Mission item = new Mission();
	                					item.setId(nid);
	                					item.setNombre(nombre);
	                					item.setDescripcion(descripcion);
	                					//item.setImagenDestacada(imagenDestacada);
	                					//Log.d("Milog", "IMAGEN: " + item.getImagenDestacada());
	                					item.setProgreso(progreso);
	                					listaMissions.add(item);

	                				}
	                			}
	                		}

	                		// Informar al delegate
	                		if(Mission.missionsInterface != null){
	                			Mission.missionsInterface.seCargoListaMissions(listaMissions);
	                			return;
	                		}


                        }


	                } catch (Exception e) {
						Log.d("Milog", "Excepcion en missions: " + e.toString());
					}
				}

				// Informar al delegate
	    		if(Mission.missionsInterface != null){
	    			Mission.missionsInterface.producidoErrorAlCargarListaMissions("Error al cargar lista de missions");
	    		}


            }

                    public void onFailure(Throwable error) {
				// Informar al delegate
				if(Mission.missionsInterface != null){
	    			Mission.missionsInterface.producidoErrorAlCargarListaMissions(error.toString());
	    		}
			}
                },
                params);

    }

    public static void cargarMission(Application application, final String nid){

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nid", nid);

        MainApp app = (MainApp)application;

        app.clienteDrupal.customMethodCallPost("mission/get_item", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {

                Mission mission = null;

                if(response != null && !response.equals("")){

                    try {
	                	JSONObject dicRes = new JSONObject(response);
	                	if(dicRes != null){

                            mission = new Mission();

                            mission.setId(nid);
	                		mission.setNombre(dicRes.getString("title"));
	                		mission.setDescripcion(dicRes.getString("body"));
	                		mission.setImagenDestacada(dicRes.getString("image"));
	                		mission.setEnigmas(dicRes.getInt("enigmas"));
	                		mission.setResueltos(dicRes.getInt("resolved"));

                            float progreso = (float)dicRes.getDouble("progress");

                            mission.setProgreso(progreso);
	                		mission.setIdEnigma(dicRes.getString("enigma"));

                            mission.setDone(dicRes.getBoolean("solved"));

                            Object objStamp = dicRes.get("stamp");
	                		if(objStamp != null && objStamp.getClass().getName().equals(JSONObject.class.getName())){
	                			JSONObject dicStamp = (JSONObject)objStamp;
	                			Stamp stamp = new Stamp();
	                			stamp.setId( dicStamp.getString("nid") );
	                			stamp.setNombre( dicStamp.getString("title") );
	                			stamp.setImagenDestacada( dicStamp.getString("image") );

                                mission.setStamp(stamp);
	                		}


                            // Informar al delegate
	                		if(Mission.missionsInterface != null){
	                			Mission.missionsInterface.seCargoMission(mission);
	                			return;
	                		}

                        }


                    } catch (Exception e) {
						Log.d("Milog", "Excepcion get mission: " + e.toString());
					}
				}

                // Informar al delegate
	    		if(Mission.missionsInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Mission.missionsInterface.producidoErrorAlCargarMission("Error al cargar mission");
	    		}


            }

                    public void onFailure(Throwable error) {
				// Informar al delegate
				if(Mission.missionsInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Mission.missionsInterface.producidoErrorAlCargarMission(error.toString());
	    		}
			}
                },
                params);

    }

    public static void marcarMisionDone(Application application, String nidMision){

		MainApp app = (MainApp)application;

        HashMap<String, String> params = new HashMap<String, String>();
		params.put("nid", nidMision);
		params.put("key", app.drupalSecurity.encrypt(nidMision));


        app.clienteDrupal.customMethodCallPost("mission/solve", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {

                Log.d("Milog", "Respuesta de resolver mission: " + response);
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
	    	                		if(Mission.missionsInterface != null){
	    	                			Mission.missionsInterface.seHaMarcadoMisionComoDone(true);
	    	                			return;
	    	                		}
		                		}else{
		                			// Esto es que ha respondido muy mal
	                				// Informar al delegate
	    	                		if(Mission.missionsInterface != null){
	    	                			Mission.missionsInterface.seHaMarcadoMisionComoDone(false);
	    	                			return;
	    	                		}
		                		}
	                		}else{
	                			// Informar al delegate
	            	    		if(Mission.missionsInterface != null){
	            	    			Log.d("Milog", "Antes de informar al delegate de un error: " + error);
	            	    			Mission.missionsInterface.producidoErrorAlMarcarMisionDone("No se puede procesar la respuesta en el servidor");
	            	    			return;
	            	    		}
	                		}

                        }


                    } catch (Exception e) {
						Log.d("Milog", "Excepcion resolver mision: " + e.toString());
					}
				}

                // Informar al delegate
	    		if(Mission.missionsInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Mission.missionsInterface.producidoErrorAlMarcarMisionDone("Error al recoger respuesta");
	    		}


            }

                    public void onFailure(Throwable error) {
				// Informar al delegate
				if(Mission.missionsInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Mission.missionsInterface.producidoErrorAlMarcarMisionDone(error.toString());
	    		}
			}
                },
                params);

    }

    public Stamp getStamp() {
        return stamp;
    }

    public void setStamp(Stamp stamp) {
        this.stamp = stamp;
    }

    /***************************************************/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descricpion;
    }

    public void setDescripcion(String descripcion) {
        this.descricpion = descripcion;
    }

    public String getImagenDestacada() {
        return imagenDestacada;
    }

    public void setImagenDestacada(String imagenDestacada) {
        this.imagenDestacada = imagenDestacada;
    }

    public int getEnigmas() {
        return enigmas;
    }

    public void setEnigmas(int enigmas) {
        this.enigmas = enigmas;
    }

    public int getResueltos() {
        return resueltos;
    }

    public void setResueltos(int resueltos) {
        this.resueltos = resueltos;
    }

    public float getProgreso() {
        return progreso;
    }

    public void setProgreso(float progreso) {
        this.progreso = progreso;
    }

    public String getIdEnigma() {
        return idEnigma;
    }

    public void setIdEnigma(String idEnigma) {
        this.idEnigma = idEnigma;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }


    public interface MissionsInterface {
        void seCargoListaMissions(ArrayList<Mission> missions);

        void producidoErrorAlCargarListaMissions(String error);

        void seCargoMission(Mission mission);

        void producidoErrorAlCargarMission(String error);

        void seHaMarcadoMisionComoDone(boolean res);

        void producidoErrorAlMarcarMisionDone(String error);
    }
	
	
	
	
}
