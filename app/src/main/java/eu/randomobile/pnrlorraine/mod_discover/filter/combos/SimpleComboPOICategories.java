package eu.randomobile.pnrlorraine.mod_discover.filter.combos;

import java.util.ArrayList;

import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.MainApp;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.DataConection;
import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.PoiCategoryTerm;
import eu.randomobile.pnrlorraine.mod_global.model.taxonomy.PoiCategoryTerm.PoiCategoriesInterface;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.widget.Toast;

public class SimpleComboPOICategories implements PoiCategoriesInterface {

	
	
	private MainApp app;
	private Context context;
	private ProgressDialog dialogoProgreso;
	private AlertDialog.Builder constructorCombo;
	



	private AlertDialog combo;

	private ArrayList<PoiCategoryTerm> categoriasPois;
	
	private String tidCategoriaPOISeleccionado = null;
	private String nombreCategoriaPOISeleccionado = null;
	
	// Interface para comunicarse con el resto de pantallas
	public ComboSimpleCategoriasPoisInterface comboSimpleCategoriasPoisInterface;	
	
	public static interface ComboSimpleCategoriasPoisInterface {
		public void seCerroComboCategoriasPois(String tidSeleccionado, String nombreSeleccionado);
	}
	
	
	
	
	public SimpleComboPOICategories(Application _app, Context _ctx){
		this.app = (MainApp)_app;
		this.context = _ctx;
		
		String tidCatSeleccionado = this.app.preferencias.getString(app.FILTER_KEY_POI_CATEGORY_TID, null);
		if(tidCatSeleccionado != null && !tidCatSeleccionado.equals("")){	tidCategoriaPOISeleccionado = tidCatSeleccionado;	}
	}
	
	
	
	public void show(){
		
		String tidSeleccionado = this.app.preferencias.getString(app.FILTER_KEY_POI_CATEGORY_TID, null);
		this.tidCategoriaPOISeleccionado = tidSeleccionado;
		
		if(categoriasPois == null){
			// Mostrar que est‡ cargando elementos
			dialogoProgreso = new ProgressDialog(context);
			dialogoProgreso.setCancelable(true);
			dialogoProgreso.setMessage( context.getResources().getString(R.string.mod_global__cargando) );
			dialogoProgreso.show();

			// Cargar los elementos
			if(DataConection.hayConexion(context)){
				PoiCategoryTerm.poiCategoriesInterface = this;
				PoiCategoryTerm.cargarListaCategoriasPois(app);
			}else{
				Toast.makeText(context, 
						context.getResources().getString(R.string.mod_global__sin_conexion_a_internet) , 
						Toast.LENGTH_SHORT).show();
			}
		}else{
			mostrarSingleSelect(this.categoriasPois, context.getResources().getString(R.string.mod_discover__selecciona_categoria) );
		}
		
		
	}
	


	public void seCargoListaCategoriasPois(ArrayList<PoiCategoryTerm> pois) {
		this.setCategoriasPois(pois);
		mostrarSingleSelect(pois, context.getResources().getString(R.string.mod_discover__selecciona_categoria) );
	}


	public ArrayList<PoiCategoryTerm> getCategoriasPois() {
		return categoriasPois;
	}



	public void setCategoriasPois(ArrayList<PoiCategoryTerm> categoriasPois) {
		this.categoriasPois = categoriasPois;
	}



	public void producidoErrorAlCargarListaCategoriasPois(String error) {
		// Cerrar el di‡logo de progreso
		dialogoProgreso.dismiss();
		Util.mostrarMensaje(context, 
				context.getResources().getString(R.string.mod_global__error), 
				context.getResources().getString(R.string.mod_global__no_se_pueden_recuperar_los_datos_del_servidor));
	}
	
	
	
	private void mostrarSingleSelect(final ArrayList<PoiCategoryTerm> lista, String tituloDialogo){
		
		// Cerrar el di‡logo de progreso
		dialogoProgreso.dismiss();
		
		nombreCategoriaPOISeleccionado = context.getResources().getString(R.string.mod_discover__todas_las_categorias);
		
		int indiceSel = 0;
		String[] textoOpciones = new String[ lista.size() + 1 ];
		textoOpciones[0] = nombreCategoriaPOISeleccionado;
		for(int i=0; i<lista.size(); i++){
			PoiCategoryTerm poiCat = lista.get(i);
			textoOpciones[i+1] = poiCat.getName();
			if(this.tidCategoriaPOISeleccionado != null){
				if(poiCat.getTid().equals(this.tidCategoriaPOISeleccionado)){
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
					PoiCategoryTerm poiCatSel = lista.get(which-1);
					tidCategoriaPOISeleccionado = poiCatSel.getTid();
					nombreCategoriaPOISeleccionado = poiCatSel.getName();
				}else{
					tidCategoriaPOISeleccionado = null;
					nombreCategoriaPOISeleccionado = context.getResources().getString(R.string.mod_discover__todas_las_categorias);
				}
			}
		});
		String okTxt = context.getResources().getString(R.string.mod_global__ok);
		constructorCombo.setNegativeButton(okTxt, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(comboSimpleCategoriasPoisInterface != null){
					comboSimpleCategoriasPoisInterface.seCerroComboCategoriasPois(tidCategoriaPOISeleccionado, nombreCategoriaPOISeleccionado);
				}
			}
		});
		String borrarFiltrosTxt = context.getResources().getString(R.string.mod_global__borrar_filtros);
		constructorCombo.setPositiveButton(borrarFiltrosTxt, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				tidCategoriaPOISeleccionado = null;
				nombreCategoriaPOISeleccionado = context.getResources().getString(R.string.mod_discover__todas_las_categorias);
				if(comboSimpleCategoriasPoisInterface != null){
					comboSimpleCategoriasPoisInterface.seCerroComboCategoriasPois(tidCategoriaPOISeleccionado, nombreCategoriaPOISeleccionado);
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



	public String getTidCategoriaPOISeleccionado() {
		return tidCategoriaPOISeleccionado;
	}



	public void setTidCategoriaPOISeleccionado(String tidCategoriaPOISeleccionado) {
		this.tidCategoriaPOISeleccionado = tidCategoriaPOISeleccionado;
	}



	



	



	

	
    
}
