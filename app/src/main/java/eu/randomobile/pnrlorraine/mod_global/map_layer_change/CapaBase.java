package eu.randomobile.pnrlorraine.mod_global.map_layer_change;

import android.app.Application;
import android.util.Log;

import eu.randomobile.pnrlorraine.MainApp;


import com.esri.arcgisruntime.layers.ArcGISTiledLayer;


public class CapaBase {

	private String identificador;
	private String etiqueta;
	private Class<?> claseCapaBase;
	private boolean seleccionada;
	
	public static String CAPA_BASE_TIPO_BING_AERIAL = "bingAerial";
	public static String CAPA_BASE_TIPO_BING_AERIAL_WITH_LABELS = "bingAerialWithLabels";
	public static String CAPA_BASE_TIPO_BING_ROAD = "bingRoad";
	public static String CAPA_BASE_TIPO_WORLD_IMAGERY = "worldImagery";
	public static String CAPA_BASE_TIPO_WORLD_PHISICAL = "worldPhisical";
	public static String CAPA_BASE_TIPO_WORLD_SHADED_RELIEF = "worldShadedRelief";
	public static String CAPA_BASE_TIPO_WORLD_STREET_MAP = "worldStreetMap";
	public static String CAPA_BASE_TIPO_WORLD_TERRAIN_BASE = "worldTerrainBase";
	public static String CAPA_BASE_TIPO_WORLD_TOPO = "worldTopo";
	
	MainApp app;
	
	public CapaBase(Application application){
		this.app = (MainApp)application;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public void setClaseCapaBase(Class<?> claseCapaBase) {
		this.claseCapaBase = claseCapaBase;
	}

	public Class<?> getClaseCapaBase() {
		return claseCapaBase;
	}

	public void setSeleccionada(boolean seleccionada) {
		this.seleccionada = seleccionada;
	}

	public boolean isSeleccionada() {
		return seleccionada;
	}

	
	public Object getMapLayer(){
		Object capaBaseADevolver = null;
		
		if(claseCapaBase != null){
			Log.d("Milog", "En getMapLayer la claseCapaBase no es nula: " + claseCapaBase.getName());
		}else{
			Log.d("Milog", "En getMapLayer la claseCapaBase es nula");
		}

		
		/*if(claseCapaBase == BingMapsLayer.class){
			Log.d("Milog", "La capa es de tipo BING");
			if(identificador.equals(CAPA_BASE_TIPO_BING_AERIAL)){
				capaBaseADevolver = new BingMapsLayer(app.BING_MAPS_KEY, MapStyle.AERIAL);
			}else if(identificador.equals(CAPA_BASE_TIPO_BING_AERIAL_WITH_LABELS)){
				capaBaseADevolver = new BingMapsLayer(app.BING_MAPS_KEY, MapStyle.AERIAL_WITH_LABELS);
			}else if(identificador.equals(CAPA_BASE_TIPO_BING_ROAD)){
				capaBaseADevolver = new BingMapsLayer(app.BING_MAPS_KEY, MapStyle.ROAD);
			}
		}else*/
		if (claseCapaBase == ArcGISTiledLayer.class) {
			Log.d("Milog", "La capa es de tipo TiledMap");
			if(identificador.equals(CAPA_BASE_TIPO_WORLD_IMAGERY)){
				capaBaseADevolver = new ArcGISTiledLayer("http://services.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer");
			}else if(identificador.equals(CAPA_BASE_TIPO_WORLD_PHISICAL)){
				capaBaseADevolver = new ArcGISTiledLayer("http://services.arcgisonline.com/ArcGIS/rest/services/World_Physical_Map/MapServer");
			}else if(identificador.equals(CAPA_BASE_TIPO_WORLD_SHADED_RELIEF)){
				capaBaseADevolver = new ArcGISTiledLayer("http://services.arcgisonline.com/ArcGIS/rest/services/World_Shaded_Relief/MapServer");
			}else if(identificador.equals(CAPA_BASE_TIPO_WORLD_STREET_MAP)){
				capaBaseADevolver = new ArcGISTiledLayer("http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer");
			}else if(identificador.equals(CAPA_BASE_TIPO_WORLD_TERRAIN_BASE)){
				capaBaseADevolver = new ArcGISTiledLayer("http://services.arcgisonline.com/ArcGIS/rest/services/World_Terrain_Base/MapServer");
			}else if(identificador.equals(CAPA_BASE_TIPO_WORLD_TOPO)){
				capaBaseADevolver = new ArcGISTiledLayer("http://services.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer");
			}
		}
		
		return capaBaseADevolver;
	}
	
	
	
	
}
