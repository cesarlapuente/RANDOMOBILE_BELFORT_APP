package eu.randomobile.pnrlorraine;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;

import java.util.ArrayList;

import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.data_access.DBHandler;
import eu.randomobile.pnrlorraine.mod_global.environment.Drupal7RESTClient;
import eu.randomobile.pnrlorraine.mod_global.environment.Drupal7Security;
import eu.randomobile.pnrlorraine.mod_global.environment.ExternalStorage;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.CapaBase;
import eu.randomobile.pnrlorraine.mod_global.model.Especie;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_global.model.Route;
import eu.randomobile.pnrlorraine.mod_offline.Offline;
import eu.randomobile.pnrlorraine.mod_offline.OfflinePoi;
import eu.randomobile.pnrlorraine.mod_offline.OfflineRoute;

public class MainApp extends Application {
	private DBHandler dBHandler;

	// Nombre del fichero de la BBDD
	public String NOMBRE_FICH_BBDD = "bd_app_pnrlorraine.sqlite";

	// Identificador del recurso del fichero de BBDD
	public int RES_ID_FICH_BBBDD = R.raw.bd_app_pnrlorraine;

	// Nombre de la carpeta en la SD donde est� la BBDD de la app
	public String CARPETA_SD = "PNRLorraine";
	public long LENGTH_LORRAINE_TPK = 0;
	// Claves para acceder a las cookies
	public String COOKIE_KEY_ID_USUARIO_LOGUEADO = "idUsuarioLogueado";
	public String COOKIE_KEY_ID_SESION_USUARIO_LOGUEADO = "idSesionUsuarioLogueado";
	public String COOKIE_KEY_TIMESTAMP_SESSID = "sessionid_timestamp";
	public String COOKIE_KEY_NICK_USUARIO_LOGUEADO = "nickUsuarioLogueado";
	public String COOKIE_KEY_EMAIL_USUARIO_LOGUEADO = "emailUsuarioLogueado";
	public String COOKIE_KEY_NOMBRE_USUARIO_LOGUEADO = "nombreUsuarioLogueado";
	public String COOKIE_KEY_APELLIDOS_USUARIO_LOGUEADO = "apellidosUsuarioLogueado";
	public String COOKIE_KEY_SESSION_NAME = "sessionName";
	public String COOKIE_KEY_RANKING_USUARIO_LOGUEADO = "rankingUsuarioLogueado";
	public String COOKIE_KEY_COMUNIDAD_AUTONOMA_USUARIO_LOGUEADO = "isoCCAAUsuarioLogueado";
	public String COOKIE_KEY_PAIS_USUARIO_LOGUEADO = "isoPaisUsuarioLogueado";

	public String KEY_ESPECIES_NID="especie_nid";
	public String KEY_ESPECIES_ESPACIOS_STATE = "0";

	private ArrayList<Route> routesList;
	private ArrayList<Poi> poisList;
	private ArrayList<Especie> especies;
	private ArrayList<Especie> especiesInRoute;
	private int poisOfEspecie[];
	private int especiesListaEspacios[];
	private int espaciosRuta[];
	private int filtroCategoriasPOIs[];
	private String id_especie_ficha;

	public String getId_especie_ficha() {
		return id_especie_ficha;
	}

	public void setId_especie_ficha(String id_especie_ficha) {
		this.id_especie_ficha = id_especie_ficha;
	}

	public ArrayList<Route> getRoutesList() {
		return routesList;
	}

	public void setRoutesList(ArrayList<Route> routesList) {
		this.routesList = routesList;
	}

	public ArrayList<Poi> getPoisList() {
		return poisList;
	}

	public int[] getPoisOfEspecie() {
		return poisOfEspecie;
	}

	public void setPoisOfEspecie(int[] poisOfEspecie) {
		this.poisOfEspecie = poisOfEspecie;
	}

	public void setPoisList(ArrayList<Poi> poisList) {
		this.poisList = poisList;
	}

	public DBHandler getDBHandler (){
		return dBHandler;
	}

	public int[] getFiltroCategoriasPOIs() {
		return filtroCategoriasPOIs;
	}

	public void setFiltroCategoriasPOIs(int[] filtroCategoriasPOIs) {
		this.filtroCategoriasPOIs = filtroCategoriasPOIs;
	}

	public int[] getEspaciosRuta() {
		return espaciosRuta;
	}

	public void setEspaciosRuta(int[] espaciosRutaN) {
		this.espaciosRuta = espaciosRutaN;
	}

	public int[] getEspeciesListaEspacios() {
		return especiesListaEspacios;
	}

	public void setEspeciesListaEspacios(int[] especiesListaEspacios) {
		this.especiesListaEspacios = especiesListaEspacios;
	}

	public void setEspecies(ArrayList<Especie> especies) {
		this.especies = especies;
	}

	public ArrayList<Especie> getEspecies() {

		return especies;
	}

	public void setEspeciesInRoute(ArrayList<Especie> especiesInRoute) {
		this.especiesInRoute = especiesInRoute;
	}

	public ArrayList<Especie> getEspeciesInRoute() {

		return especiesInRoute;
	}

	// Nombres de dominios y urls de servicios
	//public String URL_SERVIDOR = "http://185.18.198.182/"; // Altoagueda (por defecto)
	//public String URL_SERVIDOR = "http://dns198182.phdns.es/"; //Altoagueda (nuevo -ha habido redireccion ips-)
	public String URL_SERVIDOR = "http://belfort.randomobile.eu/";
	
	public String ENDPOINT = "api";

	// Tiempo m�ximo que dura una sesi�n abierta (en segundos)
	public long MAX_SESION_LIFETIME = 1296000000; // 15 d�as
	
	// Constante del radio maximo de distancia para buscar pois y rutas (en metros)
	public int MAX_DISTANCE_SEARCH_GEOMETRIES = /*1000000*/ 		1000000;

	// Constante del radio maximo de distancia para resolver enigma (en metros)
	public int MAX_DISTANCE_RESOLVE_ENIGMA_METERS = /*1000000*/		150;
	
	// Constante que define el radio de distancia en el que se buscar�n geocach�s cercanos (en kms)
	public int DISTANCE_SEARCH_GEOCACHES_KMS = /*200000*/ 			20000;
	
	// Constante que define la distancia m�xima en la que el usuario podr� realizar un checkin (en metros)
	public int MAX_DISTANCE_MAKE_CHECKIN_METERS = /*1000000*/		150;
	
	// Constante que define la distancia m�xima en la que el usuario podr� capturar un geocach� (en metros)
	public int MAX_DISTANCE_CAPTURE_GEOCACHE_METERS = /*1000000*/		150;

	// Clave para el sdk de Wikitude (realidad aumentada)
	public static final String	WIKITUDE_SDK_KEY = "fBaXWL8mKNa8JQ8UA59hRz3l8hRhU5NgcQqsffzxIvjfgwvmwJJF0mf3XsigK3Nt/EX7DS0eJbNhwxGCPv8qiZF8QLiGZ+Ekt8DIjEmKHuZOFEHt31XYGKSXB2vvD0Es/6W/+8IMaeNdPIMd01qXt46HO0Ki/eDVjYfeuWa0Ix9TYWx0ZWRfX0Nd/19q9Av3yhIax1+dkU8B8p0UKYT9lVflA6EIDZApLmQevvk9Yw/LJzNohufaWj7ehvPMObF0C/xWigIkakGZe94L58eN0B4+hA3VSeqe9BBY3vn8xYuZZtwhEU1o39wL0F0XcXUGe4SxIbsMf5NjAufHtieDyfB3ZppEUyCHiroxxaJ9R9gbGhiMjOZdbBTq1Al3JL8WYQeRJxIy8TeyjtIVB8Jo4WsT6AjToIYdzgRR409zUgwaoofadANI02x50tg7aH7AOAvebNMoArTFIVrbbOUUlj4tlfbwpFZN1//UXQQkkzzxJO3+dxxbO2eJNaDwthX/2NBw/GYJwjuE5Dozeaa5ABiLq5BLJQKlPIH61nh+NbrPrCXhAL6PBzyDiC5G6BGq/YSyqgPztZrLvsA1yJ7/Cz2biTJSBtwtLYBWukaFhS5GQuTxmReSOGOMrCAJzrWBlBWqm+as9q1qFFw7P9EXJLsTEwfLW/sDuuWTB4nqRDTx8CrdA4/ZLEaV8b/c+u22";
	
	// Coordenadas
	public String FILTER_KEY_LAST_LOCATION_LATITUDE = "filter_key_last_location_latitude";
	public String FILTER_KEY_LAST_LOCATION_LONGITUDE = "filter_key_last_location_longitude";
	public String FILTER_KEY_LAST_LOCATION_ALTITUDE = "filter_key_last_location_altitude";
	
	// Claves para acceder a los filtros de pois
	public String FILTER_KEY_POI_RADIO_DISTANCIA_KMS = "filter_key_poi_radio_distancia";
	public String FILTER_KEY_POI_CATEGORY_TID = "filter_key_poi_category_tid";
	public String FILTER_KEY_POI_TEXT = "filter_key_poi_text_search";
	
	// Claves para acceder a los filtros de pois
	public String FILTER_KEY_ROUTE_RADIO_DISTANCIA_KMS = "filter_key_route_radio_distancia";
	public String FILTER_KEY_ROUTE_CATEGORY_TID = "filter_key_route_category_tid";
	public String FILTER_KEY_ROUTE_DIFFICULTY_TID = "filter_key_route_difficulty_tid";
	public String FILTER_KEY_ROUTE_TEXT = "filter_key_route_text_search";

	// Claves para acceder a los filtros de Panoramio
	public String FILTER_KEY_NUM_PANORAMIOS_CARGAR = "filter_key_num_panoramios_cargar";
	public String FILTER_KEY_NUM_WIKIPEDIAS_CARGAR = "filter_key_num_wikipedias_cargar";
	public String FILTER_KEY_NUM_YOUTUBES_CARGAR = "filter_key_num_youtubes_cargar";

	// Define la el api key para los mapas de bing
	public String BING_MAPS_KEY = "AknzEuD2VRWfmk_HRnAq7SIBMNE6n3qXskFXUDhEW5kWjTlnec8I5dHjWht9_j_O";

	// Types de drupal
	public String DRUPAL_TYPE_POI = "poi";
	public String DRUPAL_TYPE_ROUTE = "route";

	// Preferencias de la aplicaci�n
	public SharedPreferences preferencias;

	// Referencia espacial para los mapas
	//public SpatialReference spatialReference = SpatialReference.create(102100);
	// Capa base que se encuentra seleccionada actualmente
	public CapaBase capaBaseSeleccionada = null;

	// Nombre del almacen de cookies
	public String getPreferencesKEY(Context ctx) {
		return Util.getPackageName(ctx) + "_cookies";
	}

	// Cliente del API de conexi�n con Servicios 3. Se inicializa al arrancar la
	// aplicaci�n
	public Drupal7RESTClient clienteDrupal;
	
	// Clase para gestionar la seguridad entre la app y los servicios
	public Drupal7Security drupalSecurity;


	public int getNetStatus(){
		// Checking for network status.
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity != null) {
			// Checking for wifi network status.
			NetworkInfo info_wifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (info_wifi != null) {
				if (info_wifi.isConnected()) {
					return 1;
				}
			}

			// Checking for mobile network status.
			NetworkInfo info_mobile = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			if (info_mobile != null) {
				if (info_mobile.isConnected()) {
					return 2;
				}
			}
		}

		return 0;
	}
	
	
	// Evento al lanzarse la aplicaci�n. Poner aqu� las inicializaciones
	public void onCreate(){
		super.onCreate();
		this.inicializarAplicacion();
		System.loadLibrary("runtimecore_java");
		//System.loadLibrary("rs.main"); //crash ...
		System.loadLibrary("render_script_analysis");
		//initImageLoader();

		dBHandler = new DBHandler(getApplicationContext(), null, null, 1, MainApp.this);
	}

	
	// M�todo que hace las primeras operaciones al arrancar la aplicaci�n
	public void inicializarAplicacion() {
		Context _ctx = getApplicationContext();

		//Registrar en Esri
        //ArcGISRuntimeEnvironment.setLicense("HDPgu8RTqQsIuaki");
		// Obtener una copia de la BBDD con permisos de escritura
		boolean haySD = ExternalStorage.tieneSD(_ctx);
		if (haySD) {
			// Si tiene almacenamiento externo disponible, generamos la
			// estructura de directorios y ficheros
			ExternalStorage.comprobarDirectorioAppSD(this);
		} else {
			// No tiene tarjeta SD. Avisarle y salir.
			Util.mostrarMensaje(
					_ctx,
					"Pas de carte SD",
					"Se il vous pla�t installer une carte m�moire dans votre appareil pour utiliser l'application");
			return;
		}

		// Inicializar las cookies
		this.preferencias = _ctx.getSharedPreferences(
				this.getPreferencesKEY(_ctx), Context.MODE_PRIVATE);

		// Inicializar el cliente de Drupal para Servicios 3
		String preferencesKey = this.getPreferencesKEY(_ctx);
		String server = this.URL_SERVIDOR + this.ENDPOINT + "/";
		String domain = this.URL_SERVIDOR;
		Long maxLifeTime = Long.valueOf(this.MAX_SESION_LIFETIME);
		if (this.clienteDrupal == null) {
			Log.d("Milog", "Cliente drupal es nulo. Voy a inicializarlo");
			this.clienteDrupal = new Drupal7RESTClient(this,
					preferencesKey, server, domain, maxLifeTime);
		} else {
			Log.d("Milog", "Cliente drupal no es nulo");
		}
		
		// Inicializar la seguridad de drupal
		//if(this.drupalSecurity == null){
			this.drupalSecurity = new Drupal7Security();
		//}

		// Colocar la capa base por defecto para los mapas
		this.setCapaBaseSeleccionadaPorDefecto();
		
		
		// Reestablecer los filtros
		this.resetPoiFilters();
		
		//Ver si se tiene que crear la BBDD para el modo Offline
		Util.setRouteFolder(_ctx);
		Offline.createDB(this);
		String jsonString;
//		Offline.fillPoisTable(this);
//		Offline.fillRoutesTable(this);
//		OfflineRoute.cargaListaRutasOffline (this);
//		OfflineRoute.fillRouteItem(this, "962");
		OfflineRoute.fillRoutesTable(this);
		OfflinePoi.fillPoisTable(this);
//		jsonString = Offline.extractJsonList (this, this.DRUPAL_TYPE_POI);
//		jsonString = Offline.extractJsonList (this, this.DRUPAL_TYPE_ROUTE);
//		jsonString = "";
	}
	
	public void setCapaBaseSeleccionadaPorDefecto() {
		CapaBase capa = new CapaBase(this);
		capa.setIdentificador(CapaBase.CAPA_BASE_TIPO_BING_AERIAL_WITH_LABELS); //TODO this one bug, need to be treated
		capa.setIdentificador(CapaBase.CAPA_BASE_TIPO_WORLD_STREET_MAP);
		capa.setEtiqueta("Bing Road");
		capa.setClaseCapaBase(ArcGISTiledLayer.class);
		this.capaBaseSeleccionada = capa;
	}

	public void resetPoiFilters(){
		SharedPreferences.Editor edit = this.preferencias.edit();
		
		// Quitar el filtro de la ubicaci�n del usuario
		edit.remove(this.FILTER_KEY_LAST_LOCATION_LATITUDE);
		edit.remove(this.FILTER_KEY_LAST_LOCATION_LONGITUDE);
		edit.remove(this.FILTER_KEY_LAST_LOCATION_ALTITUDE);
		
		// Eliminar el filtro el radio de distancia
		if(this.preferencias.getInt(this.FILTER_KEY_POI_RADIO_DISTANCIA_KMS, 0) == 0 ){
			edit.remove(this.FILTER_KEY_POI_RADIO_DISTANCIA_KMS);
		}
		
		// Eliminar el filtro de categoria para los pois
		edit.remove(this.FILTER_KEY_POI_CATEGORY_TID);
		
		// Eliminar el filtro de texto para los pois
		edit.remove(this.FILTER_KEY_POI_TEXT);
		
		edit.commit();
	}
	
	public void resetRouteFilters(){
		SharedPreferences.Editor edit = this.preferencias.edit();
		
		// Quitar el filtro de la ubicaci�n del usuario
		edit.remove(this.FILTER_KEY_LAST_LOCATION_LATITUDE);
		edit.remove(this.FILTER_KEY_LAST_LOCATION_LONGITUDE);
		edit.remove(this.FILTER_KEY_LAST_LOCATION_ALTITUDE);
		
		// Eliminar el filtro el radio de distancia
		if(this.preferencias.getInt(this.FILTER_KEY_ROUTE_RADIO_DISTANCIA_KMS, 0) == 0 ){
			edit.remove(this.FILTER_KEY_ROUTE_RADIO_DISTANCIA_KMS);
		}
		
		// Eliminar el filtro de categoria para las rutas
		edit.remove(this.FILTER_KEY_ROUTE_CATEGORY_TID);
		
		// Eliminar el filtro de dificultad para las rutas
		edit.remove(this.FILTER_KEY_ROUTE_DIFFICULTY_TID);
		
		// Eliminar el filtro de texto para las rutas
		edit.remove(this.FILTER_KEY_ROUTE_TEXT);
		
		edit.commit();
	}
	
	public void resetExternalPoiFilters(){
		SharedPreferences.Editor edit = this.preferencias.edit();
		
		if(this.preferencias.getInt(this.FILTER_KEY_NUM_PANORAMIOS_CARGAR, -1) == -1){
			edit.putInt(this.FILTER_KEY_NUM_PANORAMIOS_CARGAR, 0);
		}
		
		if(this.preferencias.getInt(this.FILTER_KEY_NUM_WIKIPEDIAS_CARGAR, -1) == -1){
			edit.putInt(this.FILTER_KEY_NUM_WIKIPEDIAS_CARGAR, 0);
		}
		
		if(this.preferencias.getInt(this.FILTER_KEY_NUM_YOUTUBES_CARGAR, -1) == -1){
			edit.putInt(this.FILTER_KEY_NUM_YOUTUBES_CARGAR, 0);
		}
		edit.commit();
	}
	

}
