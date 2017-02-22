package eu.randomobile.pnrlorraine.mod_global.libraries.download;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_global.Util;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

// usually, subclasses of AsyncTask are declared inside the activity class.
// that way, you can easily modify the UI thread from here
public class DownloadUrl extends AsyncTask<String, Integer, String> {

    private Context context;
    private PowerManager.WakeLock mWakeLock;
    
	public static OnTaskCompletedInterface completedInterface;

	public static interface OnTaskCompletedInterface {
		public void seCompletoDescarga();
		public void setProgresoDescarga(int progress);
		public void inicioDescarga();
	}
	
    public DownloadUrl(Context context) {
        this.context = context;
        if(DownloadUrl.completedInterface != null)
        	DownloadUrl.completedInterface.inicioDescarga();
    }

    public DownloadUrl (Context context, RelativeLayout rl) {
    	this.context = context;
    }
    
    // Obtiene el tamaño global de las imagenes a descargar.
    private int getFilesSizes(String... sUrl) {
    	int size = 0;
    	HttpURLConnection connection = null;
    	for (int i=0; i<sUrl.length; i++) {
    		try {
    			URL url = new URL(sUrl[i]);
    			connection = (HttpURLConnection) url.openConnection();
    			int fileLength = connection.getContentLength();
    			size += fileLength;
    		}
    		catch (Exception e) {
    			Log.d("Milog", "Excepcion al calcular tamaño ficheros descarga: " + e.toString());
            }
            if (connection != null)
                connection.disconnect();
    	}
    	return size;
    }
    
    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        int globalSize = getFilesSizes(sUrl);
        long total = 0;
        for (int i=0; i<sUrl.length; i++) {
	        try {
	            URL url = new URL(sUrl[i]);
	            connection = (HttpURLConnection) url.openConnection();
	            connection.connect();
	
	            // expect HTTP 200 OK, so we don't mistakenly save error report
	            // instead of the file
	            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	                return "Server returned HTTP " + connection.getResponseCode()
	                        + " " + connection.getResponseMessage();
	            }
	
	            // this will be useful to display download percentage
	            // might be -1: server did not report the length
	            //int fileLength = connection.getContentLength();
	
	            // download the file
	            input = connection.getInputStream();
	            if (sUrl[i].endsWith(".tpk") || sUrl[i].endsWith(".TPK")) {
	            	String idStr = sUrl[i].substring(sUrl[i].lastIndexOf('/') + 1);
	            	output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + ((MainApp)context).CARPETA_SD + "/" + idStr);
	            }
	            else
	            	output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + ((MainApp)context).CARPETA_SD + "/" + Util.md5Sum(sUrl[i]));
	            byte data[] = new byte[4096];
	            int count;
	            while ((count = input.read(data)) != -1) {
	                // allow canceling with back button
	                if (isCancelled()) {
	                    input.close();
	                    return null;
	                }
	                total += count;
	                // publishing the progress....
	                if (globalSize > 0) // only if total length is known
	                    publishProgress((int) (total * 100 / globalSize));
	                output.write(data, 0, count);
	            }
	        } catch (Exception e) {
	            return e.toString();
	        } finally {
	            try {
	                if (output != null)
	                    output.close();
	                if (input != null)
	                    input.close();
	            } catch (IOException ignored) {
	            }
	
	            if (connection != null)
	                connection.disconnect();
	        }
        }
        return null;
    }
    
    @Override
    protected void onProgressUpdate(final Integer... values) {
    	if(DownloadUrl.completedInterface != null)
    		DownloadUrl.completedInterface.setProgresoDescarga(values[0]);
    }
    
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		// Informar al delegate
		if(DownloadUrl.completedInterface != null){
    		DownloadUrl.completedInterface.seCompletoDescarga();
		}   		
	}
}