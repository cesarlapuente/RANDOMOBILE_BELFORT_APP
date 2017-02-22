package eu.randomobile.pnrlorraine.mod_global;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;











import org.json.JSONObject;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_offline.Offline;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class Util {

	private static String routeFolder = null;
	private static char[] hexDigits = "0123456789abcdef".toCharArray();
	
	public static Typeface fontScala_Bold(Context ctx) {
		return Typeface.createFromAsset(ctx.getAssets(),
				"fonts/scala-sans-bold.ttf");
	}
	
	public static Typeface fontBenton_Bold(Context ctx) {
		return Typeface.createFromAsset(ctx.getAssets(),
				"fonts/BentonCourtaousSans-CondBol.ttf");
	}
	
	public static Typeface fontBenton_Boo(Context ctx) {
		return Typeface.createFromAsset(ctx.getAssets(),
				"fonts/BentonCourtaousSans-CondBoo.ttf");
	}
	
	public static Typeface fontBenton_Light(Context ctx) {
		return Typeface.createFromAsset(ctx.getAssets(),
				"fonts/BentonCourtaousSans-Light.ttf");
	}
	
	public static Typeface fontBenton_Medium(Context ctx) {
		return Typeface.createFromAsset(ctx.getAssets(),
				"fonts/BentonCourtaousSans-Medium.ttf");
	}
	
	public static Typeface fontBubblegum_Regular(Context ctx) {
		return Typeface.createFromAsset(ctx.getAssets(),
				"fonts/BubblegumCourtaouSans-Regul.ttf");
	}
	

	// Muestra un mensaje en el contexto que se le env�e
	public static void mostrarMensaje(Context ctx, String titulo, String mensaje) {
		AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);

		if (titulo == null || titulo.equals("")) {
			titulo = "";
		}

		dialogo.setTitle(titulo);
		dialogo.setMessage(mensaje);
		dialogo.setNegativeButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		dialogo.show();
	}

	public static String getAppName(Context ctx) {
		Resources res = ctx.getResources();
		String appName = res.getString(R.string.app_name);
		return appName;
	}

	public static String getPackageName(Context ctx) {
		return ctx.getPackageName();
	}

	public static String getAppVersion(Context ctx) {
		String version;
		try {
			String pkg = ctx.getPackageName();
			version = ctx.getPackageManager().getPackageInfo(pkg, 0).versionName;
		} catch (NameNotFoundException e) {
			version = "?";
		}
		return version;
	}

	public static boolean validateEmail(String email) {
		return email.matches("[a-zA-Z0-9._-]+@[a-zA-Z0-9]+\\.[a-z]+");
	}

	public static String fileExt(String url) {
		if (url.indexOf("?") > -1) {
			url = url.substring(0, url.indexOf("?"));
		}
		if (url.lastIndexOf(".") == -1) {
			return null;
		} else {
			String ext = url.substring(url.lastIndexOf("."));
			if (ext.indexOf("%") > -1) {
				ext = ext.substring(0, ext.indexOf("%"));
			}
			if (ext.indexOf("/") > -1) {
				ext = ext.substring(0, ext.indexOf("/"));
			}
			return ext.toLowerCase();

		}
	}

	public static float calcularDistancia(float lat1, float lat2, float lon1,
			float lon2) {
		double radioTierra = 3958.75;
		double dLat = 0;
		double dLng = 0;
		if (lat1 > lat2) {
			dLat = Math.toRadians(lat1 - lat2);
		} else {
			dLat = Math.toRadians(lat2 - lat1);
		}
		if (lon1 > lon2) {
			dLng = Math.toRadians(lon1 - lon2);
		} else {
			dLng = Math.toRadians(lon2 - lon1);
		}
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = radioTierra * c;
		int meterConversion = 1609;
		return (float) (dist * meterConversion);
	}
	
	
	public static void applyLetterSpacing(TextView textView, float letterSpacing) {
        StringBuilder builder = new StringBuilder();
        
        CharSequence originalText = textView.getText();
        
        for(int i = 0; i < originalText.length(); i++) {
            builder.append(originalText.charAt(i));
            if(i+1 < originalText.length()) {
                builder.append(" ");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if(builder.toString().length() > 1) {
            for(int i = 1; i < builder.toString().length(); i+=2) {
                finalText.setSpan(new ScaleXSpan((letterSpacing+1)/10), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        textView.setText(finalText, BufferType.SPANNABLE);
    }
	
	
	
	
	
	// M�todo que sirve para serializar un array de strings y guardarlo en las SharedPreferences
	public static void setStringArrayPref(Application application, String key, ArrayList<String> values) {
		MainApp app = (MainApp)application;
	    SharedPreferences.Editor editor = app.preferencias.edit();
	    
	    if(values == null){
	    	editor.remove(key);
	    }else{
	    	JSONArray a = new JSONArray();
		    for (int i = 0; i < values.size(); i++) {
		        a.put(values.get(i));
		    }
		    if (!values.isEmpty()) {
		        editor.putString(key, a.toString());
		    } else {
		        editor.putString(key, null);
		    }
	    }

	    editor.commit();
	}
	
	// M�todo que sirve para recuperar un array de strings de las SharedPreferences
	public static ArrayList<String> getStringArrayPref(Application application, String key) {
		MainApp app = (MainApp)application;
	    String json = app.preferencias.getString(key, null);
	    ArrayList<String> values = new ArrayList<String>();
	    if (json != null) {
	        try {
	            JSONArray a = new JSONArray(json);
	            for (int i = 0; i < a.length(); i++) {
	                String value = a.optString(i);
	                values.add(value);
	            }
	        } catch (JSONException e) {
	        	Log.d("Milog", "Excepci�n al recuperar array de strings de las sharedPreferences");
	            e.printStackTrace();
	        }
	    }
	    return values;
	}
	
	
	public static String getCurrentTimeStampFormatoUNIX()	{
		long unixTime = System.currentTimeMillis() / 1000L;
		return String.valueOf(unixTime);
	}

	public static String formatDesnivel(double desnivel) {
		String slopeString = "";
		if (desnivel < 0)
			slopeString = "-" + Integer.toString(((int) desnivel)) + "m";
		else
			slopeString = "+" + Integer.toString(((int) desnivel)) + "m";
		return slopeString;
	}

	public static String formatDuracion (double time) {
		String timeString = "";
		timeString = Integer.toString (((int) time) /60) + "H" + Integer.toString (((int)time)%60);
		return timeString;
	}
	public static String formatDistanciaRoute (double distancia) {
		String distanceString = "";
		distanceString = Integer.toString (((int) distancia) /1000) + "," + Integer.toString (((int)distancia)%1000).charAt(0) + " km";
		return distanceString;
	}
	
	public static String md5Sum (String sMessage) {
		byte[] bytesOfMessage = null;
		byte[] thedigest = null;
		String res = null;
		try {
			bytesOfMessage = sMessage.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			thedigest = md.digest(bytesOfMessage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (thedigest != null) {
			StringBuilder sb = new StringBuilder(32);
			for (byte b : thedigest) {
				sb.append(hexDigits[(b >> 4) & 0x0f]);
				sb.append(hexDigits[b & 0x0f]);
			}
			res = sb.toString();
		}
		return res;
	}
	
	// Genera un entero aleatorio entre min y max 
	public static int randInt(int min, int max) {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	// return inputDrawable scaled to newHeight and newWidth based on the display metrics of rs (resources). 
	public static Drawable scaleDrawable (Resources rs, Drawable inputDrawable, int newWidth, int newHeight) { 
		Bitmap bitmap = ((BitmapDrawable) inputDrawable).getBitmap();
	// Scale it to newWidth x newHeight
		Drawable dr = new BitmapDrawable(rs, Bitmap.createScaledBitmap(bitmap, newWidth, newHeight,  true));
		return dr;
	}
	
	/**
	 * This method converts dp unit to equivalent pixels, depending on device density. 
	 * 
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static float convertDpToPixel(float dp, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi / 160f);
	    return px;
	}

	/**
	 * This method converts device specific pixels to density independent pixels.
	 * 
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float dp = px / (metrics.densityDpi / 160f);
	    return dp;
	}
	
	public static void setRouteFolder (Context context) {
		routeFolder = "file:/" + Environment.getExternalStorageDirectory() + "/" + ((MainApp)context).CARPETA_SD + "/";
	}
	public static String getRouteFolder() {
		return routeFolder;
	}
	
    public static String getUrlRouteBaseLayerOffline(MainApp app, String paramNid, String paramMapUrl) {
    	File demoDataFile = Environment.getExternalStorageDirectory();
	    //String basemap = demoDataFile + File.separator + "PNRLorraine" + File.separator + "ImageryTPK.tpk";
		String jsonItem = Offline.extractJsonItem(app, app.DRUPAL_TYPE_ROUTE, Integer.valueOf(paramNid));
		if (jsonItem != null) {
			try {
	        	JSONObject dicRes = new JSONObject(jsonItem);
	        	if(dicRes != null) {
	        		paramMapUrl = dicRes.getString("map");
	        	}
			}
			catch (Exception e) {
				Log.d("Milog", "Excepcion cargar route de DB: " + e.toString());
			}
		}
		//cogemos la ultima parte de la url del mapa.
		String idStr = "";
		if (paramMapUrl != null)
			idStr = paramMapUrl.substring(paramMapUrl.lastIndexOf('/') + 1);
		String basemap = demoDataFile + File.separator + "PNRLorraine" + File.separator + idStr;
	    String basemapurl = "file://" + basemap;
	    
	    // Si no existe el fichero de mapas local, se carga el general.
	    File file = new File(basemap);
	    if ((paramMapUrl == null) || !file.exists()) {
	    	basemapurl = getUrlGeneralBaseLayerOffline(app);
	    }
	    return basemapurl;
    }
    
    public static String getUrlGeneralBaseLayerOffline(MainApp app) {
		File demoDataFile = Environment.getExternalStorageDirectory();
	    String basemapurl;
	    // Si no se ha copiado previamente el fichero de mapas desde el assets a la carpeta sd se copia ahora
	    String siteMapFile = demoDataFile + File.separator + "PNRLorraine" + File.separator + "LorraineALL.tpk";
	    File file = new File(siteMapFile);
	    if((!file.exists()) || !(file.length()==app.LENGTH_LORRAINE_TPK)) {      
	    	copyMapFileInAppFileDir(app, siteMapFile, "LorraineALL.tpk");
	    }
	    
	    // Cargamos el mapa general (modo de no conexi�n).
	    basemapurl = "file://" + siteMapFile;
	    return basemapurl;
    }
    
    private static void copyMapFileInAppFileDir(MainApp app, String fileOutput, String fileAssetsInput)
    {
            FileOutputStream destinationFileStream = null;
            InputStream assetsOriginFileStream = null;
            try{
                    //destinationFileStream = openFileOutput(fileOutput, Context.MODE_PRIVATE);
                    destinationFileStream = new FileOutputStream (new File(fileOutput)); 
                    assetsOriginFileStream = app.getAssets().open(fileAssetsInput);

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = assetsOriginFileStream.read(buf)) > 0) {
                    	destinationFileStream.write(buf, 0, len);
                    }
            }
            catch (Exception e){
                    Log.d("PNRLorraine", e.getMessage());
            }
            finally{
                    try
                    {
                            assetsOriginFileStream.close();
                            destinationFileStream.close();
                    }
                    catch (Exception e){
                            e.printStackTrace();
                    }
            }
    }
}
