package eu.randomobile.pnrlorraine.mod_global.model;

public class ItemWinners {

	private String uid;
	private String nombre;
	private String posicion;

	
	
	/****************CONSTRUCTORES********************/
	public ItemWinners(){
		
	}
	public ItemWinners(String uid, String nombre, String posicion){
		this.uid = uid;
		this.nombre = nombre;
		this.posicion = posicion;
	}
	/***************************************************/
	
	public String getUid(){
		return uid;
	}
	public void setUid(String uid){
		this.uid = uid;
	}
	
	public String getNombre(){
		return nombre;
	}
	public void setNombre(String nombre){
		this.nombre = nombre;
	}

	public String getPosicion(){
		return posicion;
	}
	public void setPosicion(String posicion){
		this.posicion = posicion;
	}

	
	
	
}
