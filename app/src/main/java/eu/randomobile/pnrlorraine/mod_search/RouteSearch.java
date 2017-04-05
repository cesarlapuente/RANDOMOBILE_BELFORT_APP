package eu.randomobile.pnrlorraine.mod_search;

import android.content.Context;
import android.text.Html;

import java.text.Normalizer;
import java.util.Locale;

import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.model.Route;


public class RouteSearch {
	private static int duracionMinutos = 600; // duracion estimada de la ruta
	private static String dificultad = "Dificultad"; // dificultad de la ruta
	private static int distanciaMetros = 100000; //distancia desde el movil a la ruta
	private static int longitudMetros = 50000; // longitud de la ruta
	private static String keyword = null;
	private static Boolean checkDistancia = false;
	
	public static String getKeyword() {
		return keyword;
	}
	public static void setKeyword(String keyword) {
		RouteSearch.keyword = keyword;
	}
	public static int getDuracion() {
		return duracionMinutos;
	}

	public static void setDuracion(int duracion) {
		RouteSearch.duracionMinutos = duracion;
	}
	
	public static String getDificultad() {
		return dificultad;
	}

	public static void setDificultad(String dificultad) {
		RouteSearch.dificultad = dificultad;
	}
	
	public static int getDistanciaMetros() {
		return distanciaMetros;
	}

	public static void setDistancia(int distancia) {
		RouteSearch.distanciaMetros = distancia;
	}
	public static int getLongitud() {
		return longitudMetros;
	}

	public static void setLongitud(int longitud) {
		RouteSearch.longitudMetros = longitud;
	}
	
	public static void setCheckDistancia (Boolean check) {
		RouteSearch.checkDistancia = check;
	}
	public static Boolean getCheckDistancia () {
		return checkDistancia;
	}
	
	private static Boolean checkKeyword (Route miRoute) {
		Boolean res = true;
		if ((keyword != null) && (keyword.equals("") == false)) {
			Locale locale = new Locale("fr", "FR");
			String strBody = Html.fromHtml(miRoute.getBody()).toString().toLowerCase(locale);
			String strTitle = miRoute.getTitle().toLowerCase(locale);
			//quitar los acentos diacriticos
			strBody = Normalizer.normalize(strBody, Normalizer.Form.NFD);
			strBody = strBody.replaceAll("\\p{M}", "");
			strTitle = Normalizer.normalize(strTitle, Normalizer.Form.NFD);
			strTitle = strTitle.replaceAll("\\p{M}", "");
			if ((strBody.contains(keyword) == false) &&
				 (strTitle.contains(keyword) == false))
				res = false;
		}
		return res;
	}
    // Nos dice si la ruta cumple con los criterios actuales del filtrado
	public static Boolean checkCriteria(Route route, Context ctx) {
    	Boolean result = true;
    	String routeDifficulty = route.getDifficulty_tid(); 
    	if (longitudMetros < route.getRouteLengthMeters())
    		result = false;
    	
    	if (checkDistancia && (distanciaMetros < route.getDistanceMeters()))
    		result = false;
    	if (duracionMinutos < route.getEstimatedTime())
    		result = false;
    	
    	if (route.getDifficulty_tid() != null) {
	    	// Moyen
			String muyFacil=ctx.getResources().getString(R.string.muy_facil);
			String facil=ctx.getResources().getString(R.string.facil);
			String medio=ctx.getResources().getString(R.string.medio);
			String dificil=ctx.getResources().getString(R.string.dificil);
    		if (dificultad.equals(medio))
    			if (routeDifficulty.equals("22"))
    				result = false;
    		// Facile
    		if (dificultad.equals(facil))
    			if (routeDifficulty.equals("22") || routeDifficulty.equals("17"))
    				result = false;
    		// Très Facile
    		if (dificultad.equals(muyFacil))
    			if (routeDifficulty.equals("18")== false)
    				result = false;
    	}
    	
    	if (result)
    		result = checkKeyword(route);
    	return result;
    }


}
