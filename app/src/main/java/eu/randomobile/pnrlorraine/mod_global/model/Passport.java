package eu.randomobile.pnrlorraine.mod_global.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.util.Log;

import eu.randomobile.pnrlorraine.MainApp;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class Passport {

	private String id;
	private String nombre;
	private String apellido;
	private String nick;
	private int puntos;
	private String ranking;
	private String ccaa;
	private String pais;
	private ArrayList<Stamp> arrItemStamps;
	private ArrayList<Badge> arrItemBadges;


	public Passport(){
		
	}
	public Passport(String id, String nombre, String apellido, String nick, int puntos){
		this.id = id;
		this.nombre = nombre;
		this.apellido = apellido;
		this.nick = nick;
		this.puntos = puntos;
		
		
	}

	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id = id;
	}
	
	public String getNombre(){
		return nombre;
	}
	public void setNombre(String nombre){
		this.nombre = nombre;
	}

	public String getApellido(){
		return apellido;
	}
	public void setApellido(String apellido){
		this.apellido = apellido;
	}

	public String getNick(){
		return nick;
	}
	public void setNick(String nick){
		this.nick = nick;
	}
	
	public int getPuntos() {
		return puntos;
	}
	public void setPuntos(int puntos) {
		this.puntos = puntos;
	}
	
	public ArrayList<Stamp> getArrItemStamps() {
		return arrItemStamps;
	}
	public void setArrItemStamps(ArrayList<Stamp> arrItemStamps) {
		this.arrItemStamps = arrItemStamps;
	}
	
	public ArrayList<Badge> getArrItemBadges() {
		return arrItemBadges;
	}
	public void setArrItemBadges(ArrayList<Badge> arrItemBadges) {
		this.arrItemBadges = arrItemBadges;
	}
	
	public String getRanking() {
		return ranking;
	}
	public void setRanking(String ranking) {
		this.ranking = ranking;
	}
	public String getCcaa() {
		return ccaa;
	}
	public void setCcaa(String ccaa) {
		this.ccaa = ccaa;
	}
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}

	
	
	
	
	// Interface para comunicarse con las llamadas asíncronas
	public static PassportsInterface passportsInterface;
	public static interface PassportsInterface {
		public void seCargoPassport(Passport passport);
		public void producidoErrorAlCargarPassport(String error);
	}
	
	
	
	
	public static void cargarPassport(Application application, final String nid){
		
		Log.d("Milog", "Llamada al servicio");
		
		MainApp app = (MainApp)application;
		
		app.clienteDrupal.customMethodCallPost("basic/passport", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de cargar un passport: " + response);
				
				Passport passport = null;
				
				if(response != null && !response.equals("")){
					
					try {
	                	JSONObject dicRes = new JSONObject(response);
	                	if(dicRes != null){
	                		
	                		passport = new Passport();
	                		Log.d("Milog", "PASSPORT UID => : " + dicRes.getString("uid"));
	                		Log.d("Milog", "PASSPORT NOMBRE => : " + dicRes.getString("first_name"));
	                		Log.d("Milog", "PASSPORT APELLIDOS => : " + dicRes.getString("last_name"));
	                		Log.d("Milog", "PASSPORT NICK => : " + dicRes.getString("nick"));
	                		Log.d("Milog", "PASSPORT POINTS => : " + dicRes.getString("points"));
	                		
	                		passport.setId(dicRes.getString("uid"));
	                		passport.setNombre(dicRes.getString("first_name"));
	                		passport.setApellido(dicRes.getString("last_name"));
	                		passport.setNick(dicRes.getString("nick"));
	                		passport.setPuntos(dicRes.getInt("points"));
	                		
	                		
	                		Object objStamps = dicRes.get("stamps");
	                		if(objStamps != null && objStamps.getClass().getName().equals(JSONArray.class.getName())){
	                			JSONArray arrStamps = (JSONArray)objStamps;
	                			
	                			Log.d("Milog", "ELEMENTOS ArrPassports => : " + arrStamps.length());
	                			
	                			ArrayList<Stamp> miArrItemStamps = new ArrayList<Stamp>();
	                			for(int i = 0; i < arrStamps.length(); i++){
	                				Object objStamp = arrStamps.get(i);
	                				if(objStamp != null && objStamp.getClass().getName().equals(JSONObject.class.getName())){
	                					JSONObject dicStamp = (JSONObject)objStamp;
	                					Stamp miItemStamp = new Stamp();
	                					Log.d("Milog", "NID => : " + dicStamp.getString("nid"));
	                					miItemStamp.setId(dicStamp.getString("nid"));
	                					Log.d("Milog", "TITLE => : " + dicStamp.getString("title"));
	                					miItemStamp.setNombre(dicStamp.getString("title"));
	                					Log.d("Milog", "DESC => : " + dicStamp.getString("body"));
	                					miItemStamp.setDescripcion(dicStamp.getString("body"));
	                					Log.d("Milog", "IMAGEN => : " + dicStamp.getString("image"));
	                					miItemStamp.setImagenDestacada(dicStamp.getString("image"));
	                					Log.d("Milog", "HECHO => : " + dicStamp.getString("done"));
	                					miItemStamp.setHecho(dicStamp.getBoolean("done"));
	                					miArrItemStamps.add(miItemStamp);
	                				}
	                			}
	                			passport.setArrItemStamps(miArrItemStamps);
	                			Log.d("Milog", "TOTAL DE SELLOS : " + passport.getArrItemStamps().size());
	                		}

	                		Object objBadges = dicRes.get("badges");
	                		if(objBadges != null && objBadges.getClass().getName().equals(JSONArray.class.getName())){
	                			JSONArray arrBadges = (JSONArray)objBadges;
	                			
	                			Log.d("Milog", "ELEMENTOS ArrWinners => : " + arrBadges.length());
	                			
	                			ArrayList<Badge> miArrItemBadges = new ArrayList<Badge>();
	                			for(int i = 0; i < arrBadges.length(); i++){
	                				Object objBadge = arrBadges.get(i);
	                				if(objBadge != null && objBadge.getClass().getName().equals(JSONObject.class.getName())){
	                					JSONObject dicBadge = (JSONObject)objBadge;
	                					Badge miItemBadge = new Badge();
	                					Log.d("Milog", "NID => : " + dicBadge.getString("nid"));
	                					miItemBadge.setNid(dicBadge.getString("nid"));
	                					Log.d("Milog", "TITLE => : " + dicBadge.getString("title"));
	                					miItemBadge.setTitle(dicBadge.getString("title"));
	                					Log.d("Milog", "DESC => : " + dicBadge.getString("body"));
	                					miItemBadge.setBody(dicBadge.getString("body"));
	                					Log.d("Milog", "IMAGEN => : " + dicBadge.getString("image"));
	                					miItemBadge.setImage(dicBadge.getString("image"));
	                					miItemBadge.setDone(dicBadge.getBoolean("done"));
	                					miArrItemBadges.add(miItemBadge);
	                				}
	                			}
	                			passport.setArrItemBadges(miArrItemBadges);
	                			Log.d("Milog", "TOTAL DE MEDALLAS : " + passport.getArrItemBadges().size());
	                		}
	                		
	                		
	                		
	                		
	                		
	                		
	                		Log.d("Milog", "VALORES DEL ITEM PASSPORTS CORRECTOS!!!");


	                		// Informar al delegate
	                		if(Passport.passportsInterface != null){
	                			Passport.passportsInterface.seCargoPassport(passport);
	                			return;
	                		}
	                		
	                	}
	                } catch (Exception e) {
						Log.d("Milog", "Excepcion get passport: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(Passport.passportsInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error");
	    			Passport.passportsInterface.producidoErrorAlCargarPassport("Error al cargar award");
	    		}
			}

			public void onFailure(Throwable error) {
				// Informar al delegate
				if(Passport.passportsInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error. " + error.toString());
	    			Passport.passportsInterface.producidoErrorAlCargarPassport(error.toString());
	    		}
			}
		}, 
		null);
		
	}
	
	
	
}