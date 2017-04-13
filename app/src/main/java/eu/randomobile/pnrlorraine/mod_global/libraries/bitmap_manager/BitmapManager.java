package eu.randomobile.pnrlorraine.mod_global.libraries.bitmap_manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public enum BitmapManager {
	INSTANCE;

	public final Map<String, SoftReference<Bitmap>> cache;
	private final ExecutorService pool;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	private Bitmap placeholder;

	BitmapManager() {
		cache = new HashMap<String, SoftReference<Bitmap>>();
		pool = Executors.newFixedThreadPool(5);
	}

	public void setPlaceholder(Bitmap bmp) {
		placeholder = bmp;
	}

	public Bitmap getBitmapFromCache(String url) {
		if (cache.containsKey(url)) {
			return cache.get(url).get();
		}
		return null;
	}

	public void queueJob(final String url, final ImageView imageView,
			final int width, final int height) {
		/* Create handler in UI thread. */
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String tag = imageViews.get(imageView);
				if (tag != null && tag.equals(url)) {
					if (msg.obj != null) {
						imageView.setImageBitmap((Bitmap) msg.obj);
					} else {
						imageView.setImageBitmap(placeholder);
						Log.d(null, "fail " + url);
					}
				}
			}
		};

		pool.submit(new Runnable() {
			public void run() {
				final Bitmap bmp = downloadBitmap(url, width, height);
				Message message = Message.obtain();
				message.obj = bmp;
				Log.d(null, "Item downloaded: " + url);

				handler.sendMessage(message);
			}
		});
	}

	public void loadBitmap(final String url, final ImageView imageView,
			final int width, final int height) {
		imageViews.put(imageView, url);
		Bitmap bitmap = getBitmapFromCache(url);
		// check in UI thread, so no concurrency issues
		if (bitmap != null) {
			Log.d(null, "Item loaded from cache: " + url);
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageBitmap(placeholder);
			queueJob(url, imageView, width, height);
		}
	}

	private Bitmap downloadBitmap(String url, int width, int height) {
		try {
			Bitmap bitmap = null;
			String url_temp = url;
			if (!DataConection.connection) {

				if (url_temp.lastIndexOf("?itok=") > 0)
					url_temp = url.substring(0, url_temp.lastIndexOf("?itok="));
				url_temp = url_temp.replace("/styles/listados_app/public", "");
                url_temp = Util.getRouteFolder() + Util.md5Sum(url_temp);
            }

			/* provoque le bug sur des thread
            les images ne sont jamais enregistrees donc impossible de faire un newFile(f) */

			/*if(url_temp.startsWith("file://")){
                String rutaCompleta = url_temp.replace("file://", "");
				File f = new File(rutaCompleta);

		        FileInputStream is = null;
		        try {

		            is = new FileInputStream(f);
					bitmap = BitmapFactory.decodeStream(is);
					is.close();

		        } catch (FileNotFoundException e) {
		            Log.d("error: ",String.format( "BitmapManager Exception file[%s]Not Found",rutaCompleta)); 
		        }


		        //bitmap = BitmapFactory.decodeStream(is, null, null);
			}else{*/

			/* les images ne sont pas enregistrees dans la memoire mais recuperees directement*/
            bitmap = BitmapFactory.decodeStream( (InputStream) new URL(url).getContent());
            //}

			if(width != 0 && height != 0){
				bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
			}


            cache.put(url, new SoftReference<Bitmap>(bitmap));
			return bitmap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}

