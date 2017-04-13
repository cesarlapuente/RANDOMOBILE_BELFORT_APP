package eu.randomobile.pnrlorraine.mod_global.map_layer_change;

import java.util.ArrayList;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;


import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.esri.arcgisruntime.layers.ArcGISTiledLayer;

public class ComboCapasMapa {
	
	private Context context;
	ArrayList<CapaBase> capas;
	private AlertDialog.Builder constructorCombo;
	private AlertDialog combo;
	MainApp app;
	
	
	// Interface para comunicarse con el resto de pantallas
	public ComboCapasMapaInterface comboCapasMapaInterface;	
	public static interface ComboCapasMapaInterface {
		public void seCerroComboCapas();
	}
	
	
	
	public ComboCapasMapa(Application application, Context _ctx){
		context = _ctx;
		app = (MainApp)application;
	}
	
	
	private void recargarDataSource(){
		
		if(this.capas == null){
			this.capas = new ArrayList<CapaBase>();
		}
		
		// Rellenar cada array
		CapaBase capaBingAerial = new CapaBase(app);
		capaBingAerial.setIdentificador(CapaBase.CAPA_BASE_TIPO_BING_AERIAL);
		capaBingAerial.setEtiqueta("Bing Aerial");
		capaBingAerial.setClaseCapaBase(ArcGISTiledLayer.class);
		
		CapaBase capaBingAerialLabels = new CapaBase(app);
		capaBingAerialLabels.setIdentificador(CapaBase.CAPA_BASE_TIPO_BING_AERIAL_WITH_LABELS);
		capaBingAerialLabels.setEtiqueta("Bing Aerial with labels");
		capaBingAerialLabels.setClaseCapaBase(ArcGISTiledLayer.class);
		
		CapaBase capaBingRoad = new CapaBase(app);
		capaBingRoad.setIdentificador(CapaBase.CAPA_BASE_TIPO_BING_ROAD);
		capaBingRoad.setEtiqueta("Bing Road");
		capaBingRoad.setClaseCapaBase(ArcGISTiledLayer.class);
		
		CapaBase capaWorldImagery = new CapaBase(app);
		capaWorldImagery.setIdentificador(CapaBase.CAPA_BASE_TIPO_WORLD_IMAGERY);
		capaWorldImagery.setEtiqueta("World Imagery");
		capaWorldImagery.setClaseCapaBase(ArcGISTiledLayer.class);
		
		CapaBase capaWorldPhisical = new CapaBase(app);
		capaWorldPhisical.setIdentificador(CapaBase.CAPA_BASE_TIPO_WORLD_PHISICAL);
		capaWorldPhisical.setEtiqueta("World Phisical");
		capaWorldPhisical.setClaseCapaBase(ArcGISTiledLayer.class);
		
		CapaBase capaWorldShadedRelief = new CapaBase(app);
		capaWorldShadedRelief.setIdentificador(CapaBase.CAPA_BASE_TIPO_WORLD_SHADED_RELIEF);
		capaWorldShadedRelief.setEtiqueta("World Shaded Relief");
		capaWorldShadedRelief.setClaseCapaBase(ArcGISTiledLayer.class);
		
		CapaBase capaWorldStreetMap = new CapaBase(app);
		capaWorldStreetMap.setIdentificador(CapaBase.CAPA_BASE_TIPO_WORLD_STREET_MAP);
		capaWorldStreetMap.setEtiqueta("World Street Map");
		capaWorldStreetMap.setClaseCapaBase(ArcGISTiledLayer.class);
		
		CapaBase capaWorldTerrainBase = new CapaBase(app);
		capaWorldTerrainBase.setIdentificador(CapaBase.CAPA_BASE_TIPO_WORLD_TERRAIN_BASE);
		capaWorldTerrainBase.setEtiqueta("World Terrain Base");
		capaWorldTerrainBase.setClaseCapaBase(ArcGISTiledLayer.class);
		
		CapaBase capaWorldTopo = new CapaBase(app);
		capaWorldTopo.setIdentificador(CapaBase.CAPA_BASE_TIPO_WORLD_TOPO);
		capaWorldTopo.setEtiqueta("World Topo");
		capaWorldTopo.setClaseCapaBase(ArcGISTiledLayer.class);

		/* A decommenter si le premier if de getMapLayer est decommenter aussi *
		this.capas.add(capaBingAerial);
		this.capas.add(capaBingAerialLabels);
		this.capas.add(capaBingRoad);
		*/
		this.capas.add(capaWorldImagery);
		//this.capas.add(capaWorldPhisical);
		this.capas.add(capaWorldShadedRelief);
		this.capas.add(capaWorldStreetMap);
		//this.capas.add(capaWorldTerrainBase);
		this.capas.add(capaWorldTopo);
	    
	    
	    // Poner la que est‡ seleccionada
		for(int i=0; i<this.capas.size(); i++){
			CapaBase capaActual = this.capas.get(i);
			CapaBase capaSeleccionada = app.capaBaseSeleccionada;
			
			if(capaActual.getIdentificador().equals( capaSeleccionada.getIdentificador() )){
				this.capas.get(i).setSeleccionada(true);
				break;
			}
		}
	}
	
	
	private String[] getTextosCapas(){
		String[] textos = new String[capas.size()];
		for(int i=0; i<capas.size();i++){
			CapaBase capa = capas.get(i);
			textos[i] = capa.getEtiqueta();
		}
		return textos;
	}
	
	private int getIndiceSeleccionado(){
		int indiceSel = 0;
		for(int i=0; i<capas.size();i++){
			CapaBase capaActual = this.capas.get(i);
			CapaBase capaSeleccionada = app.capaBaseSeleccionada;
			if(capaActual.getIdentificador().equals( capaSeleccionada.getIdentificador() )){
				indiceSel = i;
				break;
			}
		}
		return indiceSel;
	}
	
	
	
	public void show(){
		
		recargarDataSource();
		String[] textos = getTextosCapas();
		
		constructorCombo = new AlertDialog.Builder(context);
		constructorCombo.setTitle(app.getResources().getString(R.string.mod_global__seleccion_capa));
		constructorCombo.setSingleChoiceItems(textos, getIndiceSeleccionado(), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				app.capaBaseSeleccionada = capas.get(which);
			}
		});
		constructorCombo.setNegativeButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(comboCapasMapaInterface != null){
					comboCapasMapaInterface.seCerroComboCapas();
				}
			}
		});
		constructorCombo.show();
	}
	
	
	
	
	

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}




	public AlertDialog getCombo() {
		return combo;
	}




	public void setCombo(AlertDialog combo) {
		this.combo = combo;
	}

}
