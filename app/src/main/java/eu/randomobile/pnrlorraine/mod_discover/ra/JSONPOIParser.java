package eu.randomobile.pnrlorraine.mod_discover.ra;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.util.Log;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;

public class JSONPOIParser {

	// ensure these attributes are also used in JavaScript when extracting POI data
	public static final String ATTR_ID = "id";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_DESCRIPTION = "description";
	public static final String ATTR_LATITUDE = "latitude";
	public static final String ATTR_LONGITUDE = "longitude";
	public static final String ATTR_ALTITUDE = "altitude";
	public static final String ATTR_ICON = "icon";
	
	
	public static JSONArray parseToJSONArray(Application app, List<Poi> arrayListPois) {

		JSONArray poisJSONArray = new JSONArray();
		
		if(arrayListPois != null){
			for (Poi poi : arrayListPois) {
				HashMap<String, String> poiInformation = new HashMap<String, String>();
				poiInformation.put(ATTR_ID, poi.getNid());
				poiInformation.put(ATTR_NAME, poi.getTitle());
				poiInformation.put(ATTR_DESCRIPTION, "");
				
				double lat = 0;
				double lon = 0;
				double alt = 0;
				if(poi.getCoordinates() != null){
					lat = poi.getCoordinates().getLatitude();
					lon = poi.getCoordinates().getLongitude();
					alt = poi.getCoordinates().getAltitude();
				}
				poiInformation.put(ATTR_LATITUDE, String.valueOf(lat));
				poiInformation.put(ATTR_LONGITUDE, String.valueOf(lon));
				poiInformation.put(ATTR_ALTITUDE, String.valueOf(alt));
				
				String icon = null;
				
				icon = "assets/ic_launcher.png";
				if(poi.getCategory() != null) { //&& poi.getCategory().getIcon() != null){
					String category = poi.getCategory().getName();
					if (category.equals("Chambre d'hôtes")||category.equals("Hôtellerie")
						||category.equals("Hébergement collectif") || category.equals("Hôtellerie de plein air")
						||category.equals("Meublé")||category.equals("Résidence")) {
						icon = "assets/icono_hotel.png";
					}
					else if (category.equals("Musée") || category.equals("Patrimoine Naturel")
							||category.equals("Site et Monument")||category.equals("Office de Tourisme")
							||category.equals("Parc et Jardin")){
						icon = "assets/icono_descubrir.png";
					}
					else if (category.equals("Restauration"))
						icon = "assets/icono_restaurante.png";
				}
				
				poiInformation.put(ATTR_ICON, icon);
				
				poisJSONArray.put(new JSONObject(poiInformation));
			}
		}

		Log.d("Milog", "POIS Reales generados: " + poisJSONArray.toString());
		
		return poisJSONArray;
	}
}
