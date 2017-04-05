package eu.randomobile.pnrlorraine.mod_notification;

import java.io.File;
import java.util.List;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_discover.list.PoisListActivity;
import eu.randomobile.pnrlorraine.mod_discover.map.PoisGeneralMapActivity;
import eu.randomobile.pnrlorraine.mod_discover.ra.JSONPOIParser;
//import eu.randomobile.pnrlorraine.mod_discover.ra.MetaIORAActivity;
import eu.randomobile.pnrlorraine.mod_global.environment.GPS;
import eu.randomobile.pnrlorraine.mod_global.model.GeoPoint;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;

public class NotificationService extends Service implements LocationListener {
    
    private WakeLock mWakeLock;
    private MainApp app;
    GPS gps;
    
    /**
     * Simply return null, since our Service will not be communicating with
     * any other components. It just does its work silently.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    /**
     * This is where we initialize. We call this when onStart/onStartCommand is
     * called by the system. We won't do anything with the intent here, and you
     * probably won't, either.
     */
    private void handleIntent(Intent intent) {
        // obtain the wake lock
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PNRL Notification Service");
        mWakeLock.acquire();
        
        // check the global background data setting
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (!cm.getBackgroundDataSetting()) {
            stopSelf();
            return;
        }
        
        // do the actual work, in a separate thread
        new PollTask().execute();
    }
    
    //Metodo que comprueba si hay algun poi cercano.
    //En caso de que haya un poi cercano lanza una notificacion
    private void checkNearbyPois() {
    	List<Poi> aPois = Cache.arrayPois;
    	GeoPoint ultimaUbicacion = new GeoPoint();
  		ultimaUbicacion.setLatitude(gps.getLastLocation().getLatitude());
  		ultimaUbicacion.setLongitude(gps.getLastLocation().getLongitude());
    	for (int i=0; i<aPois.size(); i++)
    		if (GeoPoint.calculateDistance(aPois.get(i).getCoordinates(), ultimaUbicacion) < 300) {
    			//Lanzar notificacion de Poi cercano
    			createNotification();
    			break;
    		}
    			
    }
    
    @SuppressLint("NewApi")
    private void createNotification() {
        // Prepare intent which is triggered if the
        // notification is selected
    	  
    	// Dos intents para mostrar lista o para mostrar mapas
        Intent intentList = new Intent(this, PoisListActivity.class);
        Intent intentMap = new Intent(this, PoisGeneralMapActivity.class);
//        Intent intentRA = new Intent(this, RAActivity.class);
//    	intentRA.putExtra(RAActivity.EXTRAS_KEY_ACTIVITY_TITLE_STRING, "RA");
//    	intentRA.putExtra(RAActivity.EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL, "wikitudeWorld" + File.separator + "index.html");
//    	intentRA.putExtra(RAActivity.PARAM_KEY_JSON_POI_DATA, JSONPOIParser.parseToJSONArray(app, Cache.arrayPois).toString() );
        //Intent intentRA = new Intent(this,MetaIORAActivity.class);
        intentMap.putExtra(PoisGeneralMapActivity.PARAM_KEY_MOSTRAR, PoisGeneralMapActivity.PARAM_MAPA_GENERAL_MOSTRAR_POIS);
        PendingIntent pIntentList = PendingIntent.getActivity(this, 0, intentList, 0);
        PendingIntent pIntentMap = PendingIntent.getActivity(this, 0, intentMap, 0);
//        PendingIntent pIntentRA = PendingIntent.getActivity(this, 0, intentRA, 0);
        
        // Build notification
        Notification noti;
        if (Cache.arrayPois != null)
        	noti = new Notification.Builder(this)
            .setContentTitle(getResources().getString(R.string.mod_notification__new_pois))
            .setContentText("Subject").setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(pIntentList)
            .addAction(R.drawable.icon_mapa, "Carte", pIntentMap)
            .addAction(R.drawable.icon_lista, "List", pIntentList).build();
            //           .addAction(R.drawable.icon_ra, "RA", pIntentRA)
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
    private class PollTask extends AsyncTask<Void, Void, Void> {
        /**
         * This is where YOU do YOUR work. There's nothing for me to write here
         * you have to fill this in. Make your HTTP request(s) or whatever it is
         * you have to do to get your updates in here, because this is run in a
         * separate thread
         */
        @Override
        protected Void doInBackground(Void... params) {
            // do stuff!
        	if ((Cache.notificationEnabled == false) || Cache.arrayPois == null)
        		return null;
        	if (gps != null) {
        		if ((gps.getLastLocation() != null) && (Cache.notificationEnabled == true) )
        			checkNearbyPois();
        	}
        	return null;	
        }
        
        /**
         * In here you should interpret whatever you fetched in doInBackground
         * and push any notifications you need to the status bar, using the
         * NotificationManager. I will not cover this here, go check the docs on
         * NotificationManager.
         *
         * What you HAVE to do is call stopSelf() after you've pushed your
         * notification(s). This will:
         * 1) Kill the service so it doesn't waste precious resources
         * 2) Call onDestroy() which will release the wake lock, so the device
         *    can go to sleep again and save precious battery.
         */
        @Override
        protected void onPostExecute(Void result) {
            // handle your data
            stopSelf();
        }
    }
    
    /**
     * This is deprecated, but you have to implement it if you're planning on
     * supporting devices with an API level lower than 5 (Android 2.0).
     */
    @Override
    public void onStart(Intent intent, int startId) {
    	app = (MainApp) getApplication();
       	// Inicializar el locationManager
    	if (Cache.notificationEnabled) {
	        gps = new GPS(this, this);
			if (this.gps.getLastLocation() == null) {
				this.gps.startLocating();
			}
    	}
        handleIntent(intent);
    }
    
    /**
     * This is called on 2.0+ (API level 5 or higher). Returning
     * START_NOT_STICKY tells the system to not restart the service if it is
     * killed because of poor resource (memory/cpu) conditions.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	app = (MainApp) getApplication();
    	// Inicializar el locationManager
    	if (Cache.notificationEnabled) {
	        gps = new GPS(this, this);
			if (this.gps.getLastLocation() == null) {
				this.gps.startLocating();
			}
    	}
        handleIntent(intent);
        return START_NOT_STICKY;
    }
    
    /**
     * In onDestroy() we release our wake lock. This ensures that whenever the
     * Service stops (killed for resources, stopSelf() called, etc.), the wake
     * lock will be released.
     */
    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (this.gps.getLastLocation() != null) {

			// Parar el gps si ya tenemos una coordenada
			this.gps.stopLocating();
			if ((Cache.arrayPois != null) && (Cache.notificationEnabled == true))
				this.checkNearbyPois();
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}


}