package eu.randomobile.pnrlorraine.mod_global.environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_global.data_access.DBManager;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

public class ExternalStorage {

	// Comprueba si tiene almacenamiento externo montado (interno o SD)
	public static boolean tieneSD(Context contexto){
		// Comprobamos la ruta hasta la raiz de la tarjeta SD
		File dirSD = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		if(dirSD.exists()){
			return true; //tiene tarjeta SD
		}else{
			return false; //no tiene tarjeta SD
		}
	}
	
	
	
	
	
	/* Método que comprueba la estructura de carpetas 
	   en caso de que se haya comprobado que el dispositivo
	   tiene insertada una tarjeta SD
	 */
	public static void comprobarDirectorioAppSD(Application application){
		
		MainApp app = (MainApp)application;
		
		boolean tieneFicheroBD = false;
		
		/*comprobamos que exista la carpeta de la aplicaci—n en la SD y  
		  el fichero de la BBDD */
		File directorioAppSD = new File(Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD);
		
		if(directorioAppSD.exists()){
			// Existe el directorio de la aplicaci—n en la SD
			
			// Establecemos la ruta inicial en la raíz de la tarjeta
			File rutaRaiz = new File(Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/");
			
			// Guardamos los archivos que hay dentro de la ruta
			String[] archivos = rutaRaiz.list();

			// Recorremos los archivos para comprobar cuales son directorios
			for(int i = 0; i < archivos.length; i++){

				// Creamos un File para poder comprobar si ese archivo es realmente un directorio o no
				File fileTmp = new File(Environment.getExternalStorageDirectory() + "/" + archivos[i].toString());
				
				if(!fileTmp.isDirectory()){
					if(archivos[i].equals(app.NOMBRE_FICH_BBDD)){
						tieneFicheroBD = true;
					}
				}

			}
		}else{
			Log.d("Milog", "No Existe el directorio de la app. Voy a recrearlo");
			// No existe el directorio de la app en la SD. Hay que crearlo
			File f = new File(Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD);
			f.mkdir();
		}
		
		// Fichero de BBDDD
		if(!tieneFicheroBD){
			Log.d("Milog", "No tiene el fichero de BBDD. Voy a copiarlo");
			// No tiene el fichero de BBDD. Hay que copiarlo
			copiarBDDeRecursosASD(application, app.RES_ID_FICH_BBBDD, app.NOMBRE_FICH_BBDD);
		}else{
			Log.d("Milog", "Ya tiene el fichero de BBDD. Compruebo si necesita version nueva");
			// Ya tiene el fichero de BBDD. Hay que actualizarlo si ha cambiado la versi—n del fichero
			actualizarBDExistente(application, app.NOMBRE_FICH_BBDD);
		}
	}

	
	
	
	
	private static boolean actualizarBDExistente(Application application,String nombreBBDD) {
		
		MainApp app = (MainApp)application;
		
		// Aquí hay que comprobar las versiones de la BBDD y si son distintas hay que actualizar con la que va en
		// el paquete, que es la más actualizada
		// En las BBDDs hay una tabla que se llama "metadatos_apps", con una columna llamada "bd_version", que es la fecha en formato AAAAMMDDHHMM

		String consultaVersion = "SELECT bd_version FROM bd_info";
		
		Cursor  c = DBManager.consultar(consultaVersion, application);
		
		String versionOriginal = "";
		String versionNueva = "";
		
		boolean hayQueActualizar = false;
		

		try{
	    	if (c != null) {
	    		Log.d("Milog", "El cursor no es nulo");
	            if(c.moveToFirst()){
	            	versionOriginal = c.getString(c.getColumnIndex("bd_version"));
	            }else{
	            	hayQueActualizar = true;
	            }
	        }else{
	        	hayQueActualizar = true;
	        }
    	}catch(Exception ex){
    		
    	}finally{
    		if(c != null){
    			c.close();
    		}
    		c = null;
    	}
		
		
		
		// Paso 1 renombrar la BBDD que ya había en la SD
		File ficheroOriginal = new File(Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/" + nombreBBDD);
		
		
		
		boolean resRename = ficheroOriginal.renameTo( new File(Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/" + nombreBBDD + "_antes") );
		if(resRename){
			// Paso 2: copiar la nueva BBDD al directorio SD
			copiarBDDeRecursosASD(application, app.RES_ID_FICH_BBBDD, app.NOMBRE_FICH_BBDD);

			// Paso 3: Consultar la version de la nueva BBDD
			c = DBManager.consultar(consultaVersion, application);
			try{
		    	if (c != null) {
		    		Log.d("Milog", "El cursor no es nulo");
		            if(c.moveToFirst()){
		            	versionNueva = c.getString(c.getColumnIndex("bd_version"));
		            	
		            	if(!versionNueva.equals(versionOriginal)){
		            		hayQueActualizar = true;
		            	}
		            }
		        }
	    	}catch(Exception ex){
	    		
	    	}finally{
	    		if(c != null){
	    			c.close();
	    		}
	    		c = null;
	    	}
			
			
			
			
			if(hayQueActualizar){
				Log.d("Milog", "Hay que actualizar");
				// Eliminar el ficheroOriginal
				File fOrig = new File(Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/" + nombreBBDD + "_antes");
				fOrig.delete();
				
			}else{
				
				Log.d("Milog", "No hay que actualizar");
				
				// Eliminar el ficheroNuevo
				File fNuevo = new File(Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/" + nombreBBDD);
				fNuevo.delete();
				
				// Renombrar el fichero original a como estaba
				File fOrig = new File(Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/" + nombreBBDD + "_antes");
				fOrig.renameTo(new File(Environment.getExternalStorageDirectory() + "/" + app.CARPETA_SD + "/" + nombreBBDD));
			}
			
			
			
			
		}
		
		
		return true;
	}
	
	
	
	
	private static boolean copiarBDDeRecursosASD(Application application,int idRecurso, String nombreBBDD){
		MainApp app = (MainApp)application;
		try {
			InputStream ins = app.getApplicationContext().getResources().openRawResource(idRecurso);
			int size;
			size = ins.available();
			
			// Leer el recurso local a un buffer
			byte[] buffer = new byte[size];
			ins.read(buffer);
			ins.close();
			
			// Copiar el buffer a un nuevo fichero
			FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + app.CARPETA_SD + "/" + nombreBBDD);
			fos.write(buffer);
			fos.close();
			return true;
		} catch (Exception e) {
			//mostrarMensajeNoTieneBD();
			return false;
		}
	}
	
	
}
