package eu.randomobile.pnrlorraine.mod_global.model.taxonomy;

import android.app.Application;
import android.graphics.Color;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import eu.randomobile.pnrlorraine.MainApp;

public class RouteCategoryTerm extends Term {

	public static RouteCategoriesInterface routeCategoriesInterface;
	private Color color;

	// Modif Thibault
	public RouteCategoryTerm(String tid, String name, String description, String idp) {
		super(tid, name, description, idp);
	}

	//

	public RouteCategoryTerm() {
		super();
	}

	public static void cargarListaCategoriasRutas(Application application){

		HashMap<String, String> params = null;

		MainApp app = (MainApp)application;

		app.clienteDrupal.customMethodCallPost("taxonomy/routes", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {


				ArrayList<RouteCategoryTerm> listaCategorias = null;

				if(response != null && !response.equals("")){

					try {
	                	JSONArray arrayRes = new JSONArray(response);
	                	if(arrayRes != null){
	                		if(arrayRes.length() > 0){
	                			listaCategorias = new ArrayList<RouteCategoryTerm>();
	                		}

							for(int i=0; i< arrayRes.length(); i++){
	                			Object recObj = arrayRes.get(i);
	                			if(recObj != null){
	                				if(recObj.getClass().getName().equals(JSONObject.class.getName())){
	                					JSONObject recDic = (JSONObject)recObj;
	                					String tid = recDic.getString("tid");
	                					String title = recDic.getString("title");

										RouteCategoryTerm item = new RouteCategoryTerm();
	                					item.setTid(tid);
	                					item.setName(title);
	                					listaCategorias.add(item);
	                				}
	                			}
	                		}

	                		// Informar al delegate
	                		if(RouteCategoryTerm.routeCategoriesInterface != null){
	                			RouteCategoryTerm.routeCategoriesInterface.seCargoListaCategoriasRutas(listaCategorias);
	                			return;
	                		}


						}


					} catch (Exception e) {
						Log.d("Milog", "Excepcion en lista categorias rutas: " + e.toString());
					}
				}

				// Informar al delegate
	    		if(RouteCategoryTerm.routeCategoriesInterface != null){
	    			RouteCategoryTerm.routeCategoriesInterface.producidoErrorAlCargarListaCategoriasRutas("Error al cargar lista de pois");
	    		}


			}

					public void onFailure(Throwable error) {
				// Informar al delegate
				if(RouteCategoryTerm.routeCategoriesInterface != null){
	    			RouteCategoryTerm.routeCategoriesInterface.producidoErrorAlCargarListaCategoriasRutas(error.toString());
	    		}
			}
				},
		params);

	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}


	public interface RouteCategoriesInterface {
		void seCargoListaCategoriasRutas(ArrayList<RouteCategoryTerm> routeCategories);

		void producidoErrorAlCargarListaCategoriasRutas(String error);
	}
	
	
	
}
