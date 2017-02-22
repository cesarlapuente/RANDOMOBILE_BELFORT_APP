package eu.randomobile.pnrlorraine.mod_grtgaz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;

public class GRTGazActivity extends Activity {
	private boolean inicio = true;
	private ImageMap mImageMap = null;
	private ImageMap mImageMap2 = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_grtgaz_activity);
		inicializarElementos();
		capturarEventos();
	}

	private void inicializarElementos() {
		try {
			mImageMap = (ImageMap) findViewById(R.id.map_menu_patrocinador);
			mImageMap.setAttributes(true, true, (float) 1.0,
					"mapa_patrocinador");
			mImageMap
					.setImageResource(R.drawable.menu_patrocinador_presentation);
			mImageMap2 = (ImageMap) findViewById(R.id.map_menu_patrocinador_2);
			mImageMap2.setAttributes(true, true, (float) 1.0,
					"mapa_patrocinador");
			mImageMap2
					.setImageResource(R.drawable.menu_patrocinador_partenaire);
			cargarTextoWebView(R.string.texto_patrocinador);
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
							} else if (attribute.equals("PRESENTATION")) {
								cargarSeccion(true);
							} else if (attribute.equals("PARTENAIRE")) {
								cargarSeccion(false);
							}
						} catch (Exception ex) {
						}
					}

					@Override
					public void onBubbleClicked(int id) {
					}
				});
		mImageMap2.addOnImageMapClickedHandler(crearListener());
	}

	private ImageMap.OnImageMapClickedHandler crearListener() {
		ImageMap.OnImageMapClickedHandler listener = null;
		try {
			listener = new ImageMap.OnImageMapClickedHandler() {
				@Override
				public void onImageMapClicked(int id, ImageMap imageMap) {
					try {
						final String attribute = mImageMap.getAreaAttribute(id,
								"name");
						if (attribute.equals("HOME")) {
							cargaActivityHome();
						} else if (attribute.equals("BACK")) {
							finish();
						} else if (attribute.equals("PRESENTATION")) {
							cargarSeccion(true);
						} else if (attribute.equals("PARTENAIRE")) {
							cargarSeccion(false);
						}
					} catch (Exception ex) {
					}
				}

				@Override
				public void onBubbleClicked(int id) {
				}
			};
		} catch (Exception ex) {
		}
		return listener;
	}

	/**
	 * Carga la sección correspondiente al botón pulsado.
	 * 
	 * @param presentation
	 *            Sirve para saber qué botón se ha pulsado. true: presentation,
	 *            false: Partenaire.
	 */
	private void cargarSeccion(final boolean presentation) {
		try {
			if (presentation && !this.inicio) {
				this.inicio = true;
				this.mImageMap2.setVisibility(LinearLayout.INVISIBLE);
				this.cargarTextoWebView(R.string.texto_patrocinador);
			} else if (!presentation && this.inicio) {
				this.mImageMap2.setVisibility(LinearLayout.VISIBLE);
				this.inicio = false;
				this.cargarTextoWebView(R.string.texto_partenaire);
			}
		} catch (Exception ex) {
		}
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
		Intent intent = new Intent(GRTGazActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
