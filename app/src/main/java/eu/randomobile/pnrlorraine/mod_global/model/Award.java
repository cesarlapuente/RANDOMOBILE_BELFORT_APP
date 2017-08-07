package eu.randomobile.pnrlorraine.mod_global.model;

import android.app.Application;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import eu.randomobile.pnrlorraine.MainApp;

public class Award {

    // Interface para comunicarse con las llamadas as�ncronas
    public static AwardsInterface awardsInterface;
    private String id;
	private String nombre;
	private String descricpion;
	private String imagenDestacada;
	private ArrayList<ItemWinners> arrItemWinners;
	public Award(){

	}

    public Award(String id, String nombre, String descripcion, String imagenDestacada){
		this.id = id;
		this.nombre = nombre;
		this.descricpion = descripcion;
		this.imagenDestacada = imagenDestacada;



	}

	public static void cargarListaAward(Application application){

		HashMap<String, String> params = null;


        MainApp app = (MainApp)application;

		app.clienteDrupal.customMethodCallPost("award/get_list", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {


                ArrayList<Award> listaAwards = null;

				if(response != null && !response.equals("")){

					try {
	                	JSONArray arrayRes = new JSONArray(response);
	                	if(arrayRes != null){
	                		if(arrayRes.length() > 0){
	                			listaAwards = new ArrayList<Award>();
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

                                        Award item = new Award();
	                					item.setId(nid);
	                					item.setNombre(nombre);
	                					item.setDescripcion(descripcion);
	                					item.setImagenDestacada(imagenDestacada);
	                					listaAwards.add(item);

                                    }
	                			}
	                		}

                            // Informar al delegate
	                		if(Award.awardsInterface != null){
	                			Award.awardsInterface.seCargoListaAwards(listaAwards);
	                			return;
	                		}


                        }


                    } catch (Exception e) {
						Log.d("Milog", "Excepcion en awards: " + e.toString());
					}
				}

                // Informar al delegate
	    		if(Award.awardsInterface != null){
	    			Award.awardsInterface.producidoErrorAlCargarListaAwards("Error al cargar lista de awards");
	    		}


            }

                    public void onFailure(Throwable error) {
				// Informar al delegate
				if(Award.awardsInterface != null){
	    			Award.awardsInterface.producidoErrorAlCargarListaAwards(error.toString());
	    		}
			}
                },
                params);

    }
	
	public static void cargarAward(Application application, final String nid){

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nid", nid);

		MainApp app = (MainApp)application;

        app.clienteDrupal.customMethodCallPost("award/get_item", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {


                Award award = null;

                if(response != null && !response.equals("")){

                    try {
	                	JSONObject dicRes = new JSONObject(response);
	                	if(dicRes != null){

                            award = new Award();

                            award.setId(nid);
	                		award.setNombre(dicRes.getString("title"));
	                		award.setDescripcion(dicRes.getString("body"));
	                		award.setImagenDestacada(dicRes.getString("image"));


                            Object objWinners = dicRes.get("winners");
	                		if(objWinners != null && objWinners.getClass().getName().equals(JSONArray.class.getName())){
	                			JSONArray arrWinners = (JSONArray)objWinners;
	                			ArrayList<ItemWinners> miArrItemWinners = new ArrayList<ItemWinners>();
	                			award.setArrItemWinners(miArrItemWinners);
	                			for(int i = 0; i < arrWinners.length(); i++){
	                				Object objWinner = arrWinners.get(i);
	                				if(objWinner != null && objWinner.getClass().getName().equals(JSONObject.class.getName())){
	                					JSONObject dicWinner = (JSONObject)objWinner;
	                					ItemWinners miItemWinner = new ItemWinners();
	                					miItemWinner.setUid(dicWinner.getString("uid"));
	                					miItemWinner.setNombre(dicWinner.getString("name"));
	                					miItemWinner.setPosicion(dicWinner.getString("position"));
	                					miArrItemWinners.add(miItemWinner);
	                				}
	                			}
	                		}

	                		// Informar al delegate
	                		if(Award.awardsInterface != null){
	                			Award.awardsInterface.seCargoAward(award);
	                			return;
	                		}

                        }
	                } catch (Exception e) {
						Log.d("Milog", "Excepcion get award: " + e.toString());
					}
				}

                // Informar al delegate
	    		if(Award.awardsInterface != null){
	    			Award.awardsInterface.producidoErrorAlCargarAward("Error al cargar award");
	    		}
			}

			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Award.awardsInterface != null){
	    			Award.awardsInterface.producidoErrorAlCargarAward(error.toString());
	    		}
			}
                },
                params);

    }

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

    public ArrayList<ItemWinners> getArrItemWinners() {
        return arrItemWinners;
    }

    public void setArrItemWinners(ArrayList<ItemWinners> arrItemWinners) {
        this.arrItemWinners = arrItemWinners;
    }


    public interface AwardsInterface {
        void seCargoListaAwards(ArrayList<Award> awards);

        void producidoErrorAlCargarListaAwards(String error);

        void seCargoAward(Award award);

        void producidoErrorAlCargarAward(String error);
    }
	
}
