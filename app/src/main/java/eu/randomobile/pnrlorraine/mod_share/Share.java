package eu.randomobile.pnrlorraine.mod_share;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import eu.randomobile.pnrlorraine.mod_global.model.Route;

public class Share {
	
	public static void compartir(Application application, Context ctx, Object obj){
		
		MainApp app = (MainApp)application;
		
		String frase = "";
		String nombreItem = "";
		String enlace = app.URL_SERVIDOR;
		
		
		if(obj.getClass().getName().equals(Poi.class.getName())){
			Poi poi = (Poi)obj;
			frase = app.getResources().getString(R.string.mod_share__me_gusta);
			nombreItem = poi.getTitle();
			//enlace = app.URL_SERVIDOR + "node/" + poi.getNid();
		}else if(obj.getClass().getName().equals(Route.class.getName())){
			Route route = (Route)obj;
			frase = app.getResources().getString(R.string.mod_share__me_gusta);
			nombreItem = route.getTitle();
			//enlace = app.URL_SERVIDOR + "node/" + route.getNid();
		}
		
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
	    shareIntent.setType("text/plain");
	    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, frase + nombreItem + " " + enlace );
	    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    ctx.startActivity(Intent.createChooser(shareIntent, app.getResources().getString(R.string.mod_share__compartir_con)));
	}

}
