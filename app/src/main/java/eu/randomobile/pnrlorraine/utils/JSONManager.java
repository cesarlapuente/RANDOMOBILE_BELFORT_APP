package eu.randomobile.pnrlorraine.utils;

import org.json.JSONObject;

public abstract class JSONManager {
	
	/**
	 * Devuelve el valor de un par�metro del JSON.
	 * @param json JSON a parsear.
	 * @param parametro Par�metro a recuperar.
	 * @return
	 */
	public static String getString(final JSONObject json, final String parametro) {
		String valor = "";
		try {
			valor = json.getString(parametro);
			if (valor.equals("null")) {
				valor = "";
			}
		} catch(Exception ex) {}
		return valor;
	}
	
	public static int getInt(final JSONObject json, final String parametro) {
		int valor = -1;
		try {
			valor = json.getInt(parametro);
		} catch(Exception ex) {}
		return valor;
	}

}
