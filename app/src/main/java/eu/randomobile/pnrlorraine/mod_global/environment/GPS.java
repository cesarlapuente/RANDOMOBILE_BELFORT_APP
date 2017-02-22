package eu.randomobile.pnrlorraine.mod_global.environment;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

public class GPS {

	private static final String TAG = "Milog";
	private Context context;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Location lastLocation;
//	private static Location staticLocation;

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 5; // 5
																	// meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 0; // Ever

	// flag for GPS status
	private boolean isGPSEnabled = false;

	// flag for network status
	private boolean isNetworkEnabled = false;

	private boolean canGetLocation = false;

	// Inicializa el GPS (Constructor)
	public GPS(LocationListener locListener, Context _ctx) {
		this.context = _ctx;
		this.locationListener = locListener;
		this.locationManager = (LocationManager) this.context
				.getSystemService(Context.LOCATION_SERVICE);
	}

	// Comienza a localizar
	public void startLocating() {
		if (locationManager != null) {
			try {
				locationManager = (LocationManager) context
						.getSystemService(Context.LOCATION_SERVICE);

				// getting GPS status
				isGPSEnabled = locationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER);

				// getting network status
				isNetworkEnabled = locationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

				if (!isGPSEnabled && !isNetworkEnabled) {
					// no network provider is enabled
					Log.d(TAG, "No est� habilitado ni el gps ni el network");
					this.canGetLocation = false;
				} else {
					this.canGetLocation = true;
					// First get location from Network Provider
					if (isNetworkEnabled) {
						locationManager.requestLocationUpdates(
								LocationManager.NETWORK_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES,
								locationListener);
						Log.d(TAG, "Network");
						if (lastLocation == null) {
							lastLocation = locationManager
									.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						}
					}
					// if GPS Enabled get lat/long using GPS Services
					if (isGPSEnabled) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES,
								locationListener);
						Log.d(TAG, "GPS Enabled");
						if (lastLocation == null) {
							lastLocation = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						}
					}
				}
			} catch (Exception ex) {
				Log.d(TAG,
						"Excepcion en metodo buscarUbicacionGPS(): + "
								+ ex.toString());
			}
		}

	}

	// Detiene el servicio de localizaci�n del gps
	public void stopLocating() {
		// Detener el hilo de b�squeda de GPS
		if (locationManager != null) {
			locationManager.removeUpdates(locationListener);
		}
	}

	
	
	public Location getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

	public boolean isCanGetLocation() {
		return canGetLocation;
	}

	public void setCanGetLocation(boolean canGetLocation) {
		this.canGetLocation = canGetLocation;
	}

	/*
	 * Metodo estatico que comprueba si hay conexion de GPS Devuelve true: si
	 * hay conexion de GPS Devuelve false: si no hay conexion de GPS
	 */
	public static boolean isOn(Context contexto) {
		final LocationManager manager = (LocationManager) contexto
				.getSystemService(Context.LOCATION_SERVICE);
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return false;
		} else {
			return true;
		}
	}
	
	public static double[] getGPS(Context ctx) {
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);  
        List<String> providers = lm.getProviders(true);

        /* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
        Location l = null;
        
        for (int i=providers.size()-1; i>=0; i--) {
                l = lm.getLastKnownLocation(providers.get(i));
                if (l != null) break;
        }
        
        double[] gps = new double[2];
        gps[0]= 0;
        gps[1] = 0;
        if (l != null) {
                gps[0] = l.getLatitude();
                gps[1] = l.getLongitude();
        }
        return gps;
}

//	public static Location getStaticLocation() {
//		return staticLocation;
//	}

}
