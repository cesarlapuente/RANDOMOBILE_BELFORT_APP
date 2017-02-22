package eu.randomobile.pnrlorraine.mod_search;

import android.content.Context;

import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;


public class PoisSearch {
	
	private static Boolean check1 = true;
	private static Boolean check2 = true;
	private static Boolean check3 = true;
	private static Boolean check4 = true;
	private static Boolean check5 = true;
	private static Boolean check6 = true;
	private static Boolean check7 = true;

	public static Boolean getCheck1() {
		return check1;
	}

	public static void setCheck1(Boolean check1) {
		PoisSearch.check1 = check1;
	}

	public static Boolean getCheck2() {
		return check2;
	}

	public static void setCheck2(Boolean check2) {
		PoisSearch.check2 = check2;
	}

	public static Boolean getCheck3() {
		return check3;
	}

	public static void setCheck3(Boolean check3) {
		PoisSearch.check3 = check3;
	}

	public static Boolean getCheck4() {
		return check4;
	}

	public static void setCheck4(Boolean check4) {
		PoisSearch.check4 = check4;
	}

	public static Boolean getCheck5() {
		return check5;
	}

	public static void setCheck5(Boolean check5) {
		PoisSearch.check5 = check5;
	}

	public static Boolean getCheck6() {
		return check6;
	}

	public static void setCheck6(Boolean check6) {
		PoisSearch.check6 = check6;
	}

	public static Boolean getCheck7() {
		return check7;
	}

	public static void setCheck7(Boolean check7) {
		PoisSearch.check7 = check7;
	}

	// Nos dice si el poi cumple con los criterios actuales del filtrado
	public static Boolean checkCriteria(Poi poi, Context ctx) {
    	Boolean result = false;
    	String category = poi.getCategory().getName();
    	if (getCheck1() && category.equals(ctx.getResources().getString(R.string.lugar_de_interes_cultural)))
    		return true;
    	if (getCheck2() && category.equals(ctx.getResources().getString(R.string.lugar_de_interes_natural)))
    		return true;
    	if (getCheck3() && category.equals(ctx.getResources().getString(R.string.playas)))
    		return true;
    	if (getCheck4() && category.equals(ctx.getResources().getString(R.string.servicios_oficinas_de_turismo)))
    		return true;
    	if (getCheck5() && category.equals(ctx.getResources().getString(R.string.servicios_sanidad)))
    		return true;
    	if (getCheck6() && category.equals(ctx.getResources().getString(R.string.alojamientos)))
    		return true;
    	if (getCheck7() && category.equals(ctx.getResources().getString(R.string.restauracion)))
    		return true;
    		
    	return result;
	}
}
