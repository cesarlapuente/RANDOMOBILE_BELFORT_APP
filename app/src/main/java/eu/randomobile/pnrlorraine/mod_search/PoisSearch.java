package eu.randomobile.pnrlorraine.mod_search;

import android.content.Context;

import eu.randomobile.pnrlorraine.mod_global.model.Poi;


public class PoisSearch {
	
	private static Boolean check1 = true;
	private static Boolean check2 = true;
	private static Boolean check3 = true;
	private static Boolean check4 = true;
	private static Boolean check5 = true;
	private static Boolean check6 = true;
	private static Boolean check7 = true;
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


	// ajout sauvage

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

	public static Boolean getCheckADescubrir() {
		return checkADescubrir;
	}

	public static void setCheckADescubrir (Boolean check) {
		PoisSearch.checkADescubrir = check;
	}

	public static Boolean getCheckOTurismo() {
		return checkOTurismo;
	}

	public static void setCheckOTurismo (Boolean check) {
		PoisSearch.checkOTurismo = check;
	}

	public static Boolean getCheckSMonumentos () {
		return checkSMonumentos;
	}

	public static void setCheckSMonumentos(Boolean check) {
		PoisSearch.checkSMonumentos = check;
	}

	public static Boolean getCheckMuseos () {
		return checkMuseos;
	}

	public static void setCheckMuseos(Boolean check) {
		PoisSearch.checkMuseos = check;
	}

	public static Boolean getCheckPatrimonio () {
		return checkPatrimonio;
	}

	public static void setCheckPatrimonio(Boolean check) {
		PoisSearch.checkPatrimonio = check;
	}

	public static Boolean getCheckParques () {
		return checkParques;
	}

	public static void setCheckParques(Boolean check) {
		PoisSearch.checkParques = check;
	}

	public static Boolean getCheckHebergement () {
		return checkHebergement;
	}

	public static void setCheckHebergement(Boolean check) {
		PoisSearch.checkHebergement = check;
	}

	public static Boolean getCheckHabitacionesHotel () {
		return checkHabitacionesHotel;
	}

	public static void setCheckHabitacionesHotel(Boolean check) {
		PoisSearch.checkHabitacionesHotel = check;
	}

	public static Boolean getCheckColectivos () {
		return checkColectivos;
	}

	public static void setCheckColectivos(Boolean check) {
		PoisSearch.checkColectivos = check;
	}

	public static Boolean getCheckHostales () {

		return checkHostales;
	}

	public static void setCheckHostales(Boolean check) {
		PoisSearch.checkHostales = check;
	}

	public static Boolean getCheckHostalesAire () {
		return checkHostalesAire;
	}

	public static void setCheckHostalesAire(Boolean check) {
		PoisSearch.checkHostalesAire = check;
	}

	public static Boolean getCheckAmueblado () {
		return checkAmueblado;
	}

	public static void setCheckAmueblado(Boolean check) {
		PoisSearch.checkAmueblado = check;
	}

	public static Boolean getCheckResidencias () {
		return checkResidencias;
	}

	public static void setCheckResidencias(Boolean check) {
		PoisSearch.checkResidencias = check;
	}

	public static Boolean getCheckRestaurantes () {
		return checkRestaurantes;
	}

	public static void setCheckRestaurantes(Boolean check) {
		PoisSearch.checkRestaurantes = check;
	}

	// fin ajout

	// Nos dice si el poi cumple con los criterios actuales del filtrado
	public static Boolean checkCriteria(Poi poi, Context ctx) {
    	Boolean result = false;
		int category = poi.getCat();
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
		if (getCheckRestaurantes() && category == 27)
			return true;
		if (getCheckMuseos() && category == 28)
			return true;
		if (getCheckResidencias() && category == 51)
			return true;
		if (getCheckAmueblado() && category == 50)
			return true;
		if (getCheckHostalesAire() && category == 49)
			return true;
		if (getCheckHostales() && category == 48)
			return true;
		if (getCheckColectivos() && category == 26)
			return true;
		if (getCheckHabitacionesHotel() && category == 47)
			return true;
		if (getCheckParques() && category == 45)
			return true;
		if (getCheckPatrimonio() && category == 29)
			return true;
		if (getCheckSMonumentos() && category == 36)
			return true;
		if (getCheckOTurismo() && category == 25)
			return true;

		//fin ajout
    		
    	return result;
	}
}
