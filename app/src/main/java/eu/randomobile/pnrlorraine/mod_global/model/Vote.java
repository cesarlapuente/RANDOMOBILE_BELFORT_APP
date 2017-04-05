package eu.randomobile.pnrlorraine.mod_global.model;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Application;
import android.util.Log;

import eu.randomobile.pnrlorraine.MainApp;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class Vote {
	private String entity_id;
	private int numVotes;
	private int value;
	
	public String getEntity_id() {
		return entity_id;
	}
	public void setEntity_id(String entity_id) {
		this.entity_id = entity_id;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
	
	
	// Interface para comunicarse con las llamadas asíncronas
	public static VoteInterface voteInterface;
	public static interface VoteInterface {
		public void seEnvioVoto(int countVotes, int avgResults);
		public void producidoErrorAlVotar(String error);
		public void seCargoVoto(int countVotes, int avgResults);
		public void producidoErrorAlCargarVoto(String error);
	}
	
	
	
	public static void setVote(Application application, String entity_id, int value){

		MainApp app = (MainApp)application;
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nid", entity_id);
		params.put("value", String.valueOf(value) );
		params.put("key", app.drupalSecurity.encrypt(entity_id));

		

		app.clienteDrupal.customMethodCallPost("vote/set", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de set votes: " + response);
				if(response != null && !response.equals("")){
					try {
	                	JSONObject dicRes = new JSONObject(response);
	                	if(dicRes != null){
	                		
	                		boolean success = dicRes.getBoolean("success");
	                		int error = dicRes.getInt("error");
	                		String numVotosStr = dicRes.getString("count");
	                		String resultsAvgStr = dicRes.getString("results");
	                		
	                		int numVotos = 0;
                			int resultsAvg = 0;
                			if(numVotosStr != null && !numVotosStr.equals("") && !numVotosStr.equals("null")){
                				numVotos = (int) Float.parseFloat( numVotosStr );
                			}
                			if(resultsAvgStr != null && !resultsAvgStr.equals("") && !resultsAvgStr.equals("null")){
                				resultsAvg = (int) Float.parseFloat( resultsAvgStr );
                			}
	                		
	                		
	                		
	                		if(error == 0){
	                			// Si error == 0, todo ha ido de puta madre
	                			if(success){
		                			// Esto es que ha respondido todo bien
	                				// Informar al delegate
	    	                		if(Vote.voteInterface != null){
	    	                			Vote.voteInterface.seEnvioVoto(numVotos, resultsAvg);
	    	                			return;
	    	                		}
		                		}else{
		                			// Esto es que ha respondido muy mal
	                				// Informar al delegate
	    	                		if(Vote.voteInterface != null){
	    	                			Vote.voteInterface.producidoErrorAlVotar("Error al votar");
	    	                			return;
	    	                		}
		                		}
	                		}else{
	                			
	                			String strError = "";
	                			switch(error){
	                			case -1:
	                				strError = "Fallo en la Key de Seguridad";
	                				break;
	                			case -2:
	                				strError = "Fallo el NID";
	                				break;
	                			case -3:
	                				strError = "Fallo el valor";
	                				break;
	                			case -4:
	                				strError = "Fallo en voto";
	                				break;
	                			}
	                			
	                			// Informar al delegate
	            	    		if(Vote.voteInterface != null){
	            	    			Log.d("Milog", "Antes de informar al delegate de un error: " + error);
	            	    			Vote.voteInterface.producidoErrorAlVotar(strError);
	            	    			return;
	            	    		}
	                		}
	                		
	                	}
	                	

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion al hacer set_vote: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(Vote.voteInterface != null){
	    			Vote.voteInterface.producidoErrorAlVotar("Error al recoger respuesta");
	    			return;
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
	    		if(Vote.voteInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error: " + error);
	    			Vote.voteInterface.producidoErrorAlVotar(error.toString());
	    			return;
	    		}
			}
		}, 
		params);
	}
	
	
	public static void getVote(Application application, String entity_id, String uid){

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nid", entity_id);
		
		if(uid != null && !uid.equals("")){
			params.put("uid", uid );
		}

		MainApp app = (MainApp)application;

		app.clienteDrupal.customMethodCallPost("vote/get", new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				
				Log.d("Milog", "Respuesta de get votes: " + response);
				if(response != null && !response.equals("")){
					try {
	                	JSONObject dicRes = new JSONObject(response);
	                	if(dicRes != null){

	                		String numVotosStr = dicRes.getString("count");
	                		String resultsAvgStr = dicRes.getString("results");
	                		
	                		int numVotos = 0;
                			int resultsAvg = 0;
                			if(numVotosStr != null && !numVotosStr.equals("") && !numVotosStr.equals("null")){
                				numVotos = (int) Float.parseFloat( numVotosStr );
                			}
                			if(resultsAvgStr != null && !resultsAvgStr.equals("") && !resultsAvgStr.equals("null")){
                				resultsAvg = (int) Float.parseFloat( resultsAvgStr );
                			}
	                		
	                		// Informar al delegate
	            	    	if(Vote.voteInterface != null){
	            	    		Vote.voteInterface.seCargoVoto(numVotos, resultsAvg);
	            	    		return;
	            	    	}
	                	}

	                } catch (Exception e) {
						Log.d("Milog", "Excepcion al hacer get_vote: " + e.toString());
					}
				}
				
				// Informar al delegate
	    		if(Vote.voteInterface != null){
	    			Vote.voteInterface.producidoErrorAlCargarVoto("Error al recoger respuesta");
	    			return;
	    		}
				
				
				
			}
			
			public void onFailure(Throwable error) {
				// Informar al delegate
	    		if(Vote.voteInterface != null){
	    			Log.d("Milog", "Antes de informar al delegate de un error: " + error);
	    			Vote.voteInterface.producidoErrorAlCargarVoto(error.toString());
	    			return;
	    		}
			}
		}, 
		params);
	}
	public int getNumVotes() {
		return numVotes;
	}
	public void setNumVotes(int numVotes) {
		this.numVotes = numVotes;
	}
	
	
	
}
