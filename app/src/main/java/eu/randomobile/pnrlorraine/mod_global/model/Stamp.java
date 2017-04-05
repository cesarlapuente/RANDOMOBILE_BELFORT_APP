package eu.randomobile.pnrlorraine.mod_global.model;

public class Stamp {

	private String id;
	private String nombre;
	private String descripcion;
	private String imagenDestacada;
	private boolean hecho;

	
	
	/****************CONSTRUCTORES********************/
	public Stamp(){
		
	}
	public Stamp(String id, String nombre, String descripcion, String imagenDestacada, boolean hecho){
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.setImagenDestacada(imagenDestacada);
		this.setHecho(hecho);
	}
	/***************************************************/
	
	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id = id;
	}
	
	public String getNombre(){
		return nombre;
	}
	public void setNombre(String nombre){
		this.nombre = nombre;
	}

	public String getDescripcion(){
		return descripcion;
	}
	public void setDescripcion(String descripcion){
		this.descripcion = descripcion;
	}
	public String getImagenDestacada() {
		return imagenDestacada;
	}
	public void setImagenDestacada(String imagenDestacada) {
		this.imagenDestacada = imagenDestacada;
	}
	public boolean isHecho() {
		return hecho;
	}
	public void setHecho(boolean hecho) {
		this.hecho = hecho;
	}

	
	
	
}