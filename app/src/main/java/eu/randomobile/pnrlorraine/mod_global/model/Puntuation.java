package eu.randomobile.pnrlorraine.mod_global.model;

import android.app.Application;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import eu.randomobile.pnrlorraine.MainApp;


public class Puntuation {

    // Interface para comunicarse con las llamadas asíncronas
    public static PuntuacionInterface puntuacionInterface;
    private String descripcion;
	private int puntos;
	private String date;

    public Puntuation() {

	}

	public Puntuation(String descripcion, int puntos){
		this.setDescripcion(descripcion);
		this.puntos = puntos;
	}

	public static void cargarListaPuntuaciones(Application application, String uid){

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", uid);


        MainApp app = (MainApp)application;

		app.clienteDrupal.customMethodCallGet("points_log", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {


                ArrayList<Puntuation> listaPuntos = null;

				if(response != null && !response.equals("")){

					try {
	                	JSONArray arrayRes = new JSONArray(response);
	                	if(arrayRes != null){
	                		if(arrayRes.length() > 0){
	                			listaPuntos = new ArrayList<Puntuation>();
	                		}

	                		for(int i=0; i< arrayRes.length(); i++){
	                			Object recObj = arrayRes.get(i);
	                			if(recObj != null){
	                				if(recObj.getClass().getName().equals(JSONObject.class.getName())){
	                					JSONObject recDic = (JSONObject)recObj;


                                        int puntos = recDic.getInt("points");
	                					String descripcion = "";
	                					if(!recDic.isNull("description")){
	                						descripcion = recDic.getString("description");
	                					}

	                					String date = recDic.getString("thedate");

	                					Puntuation item = new Puntuation();
	                					item.setDescripcion(descripcion);
	                					item.setPuntos(puntos);
	                					item.setDate(date);


                                        listaPuntos.add(item);

                                    }
	                			}
	                		}

                            // Informar al delegate
	                		if(Puntuation.puntuacionInterface != null){
	                			Puntuation.puntuacionInterface.seCargoListaPuntuaciones(listaPuntos);
	                			return;
	                		}


                        }


                    } catch (Exception e) {
						Log.d("Milog", "Excepcion en puntuaciones: " + e.toString());
					}
				}

                // Informar al delegate
	    		if(Puntuation.puntuacionInterface != null){
	    			Puntuation.puntuacionInterface.producidoErrorAlCargarListaPuntuaciones("Error al cargar lista de puntuaciones");
	    		}


            }

                    public void onFailure(Throwable error) {
				// Informar al delegate
				if(Puntuation.puntuacionInterface != null){
	    			Puntuation.puntuacionInterface.producidoErrorAlCargarListaPuntuaciones(error.toString());
	    		}
			}
                },
                params);

    }

    public static void cargarPuntuacionUsuario(Application application){

		MainApp app = (MainApp)application;

        app.clienteDrupal.customMethodCallPost("basic/passport", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				if(response != null && !response.equals("")){

                    try {
	                	JSONObject dicRes = new JSONObject(response);
	                	if(dicRes != null){

	                		int puntuacion = dicRes.getInt("points");

	                		// Informar al delegate
	                		if(Puntuation.puntuacionInterface != null){
	                			Puntuation.puntuacionInterface.seCargoPuntuacionUsuario(puntuacion);
	                			return;
	                		}

                        }
	                } catch (Exception e) {
						Log.d("Milog", "Excepcion get puntuacion: " + e.toString());
					}
				}

                // Informar al delegate
	    		if(Puntuation.puntuacionInterface != null){
	    			Puntuation.puntuacionInterface.producidoErrorAlCargarPuntuacionUsuario("Error al cargar puntuacion");
	    		}
			}

			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Puntuation.puntuacionInterface != null){
	    			Puntuation.puntuacionInterface.producidoErrorAlCargarPuntuacionUsuario(error.toString());
	    		}
			}
                },
                null);

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }


    public interface PuntuacionInterface {
        void seCargoListaPuntuaciones(ArrayList<Puntuation> puntuaciones);

        void producidoErrorAlCargarListaPuntuaciones(String error);

        void seCargoPuntuacionUsuario(int puntuacion);

        void producidoErrorAlCargarPuntuacionUsuario(String error);
    }
	
	
	
}
