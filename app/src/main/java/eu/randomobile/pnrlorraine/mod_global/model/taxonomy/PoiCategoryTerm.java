package eu.randomobile.pnrlorraine.mod_global.model.taxonomy;

import android.app.Application;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import eu.randomobile.pnrlorraine.MainApp;

@SuppressWarnings("serial")
public class PoiCategoryTerm extends Term implements Serializable {
    public static PoiCategoriesInterface poiCategoriesInterface;

    // modif thib
    private String icon;

    public PoiCategoryTerm(String tid, String name, String idParent) {
        super(tid, name, "", idParent);
    }

    public PoiCategoryTerm() {
    }

    public static void cargarListaCategoriasPois(Application application) {
        HashMap<String, String> params = null;
        MainApp app = (MainApp) application;


        app.clienteDrupal.customMethodCallPost("taxonomy/pois", new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {

                ArrayList<PoiCategoryTerm> listaCategorias = null;

                if (response != null && !response.equals("")) {
                    try {
                        JSONArray arrayRes = new JSONArray(response);

                        if (arrayRes != null) {
                            if (arrayRes.length() > 0) {

                                listaCategorias = new ArrayList<PoiCategoryTerm>();
                            }

                            for (int i = 0; i < arrayRes.length(); i++) {
                                Object recObj = arrayRes.get(i);

                                if (recObj != null) {
                                    if (recObj.getClass().getName().equals(JSONObject.class.getName())) {
                                        JSONObject recDic = (JSONObject) recObj;
                                        String tid = recDic.getString("tid");
                                        String title = recDic.getString("title");

                                        PoiCategoryTerm item = new PoiCategoryTerm();

                                        item.setTid(tid);
                                        item.setName(title);
                                        listaCategorias.add(item);
                                    }
                                }
                            }

                            // Informar al delegate
                            if (PoiCategoryTerm.poiCategoriesInterface != null) {
                                PoiCategoryTerm.poiCategoriesInterface.seCargoListaCategoriasPois(listaCategorias);

                                return;
                            }
                        }

                    } catch (Exception e) {
                        Log.d("Milog", "Excepcion en lista categorias pois: " + e.toString());
                    }
                }

                // Informar al delegate
                if (PoiCategoryTerm.poiCategoriesInterface != null) {
                    PoiCategoryTerm.poiCategoriesInterface.producidoErrorAlCargarListaCategoriasPois("Error al cargar lista de pois");
                }
            }

            public void onFailure(Throwable error) {
                // Informar al delegate
                if (PoiCategoryTerm.poiCategoriesInterface != null) {
                    PoiCategoryTerm.poiCategoriesInterface.producidoErrorAlCargarListaCategoriasPois(error.toString());
                }
            }
        }, params);

        Log.d("PoiCategoryTerm sais:", " <----------> Salida detectada <---------->");
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public interface PoiCategoriesInterface {
        void seCargoListaCategoriasPois(ArrayList<PoiCategoryTerm> pois);

        void producidoErrorAlCargarListaCategoriasPois(String error);
    }
}
