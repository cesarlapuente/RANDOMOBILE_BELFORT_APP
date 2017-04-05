package eu.randomobile.pnrlorraine.mod_notification;

import java.io.File;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_discover.list.PoisListActivity;
import eu.randomobile.pnrlorraine.mod_discover.map.PoisGeneralMapActivity;
import eu.randomobile.pnrlorraine.mod_discover.ra.JSONPOIParser;
//import eu.randomobile.pnrlorraine.mod_discover.ra.MetaIORAActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CreateNotificationActivity extends Activity {
	
  MainApp app;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.app = (MainApp) getApplication();
    setContentView(R.layout.notification_create);
  }

  @SuppressLint("NewApi")
public void createNotification(View view) {
    // Prepare intent which is triggered if the
    // notification is selected
	  
	// Dos intents para mostrar lista o para mostrar mapas
    Intent intentList = new Intent(this, PoisListActivity.class);
    Intent intentMap = new Intent(this, PoisGeneralMapActivity.class);
//    Intent intentRA = new Intent(this, RAActivity.class);
//	intentRA.putExtra(RAActivity.EXTRAS_KEY_ACTIVITY_TITLE_STRING, "RA");
//	intentRA.putExtra(RAActivity.EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL, "wikitudeWorld" + File.separator + "index.html");
//	intentRA.putExtra(RAActivity.PARAM_KEY_JSON_POI_DATA, JSONPOIParser.parseToJSONArray(app, Cache.arrayPois).toString() );
      //Intent intentRA = new Intent(this,MetaIORAActivity.class);
	
    intentMap.putExtra(PoisGeneralMapActivity.PARAM_KEY_MOSTRAR, PoisGeneralMapActivity.PARAM_MAPA_GENERAL_MOSTRAR_POIS);
    PendingIntent pIntentList = PendingIntent.getActivity(this, 0, intentList, 0);
    PendingIntent pIntentMap = PendingIntent.getActivity(this, 0, intentMap, 0);
   // PendingIntent pIntentRA = PendingIntent.getActivity(this, 0, intentRA, 0);
    
    // Build notification
    Notification noti;
    if (Cache.arrayPois != null)
    	noti = new Notification.Builder(this)
        .setContentTitle(getResources().getString(R.string.mod_notification__new_pois))
        .setContentText("Subject").setSmallIcon(R.drawable.ic_launcher)
        .setContentIntent(pIntentList)
        .addAction(R.drawable.icon_mapa, "Carte", pIntentMap)
        .addAction(R.drawable.icon_lista, "List", pIntentList).build();
        //.addAction(R.drawable.icon_ra, "RA", pIntentRA).build();
    else
       	noti = new Notification.Builder(this)
	    .setContentTitle(getResources().getString(R.string.mod_notification__new_pois))
	    .setContentText("Subject").setSmallIcon(R.drawable.ic_launcher)
	    .setContentIntent(pIntentList)
	    .addAction(R.drawable.icon_mapa, "Carte", pIntentMap)
	    .addAction(R.drawable.icon_lista, "List", pIntentList).build();
    
    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    // hide the notification after its selected
    noti.flags |= Notification.FLAG_AUTO_CANCEL;

    notificationManager.notify(0, noti);

  }
} 