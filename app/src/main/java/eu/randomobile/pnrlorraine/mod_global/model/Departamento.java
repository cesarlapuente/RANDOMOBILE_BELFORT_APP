package eu.randomobile.pnrlorraine.mod_global.model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import eu.randomobile.pnrlorraine.R;

import android.content.Context;

public class Departamento {
	private String code;
	private String name;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public static ArrayList<Departamento> getListComunidades(Context ctx){
		ArrayList<Departamento> comunidades = new ArrayList<Departamento>();
		String[] nombreComunidades = ctx.getResources().getStringArray(R.array.nombre_comunidades);
		String[] codeComunidades = ctx.getResources().getStringArray(R.array.code_comunidades);
		for(int i=0; i<codeComunidades.length; i++){
			Departamento com = new Departamento();
			com.setCode(codeComunidades[i]);
			com.setName(nombreComunidades[i]);
			comunidades.add(com);
		}
		Collections.sort(comunidades, new ComunidadComparator());
		return comunidades;
	}
	
	public String toString() {
        //return iso + " - " + code + " - " + name.toUpperCase();
    	return name;
    }
    
    
    
    
    /**
     * CountryComparator class.
     */
    static class ComunidadComparator implements Comparator<Departamento> {
        @SuppressWarnings("rawtypes")
		private Comparator comparator;

        ComunidadComparator() {
            comparator = Collator.getInstance();
        }

        @SuppressWarnings("unchecked")
        public int compare(Departamento c1, Departamento c2) {
            return comparator.compare(c1.name, c2.name);
        }
    }
	
}
