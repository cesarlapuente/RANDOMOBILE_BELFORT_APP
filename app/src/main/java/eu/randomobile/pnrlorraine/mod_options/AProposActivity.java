package eu.randomobile.pnrlorraine.mod_options;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;

public class AProposActivity extends Activity {
	private boolean inicio = true;
	private ImageMap mImageMap = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_options__a_propos_activity);
		inicializarElementos();
		capturarEventos();
	}

	private void inicializarElementos() {
		try {
			mImageMap = (ImageMap) findViewById(R.id.menu_a_propos);
			mImageMap.setAttributes(true, true, (float) 1.0,
					"mapa_patrocinador");
			mImageMap
					.setImageResource(R.drawable.menu_a_propos);
			cargarTextoWebView(R.string.texto_a_propos);
		} catch (Exception ex) {
		}
	}

	private void capturarEventos() {
		mImageMap
				.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
					@Override
					public void onImageMapClicked(int id, ImageMap imageMap) {
						try {
							final String attribute = mImageMap
									.getAreaAttribute(id, "name");
							if (attribute.equals("HOME")) {
								cargaActivityHome();
							} else if (attribute.equals("BACK")) {
								finish();
							}
						} catch (Exception ex) {
						}
					}

					@Override
					public void onBubbleClicked(int id) {
					}
				});
	}

	/**
	 * Carga la información correspondiente en el web view.
	 * 
	 * @param stringId
	 *            ID del texto a cargar.
	 */
	private void cargarTextoWebView(final int stringId) {
		try {
			WebView webView = (WebView) this
					.findViewById(R.id.webview_descripcion);
			String descripcion = this.getString(stringId);
			webView.loadDataWithBaseURL("file:///android_asset/", descripcion,
					"text/html", "utf-8", null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Carga el home de la aplicación eliminando todas las actividades que
	 * estuvieran abiertas hasta el momento.
	 */
	private void cargaActivityHome() {
		Intent intent = new Intent(AProposActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
