package eu.randomobile.pnrlorraine.mod_global.environment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class DataConection {
	public static boolean connection = false;
	public static boolean hayConexion(Context ctx){
		ConnectivityManager connec =  (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobileNetwork = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if ( (mobileNetwork != null) && (connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) ||
				connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ||
				(mobileNetwork != null) && (connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTING) ||
				connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTING ) {
			connection = true;
			return true;
		} else if ((mobileNetwork != null) && (connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED) ||  
				connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED  ) {
			connection = false;
			return false;
		}
		connection = false;
		return false;
	}
}
