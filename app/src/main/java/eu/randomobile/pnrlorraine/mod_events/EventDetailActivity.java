package eu.randomobile.pnrlorraine.mod_events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.view.*;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.pnrlorraine.mod_global.map_layer_change.CapaBase;
import eu.randomobile.pnrlorraine.mod_global.model.Event;
import eu.randomobile.pnrlorraine.mod_global.model.Event.EventsInterface;
import eu.randomobile.pnrlorraine.mod_global.model.GeoPoint;
import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.PoiCategoryTerm;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.utils.GestorIntent;

public class EventDetailActivity extends Activity implements EventsInterface {
	public static final String PARAM_KEY_NID = "nid";
	public static final String PARAM_KEY_COORDENADAS = "coordenadas";
	public static final String PARAM_KEY_CATEGORIA_POI = "categoria_poi";

	MainApp app = null;
	String paramNid = "";
	MapView mapa = null;
	Callout callout = null;
	private Event evento = null;
	private GeoPoint coordenadas = null;
	private PoiCategoryTerm categoria = null;
	GraphicsOverlay capaGeometrias = null;

	RelativeLayout panelCargando;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_event__layout_detail_event);
		this.app = (MainApp) getApplication();
		// Recoger par‡metros
		paramNid = GestorIntent.getString(this.getIntent(), PARAM_KEY_NID);
		this.coordenadas = (GeoPoint) GestorIntent.getParcelableObject(
				this.getIntent(), PARAM_KEY_COORDENADAS);
		this.categoria = (PoiCategoryTerm) GestorIntent.getSerializableObject(
				this.getIntent(), PARAM_KEY_CATEGORIA_POI);
		inicializarForm();
		capturarControles();
		escucharEventos();
		inicializarMapa();
		recargarDatos();
	}

	private void capturarControles() {
		try {
			// Para el tratamiento del menu
			mapa = (MapView) findViewById(R.id.mapa);
			panelCargando = (RelativeLayout) findViewById(R.id.panelCargando);
		} catch (Exception ex) {
		}
	}

	private void escucharEventos() {
		final ImageMap imageMap = (ImageMap) findViewById(R.id.menu_detalle_evento);
		imageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
			@Override
			public void onImageMapClicked(int id, ImageMap imageMap) {
				imageMap.showBubble(id);
				switch (imageMap.getAreaAttribute(id, "name")) {
				case "HOME":
					cargaActivityHome();
					break;
				case "BACK":
					finish();
					break;
				}

			}

			@Override
			public void onBubbleClicked(int id) {
			}
		});

		LocationDisplay ls = mapa.getLocationDisplay();
		ls.setAutoPanMode(LocationDisplay.AutoPanMode.OFF);
		ls.startAsync();


	}

	@SuppressLint("NewApi")
	private void inicializarForm() {
		try {
			if (android.os.Build.VERSION.SDK_INT >= 11) {
				ActionBar ab = getActionBar();
				if (ab != null) {
					ab.hide();
				}
			}
			ImageMap imageMap = (ImageMap) findViewById(R.id.menu_detalle_evento);
			imageMap.setAttributes(true, false, (float) 1.0,
					"mapa_detalle_evento");
			imageMap.setImageResource(R.drawable.detalle_evento);
			panelCargando = (RelativeLayout) this
					.findViewById(R.id.panelCargando);
			if (coordenadas != null) {
				final ImageView iView = (ImageView) this
						.findViewById(R.id.imgViewPrincipal);
				Button boton = (Button) this.findViewById(R.id.btn_abrir_mapa);
				boton.setVisibility(LinearLayout.VISIBLE);
				boton.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						if (mapa.getVisibility() == View.GONE) {
							iView.setVisibility(View.GONE);
							mapa.setVisibility(View.VISIBLE);
							ponerPoiEnMapa();
						} else {
							iView.setVisibility(View.VISIBLE);
							mapa.setVisibility(View.GONE);
						}
					}
				});
			}
		} catch (Exception ex) {
		}
	}

	private void inicializarMapa() {
		ponerCapaBase();

		capaGeometrias = new GraphicsOverlay();
		mapa.getGraphicsOverlays().add(capaGeometrias);
		mapa.getMap().setMaxScale(5000);
		mapa.getMap().setMinScale(5000);
	}

	private void recargarDatos() {
		if (DataConection.hayConexion(this)) {
			// Si hay conexi—n, recargar los datos
			panelCargando.setVisibility(View.VISIBLE);
			Event.eventsInterface = this;
			Event.obtenerEvento(this.app, paramNid);
		} else {
			// Si no hay conexi—n a Internet
			Util.mostrarMensaje(
					this,
					getResources().getString(
							R.string.mod_global__sin_conexion_a_internet),
					getResources()
							.getString(
									R.string.mod_global__no_dispones_de_conexion_a_internet));
		}
	}

	private void cargaActivityHome() {
		Intent intent = new Intent(EventDetailActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	public void cargarEvento(final Event e) {
		try {
			if (e != null) {
				this.evento = e;
				TextView txt = (TextView) this.findViewById(R.id.txt_titulo);
				txt.setText(e.getTitle());
				if (e.getBody() != null) {
					txt = (TextView) this.findViewById(R.id.txt_descripcion);
					txt.setText(Html.fromHtml(e.getBody()));
				}
				txt = (TextView) this.findViewById(R.id.txt_desde);
				txt.setText(e.getDateStart());
				txt = (TextView) this.findViewById(R.id.txt_hasta);
				txt.setText(e.getDateEnd());

				if (e.getMainImage() != null && !e.getMainImage().equals("")) {
					DisplayMetrics displayMetrics = app.getResources()
							.getDisplayMetrics();
					RelativeLayout layout = (RelativeLayout) this
							.findViewById(R.id.layout_mapa);
					ImageView iView = (ImageView) this
							.findViewById(R.id.imgViewPrincipal);
					float dpHeight = layout.getHeight()
							/ displayMetrics.density;
					float dpWidth = displayMetrics.widthPixels
							/ displayMetrics.density;
					BitmapManager.INSTANCE.loadBitmap(e.getMainImage(), iView,
							(int) dpWidth, (int) dpHeight);
				}
			}
			panelCargando.setVisibility(View.GONE);
		} catch (Exception ex) {
		}
	}

	public void producidoErrorAlCargarPoi(String error) {
		Log.d("Milog", "producidoErrorAlCargarPoi: " + error);
		panelCargando.setVisibility(View.GONE);
		Util.mostrarMensaje(this,
				getResources().getString(R.string.mod_global__error),
				getResources().getString(R.string.mod_global__error));
		finish();
	}

	public void ponerCapaBase() {
		CapaBase capaSeleccionada = app.capaBaseSeleccionada;
		Log.d("Milog", "Identificador: " + capaSeleccionada.getIdentificador());
		Log.d("Milog", "Etiqueta: " + capaSeleccionada.getEtiqueta());

		Object capaBase = capaSeleccionada.getMapLayer();
		Log.d("Milog", "Object capaBase");

		// Corrección, para que no cambie la capa base cuando la seleccionada es
		// la misma que ya estaba (ahorra datos)
		LayerList capas = mapa.getMap().getOperationalLayers();
		if (capas != null) {
			Log.d("Milog", "capas no es nulo");
			if (capas.size() > 0) {

				Log.d("Milog", "Hay alguna capa");
				Object capa0 = capas.get(0);
				Log.d("Milog", "Tenemos capa0");
				// si la capa base seleccionada es del mismo tipo que la capa 0
				if (capaBase.getClass().getName()
						.equals(capa0.getClass().getName())) {
					Log.d("Milog",
							"La clase de la capa base es igual que la clase de la capa0");

					if (capaBase.getClass() == ArcGISTiledLayer.class) {
						Log.d("Milog", "capaBase es de tipo TiledMap");
						ArcGISTiledLayer capaBaseCasted = (ArcGISTiledLayer) capaBase;
						ArcGISTiledLayer capa0Casted = (ArcGISTiledLayer) capa0;
						String strUrlCapaBaseCasted = capaBaseCasted.getUri()
								.toString();
						String strUrlCapa0Casted = capa0Casted.getUri()
								.toString();
						if (strUrlCapaBaseCasted.equals(strUrlCapa0Casted)) {
							return;
						} else {
							mapa.getMap().getOperationalLayers().remove(0);
							Log.d("Milog",
									"PUNTO INTERMEDIO TILED: el mapa tiene "
											+ mapa.getGraphicsOverlays().size()
											+ " capas");
						}
					}
					Log.d("Milog", "La capa 0 es de clase "
							+ capa0.getClass().getName());
				} else {// si la capa base seleccionada no es del mismo tipo que
					// la capa 0

					if (capaBase.getClass() == ArcGISTiledLayer.class) {
						mapa.getMap().getOperationalLayers().remove(0);
					}
				}
				// btnAbrirCapas.setEnabled(true);
				if (capaBase.getClass() == ArcGISTiledLayer.class) {

					if (capas.size() > 0) {
						mapa.getMap().getOperationalLayers().add(0, (ArcGISTiledLayer) capaBase);
					} else {
						mapa.getMap().getOperationalLayers().add((ArcGISTiledLayer) capaBase);
					}

				} else {
					// otro tipo de capa
				}

				app.capaBaseSeleccionada = capaSeleccionada;
				Log.d("Milog", "El mapa tiene " + mapa.getMap().getOperationalLayers().size()
						+ " capas");
			}
		}
	}

	/**
	 * Muestra la posición del POI al que pertenece el evento en el mapa.
	 * 
	 * @param poi
	 *            POI a mostrar en el mapa.
	 */
	private void ponerPoiEnMapa() {
		try {
			capaGeometrias.getGraphics().clear();
			Point puntoProyectado = new Point(
					this.coordenadas.getLongitude(),
					this.coordenadas.getLatitude(), SpatialReference.create(102100));

			ArrayList<Object> geometrias = new ArrayList<Object>();
			geometrias.add(puntoProyectado);

			String icon = null;
			if (this.categoria != null && this.categoria.getIcon() != null) {
				icon = this.categoria.getIcon();
			}

			dibujarGeometrias(geometrias, this.evento.getTitle(), this.evento
					.getClass().getName(), this.evento.getNid(),
					this.categoria.getName(), icon);
			Object geomObj = geometrias.get(0);
			final Point punto = (Point) geomObj;
			double scale = 5000.0;
			mapa.setViewpointCenterAsync(punto, scale);
		} catch (Exception ex) {
		}
	}

	private void dibujarGeometrias(ArrayList<Object> geometrias,
			String paramNombre, String paramNombreClase, String paramNid,
			String paramCat, final String urlIcon) {

		int polygonFillColor = Color.rgb(55, 132, 218);
		int polygonBorderColor = Color.rgb(27, 87, 187);
		// int pointColor = Color.rgb(206, 240, 5);
		if (geometrias != null) {
			for (int j = 0; j < geometrias.size(); j++) {
				Object geomObj = geometrias.get(j);

				final HashMap<String, Object> attrs = new HashMap<String, Object>();
				attrs.put("clase", paramNombreClase);
				attrs.put("nid", paramNid);
				attrs.put("nombre", paramNombre);
				attrs.put("cat", paramCat);

				if (geomObj != null
						&& geomObj.getClass().getName()
								.equals(Polygon.class.getName())) {
					Polygon polygon = (Polygon) geomObj;
					SimpleFillSymbol sym = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID,
							polygonFillColor, null);
					//sym.setAlpha(100);
					sym.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, polygonBorderColor, 8));
					Graphic gr = new Graphic(polygon, sym);
					gr.getAttributes().putAll(attrs);
					capaGeometrias.getGraphics().add(gr);
				} else if (geomObj != null
						&& geomObj.getClass().getName()
								.equals(Point.class.getName())) {

					final Point point = (Point) geomObj;

					PictureMarkerSymbol sym = null;
					if (paramCat.equals("Chambre d'hôtes")
							|| paramCat.equals("Hôtellerie")
							|| paramCat.equals("Hébergement collectif")
							|| paramCat.equals("Hôtellerie de plein air")
							|| paramCat.equals("Meublé")
							|| paramCat.equals("Résidence")) {
						// Drawable d =
						// getResources().getDrawable(R.drawable.icono_hotel);
						// Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
						sym = new PictureMarkerSymbol((BitmapDrawable) getResources()
								.getDrawable(R.drawable.icono_hotel));
						// Scale it to 50 x 50
						// Drawable dr = new BitmapDrawable(getResources(),
						// Bitmap.createScaledBitmap(bitmap, 50, 50, true));
						// sym = new PictureMarkerSymbol(dr);
						// sym.setOffsetY(-px2dip(this.getApplicationContext(),d.getIntrinsicHeight()
						// / 2));
					} else if (paramCat.equals("Musée")
							|| paramCat.equals("Patrimoine Naturel")
							|| paramCat.equals("Site et Monument")
							|| paramCat.equals("Office de Tourisme")
							|| paramCat.equals("Parc et Jardin")) {
						sym = new PictureMarkerSymbol((BitmapDrawable)getResources()
								.getDrawable(R.drawable.icono_descubrir));
					} else if (paramCat.equals("Restauration"))
						sym = new PictureMarkerSymbol((BitmapDrawable)getResources()
								.getDrawable(R.drawable.icono_restaurante));
					else
						sym = new PictureMarkerSymbol((BitmapDrawable)getResources()
								.getDrawable(R.drawable.ic_launcher));

					Graphic gr = new Graphic(point, attrs, sym);
					capaGeometrias.getGraphics().add(gr);

					// Centrar en el extent de la capa
					// centrarEnExtentCapa(capaGeometrias);

				} else if (geomObj != null
						&& geomObj.getClass().getName()
								.equals(Polyline.class.getName())) {
					Polyline polyline = (Polyline) geomObj;

					int color = Color.BLUE;

					Graphic gr = new Graphic(polyline, attrs, new SimpleLineSymbol(
							SimpleLineSymbol.Style.SOLID, color, 10));
					capaGeometrias.getGraphics().add(gr);
				}
			}
		}
	}

	@Override
	public void cargarEventos(List<Event> events) {
	}

	@Override
	public void producidoErrorAlCargarListaEvents(String error) {
	}

	@Override
	public void producidoErrorAlCargarEvent(String error) {
	}

}
