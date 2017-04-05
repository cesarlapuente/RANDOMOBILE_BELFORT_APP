package eu.randomobile.pnrlorraine.mod_offline;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_global.environment.GPS;
import eu.randomobile.pnrlorraine.mod_global.model.Route;

public class OfflineRoute {
    public static RoutesModeOfflineInterface routesInterface;

    public static interface RoutesModeOfflineInterface {
        public void seCargoListaRoutesOffline(ArrayList<Route> routes);

        public void producidoErrorAlCargarListaRoutesOffline(String error);

        public void seCargoRouteOffline(Route route);

        public void producidoErrorAlCargarRouteOffline(String error);
    }

    public static void fillRoutesTable(Application application) {
        final MainApp app = (MainApp) application;
        HashMap<String, String> params = new HashMap<String, String>();
        double[] coords = GPS.getGPS(app.getApplicationContext());
        params.put("lat", String.valueOf(coords[0]));
        params.put("lon", String.valueOf(coords[1]));

        app.clienteDrupal.customMethodCallPost("route/get_list_distance", new AsyncHttpResponseHandler() {
                    public void onSuccess(String response) {

                        Log.d("Milog", "Respuesta de cargar rutas distance: " + response);

                        if (response != null && !response.equals("")) {
                            Offline.insertJsonList(app, app.DRUPAL_TYPE_ROUTE, response);

                        }
                        if (OfflineRoute.routesInterface != null) {
                            OfflineRoute.routesInterface.seCargoListaRoutesOffline(null);
                        }
                    }

                    public void onFailure(Throwable error) {
                        Log.d("Milog", error.toString());
                        if (OfflineRoute.routesInterface != null) {
                            Log.d("Milog",
                                    "Antes de informar al delegate de un error: " + error.toString());
                            OfflineRoute.routesInterface
                                    .producidoErrorAlCargarListaRoutesOffline(error
                                            .toString());
                        }
                    }
                },
                params);
    }

    public static void fillRouteItem(Application application, final String nid) {
        final MainApp app = (MainApp) application;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("nid", nid);

        app.clienteDrupal.customMethodCallPost("route/get_item", new AsyncHttpResponseHandler() {
                    public void onSuccess(String response) {

                        Log.d("Milog", "Respuesta de cargar ruta: " + response);

                        if (response != null && !response.equals("")) {
                            Offline.insertJsonItem(app, app.DRUPAL_TYPE_ROUTE, Integer.parseInt(nid), response);
                        }
                        if (OfflineRoute.routesInterface != null) {
                            OfflineRoute.routesInterface.seCargoRouteOffline(null);
                        }
                    }

                    public void onFailure(Throwable error) {
                        Log.d("Milog", error.toString());
                        if (OfflineRoute.routesInterface != null) {
                            Log.d("Milog",
                                    "Antes de informar al delegate de un error: " + error.toString());
                            OfflineRoute.routesInterface
                                    .producidoErrorAlCargarRouteOffline(error
                                            .toString());
                        }
                    }
                },
                params);
    }

    public static void cargaListaRutasOffline(Application application) {
        final MainApp app = (MainApp) application;
        String jsonRes = Offline.extractJsonList(app, app.DRUPAL_TYPE_ROUTE);
        ArrayList<Route> listaRoutes = Route.fillRouteList(jsonRes, application);
        if (OfflineRoute.routesInterface != null) {
            if (listaRoutes != null)
                OfflineRoute.routesInterface.seCargoListaRoutesOffline(listaRoutes);
            else {
                Log.d("Milog",
                        "error cargaListaRutasOffline");
                OfflineRoute.routesInterface
                        .producidoErrorAlCargarListaRoutesOffline("cargaListaRutasOffline");
            }
        }
    }

    public static void cargarRouteOffline(Application application, String nid) {
        final MainApp app = (MainApp) application;
        String jsonRes = Offline.extractJsonItem(app, app.DRUPAL_TYPE_ROUTE, Integer.valueOf(nid));
        Route item = Route.fillRoute(jsonRes);
        if (OfflineRoute.routesInterface != null) {
            if (item != null)
                OfflineRoute.routesInterface.seCargoRouteOffline(item);
            else {
                Log.d("Milog",
                        "error cargarRouteOffline");
                OfflineRoute.routesInterface
                        .producidoErrorAlCargarRouteOffline("cargarRouteOffline: nid= " + nid);
            }
        }
    }

}
