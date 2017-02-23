/*
 * Copyright (C) 2011 Scott Lund
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.randomobile.pnrlorraine.mod_home;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_discover.list.PoisListActivity;
import eu.randomobile.pnrlorraine.mod_discover.list.RoutesListActivity;
import eu.randomobile.pnrlorraine.mod_discover.map.RoutesGeneralMapActivity;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.data_access.DBHandler;
import eu.randomobile.pnrlorraine.mod_global.model.Especie;
import eu.randomobile.pnrlorraine.mod_global.model.Page;
import eu.randomobile.pnrlorraine.mod_guia.GuiaNaturaleza;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_notification.Cache;
import eu.randomobile.pnrlorraine.mod_notification.NotificationService;
import eu.randomobile.pnrlorraine.mod_options.OptionsActivity;

public class MainActivity extends Activity {
    private Dialog dialogPoi = null;
    ImageMap mImageMap;
    MainApp app;
    ImageView imgMainPhoto;
    static private boolean firstTime = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_home_activity_main);
        this.app = (MainApp) getApplication();

        // app.setRoutesList(app.getDBHandler().getRouteList());
        // app.setPoisList();  -- Not implemented yet.
        app.setEspecies(app.getDBHandler().getEspeciesList());

        // find the image map in the view
        mImageMap = (ImageMap) findViewById(R.id.map);
        mImageMap.setAttributes(true, false, (float) 1.0, "menu_lorraine");
        mImageMap.setImageResource(R.drawable.menu_lorraine);
        imgMainPhoto = (ImageView) findViewById(R.id.img_menu);
        if (Util.randInt(1, 2) == 1)
            imgMainPhoto.setBackgroundResource(R.drawable.main_foto1);
        else
            imgMainPhoto.setBackgroundResource(R.drawable.main_foto2);
        // mImageMap.setImageResource(R.drawable.btn_dialog);

        // add a click handler to react when areas are tapped
        mImageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
            @Override
            public void onImageMapClicked(int id, ImageMap imageMap) {
                mImageMap.showBubble(id);
                if (mImageMap.getAreaAttribute(id, "name").equals(
                        "LE PARCOURS")) {
                    // image2.setImageResource(R.drawable.capa7);
                    // image2.setVisibility(View.VISIBLE);
                    // SystemClock.sleep(100);
                    // End of new code.
                    app.resetRouteFilters();
                    Cache.filteredPois = null;
                    // Abrir la pantalla de pois
                    Intent intent = new Intent(MainActivity.this, RoutesListActivity.class);
                    startActivity(intent);

                } else if (mImageMap.getAreaAttribute(id, "name").equals("lE POINTS D'INTERESTS")) {
                    app.resetPoiFilters();
                    Cache.filteredPois = null;
                    // Abrir la pantalla de pois
                    Intent intent = new Intent(MainActivity.this, PoisListActivity.class);
                    startActivity(intent);

                } else if (mImageMap.getAreaAttribute(id, "name").equals("OPTIONS")) {
                    // Abrir la pantalla de opciones
                    Intent intent = new Intent(MainActivity.this, OptionsActivity.class);
                    startActivity(intent);

                } else if (mImageMap.getAreaAttribute(id, "name").equals("CARTE")) {
                    app.resetRouteFilters();
                    cargaActivityMaps();

                } else if (mImageMap.getAreaAttribute(id, "name").equals("PNRL")) {
                    // Abrir la pantalla de info de belfort
                    Intent intent = new Intent(MainActivity.this, PNRActivity.class);
                    startActivity(intent);

                } else if (mImageMap.getAreaAttribute(id, "name").equals("CONSEILS PRATIQUES")) {
                    // Abrir la pantalla de info de PNRL
                    Intent intent = new Intent(MainActivity.this, AdvicesActivity.class);
                    startActivity(intent);

                } else if (mImageMap.getAreaAttribute(id, "name").equals("FFRP")) {
                    // Abrir la pantalla de Guia de Naturaleza
                    Intent intent = new Intent(MainActivity.this, GuiaNaturaleza.class);
                    startActivity(intent);

                } else {
                    // when the area is tapped, show the name in a
                    // text bubble
                    mImageMap.showBubble(id);
                }
            }

            @Override
            public void onBubbleClicked(int id) {
                // react to info bubble for area being tapped

            }
        });

        // Internet acces checking.
        if (isInternetAvailable()) { AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Sin conexión");
            builder.setMessage("Es posible que algunas caracteristicas no se encuentren disponibles sin conexión a internet.");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Dismis
                }
            });

            builder.show();
        }
    }

    private void cargaActivityMaps() {
        if (app.getNetStatus() != 0) {
            Intent intent = new Intent(MainActivity.this, RoutesGeneralMapActivity.class);
            intent.putExtra(RoutesGeneralMapActivity.PARAM_KEY_MOSTRAR, RoutesGeneralMapActivity.PARAM_MAPA_GENERAL_MOSTRAR_RUTAS);
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle(R.string.txt_sin_conexion);
            builder.setMessage(R.string.txt_funcion_no_disponible);
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Dismis
                }
            });

            builder.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firstTime) {
            try {
                // updateLocalDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //showDialog();
        firstTime = false;
    }

    private void showDialog() {
        // Creating alert Dialog with one Button
        dialogPoi = new Dialog(this);
        dialogPoi.setContentView(R.layout.mod_discover_dialog_poi_route);
        dialogPoi.setTitle("Info " + "T�l�chargement");
        dialogPoi.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!

        //set up text
        TextView text = (TextView) dialogPoi.findViewById(R.id.txtPoiDescription);
        text.setText(R.string.mod_home__info_telecarga);

        //set up button
        Button button = (Button) dialogPoi.findViewById(R.id.Button01);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPoi.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it    
        dialogPoi.show();

    }
    // @Override
    // protected void onStart() {
    // // TODO Auto-generated method stub
    // super.onStart();
    // SystemClock.sleep(1000);
    // imgBaliza1.setVisibility(View.GONE);
    // imgBaliza2.setVisibility(View.VISIBLE);
    // SystemClock.sleep(2000);
    //
    // }

    @Override
    protected void onResume() {
        super.onResume();
        mImageMap.mBubbleMap.clear();
        mImageMap.postInvalidate();
        // SharedPreferences prefs =
        // PreferenceManager.getDefaultSharedPreferences(this);
        // int minutes = prefs.getInt("interval");
        int minutes = 1;
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, NotificationService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        am.cancel(pi);
        // by my own convention, minutes <= 0 means notifications are disabled
        if (minutes > 0) {
            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + minutes * 60 * 1000,
                    minutes * 60 * 1000, pi);
        }
    }

    private void updateLocalDatabase() {
        if (app.getNetStatus() != 0) {
            Log.d("MainActivity sais:", " Updating local database.");
            Toast.makeText(getApplicationContext(), "ACTUALIZANDO DATOS", Toast.LENGTH_LONG);



            // <-------------------->_ESPECIES_SERVER_DOWNLOAD_<-------------------->

            AsyncHttpClient client_especies = new AsyncHttpClient();
            client_especies.get(MainActivity.this, "http://belfort.randomobile.eu/api/routedata/route/retrieve.json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String s) {
                    super.onSuccess(s);

                    Gson gson = new Gson();
                    Especie[] especiesArray = gson.fromJson(s, Especie[].class);

                    ArrayList<Especie> especies = new ArrayList<Especie>(Arrays.asList(especiesArray));

                    for (Especie especie : especies) {
                        Log.d("updateLocalDatabase():", " Especie Nid: " + especie.getNid());
                        Log.d("updateLocalDatabase():", " Especie Title: " + especie.getTitle());
                        Log.d("updateLocalDatabase():", " Especie Body: " + especie.getBody());

                        app.getDBHandler().addOrReplaceEspecie(especie);
                    }
                }

                @Override
                public void onFailure(Throwable throwable, String s) {
                    super.onFailure(throwable, s);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }
            });

            // <-------------------->_PAGES_SERVER_DOWNLOAD_<-------------------->

            AsyncHttpClient client_pages = new AsyncHttpClient();
            client_pages.get(MainActivity.this, "http://belfort.randomobile.eu/api/routedata/pages/retrieve.json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String string) {
                    super.onSuccess(string);

                    Gson gson = new Gson();
                    Page[] list_Pages = gson.fromJson(string, Page[].class);

                    ArrayList<Page> pages = new ArrayList<Page>(Arrays.asList(list_Pages));

                    for (Page page : pages) {
                        Log.d("updateLocalDatabase():", " Page Nid: " + page.getNid());
                        Log.d("updateLocalDatabase():", " Page Title: " + page.getTitle());
                        Log.d("updateLocalDatabase():", " Page Body: " + page.getBody());

                        app.getDBHandler().addOrReplacePage(page);
                    }
                }

                @Override
                public void onFailure(Throwable throwable, String s) {
                    super.onFailure(throwable, s);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }
            });

            // <-------------------->_SERVER_DOWNLOAD_FINISHED_<-------------------->

            Toast.makeText(getApplicationContext(), "DATOS ACTUALIZADOS", Toast.LENGTH_LONG);
            Log.d("MainActivity sais:", " Local database updated.");

        } else {
            Toast.makeText(getApplicationContext(), "NO SE HAN PODIDO ACTUALIZAR LOS DATOS", Toast.LENGTH_LONG);
        }

        app.setEspecies(app.getDBHandler().getEspeciesList());
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");

            if (ipAddr.equals("")) {
                return false;

            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }
    }
}