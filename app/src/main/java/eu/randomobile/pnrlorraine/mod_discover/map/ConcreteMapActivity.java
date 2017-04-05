package eu.randomobile.pnrlorraine.mod_discover.map;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_discover.detail.PoiDetailActivity;
import eu.randomobile.pnrlorraine.mod_discover.detail.RouteDetailActivity;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.WKTUtil;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.CapaBase;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.ComboCapasMapa;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.ComboCapasMapa.ComboCapasMapaInterface;
import eu.randomobile.pnrlorraine.mod_global.model.GeoPoint;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_global.model.Poi.PoisInterface;
import eu.randomobile.pnrlorraine.mod_global.model.Route;
import eu.randomobile.pnrlorraine.mod_global.model.Route.RoutesInterface;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;

public class ConcreteMapActivity extends Activity implements
		ComboCapasMapaInterface, PoisInterface, RoutesInterface {

	public static final String PARAM_KEY_NID_MOSTRAR = "mapa_nid_mostrar";
	public static final String PARAM_KEY_TYPE_DRUPAL = "mapa_type_mostrar";


	String paramNid;
	String paramType;

	MainApp app;

	MapView mapa;
	GraphicsOverlay
			capaGeometrias;
	Callout callout;

	Button btnSeleccionarCapaBase;
	Button btnVolver;
	TextView txtTitulo;
	Button btnHome;

	RelativeLayout panelCargando;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_discover__mapa_concreto);

		this.app = (MainApp) getApplication();
		// Recuperar parametros
		Bundle b = getIntent().getExtras();
		if (b != null) {
			paramNid = b.getString(PARAM_KEY_NID_MOSTRAR);
			paramType = b.getString(PARAM_KEY_TYPE_DRUPAL);
		}

		// Capturar controles
		this.capturarControles();

		// Configurar formulario
		this.inicializarMapa();

		// Escuchar eventos
		this.escucharEventos();
	}

	public void onResume() {
		super.onResume();

	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		Log.d("Milog", "Cambio la configuracion");
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (callout != null && callout.isShowing()) {
				callout.dismiss();
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	public void capturarControles() {
		btnVolver = (Button) findViewById(R.id.btnVolver);
		btnHome = (Button) findViewById(R.id.btnHome);
		txtTitulo = (TextView) findViewById(R.id.txtNombre);
		mapa = (MapView) findViewById(R.id.mapa);
		btnSeleccionarCapaBase = (Button) findViewById(R.id.btnAbrirCapas);
		panelCargando = (RelativeLayout) findViewById(R.id.panelCargando);
	}

	public void inicializarMapa() {

		ponerCapaBase();

		capaGeometrias = new GraphicsOverlay();
		mapa.getGraphicsOverlays().add(capaGeometrias);

		
		// Tipografias
		Typeface tfBubleGum = Util.fontBubblegum_Regular(this);
		this.txtTitulo.setTypeface(tfBubleGum);
		this.btnSeleccionarCapaBase.setTypeface(tfBubleGum);
		
		// Título
		this.txtTitulo.setText(getResources().getString(R.string.mod_discover__mapa));
	}

	public void escucharEventos() {

		// Al tocar un punto en el mapa
		this.mapa.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mapa){
			@Override
			public boolean onSingleTapConfirmed(final MotionEvent e) {
				// Si el mapa no est? cargado, salir
				if (mapa.getMap().getLoadStatus() == LoadStatus.NOT_LOADED) {
					return false;
				}
				final android.graphics.Point point = new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()));
				final double tolerance = 8;
				final ListenableFuture<List<IdentifyGraphicsOverlayResult>> graphicsIDS = mapa.identifyGraphicsOverlaysAsync(point, tolerance, false);
				graphicsIDS.addDoneListener(new Runnable() {
					@Override
					public void run() {
						// Recuperamos los gráficos
						try {
							if (graphicsIDS != null && graphicsIDS.get().size() > 0) {
                                Log.d("Milog", "Hay graficos en la zona pulsada");
                                List lr = null;
                                lr = graphicsIDS.get();

                                Graphic gr = capaGeometrias.getGraphics().get(capaGeometrias.getGraphics().indexOf(lr.get(0)));

                                if (gr != null) {
                                    String nombre = (String) gr.getAttributes().get(
                                            "nombre");
                                    String clase = (String) gr.getAttributes().get("clase");
                                    String nid = (String) gr.getAttributes().get("nid");

                                    callout = mapa.getCallout();
                                    // Establecer el estilo del callout
                                    callout.setStyle(new Callout.Style(getApplicationContext(), R.xml.style_callout_mapa_global));
                                    // Establecer el contenido del callout
                                    View contenidoCallout = getViewForCallout(nombre,
                                            clase, nid);
                                    callout.setContent(contenidoCallout);
                                    callout.setLocation(mapa.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()))));
                                    callout.show();
                                }
                            } else {
                                Log.d("Milog", "No hay graficos en la zona pulsada");
                                if (callout != null && callout.isShowing()) {
                                    callout.dismiss();
                                }
                            }
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							e1.printStackTrace();
						}
					}

				});
				return true;
			}
		});


		this.mapa.getMap().addDoneLoadingListener(new Runnable() {
			@Override
			public void run() {
				LocationDisplay ls = mapa.getLocationDisplay();
				ls.addLocationChangedListener(new MyLocationListener());
//					ls.setAutoPan(false);
				ls.setAutoPanMode(LocationDisplay.AutoPanMode.OFF);
				ls.startAsync();
				representarGeometrias();
			}
		});



		btnSeleccionarCapaBase.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				ComboCapasMapa comboCapas = new ComboCapasMapa(
						getApplication(), ConcreteMapActivity.this);
				comboCapas.comboCapasMapaInterface = ConcreteMapActivity.this;
				comboCapas.show();
			}
		});

		btnVolver.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});

		btnHome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ConcreteMapActivity.this,
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

	}

	public void seCerroComboCapas() {
		Log.d("Milog", "Antes de poner capa base");
		ponerCapaBase();
		Log.d("Milog", "Despues de poner capa base");
	}

	private void representarGeometrias() {

		if (DataConection.hayConexion(this)) {
			Log.d("Milog", "Voy a cargar los datos");

			if(paramType != null){
				if(paramType.equals(app.DRUPAL_TYPE_POI)){
					Poi.poisInterface = this;
					Poi.cargarPoi(app, paramNid);
				}else if(paramType.equals(app.DRUPAL_TYPE_ROUTE)){
					Route.routesInterface = this;
					Route.cargarRoute(app, paramNid);
				}
			}
		} else {
			panelCargando.setVisibility(View.GONE);
			Util.mostrarMensaje(
					this,
					getResources().getString(
							R.string.mod_global__sin_conexion_a_internet),
					getResources()
							.getString(
									R.string.mod_global__no_dispones_de_conexion_a_internet));
		}
	}

	public void seCargoPoi(Poi poi) {
		if(poi != null){
			GeoPoint gp = poi.getCoordinates();
			Point puntoProyectado = (Point) GeometryEngine.project(new Point(gp.getLongitude(), gp.getLatitude()),
					SpatialReference.create(102100));
			ArrayList<Object> geometrias = new ArrayList<Object>();
			geometrias.add(puntoProyectado);
			
			String icon = null;
			if(poi.getCategory() != null && poi.getCategory().getIcon() != null){
				icon = poi.getCategory().getIcon();
			}
			
			dibujarGeometrias(geometrias, poi.getTitle(), poi.getClass()
					.getName(), poi.getNid(), icon);
		}

		// Quitar el panel de cargando
		panelCargando.setVisibility(View.GONE);
	}

	public void producidoErrorAlCargarPoi(String error) {
		// Quitar el panel de cargando
		panelCargando.setVisibility(View.GONE);
	}

	public void seCargoRoute(Route route) {
		if(route != null){
			Polyline polylineProyectado = null;
			if(route.getTrack() != null){
				polylineProyectado = WKTUtil.getPolylineFromWKTLineStringField(app, route.getTrack());
			}
			ArrayList<Object> geometrias = new ArrayList<Object>();
			geometrias.add(polylineProyectado);
			dibujarGeometrias(geometrias, route.getTitle(), route.getClass()
					.getName(), route.getNid(), null);
		}
		
		// Centrar en el extent de la capa
		centrarEnExtentCapa(capaGeometrias);
		
		// Quitar el panel de cargando
		panelCargando.setVisibility(View.GONE);
	}

	public void producidoErrorAlCargarRoute(String error) {
		// Quitar el panel de cargando
		panelCargando.setVisibility(View.GONE);
	}

	private View getViewForCallout(String nombre, String clase, final String nid) {
		View view = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.mod_discover__layout_callout_mapa, null);
		final TextView lblNombre = (TextView) view
				.findViewById(R.id.lblNombrePunto);
		final TextView lblCategoria = (TextView) view
				.findViewById(R.id.lblCategoriaPunto);
		final Button btnCerrarDialogo = (Button) view
				.findViewById(R.id.btnVerFichaPunto);

		// Ponerle las propiedades necesarias
		if (clase.equals(Poi.class.getName())) {
			Log.d("Milog", "El objeto pulsado lleva un poi");
			// Poner las propiedades en el layout
			lblNombre.setText(nombre);
			lblCategoria.setText("Punto de interés");

			// Escuchar el evento del click del botón
			btnCerrarDialogo.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(ConcreteMapActivity.this,
							PoiDetailActivity.class);
					intent.putExtra(PoiDetailActivity.PARAM_KEY_NID, nid);
					startActivity(intent);
					callout.dismiss();
				}
			});
		}else if (clase.equals(Route.class.getName())) {
			// Poner las propiedades en el layout
			lblNombre.setText(nombre);
			lblCategoria.setText("Route");

			// Escuchar el evento del click del botón
			btnCerrarDialogo.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(ConcreteMapActivity.this,
							RouteDetailActivity.class);
					intent.putExtra(PoiDetailActivity.PARAM_KEY_NID, nid);
					startActivity(intent);
					callout.dismiss();
				}
			});
		}

		return view;
	}

	private void dibujarGeometrias(ArrayList<Object> geometrias,
			String paramNombre, String paramNombreClase, String paramNid, final String urlIcon) {
		int polygonFillColor = Color.rgb(55, 132, 218);
		int polygonBorderColor = Color.rgb(27, 87, 187);
		//int pointColor = Color.rgb(206, 240, 5);
		if (geometrias != null) {
			for (int j = 0; j < geometrias.size(); j++) {
				Object geomObj = geometrias.get(j);

				final HashMap<String, Object> attrs = new HashMap<String, Object>();
				attrs.put("clase", paramNombreClase);
				attrs.put("nid", paramNid);
				attrs.put("nombre", paramNombre);

				if (geomObj != null
						&& geomObj.getClass().getName()
								.equals(Polygon.class.getName())) {
					Polygon polygon = (Polygon) geomObj;
					SimpleFillSymbol sym = new SimpleFillSymbol();
					sym.setColor(polygonFillColor);
					sym.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, polygonBorderColor, 8));
					Graphic gr = new Graphic(polygon, attrs, sym);
					capaGeometrias.getGraphics().add(gr);
				} else if (geomObj != null
						&& geomObj.getClass().getName()
								.equals(Point.class.getName())) {
					
					final Point point = (Point) geomObj;

					// Cargar los iconos de remoto con AsyncTask
					new AsyncTask<Integer, Float, Integer>() {
						
						PictureMarkerSymbol sym = null;
						
						// Ejecucion pesada
						protected Integer doInBackground(Integer... params) {
							Log.d("Milog", "URL Icono: " + urlIcon);
							if(urlIcon != null){
								try {
									sym = new PictureMarkerSymbol(urlIcon);
								} catch (Exception e) {
									Log.d("Milog", "Excepcion al cargar icono: " + e.toString());
									sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher));
								}
							}else{
								sym = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher));
							}
							return 0;
						}
						// Acciones después de ejecutarse (Main Thread)
						protected void onPostExecute(Integer bytes) {
							Graphic gr = new Graphic(point, attrs, sym);
							capaGeometrias.getGraphics().add(gr);
							
							// Centrar en el poi y hacer zoom
							double scale = 5000.0;
							mapa.setViewpointCenterAsync(point, scale);
				        }
					}.execute(1);
					
				}else if (geomObj != null
						&& geomObj.getClass().getName()
						.equals(Polyline.class.getName())) {
					Polyline polyline = (Polyline) geomObj;
					Random r = new Random();
			        int color = Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255));
			        
			        // Poner el color en función de la ruta (igual que en en los PDF de Alto Águeda)
			        if(paramNid != null){
			        	if(paramNid.equals("885")){
			        		color = Color.rgb(43, 132, 62);
			        	}else if(paramNid.equals("887")){
			        		color = Color.rgb(146, 89, 128);
			        	}else if(paramNid.equals("888")){
			        		color = Color.rgb(0, 0, 0);
			        	}else if(paramNid.equals("886")){
			        		color = Color.rgb(202, 16, 35);
			        	}else if(paramNid.equals("889")){
			        		color = Color.BLUE;
			        	}
			        }
			        
					Graphic gr = new Graphic(polyline, attrs, new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, color, 10));
					capaGeometrias.getGraphics().add(gr);
				}
			}
		}
	}

	private void centrarEnExtentCapa(GraphicsOverlay capa) {
		// Hacer zoom a la capa de geometrias
		Envelope env ;
		Envelope NewEnv = capa.getExtent();
		for (int i=0 ; i<capa.getGraphics().size(); i++) {
			Geometry geom = capa.getGraphics().get(i).getGeometry();
			env = geom.getExtent();
			//geom.queryEnvelope(env);
			NewEnv.createFromInternal(env.getInternal());
			//NewEnv.merge(env);
		}

		this.mapa.setViewpointGeometryAsync(NewEnv, 100);
	}

	public void ponerCapaBase() {
		CapaBase capaSeleccionada = app.capaBaseSeleccionada;
		Log.d("Milog", "Identificador: " + capaSeleccionada.getIdentificador());
		Log.d("Milog", "Etiqueta: " + capaSeleccionada.getEtiqueta());

		Object capaBase = capaSeleccionada.getMapLayer();
		Log.d("Milog", "Object capaBase");

		// CorrecciÛn, para que no cambie la capa base cuando la seleccionada es
		// la misma que ya estaba (ahorra datos)
		LayerList capas = mapa.getMap().getOperationalLayers();
		if (capas != null) {
			Log.d("Milog", "capas no es nulo");
			if (capas.size() > 0) {

				Log.d("Milog", "Hay alguna capa");
				Object capa0 = capas.get(0);
				Log.d("Milog", "Tenemos capa0");
				// si la capa base seleccionada es del mismo tipo que la capa 0
				if (capaBase.getClass().getName()
						.equals(capa0.getClass().getName())) {
					Log.d("Milog",
							"La clase de la capa base es igual que la clase de la capa0");
					/*if (capaBase.getClass() == BingMapsLayer.class) {
						Log.d("Milog", "capaBase es de tipo BING");
						BingMapsLayer capaBaseCasted = (BingMapsLayer) capaBase;
						BingMapsLayer capa0Casted = (BingMapsLayer) capa0;

						if (capaBaseCasted.getMapStyle().equals(
								capa0Casted.getMapStyle())) {
							return;
						} else {
							mapa.removeLayer(0);
							Log.d("Milog",
									"PUNTO INTERMEDIO BING: el mapa tiene "
											+ mapa.getLayers().length
											+ " capas");
						}
					} else */if (capaBase.getClass() == ArcGISTiledLayer.class) {
						Log.d("Milog", "capaBase es de tipo TiledMap");
						ArcGISTiledLayer capaBaseCasted = (ArcGISTiledLayer) capaBase;
						ArcGISTiledLayer capa0Casted = (ArcGISTiledLayer) capa0;
						String strUrlCapaBaseCasted = capaBaseCasted.getUri()
								.toString();
						String strUrlCapa0Casted = capa0Casted.getUri()
								.toString();
						if (strUrlCapaBaseCasted.equals(strUrlCapa0Casted)) {
							return;
						} else {
							mapa.getMap().getOperationalLayers().remove(0);
							Log.d("Milog",
									"PUNTO INTERMEDIO TILED: el mapa tiene "
											//+ mapa.getLayers().length
											+ " capas");
						}
					}
					Log.d("Milog", "La capa 0 es de clase "
							+ capa0.getClass().getName());
				} else {// si la capa base seleccionada no es del mismo tipo que
						// la capa 0

					/*if (capaBase.getClass() == BingMapsLayer.class) {
						mapa.removeLayer(0);
					} else if (capaBase.getClass() == ArcGISTiledMapServiceLayer.class) {
						mapa.removeLayer(0);
					}*/
					mapa.getMap().getOperationalLayers().remove(0);
				}
			}
			// btnAbrirCapas.setEnabled(true);
			if (capaBase.getClass() == ArcGISTiledLayer.class) {

				if (capas.size() > 0) {
					mapa.getMap().getOperationalLayers().add(0, (ArcGISTiledLayer) capaBase);
				} else {
					mapa.getMap().getOperationalLayers().add((ArcGISTiledLayer) capaBase);
				}

			} /*else if (capaBase.getClass() == BingMapsLayer.class) {

				if (capas.length > 0) {
					mapa.addLayer((BingMapsLayer) capaBase, 0);
				} else {
					mapa.addLayer((BingMapsLayer) capaBase);
				}

			} */else {
				// otro tipo de capa
			}

			app.capaBaseSeleccionada = capaSeleccionada;

		}
	}

	/**
	 * Location listener propio
	 * 
	 * @author
	 * 
	 */
	private class MyLocationListener implements LocationDisplay.LocationChangedListener {

		public MyLocationListener() {
			super();
		}

		public void onProviderDisabled(String provider) {

		}

		public void onProviderEnabled(String provider) {

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
			if (locationChangedEvent.getLocation() == null)
				return;
		}
	}

	@Override
	public void producidoErrorAlCargarListaPois(String error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void seCargoListaRoutes(ArrayList<Route> routes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void producidoErrorAlCargarListaRoutes(String error) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void seCargoListaPois(ArrayList<Poi> pois) {
		

	}

}