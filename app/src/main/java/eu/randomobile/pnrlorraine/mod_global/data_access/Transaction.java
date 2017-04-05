package eu.randomobile.pnrlorraine.mod_global.data_access;


import eu.randomobile.pnrlorraine.MainApp;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class Transaction {
	
	SQLiteDatabase db = null;
	
	String nombreBBDD = null;

	MainApp app;
	
	
	// Constructor. Se le pasa el nombre de la BBDD que se quiere abrir
	public Transaction(String bdName, Application application){
		this.nombreBBDD = bdName;
		app = (MainApp) application;
	}
	
	
	
	
	// Abre la BD en modo lectura escritura
	public void abrirBBDD_LecturaEscritura() {
		try{
			db = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + app.CARPETA_SD + "/" + nombreBBDD, null, SQLiteDatabase.OPEN_READWRITE);
		}catch (Exception e) {
			Log.d("Milog", "Excepcion al abrirBBDD_LecturaEscritura: " + e.toString());
		}
		
	}
	
	
	// Comienza una transaccion. Debe estar abierta la BD previamente
	public void iniciarTransaccion() {
		try{
			//db.execSQL("BEGIN Transaction;");
			db.beginTransaction();
		}catch (Exception e) {
			Log.d("Milog", "Excepcion al iniciarTransaccion: " + e.toString());
		}
		
	}

	// Lanza una consulta de acci—n dentro de una transaccion
	public void ejecutarSentenciaSQL(String sentencia) {
		try{
			db.execSQL(sentencia);
		}catch (Exception e) {
			Log.d("Milog", "Excepcion al ejecutarSentenciaSQL: " + e.toString());
		}
		
	}
	
	// Finaliza la transaccion
	public void finalizarTransaccion() {
		try{	
			//db.execSQL("COMMIT;");
			db.setTransactionSuccessful();
			//db.execSQL("END Transaction;");
			db.endTransaction();
		}catch(Exception ex){
			Log.d("Milog", "Excepcion al confirmarTransaccion: " + ex.toString());
		}
	}


	public void cerrarBD(){
		try{
			if (db != null){
				db.close();
			}
		}catch(Exception ex){
			Log.d("Milog", "Excepcion al cerrar BD: " + ex.toString());
		}
	}
	
}
