package eu.randomobile.pnrlorraine.mod_home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ScrollView;
import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;

public class FFRPActivity extends Activity {
	ImageMap mImageMap;
	MainApp app;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_home__menu_ffrp);
		this.app = (MainApp) getApplication();
		this.cargarTextoWebView(R.string.ffrp_text);
		// find the image map in the view
		mImageMap = (ImageMap) findViewById(R.id.map_menuFFRP);
		mImageMap.setAttributes(true, false, (float) 1.0, "mapa_FFRP");
		mImageMap.setImageResource(R.drawable.menu_ffrp);
		mImageMap
				.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
					@Override
					public void onImageMapClicked(int id, ImageMap imageMap) {
						mImageMap.showBubble(id);
						if (mImageMap.getAreaAttribute(id, "name").equals(
								"HOME")) {
							cargaActivityHome();
						} else if (mImageMap.getAreaAttribute(id, "name")
								.equals("BACK")) {
							finish();
						}
					}

					@Override
					public void onBubbleClicked(int id) {
						// react to info bubble for area being tapped

					}
				});
	}

	private void cargaActivityHome() {
		Intent intent = new Intent(FFRPActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	protected void onResume() {
		super.onResume();
		mImageMap.mBubbleMap.clear();
		mImageMap.postInvalidate();
	}

	/**
	 * Carga la informaci�n correspondiente en el web view.
	 * 
	 * @param stringId
	 *            ID del texto a cargar.
	 */
	private void cargarTextoWebView(final int stringId) {
		try {
			String descripcion = this.getString(stringId);
			WebView webView = (WebView) this.findViewById(R.id.webview_descripcion);
			webView.loadDataWithBaseURL("file:///android_asset/", descripcion, "text/html", "utf-8", null);
			ScrollView scroll = (ScrollView) this.findViewById(R.id.scrollDescripcion);
			scroll.fullScroll(ScrollView.FOCUS_UP);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}