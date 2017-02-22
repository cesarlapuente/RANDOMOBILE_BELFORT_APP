package eu.randomobile.pnrlorraine.mod_events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_events.adapter.EventsAdapter;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.model.Event;
import eu.randomobile.pnrlorraine.mod_global.model.Event.EventsInterface;
import eu.randomobile.pnrlorraine.mod_home.MainActivity;
import eu.randomobile.pnrlorraine.mod_imgmapping.ImageMap;
import eu.randomobile.pnrlorraine.utils.GestorIntent;

public class EventsListActivity extends Activity implements EventsInterface {
	public static final String PARAM_KEY_COORDENADAS = "coordenadas";
	public static final String PARAM_KEY_CATEGORIA_POI = "categoria_poi";
	public static final String PARAM_KEY_NID_POI_EVENTOS_ASOCIADOS = "nid_poi_asoc";
	private TextView emptyListView;
	private String paramNidPoi = null;

	// Array con los elementos que contendra
	private List<Event> arrayEvents = null;
	private RelativeLayout panelCargando;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_events__layout_lista_eventos);
		// Recoger par‡metros
		Bundle b = getIntent().getExtras();
		if (b != null) {
			paramNidPoi = b.getString(PARAM_KEY_NID_POI_EVENTOS_ASOCIADOS);
		}
		inicializarForm();
		capturarControles();
		escucharEventos();
		recargarForm();
		// test();
	}

	private void capturarControles() {
		panelCargando = (RelativeLayout) findViewById(R.id.panelCargando);
		emptyListView = (TextView) findViewById(android.R.id.empty);
	}

	private void escucharEventos() {
		try {
			ListView listaEventos = (ListView) this
					.findViewById(R.id.lista_eventos);
			listaEventos.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int index, long arg3) {
					Event eventPulsado = arrayEvents.get(index);
					cargarActividadEvento(eventPulsado.getNid());
				}

			});
			final ImageMap imageMap = (ImageMap) findViewById(R.id.menu_lista_eventos);
			imageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
				@Override
				public void onImageMapClicked(int id, ImageMap imageMap) {
					// when the area is tapped, show the name in a
					// text bubble
					imageMap.showBubble(id);
					switch (imageMap.getAreaAttribute(id, "name")) {
					case "HOME":
						Intent intent = new Intent(EventsListActivity.this,
								MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						break;
					case "BACK":
						finish();
						break;
					default:
						imageMap.showBubble(id);
					}

				}

				@Override
				public void onBubbleClicked(int id) {
				}
			});
		} catch (Exception ex) {
		}
	}

	@SuppressLint("NewApi")
	private void inicializarForm() {
		try {
			// poner estilos (fuente, color, ...)
			if (android.os.Build.VERSION.SDK_INT >= 11) {
				ActionBar ab = getActionBar();
				if (ab != null) {
					ab.hide();
				}
			}
			ImageMap imageMap = (ImageMap) findViewById(R.id.menu_lista_eventos);
			imageMap.setAttributes(true, false, (float) 1.0,
					"mapa_lista_eventos");
			if (this.paramNidPoi != null && !this.paramNidPoi.equals("")) {
				imageMap.setImageResource(R.drawable.eventos_poi);
			} else {
				imageMap.setImageResource(R.drawable.menu_ultimos_eventos);
			}
			panelCargando.setVisibility(View.GONE);
		} catch (Exception ex) {
		}
	}

	private void recargarForm() {
		recargarDatos();
	}

	private void recargarDatos() {
		if (DataConection.hayConexion(this)) {
			// Si hay conexi—n, recargar los datos
			panelCargando.setVisibility(View.VISIBLE);
			Event.eventsInterface = this;

			if (this.paramNidPoi != null) {
				Event.cargarListaEvents(this.getApplication(), this.paramNidPoi);
			} else {
				Event.cargarListaEvents(this.getApplication(), null);
			}

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

	/**
	 * Método de prueba.
	 */
//	private void test() {
//		try {
//			List<Event> eventos = new ArrayList<Event>();
//			Event evento = new Event();
//			evento.setTitle("Evento 1");
//			evento.setMainImage("http://www.arqhys.com/contenidos/fotos/contenidos/Restaurantes.jpg");
//			evento.setDateStart("26/11/2014");
//			evento.setDateEnd("26/11/2014");
//			evento.setBody("Descripción del evento");
//			eventos.add(evento);
//			evento = new Event();
//			evento.setTitle("FETE AU BORD DU LAC");
//			evento.setMainImage("http://media-cdn.tripadvisor.com/media/photo-s/01/a4/35/6f/restaurante-argentino.jpg");
//			evento.setDateStart("26/11/2014");
//			evento.setDateEnd("26/11/2014");
//			evento.setBody("Descripción del evento");
//			eventos.add(evento);
//			evento = new Event();
//			evento.setTitle("BALLADE CULINAIRE CHEZ ARTHUR");
//			evento.setMainImage("http://www.apolo.com.es/images/var/restaurante1.jpg");
//			evento.setDateStart("26/11/2014");
//			evento.setDateEnd("26/11/2014");
//			evento.setBody("Descripción del evento");
//			eventos.add(evento);
//			this.cargarEventos(eventos);
//		} catch (Exception ex) {
//		}
//	}

	public void cargarEventos(final List<Event> events) {
		try {
			if (events != null) {
				this.arrayEvents = events;
				ListView listaEventos = (ListView) this
						.findViewById(R.id.lista_eventos);
				EventsAdapter adapter = new EventsAdapter(this, arrayEvents);
				if (adapter != null) {
					listaEventos.setAdapter(adapter);
				}
			}
			else 
				emptyListView.setVisibility(View.VISIBLE);
		} catch (Exception ex) {
		}
		panelCargando.setVisibility(View.GONE);
	}

	/**
	 * Carga la actividad para el evento.
	 */
	private void cargarActividadEvento(final String idEvento) {
		try {
			Intent intent = new Intent(EventsListActivity.this,
					EventDetailActivity.class);
			intent.putExtra(EventDetailActivity.PARAM_KEY_NID, idEvento);
			Parcelable coordenadas = GestorIntent.getParcelableObject(
					this.getIntent(), PARAM_KEY_COORDENADAS);
			if (coordenadas != null) {
				intent.putExtra(EventDetailActivity.PARAM_KEY_COORDENADAS,
						coordenadas);
			}
			Serializable categoria = GestorIntent.getSerializableObject(
					this.getIntent(), PARAM_KEY_CATEGORIA_POI);
			if (categoria != null) {
				intent.putExtra(EventDetailActivity.PARAM_KEY_CATEGORIA_POI,
						categoria);
			}
			startActivity(intent);
		} catch (Exception ex) {
		}
	}

	@Override
	public void producidoErrorAlCargarListaEvents(String error) {
		Log.d("Milog", "producidoErrorAlCargarListaEvents");
		panelCargando.setVisibility(View.GONE);
	}

	@Override
	public void producidoErrorAlCargarEvent(String error) {
	}

	@Override
	public void cargarEvento(Event event) {
	}

}
