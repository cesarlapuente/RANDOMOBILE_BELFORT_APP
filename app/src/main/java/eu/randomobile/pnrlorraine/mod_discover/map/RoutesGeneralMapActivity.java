package eu.randomobile.pnrlorraine.mod_discover.map;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.LocationDisplayManager.AutoPanMode;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.bing.BingMapsLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleLineSymbol.STYLE;

import java.util.ArrayList;
import java.util.HashMap;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_discover.detail.PoiDetailActivity;
import eu.randomobile.pnrlorraine.mod_discover.detail.RouteDetailActivity;
import eu.randomobile.pnrlorraine.mod_discover.list.RoutesListActivity;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.WKTUtil;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.CapaBase;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.ComboCapasMapa;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.ComboCapasMapa.ComboCapasMapaInterface;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_global.model.Route;
import eu.randomobile.pnrlorraine.mod_global.model.Route.RoutesInterface;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.mod_offline.OfflineRoute;
import eu.randomobile.pnrlorraine.mod_offline.OfflineRoute.RoutesModeOfflineInterface;
import eu.randomobile.pnrlorraine.mod_options.OptionsActivity;
import eu.randomobile.pnrlorraine.mod_search.RouteSearch;
import eu.randomobile.pnrlorraine.mod_search.RouteSearchActivity;

public class RoutesGeneralMapActivity extends Activity implements
		ComboCapasMapaInterface, RoutesInterface, RoutesModeOfflineInterface {

	public static final String PARAM_KEY_MOSTRAR = "mapa_mostrar";

	public static final int PARAM_MAPA_GENERAL_MOSTRAR_POIS = 200;
	public static final int PARAM_MAPA_GENERAL_MOSTRAR_RUTAS = 201;

	private int paramMapaGeneralMostrar;

	MainApp app;
	ImageMap mImageMap = null;
	
	MapView mapa;
	GraphicsLayer capaGeometrias;
	Callout callout;

	Button btnSeleccionarCapaBase;
	RelativeLayout panelCargando;
	// Array con los elementos que contendra
	ArrayList<Route> arrayRoutes = null;
	// Array con las rutas filtradas
	ArrayList<Route> arrayFilteredRoutes = null;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_discover__mapa_general_routes);

		this.app = (MainApp) getApplication();
		
		// Imagen de Cabecera con botones
        mImageMap = (ImageMap)findViewById(R.id.map_routesGeneral);
        mImageMap.setAttributes(true, false, (float)1.0, "mapa_rutas");
	    mImageMap.setImageResource(R.drawable.mapa_rutas);

	    // Recuperar parametros
		Bundle b = getIntent().getExtras();
		if (b != null) {
			setParamMapaGeneralMostrar(b.getInt(PARAM_KEY_MOSTRAR));
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
		mImageMap.mBubbleMap.clear();
		mImageMap.postInvalidate();
		// Activar GPS
		LocationDisplayManager ls = mapa.getLocationDisplayManager();
//		ls.setLocationListener(new MyLocationListener());
//		ls.setAutoPanMode(AutoPanMode.OFF);
		ls.start();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//Parar GPS
		LocationDisplayManager ls = mapa.getLocationDisplayManager();
		//ls.setLocationListener(new MyLocationListener());
		
		if(ls != null){
		    ls.stop();		 
		}
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		Log.d("Milog", "Cambio la configuracion");
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (callout != null && callout.isShowing()) {
				callout.hide();
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	public void capturarControles() {
		mapa = (MapView) findViewById(R.id.mapa);
		btnSeleccionarCapaBase = (Button) findViewById(R.id.btnAbrirCapas);
		panelCargando = (RelativeLayout) findViewById(R.id.panelCargando);
	}

	private void cargaActivityHome() {
		Intent intent = new Intent(RoutesGeneralMapActivity.this,
				MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private void cargaActivityRoutesList() {
		Intent intent = new Intent(RoutesGeneralMapActivity.this,
				RoutesListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private void cargaActivitySearch () {
		Intent intent = new Intent(RoutesGeneralMapActivity.this,
				RouteSearchActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, 1);
	}
	
	private void cargaActivityOptions() {
		// Abrir la pantalla de opciones
		Intent intent = new Intent(RoutesGeneralMapActivity.this,
				OptionsActivity.class);
		startActivity(intent);
	}
	
	private void filterRoutes () {
	    if (arrayFilteredRoutes == null)
	    	arrayFilteredRoutes = new ArrayList<Route>();
	    arrayFilteredRoutes.clear();
	    if (arrayRoutes != null)
			for(int i=0; i<arrayRoutes.size(); i++){
				if (RouteSearch.checkCriteria(arrayRoutes.get(i), this))
					arrayFilteredRoutes.add(arrayRoutes.get(i));
			}
	}
	
	@Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (data == null) {return;}
	    String name = data.getStringExtra("name");
	    filterRoutes();
	    capaGeometrias.removeAll();
	    cargarListaRoutesFiltrada(arrayFilteredRoutes);
	    
	  }
	

	public void inicializarMapa() {

		ponerCapaBase();

		mapa.setEsriLogoVisible(false);

		capaGeometrias = new GraphicsLayer();
		mapa.addLayer(capaGeometrias);
		
		// Tipografias
		Typeface tfBubleGum = Util.fontBubblegum_Regular(this);
		this.btnSeleccionarCapaBase.setTypeface(tfBubleGum);
	}

	public void escucharEventos() {
		
        mImageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
			@Override
			public void onImageMapClicked(int id, ImageMap imageMap) {
				// when the area is tapped, show the name in a
				// text bubble
				mImageMap.showBubble(id);
				switch (mImageMap.getAreaAttribute(id, "name")) {
					case "LIST":
						cargaActivityRoutesList();
						break;
					case "HOME":
						cargaActivityHome();
						break;
					case "RECHERCHER":
						cargaActivitySearch();
						break;
					case "PLUS":
						cargaActivityOptions();
						break;

					case "BACK":
						finish();
						break;
					default:
						mImageMap.showBubble(id);
				}

			}

			@Override
			public void onBubbleClicked(int id) {
				// react to info bubble for area being tapped

			}
		});


		// Al tocar un punto en el mapa
		this.mapa.setOnSingleTapListener(new OnSingleTapListener() {

			private static final long serialVersionUID = 1L;

			public void onSingleTap(float x, float y) {
				// Si el mapa no est� cargado, salir
				if (!mapa.isLoaded()) {
					return;
				}

				// Recuperamos los gr�ficos
				int[] graphicsIDS = capaGeometrias.getGraphicIDs(x, y, 8);
				if (graphicsIDS != null && graphicsIDS.length > 0) {
					Log.d("Milog", "Hay graficos en la zona pulsada");
					int targetId = graphicsIDS[0];

					Graphic gr = capaGeometrias.getGraphic(targetId);

					if (gr != null) {
						String nombre = (String) gr.getAttributes().get(
								"nombre");
						String clase = (String) gr.getAttributes().get("clase");
						String nid = (String) gr.getAttributes().get("nid");
						String cat = (String) gr.getAttributes().get("cat");
						String type = (String) gr.getAttributes().get("type");
						String color = (String) gr.getAttributes().get("color");


						callout = mapa.getCallout();
						// Establecer el estilo del callout
						callout.setStyle(R.xml.style_callout_mapa_global);
						callout.getStyle().setMaxWidth((int) Util.convertDpToPixel(300, app.getApplicationContext()));
						// Establecer el contenido del callout
						View contenidoCallout = getViewForCallout(nombre,
								clase, nid, cat, type, color);
						callout.setContent(contenidoCallout);
						callout.show(mapa.toMapPoint(new Point(x, y)));
					}

				} else {
					Log.d("Milog", "No hay graficos en la zona pulsada");
					if (callout != null && callout.isShowing()) {
						callout.hide();
					}
				}
			}
		});

		this.mapa.setOnStatusChangedListener(new OnStatusChangedListener() {
			private static final long serialVersionUID = 1L;

			public void onStatusChanged(Object source, STATUS status) {

				if (OnStatusChangedListener.STATUS.INITIALIZED == status
						&& source == mapa) {

					LocationDisplayManager ls = mapa.getLocationDisplayManager();
					ls.setLocationListener(new MyLocationListener());
					ls.setAutoPanMode(AutoPanMode.OFF);
					ls.start();

					representarGeometrias();

				}

			}

		});

		btnSeleccionarCapaBase.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				ComboCapasMapa comboCapas = new ComboCapasMapa(
						getApplication(), RoutesGeneralMapActivity.this);
				comboCapas.comboCapasMapaInterface = RoutesGeneralMapActivity.this;
				comboCapas.show();
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

			// Recoger los filtros
			double lat = (double) app.preferencias.getFloat(
					app.FILTER_KEY_LAST_LOCATION_LATITUDE, 0);
			double lon = (double) app.preferencias.getFloat(
					app.FILTER_KEY_LAST_LOCATION_LONGITUDE, 0);
			

			if(paramMapaGeneralMostrar == PARAM_MAPA_GENERAL_MOSTRAR_RUTAS){
				String tidRouteCat = app.preferencias.getString(
						app.FILTER_KEY_ROUTE_CATEGORY_TID, null);
				String tidRouteDif = app.preferencias.getString(
						app.FILTER_KEY_ROUTE_DIFFICULTY_TID, null);
				String txtBuscarRoute = app.preferencias.getString(
						app.FILTER_KEY_ROUTE_TEXT, null);
				Route.routesInterface = this;
				if ((-0.001 < lat) && (lat < 0.001)) {
					if (mapa.getLocationDisplayManager().getLocation() != null) {
						lat = mapa.getLocationDisplayManager().getLocation().getLatitude();
						lon = mapa.getLocationDisplayManager().getLocation().getLongitude();
					}
				}
				Route.cargarListaRutasOrdenadosDistancia(app, lat, lon, 0, 0, 0, tidRouteCat, tidRouteDif, txtBuscarRoute);
			}
								
		} else {
			OfflineRoute.routesInterface = this;
			OfflineRoute.cargaListaRutasOffline(getApplication());
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

	private void cargarListaRoutesFiltrada (final ArrayList<Route> routes) {
		if (routes != null) {
			cargarListaRoutes(routes);
		}
	}
	
	
	private void cargarListaRoutes (final ArrayList<Route> routes) {
		for (int i = 0; i < routes.size(); i++) {
			Route route = routes.get(i);
			Polyline polylineProyectada = null;
			if(route.getTrack() != null){
				
				HashMap<String, Object> attrs = new HashMap<String, Object>();
				attrs.put("clase",  route.getClass().getName());
				attrs.put("nid", route.getNid());
				attrs.put("nombre", route.getTitle());
				attrs.put("cat", "Ruta");
				attrs.put("type", route.getCategory().getName());
				attrs.put("color", Integer.toString(route.getColorForMap(this)));
				
				polylineProyectada = WKTUtil.getPolylineFromWKTLineStringField(app, route.getTrack());

		        //int color = Color.BLUE;
		        
		        // Definir el color de la ruta
		        int color = route.getColorForMap(this);
				Graphic gr = new Graphic(polylineProyectada, new SimpleLineSymbol(color, 6, STYLE.SOLID), attrs);
				capaGeometrias.addGraphic(gr);
				
			}
			
		}
		if (capaGeometrias.getNumberOfGraphics() > 0){
			Log.d("RoutesMap", "Ahora mismo vamos a centrar");
			centrarEnExtentCapa(capaGeometrias);
			Log.d("RoutesMap", "Ya hemos centrado, ¿qué tal?");
		}

	}
	
	public void seCargoListaRoutes(final ArrayList<Route> routes) {

		if (routes != null) {
			arrayRoutes = routes;
			cargarListaRoutes(routes);
		}		
		
		panelCargando.setVisibility(View.GONE);
		
	}

	@Override
	public void producidoErrorAlCargarListaRoutes(String error) {
		// Quitar el panel de cargando
		panelCargando.setVisibility(View.GONE);
	}
	
	@Override
	public void seCargoListaRoutesOffline(ArrayList<Route> routes) {
		// TODO Auto-generated method stub
		if (routes != null) {
			this.arrayRoutes = routes;
			cargarListaRoutes(routes);
		}
		panelCargando.setVisibility(View.GONE);
		
	}

	@Override
	public void producidoErrorAlCargarListaRoutesOffline(String error) {
		// TODO Auto-generated method stub
		Log.d("Milog", "producidoErrorAlCargarListaRoutes: " + error);
		panelCargando.setVisibility(View.GONE);
	}

	@Override
	public void seCargoRouteOffline(Route item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void producidoErrorAlCargarRouteOffline(String error) {
		// TODO Auto-generated method stub
		
	}
	
	
	

	private View getViewForCallout(final String nombre, String clase, final String nid, final String cat, final String type, final String color) {
		View view = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.mod_discover__layout_callout_mapa, null);
		Log.d("Milog", "1");
		final TextView lblNombre = (TextView) view
				.findViewById(R.id.lblNombrePunto);
		Log.d("Milog", "2");
		final TextView lblCategoria = (TextView) view
				.findViewById(R.id.lblCategoriaPunto);
		Log.d("Milog", "3");
		final Button btnCerrarDialogo = (Button) view
				.findViewById(R.id.btnVerFichaPunto);
		Log.d("Milog", "4");

		// Ponerle las propiedades necesarias
		if (clase.equals(Poi.class.getName())) {
			// Poner las propiedades en el layout
			lblNombre.setText(nombre);
			lblCategoria.setText(cat);

			// Escuchar el evento del click del bot�n
			btnCerrarDialogo.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(RoutesGeneralMapActivity.this,
							PoiDetailActivity.class);
					intent.putExtra(PoiDetailActivity.PARAM_KEY_NID, nid);
					startActivity(intent);
					callout.hide();
				}
			});
		}else if (clase.equals(Route.class.getName())) {
			// Poner las propiedades en el layout
			lblNombre.setText(nombre);
			lblCategoria.setText(cat);

			// Escuchar el evento del click del bot�n
			btnCerrarDialogo.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(RoutesGeneralMapActivity.this, RouteDetailActivity.class);
			
					intent.putExtra(RouteDetailActivity.PARAM_KEY_NID, nid);
//					intent.putExtra(RouteDetailActivity.PARAM_KEY_DISTANCE,
//							routePulsado.getDistanceMeters());
					intent.putExtra(RouteDetailActivity.PARAM_KEY_CATEGORY_ROUTE, cat);
					intent.putExtra(RouteDetailActivity.PARAM_KEY_TITLE_ROUTE, nombre);
					intent.putExtra(RouteDetailActivity.PARAM_KEY_CATEGORY_ROUTE, type);
					intent.putExtra(RouteDetailActivity.PARAM_KEY_COLOR_ROUTE, color);
					startActivity(intent);
					callout.hide();
				}
			});
		}

		return view;
	}

	private void dibujarGeometrias(ArrayList<Object> geometrias, String paramNombre, String paramNombreClase, String paramNid, String paramCat, final String urlIcon) {

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
				attrs.put("cat", paramCat);

				if (geomObj != null
						&& geomObj.getClass().getName()
								.equals(Polygon.class.getName())) {
					Polygon polygon = (Polygon) geomObj;
					SimpleFillSymbol sym = new SimpleFillSymbol(
							polygonFillColor);
					sym.setAlpha(100);
					sym.setOutline(new SimpleLineSymbol(polygonBorderColor, 8,
							SimpleLineSymbol.STYLE.SOLID));
					Graphic gr = new Graphic(polygon, sym, attrs);
					capaGeometrias.addGraphic(gr);
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
									sym = new PictureMarkerSymbol(getResources().getDrawable(R.drawable.ic_launcher));
								}
							}else{
								sym = new PictureMarkerSymbol(getResources().getDrawable(R.drawable.ic_launcher));
							}
							return 0;
						}
						// Acciones despu�s de ejecutarse (Main Thread)
						protected void onPostExecute(Integer bytes) {
							Graphic gr = new Graphic(point, sym, attrs);
							capaGeometrias.addGraphic(gr);
							
							// Centrar en el extent de la capa
							centrarEnExtentCapa(capaGeometrias);
				        }
					}.execute(1);

				}else if (geomObj != null
						&& geomObj.getClass().getName()
						.equals(Polyline.class.getName())) {
					Polyline polyline = (Polyline) geomObj;

			        int color = Color.BLUE;

					Graphic gr = new Graphic(polyline, new SimpleLineSymbol(color, 10, STYLE.SOLID), attrs);
					capaGeometrias.addGraphic(gr);
				}
			}
		}
	}

	private void centrarEnExtentCapa(GraphicsLayer capa) {
		// Hacer zoom a la capa de geometrias
		/*
		Envelope env = new Envelope();
		Envelope NewEnv = new Envelope();
		int numberOfGrapchics=capa.getGraphicIDs().length;

		for (int i : capa.getGraphicIDs()) {
			Log.d("RoutesMap", "Grafico num: " + Integer.toString(i)+"/"+Integer.toString(numberOfGrapchics));
			Geometry geom = capa.getGraphic(i).getGeometry();
			geom.queryEnvelope(env);
			NewEnv.merge(env);
		}

		this.mapa.setExtent(NewEnv, 100);
		//this.mapa.setExtent(NewEnv);
		*/
		// Envelope to focus on the map extent on the results
		Envelope extent = new Envelope();

		// iterate through results
		for (int element : capa.getGraphicIDs()) {
				Geometry geom = capa.getGraphic(element).getGeometry();
				// merge extent with point
			    Envelope env = new Envelope();
				geom.queryEnvelope(env);
				extent.merge(env);
		}

		// Set the map extent to the envelope containing the result graphics
		this.mapa.setExtent(extent, 100);
		extent.getCenter();
		mapa.centerAt(extent.getCenter(),false);
	}


	public void ponerCapaBase() {

		// <--------->_AÑADIR_COMPROBACIÓN_DE_RED_<-------->

		/* Codigo de prueba
		if(! DataConection.hayConexion(this)){
		    String basemapurl = Util.getUrlGeneralBaseLayerOffline(app);
		    ArcGISLocalTiledLayer baseLayer;
		    baseLayer = new ArcGISLocalTiledLayer(basemapurl);
			mapa.addLayer(baseLayer);
			mapa.setMaxScale(1000);
			return;
		}
		*/
		/* Fin codigo de prueba */
		CapaBase capaSeleccionada = app.capaBaseSeleccionada;
		Log.d("Milog", "Identificador: " + capaSeleccionada.getIdentificador());
		Log.d("Milog", "Etiqueta: " + capaSeleccionada.getEtiqueta());

		Object capaBase = capaSeleccionada.getMapLayer();
		Log.d("Milog", "Object capaBase");

		// Correcci�n, para que no cambie la capa base cuando la seleccionada es
		// la misma que ya estaba (ahorra datos)
		Layer[] capas = mapa.getLayers();
		if (capas != null) {
			Log.d("Milog", "capas no es nulo");
			if (capas.length > 0) {

				Log.d("Milog", "Hay alguna capa");
				Object capa0 = capas[0];
				Log.d("Milog", "Tenemos capa0");
				// si la capa base seleccionada es del mismo tipo que la capa 0
				if (capaBase.getClass().getName()
						.equals(capa0.getClass().getName())) {
					Log.d("Milog",
							"La clase de la capa base es igual que la clase de la capa0");
					if (capaBase.getClass() == BingMapsLayer.class) {
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
					} else if (capaBase.getClass() == ArcGISTiledMapServiceLayer.class) {
						Log.d("Milog", "capaBase es de tipo TiledMap");
						ArcGISTiledMapServiceLayer capaBaseCasted = (ArcGISTiledMapServiceLayer) capaBase;
						ArcGISTiledMapServiceLayer capa0Casted = (ArcGISTiledMapServiceLayer) capa0;
						String strUrlCapaBaseCasted = capaBaseCasted.getUrl()
								.toString();
						String strUrlCapa0Casted = capa0Casted.getUrl()
								.toString();
						if (strUrlCapaBaseCasted.equals(strUrlCapa0Casted)) {
							return;
						} else {
							mapa.removeLayer(0);
							Log.d("Milog",
									"PUNTO INTERMEDIO TILED: el mapa tiene "
											+ mapa.getLayers().length
											+ " capas");
						}
					}
					Log.d("Milog", "La capa 0 es de clase "
							+ capa0.getClass().getName());
				} else {// si la capa base seleccionada no es del mismo tipo que
						// la capa 0

					if (capaBase.getClass() == BingMapsLayer.class) {
						mapa.removeLayer(0);
					} else if (capaBase.getClass() == ArcGISTiledMapServiceLayer.class) {
						mapa.removeLayer(0);
					}
				}
			}
			// btnAbrirCapas.setEnabled(true);
			if (capaBase.getClass() == ArcGISTiledMapServiceLayer.class) {

				if (capas.length > 0) {
					mapa.addLayer((ArcGISTiledMapServiceLayer) capaBase, 0);
				} else {
					mapa.addLayer((ArcGISTiledMapServiceLayer) capaBase);
				}

			} else if (capaBase.getClass() == BingMapsLayer.class) {

				if (capas.length > 0) {
					mapa.addLayer((BingMapsLayer) capaBase, 0);
				} else {
					mapa.addLayer((BingMapsLayer) capaBase);
				}

			} else {
				// otro tipo de capa
			}

			app.capaBaseSeleccionada = capaSeleccionada;
			Log.d("Milog", "El mapa tiene " + mapa.getLayers().length
					+ " capas");
		}
	}

	public int getParamMapaGeneralMostrar() {
		return paramMapaGeneralMostrar;
	}

	public void setParamMapaGeneralMostrar(int paramMapaGeneralMostrar) {
		this.paramMapaGeneralMostrar = paramMapaGeneralMostrar;
	}

	
	
	
	
	/**
	 * Location listener propio
	 * 
	 * @author
	 * 
	 */
	private class MyLocationListener implements LocationListener {

		public MyLocationListener() {
			super();
		}

		public void onLocationChanged(Location loc) {
			if (loc == null)
				return;
		}

		public void onProviderDisabled(String provider) {

		}

		public void onProviderEnabled(String provider) {

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

	}
	
	

	@Override
	public void seCargoRoute(Route route) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void producidoErrorAlCargarRoute(String error) {
		// TODO Auto-generated method stub
		
	}

}