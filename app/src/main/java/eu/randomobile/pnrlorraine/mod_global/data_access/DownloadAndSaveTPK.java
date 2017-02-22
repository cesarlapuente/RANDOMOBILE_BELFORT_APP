package eu.randomobile.pnrlorraine.mod_global.data_access;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.model.Route;

public class DownloadAndSaveTPK extends AsyncTask<String, Integer, String> {
    private MainApp app;
    private int route_id;

    private Context context;
    private ProgressDialog progressDialog;


    public DownloadAndSaveTPK(MainApp app, int route_id, String url, String file_name, Context context) {
        this.app = app;
        this.route_id = route_id;
        this.context = context;

        progressDialog = new ProgressDialog(context);
        execute(url, file_name);
    }

    protected String doInBackground(String... urls) {
        String DownloadUrl = urls[0];
        String fileName = urls[1];

        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/localmaps");

            if (dir.exists() == false) {
                dir.mkdirs();
            }

            Log.d("doInBackground sais:", "<---------> URL FORMAT: " + DownloadUrl);

            URL url = new URL(DownloadUrl); //you can write here any link
            File file = new File(dir, fileName);

            Log.d("Path: ", file.getCanonicalPath());

            long startTime = System.currentTimeMillis();

            Log.d("DownloadManager", "download begining");
            Log.d("DownloadManager", "download url:" + url);
            Log.d("DownloadManager", "downloaded file name:" + fileName);

            // Open a connection to that URL. /
            URLConnection ucon = url.openConnection();
           /*
            * Define InputStreams to read from the URLConnection.
            */
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

           /*
            * Read bytes to the Buffer until there is nothing more to read(-1).
            */
            ByteArrayBuffer baf = new ByteArrayBuffer(5000);
            int current = 0;

            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            // Convert the Bytes read to a String. /
            FileOutputStream fos = new FileOutputStream(file);

            fos.write(baf.toByteArray());

            fos.flush();
            fos.close();

            Log.d("DownloadManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");

            app.getDBHandler().addOrReplaceMap(route_id, file.getCanonicalPath());

        } catch (IOException e) {
            Log.d("DownloadManager", "Error: " + e);
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Descargando mapa...");
        progressDialog.setMessage("Este proceso puede tardar varios minutos.");
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.show();

    }

    @Override
    protected void onProgressUpdate(final Integer... values) {
        // if (progressDialog.getProgress() <= progressDialog.getMax()){
        progressDialog.incrementProgressBy(1);
        // }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        progressDialog.setMessage("Descarga de mapa completada.");
        progressDialog.incrementProgressBy(100);

    }
}