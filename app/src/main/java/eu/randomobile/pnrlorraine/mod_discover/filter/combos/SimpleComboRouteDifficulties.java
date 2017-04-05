package eu.randomobile.pnrlorraine.mod_discover.filter.combos;

import java.util.ArrayList;

import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.RouteDifficultyTerm;
import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.RouteDifficultyTerm.RouteDifficultiesInterface;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.widget.Toast;

public class SimpleComboRouteDifficulties implements RouteDifficultiesInterface {

	private MainApp app;
	private Context context;
	private ProgressDialog dialogoProgreso;
	private AlertDialog.Builder constructorCombo;
	



	private AlertDialog combo;

	private ArrayList<RouteDifficultyTerm> dificultadesRutas;
	
	private String tidDificultadRutaSeleccionado = null;
	private String nombreDificultadRutaSeleccionado = null;
	
	// Interface para comunicarse con el resto de pantallas
	public ComboSimpleDificultadesRutasInterface comboSimpleDificultadesRutasInterface;	
	
	public static interface ComboSimpleDificultadesRutasInterface {
		public void seCerroComboDificultadesRutas(String tidSeleccionado, String nombreSeleccionado);
	}
	
	
	
	
	public SimpleComboRouteDifficulties(Application _app, Context _ctx){
		this.app = (MainApp)_app;
		this.context = _ctx;
		
		String tidDifSeleccionado = this.app.preferencias.getString(app.FILTER_KEY_ROUTE_DIFFICULTY_TID, null);
		if(tidDifSeleccionado != null && !tidDifSeleccionado.equals("")){	tidDificultadRutaSeleccionado = tidDifSeleccionado;	}
	}
	
	
	
	public void show(){
		
		String tidSeleccionado = this.app.preferencias.getString(app.FILTER_KEY_ROUTE_DIFFICULTY_TID, null);
		this.tidDificultadRutaSeleccionado = tidSeleccionado;
		
		if(dificultadesRutas == null){
			// Mostrar que est‡ cargando elementos
			dialogoProgreso = new ProgressDialog(context);
			dialogoProgreso.setCancelable(true);
			dialogoProgreso.setMessage( context.getResources().getString(R.string.mod_global__cargando) );
			dialogoProgreso.show();

			// Cargar los elementos
			if(DataConection.hayConexion(context)){
				RouteDifficultyTerm.routeDifficultiesInterface = this;
				RouteDifficultyTerm.cargarListaDificultadesRutas(app);
			}else{
				Toast.makeText(context, 
						context.getResources().getString(R.string.mod_global__sin_conexion_a_internet) , 
						Toast.LENGTH_SHORT).show();
			}
		}else{
			mostrarSingleSelect(this.dificultadesRutas, context.getResources().getString(R.string.mod_discover__selecciona_dificultad) );
		}
		
		
	}
	

	

	public void seCargoListaDificultadesRutas(ArrayList<RouteDifficultyTerm> routeDifficulties) {
		this.dificultadesRutas = routeDifficulties;
		mostrarSingleSelect(dificultadesRutas, context.getResources().getString(R.string.mod_discover__selecciona_dificultad) );
	}


	public void producidoErrorAlCargarListaDificultadesRutas(String error) {
		// Cerrar el di‡logo de progreso
		dialogoProgreso.dismiss();
		Util.mostrarMensaje(context, 
				context.getResources().getString(R.string.mod_global__error), 
				context.getResources().getString(R.string.mod_global__no_se_pueden_recuperar_los_datos_del_servidor));
	}
	

	
	
	private void mostrarSingleSelect(final ArrayList<RouteDifficultyTerm> lista, String tituloDialogo){
		
		// Cerrar el di‡logo de progreso
		dialogoProgreso.dismiss();
		
		nombreDificultadRutaSeleccionado = context.getResources().getString(R.string.mod_discover__todas_las_dificultades);
		
		int indiceSel = 0;
		String[] textoOpciones = new String[ lista.size() + 1 ];
		textoOpciones[0] = nombreDificultadRutaSeleccionado;
		for(int i=0; i<lista.size(); i++){
			RouteDifficultyTerm poiCat = lista.get(i);
			textoOpciones[i+1] = poiCat.getName();
			if(this.tidDificultadRutaSeleccionado != null){
				if(poiCat.getTid().equals(this.tidDificultadRutaSeleccionado)){
					indiceSel = (i+1);
				}
			}
		}
				
		constructorCombo = new AlertDialog.Builder(context);
		constructorCombo.setTitle(tituloDialogo);
		constructorCombo.setSingleChoiceItems(textoOpciones, indiceSel, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.d("Milog", "Se ha seleccionado el ’ndice " + which);
				if(which > 0){
					Log.d("Milog", "Seleccionado " + lista.get(which-1).getName() );
					RouteDifficultyTerm routeCatSel = lista.get(which-1);
					tidDificultadRutaSeleccionado = routeCatSel.getTid();
					nombreDificultadRutaSeleccionado = routeCatSel.getName();
				}else{
					tidDificultadRutaSeleccionado = null;
					nombreDificultadRutaSeleccionado = context.getResources().getString(R.string.mod_discover__todas_las_dificultades);
				}
			}
		});
		String okTxt = context.getResources().getString(R.string.mod_global__ok);
		constructorCombo.setNegativeButton(okTxt, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(comboSimpleDificultadesRutasInterface != null){
					comboSimpleDificultadesRutasInterface.seCerroComboDificultadesRutas(tidDificultadRutaSeleccionado, nombreDificultadRutaSeleccionado);
				}
			}
		});
		String borrarFiltrosTxt = context.getResources().getString(R.string.mod_global__borrar_filtros);
		constructorCombo.setPositiveButton(borrarFiltrosTxt, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				tidDificultadRutaSeleccionado = null;
				nombreDificultadRutaSeleccionado = context.getResources().getString(R.string.mod_discover__todas_las_dificultades);
				if(comboSimpleDificultadesRutasInterface != null){
					comboSimpleDificultadesRutasInterface.seCerroComboDificultadesRutas(tidDificultadRutaSeleccionado, nombreDificultadRutaSeleccionado);
				}
			}
		});
		constructorCombo.show();

	}



	public AlertDialog getCombo() {
		return combo;
	}



	public void setCombo(AlertDialog combo) {
		this.combo = combo;
	}



	




	



	



	

	
    
}
