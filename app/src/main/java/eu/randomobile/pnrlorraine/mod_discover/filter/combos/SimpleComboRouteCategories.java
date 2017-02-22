package eu.randomobile.pnrlorraine.mod_discover.filter.combos;

import java.util.ArrayList;

import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.RouteCategoryTerm;
import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.RouteCategoryTerm.RouteCategoriesInterface;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.widget.Toast;

public class SimpleComboRouteCategories implements RouteCategoriesInterface {

	private MainApp app;
	private Context context;
	private ProgressDialog dialogoProgreso;
	private AlertDialog.Builder constructorCombo;
	



	private AlertDialog combo;

	private ArrayList<RouteCategoryTerm> categoriasRutas;
	
	private String tidCategoriaRutaSeleccionado = null;
	private String nombreCategoriaRutaSeleccionado = null;
	
	// Interface para comunicarse con el resto de pantallas
	public ComboSimpleCategoriasRutasInterface comboSimpleCategoriasRutasInterface;	
	
	public static interface ComboSimpleCategoriasRutasInterface {
		public void seCerroComboCategoriasRutas(String tidSeleccionado, String nombreSeleccionado);
	}
	
	
	
	
	public SimpleComboRouteCategories(Application _app, Context _ctx){
		this.app = (MainApp)_app;
		this.context = _ctx;
		
		String tidCatSeleccionado = this.app.preferencias.getString(app.FILTER_KEY_ROUTE_CATEGORY_TID, null);
		if(tidCatSeleccionado != null && !tidCatSeleccionado.equals("")){	tidCategoriaRutaSeleccionado = tidCatSeleccionado;	}
	}
	
	
	
	public void show(){
		
		String tidSeleccionado = this.app.preferencias.getString(app.FILTER_KEY_ROUTE_CATEGORY_TID, null);
		this.tidCategoriaRutaSeleccionado = tidSeleccionado;
		
		if(categoriasRutas == null){
			// Mostrar que est‡ cargando elementos
			dialogoProgreso = new ProgressDialog(context);
			dialogoProgreso.setCancelable(true);
			dialogoProgreso.setMessage( context.getResources().getString(R.string.mod_global__cargando) );
			dialogoProgreso.show();

			// Cargar los elementos
			if(DataConection.hayConexion(context)){
				RouteCategoryTerm.routeCategoriesInterface = this;
				RouteCategoryTerm.cargarListaCategoriasRutas(app);
			}else{
				Toast.makeText(context, 
						context.getResources().getString(R.string.mod_global__sin_conexion_a_internet) , 
						Toast.LENGTH_SHORT).show();
			}
		}else{
			mostrarSingleSelect(this.categoriasRutas, context.getResources().getString(R.string.mod_discover__selecciona_categoria) );
		}
		
		
	}
	

	
	@Override
	public void seCargoListaCategoriasRutas(ArrayList<RouteCategoryTerm> routeCategories) {
		this.categoriasRutas = routeCategories;
		mostrarSingleSelect(categoriasRutas, context.getResources().getString(R.string.mod_discover__selecciona_categoria) );
	}



	@Override
	public void producidoErrorAlCargarListaCategoriasRutas(String error) {
		// Cerrar el di‡logo de progreso
				dialogoProgreso.dismiss();
				Util.mostrarMensaje(context, 
						context.getResources().getString(R.string.mod_global__error), 
						context.getResources().getString(R.string.mod_global__no_se_pueden_recuperar_los_datos_del_servidor));
	}
	

	
	
	private void mostrarSingleSelect(final ArrayList<RouteCategoryTerm> lista, String tituloDialogo){
		
		// Cerrar el di‡logo de progreso
		dialogoProgreso.dismiss();
		
		nombreCategoriaRutaSeleccionado = context.getResources().getString(R.string.mod_discover__todas_las_categorias);
		
		int indiceSel = 0;
		String[] textoOpciones = new String[ lista.size() + 1 ];
		textoOpciones[0] = nombreCategoriaRutaSeleccionado;
		for(int i=0; i<lista.size(); i++){
			RouteCategoryTerm poiCat = lista.get(i);
			textoOpciones[i+1] = poiCat.getName();
			if(this.tidCategoriaRutaSeleccionado != null){
				if(poiCat.getTid().equals(this.tidCategoriaRutaSeleccionado)){
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
					RouteCategoryTerm routeCatSel = lista.get(which-1);
					tidCategoriaRutaSeleccionado = routeCatSel.getTid();
					nombreCategoriaRutaSeleccionado = routeCatSel.getName();
				}else{
					tidCategoriaRutaSeleccionado = null;
					nombreCategoriaRutaSeleccionado = context.getResources().getString(R.string.mod_discover__todas_las_categorias);
				}
			}
		});
		String okTxt = context.getResources().getString(R.string.mod_global__ok);
		constructorCombo.setNegativeButton(okTxt, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(comboSimpleCategoriasRutasInterface != null){
					comboSimpleCategoriasRutasInterface.seCerroComboCategoriasRutas(tidCategoriaRutaSeleccionado, nombreCategoriaRutaSeleccionado);
				}
			}
		});
		String borrarFiltrosTxt = context.getResources().getString(R.string.mod_global__borrar_filtros);
		constructorCombo.setPositiveButton(borrarFiltrosTxt, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				tidCategoriaRutaSeleccionado = null;
				nombreCategoriaRutaSeleccionado = context.getResources().getString(R.string.mod_discover__todas_las_categorias);
				if(comboSimpleCategoriasRutasInterface != null){
					comboSimpleCategoriasRutasInterface.seCerroComboCategoriasRutas(tidCategoriaRutaSeleccionado, nombreCategoriaRutaSeleccionado);
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
