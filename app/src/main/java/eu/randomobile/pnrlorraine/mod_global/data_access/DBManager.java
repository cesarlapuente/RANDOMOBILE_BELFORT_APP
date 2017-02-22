package eu.randomobile.pnrlorraine.mod_global.data_access;

import java.util.ArrayList;

import eu.randomobile.pnrlorraine.MainApp;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

public class DBManager {

	// Realiza una consulta a la BBDD
    public static Cursor consultar(String consulta, Application application) {
    	MainApp app = (MainApp)application;
    	SQLiteDatabase db = null;
    	Cursor c = null;
		try {
			db = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + app.CARPETA_SD + "/" + app.NOMBRE_FICH_BBDD, null, SQLiteDatabase.OPEN_READONLY);
            
			// Lanzar la consulta
			c = db.rawQuery(consulta, null);
            
			if(c != null){
				// Controlamos que el cursor tenga al menos 1 registro
	            if(c.getCount() > 0){
	            	// Situamos el puntero en el primer registro 
	            	c.moveToFirst();
	            }
			}

		}catch(SQLiteException ex){
			Log.d("Milog","Error en metodo r(): " + ex.getMessage());
        } finally {
            if (db != null){
                db.close();
            }
        }
		return c;
    }
    
    
    // Devuelve un array de dos dimensiones con todos los elementos del cursor
    public static ArrayList<ArrayList<String>> getArrayListFromCursor(Cursor c){
    	ArrayList<ArrayList<String>> rs = new ArrayList<ArrayList<String>>();
    	if(c != null){
    		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
        	    // The Cursor is now set to the right position
        		ArrayList<String> row = new ArrayList<String>();
        		for(int i=0 ; i<c.getColumnCount() ; i++){
        			String campo = c.getString(i);
        			row.add(campo);
        		}
        		rs.add(row);
        	}
    	}
    	return rs;
    }

}