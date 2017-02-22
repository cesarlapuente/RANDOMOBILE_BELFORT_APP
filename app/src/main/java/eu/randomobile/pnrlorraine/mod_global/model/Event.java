package eu.randomobile.pnrlorraine.mod_global.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import eu.randomobile.pnrlorraine.MainApp;

public class Event {
	private String nid;
	private String title;
	private String body;
	private String dateStart;
	private String dateEnd;
	private String mainImage;
	private String nidPoiRelated;

	// Interface para comunicarse con las llamadas asíncronas
	public static EventsInterface eventsInterface;

	public static interface EventsInterface {
		public void cargarEventos(List<Event> events);

		public void producidoErrorAlCargarListaEvents(String error);

		public void cargarEvento(Event event);

		public void producidoErrorAlCargarEvent(String error);
	}

	public static void cargarListaEvents(Application application, String nidPoi) {
		HashMap<String, String> params = new HashMap<String, String>();
		if (nidPoi != null && !nidPoi.equals("")) {
			params.put("nid", nidPoi);
		}
		Log.d("Milog",
				"Parametros enviados a event/get_list: " + params.toString());
		MainApp app = (MainApp) application;
		app.clienteDrupal.customMethodCallPost("event/get_list",
				new AsyncHttpResponseHandler() {
					public void onSuccess(String response) {

						Log.d("Milog", "Respuesta de cargar eventos: "
								+ response);

						List<Event> listaEvents = null;

						if (response != null && !response.equals("")) {

							try {
								JSONArray arrayRes = new JSONArray(response);
								if (arrayRes != null) {
									if (arrayRes.length() > 0) {
										Log.d("Milog",
												"array devuelto contiene al menos 1 elemento");
										listaEvents = new ArrayList<Event>();
									}

									for (int i = 0; i < arrayRes.length(); i++) {
										Object recObj = arrayRes.get(i);
										if (recObj != null) {
											if (recObj
													.getClass()
													.getName()
													.equals(JSONObject.class
															.getName())) {
												JSONObject recDic = (JSONObject) recObj;
												String nid = recDic
														.getString("nid");
												String title = recDic
														.getString("title");
												String body = recDic
														.getString("body");
												String image = recDic
														.getString("image");
												String dateStart = null;
												String dateEnd = null;

												Object dateObj = recDic
														.get("date");
												if (dateObj != null
														&& dateObj
																.getClass()
																.getName()
																.equals(JSONObject.class
																		.getName())) {
													JSONObject dicDate = (JSONObject) dateObj;
													dateStart = dicDate
															.getString("start");
													dateEnd = dicDate
															.getString("end");
												}

												Event event = new Event();
												event.setNid(nid);
												event.setTitle(title);
												event.setBody(body);
												event.setMainImage(image);
												event.setDateStart(dateStart);
												event.setDateEnd(dateEnd);

												listaEvents.add(event);
											}
										}
									}

									// Informar al delegate
									if (Event.eventsInterface != null) {
										Event.eventsInterface
												.cargarEventos(listaEvents);
										return;
									}

								}

							} catch (Exception e) {
								Log.d("Milog", "Excepcion en lista eventos: "
										+ e.toString());
							}
						}

						// Informar al delegate
						if (Event.eventsInterface != null) {
							Log.d("Milog",
									"Antes de informar al delegate de un error");
							Event.eventsInterface
									.producidoErrorAlCargarListaEvents("Error al cargar lista de pois");
						}

					}

					public void onFailure(Throwable error) {
						// Informar al delegate
						if (Event.eventsInterface != null) {
							Log.d("Milog",
									"Antes de informar al delegate de un error: "
											+ error.toString());
							Event.eventsInterface
									.producidoErrorAlCargarListaEvents(error
											.toString());
						}
					}
				}, params);

	}

	public static void obtenerEvento(Application application, String nid) {

		MainApp app = (MainApp) application;

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nid", nid);

		app.clienteDrupal.customMethodCallPost("event/get_item",
				new AsyncHttpResponseHandler() {
					public void onSuccess(String response) {
						Log.d("Milog", "Exito al cargar event: " + response);

						if (response != null && !response.equals("")) {

							try {
								JSONObject dicRes = new JSONObject(response);
								if (dicRes != null) {

									String nid = dicRes.getString("nid");
									String title = dicRes.getString("title");
									String body = dicRes.getString("body");
									String image = dicRes.getString("image");
									String poi = dicRes.getString("poi");
									String dateStart = null;
									String dateEnd = null;

									Object dateObj = dicRes.get("date");
									if (dateObj != null
											&& dateObj
													.getClass()
													.getName()
													.equals(JSONObject.class
															.getName())) {
										JSONObject dicDate = (JSONObject) dateObj;
										dateStart = dicDate.getString("start");
										dateEnd = dicDate.getString("end");
									}

									Event event = new Event();
									event.setNid(nid);
									event.setTitle(title);
									event.setBody(body);
									event.setMainImage(image);
									event.setDateStart(dateStart);
									event.setDateEnd(dateEnd);
									event.setNidPoiRelated(poi);

									// Informar al delegate
									if (Event.eventsInterface != null) {
										Event.eventsInterface
												.cargarEvento(event);
										return;
									}

								}

							} catch (Exception e) {
								Log.d("Milog",
										"Excepcion cargar event: "
												+ e.toString());
							}
						}

						// Informar al delegate
						if (Event.eventsInterface != null) {
							Log.d("Milog",
									"Antes de informar al delegate de un error");
							Event.eventsInterface
									.producidoErrorAlCargarEvent("Error al cargar event");
						}

					}

					public void onFailure(Throwable error) {
						// Informar al delegate
						if (Event.eventsInterface != null) {
							Log.d("Milog",
									"Antes de informar al delegate de un error: "
											+ error.toString());
							Event.eventsInterface
									.producidoErrorAlCargarEvent(error
											.toString());
						}
					}
				}, params);

	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getNid() {
		return nid;
	}

	public void setNid(String nid) {
		this.nid = nid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMainImage() {
		return mainImage;
	}

	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
	}

	public String getDateStart() {
		return dateStart;
	}

	public void setDateStart(String dateStart) {
		this.dateStart = dateStart;
	}

	public String getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(String dateEnd) {
		this.dateEnd = dateEnd;
	}

	public String getNidPoiRelated() {
		return nidPoiRelated;
	}

	public void setNidPoiRelated(String nidPoiRelated) {
		this.nidPoiRelated = nidPoiRelated;
	}

}
