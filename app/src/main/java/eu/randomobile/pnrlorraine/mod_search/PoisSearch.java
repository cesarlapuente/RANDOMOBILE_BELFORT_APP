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



	// ajout sauvage

	private static Boolean checkADescubrir = true;
	private static Boolean checkOTurismo = true;
	private static Boolean checkSMonumentos = true;
	private static Boolean checkMuseos = true;
	private static Boolean checkPatrimonio = true;
	private static Boolean checkParques = true;
	private static Boolean checkHebergement = true;
	private static Boolean checkHabitacionesHotel = true;
	private static Boolean checkColectivos = true;
	private static Boolean checkHostales = true;
	private static Boolean checkHostalesAire = true;
	private static Boolean checkAmueblado = true;
	private static Boolean checkResidencias = true;
	private static Boolean checkRestaurantes = true;

	public static void setCheckADescubrir (Boolean check) {
		PoisSearch.checkADescubrir = check;
	}
	public static Boolean getCheckADescubrir () {
		return checkADescubrir;
	}
	public static void setCheckOTurismo (Boolean check) {
		PoisSearch.checkOTurismo = check;
	}
	public static Boolean getCheckOTurismo () {
		return checkOTurismo;
	}
	public static void setCheckSMonumentos (Boolean check) {
		PoisSearch.checkSMonumentos = check;
	}

	public static Boolean getCheckSMonumentos () {
		return checkSMonumentos;
	}

	public static void setCheckMuseos (Boolean check) {
		PoisSearch.checkMuseos = check;
	}

	public static Boolean getCheckMuseos () {
		return checkMuseos;
	}

	public static void setCheckPatrimonio (Boolean check) {
		PoisSearch.checkPatrimonio = check;
	}

	public static Boolean getCheckPatrimonio () {
		return checkPatrimonio;
	}

	public static void setCheckParques (Boolean check) {
		PoisSearch.checkParques = check;
	}

	public static Boolean getCheckParques () {
		return checkParques;
	}

	public static void setCheckHebergement (Boolean check) {
		PoisSearch.checkHebergement = check;
	}

	public static Boolean getCheckHebergement () {
		return checkHebergement;
	}

	public static void setCheckHabitacionesHotel (Boolean check) {
		PoisSearch.checkHabitacionesHotel = check;
	}

	public static Boolean getCheckHabitacionesHotel () {
		return checkHabitacionesHotel;
	}

	public static void setCheckColectivos (Boolean check) {
		PoisSearch.checkColectivos = check;
	}

	public static Boolean getCheckColectivos () {
		return checkColectivos;
	}

	public static void setCheckHostales (Boolean check) {
		PoisSearch.checkHostales = check;
	}

	public static Boolean getCheckHostales () {

		return checkHostales;
	}

	public static void setCheckHostalesAire (Boolean check) {
		PoisSearch.checkHostalesAire = check;
	}

	public static Boolean getCheckHostalesAire () {
		return checkHostalesAire;
	}
	public static void setCheckAmueblado (Boolean check) {
		PoisSearch.checkAmueblado = check;
	}

	public static Boolean getCheckAmueblado () {
		return checkAmueblado;
	}

	public static void setCheckResidencias (Boolean check) {
		PoisSearch.checkResidencias = check;
	}

	public static Boolean getCheckResidencias () {
		return checkResidencias;
	}


	public static void setCheckRestaurantes (Boolean check) {
		PoisSearch.checkRestaurantes = check;
	}

	public static Boolean getCheckRestaurantes () {
		return checkRestaurantes;
	}

	// fin ajout

	// Nos dice si el poi cumple con los criterios actuales del filtrado
	public static Boolean checkCriteria(Poi poi, Context ctx) {
    	Boolean result = false;
    	String category = poi.getCategory().getName();
    	/*if (getCheck1() && category.equals(ctx.getResources().getString(R.string.lugar_de_interes_cultural)))
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
    		return true;*/

		// ajout sauvage
		if (getCheckRestaurantes() && category.equals("Restauration"))
			return true;
		if (getCheckMuseos() && category.equals("Mus?e"))
			return true;
		if (getCheckResidencias() && category.equals("R?sidence"))
			return true;
		if (getCheckAmueblado() && category.equals("Meubl?"))
			return true;
		if (getCheckHostalesAire() && category.equals("H?tellerie de plein air"))
			return true;
		if (getCheckHostales() && category.equals("H?tellerie"))
			return true;
		if (getCheckColectivos() && category.equals("H?bergement collectif"))
			return true;
		if (getCheckHabitacionesHotel() && category.equals("Chambre d'h?tes"))
			return true;
		if (getCheckParques() && category.equals("Sites de Loisirs"))
			return true;
		if (getCheckPatrimonio() && category.equals("Patrimoine Naturel"))
			return true;
		if (getCheckSMonumentos() && category.equals("Site et Monument"))
			return true;
		if (getCheckOTurismo() && category.equals("Office de Tourisme"))
			return true;

		//fin ajout
    		
    	return result;
	}
}
