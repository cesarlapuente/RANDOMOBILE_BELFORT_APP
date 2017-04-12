package eu.randomobile.pnrlorraine.mod_global;

import java.util.ArrayList;

import android.app.Application;
import android.util.Log;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;

import  eu.randomobile.pnrlorraine.MainApp;

import eu.randomobile.pnrlorraine.mod_global.model.GeoPoint;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;

public class WKTUtil {

	public static int countNumberOfOcurrencesInString(String str, String findStr){
		int lastIndex = 0;
		int count =0;
		while(lastIndex != -1){
		       lastIndex = str.indexOf(findStr,lastIndex);
		       if( lastIndex != -1){
		             count ++;
		             lastIndex+=findStr.length();
		      }
		}
		return count;
	}
	
	
	
	
	public static String getWKTPoint(String lat, String lon){
		return "POINT (" + lon + " " + lat + ")";
	}
	
	public static String getWKTPoint(double lat, double lon){
		return WKTUtil.getWKTPoint( String.valueOf(lat) , String.valueOf(lon) );
	}
	
	public static String getWKTPoint(float lat, float lon){
		return WKTUtil.getWKTPoint( String.valueOf(lat) , String.valueOf(lon) );
	}
	
	
	
	public static ArrayList<Object> extractGeometriesFromWKTField(Application application, String wktField){
		
		MainApp app = (MainApp)application;
		
		ArrayList<Object> geometrias = null;
		
		if(wktField != null && !wktField.equals("")){
			geometrias = new ArrayList<Object>();
			if(wktField.startsWith("GEOMETRYCOLLECTION (")){
				// EL ELEMENTO ES UNA COLECCIîN DE GEOMETRIAS
	            // Contar el nœmero de ocurrencias de polygon
				int numPoligonos = WKTUtil.countNumberOfOcurrencesInString(wktField, "POLYGON ((");
				if(numPoligonos > 0){
					for(int i=0; i<numPoligonos; i++){
						String polygonStr = "POLYGON ((";
						String parentCierreStr = "))";
						int indexPoligon = wktField.indexOf(polygonStr);
						int indexParentesisCierre = wktField.indexOf(parentCierreStr);
						String wktPolygon = wktField.substring( indexPoligon, indexParentesisCierre + parentCierreStr.length() );
						// Retirar este poligono del string
						wktField = wktField.replace(wktPolygon, "");
						// Crear un polygon
						Polygon polygon = WKTUtil.getPolygonFromWKTPolygonField(app, wktPolygon);
						// Anadirlo al array
						geometrias.add(polygon);
						
					}
				}

	            // Contar el nœmero de ocurrencias de LineString
				int numLineString = WKTUtil.countNumberOfOcurrencesInString(wktField, "LINESTRING (");
				if(numLineString > 0){
					for(int i=0; i<numLineString; i++){
						String linestringStr = "LINESTRING (";
						String parentCierreStr = ")";
						int indexLinestring = wktField.indexOf(linestringStr);
						int indexParentesisCierre = wktField.indexOf(parentCierreStr);
						String wktLineString = wktField.substring( indexLinestring, indexParentesisCierre + parentCierreStr.length() );
						// Retirar este linestring del string
						wktField = wktField.replace(wktLineString, "");
						// Crear un Polyline
						Polyline polyline = WKTUtil.getPolylineFromWKTLineStringField(app, wktLineString);
						// Anadirlo al array
						geometrias.add(polyline);
						
					}
				}
				
				// Contar el nœmero de ocurrencias de Point
				int numPoints = WKTUtil.countNumberOfOcurrencesInString(wktField, "POINT (");
				if(numPoints > 0){
					for(int i=0; i<numPoints; i++){
						String pointStr = "POINT (";
						String parentCierreStr = ")";
						int indexLinestring = wktField.indexOf(pointStr);
						int indexParentesisCierre = wktField.indexOf(parentCierreStr);
						String wktPoint = wktField.substring( indexLinestring, indexParentesisCierre + parentCierreStr.length() );
						// Retirar este linestring del string
						wktField = wktField.replace(wktPoint, "");
						// Crear un Point
						Point point = WKTUtil.getPointFromWKTPointField(app, wktPoint);
						// Anadirlo al array
						geometrias.add(point);
						
					}
				}
				
			}else if( wktField.startsWith("POLYGON ((") ){
	            // EL ELEMENTO ES UN POLêGONO
				// Crear un polygon
				Polygon polygon = WKTUtil.getPolygonFromWKTPolygonField(app, wktField);
				// Anadirlo al array
				geometrias.add(polygon);
	        }else if( wktField.startsWith("LINESTRING (") ){
	            // EL ELEMENTO ES UNA LêNEA
	        	// Crear un Polyline
				Polyline polyline = WKTUtil.getPolylineFromWKTLineStringField(app, wktField);
				// Anadirlo al array
				geometrias.add(polyline);
	        }else if( wktField.startsWith("POINT (") ){
	            // EL ELEMENTO ES UN PUNTO
	        	// Crear un Point
				Point point = WKTUtil.getPointFromWKTPointField(app, wktField);
				// Anadirlo al array
				geometrias.add(point);
	        }
		}
		
		return geometrias;
	}
	
	
	
	
	public static String getWKTPolygonFieldFromArrayPoints(ArrayList<Point> arrayPoints){
		String wktField = null;
		if(arrayPoints != null){
			int count = arrayPoints.size();
			if(count > 0){
				wktField = "POLYGON ((";
			}
			for(int i=0; i<count; i++){
				Point punto = arrayPoints.get(i);
				
				String lat = String.valueOf(punto.getY());
				String lon = String.valueOf(punto.getX());
				
				//String latReduc = String.format("%.6f", punto.getY() );
				//String lonReduc = String.format("%.6f", punto.getX() );
				
				String latlon = lon + " " + lat;
				if(i == 0){
					wktField = wktField + latlon;
				}else{
					wktField = wktField + ", " + latlon;
				}
			}
			wktField = wktField + "))";
		}
		Log.d("Milog", "WKT field: " + wktField);
		return wktField;
	}

	
	
	public static Polygon getPolygonFromWKTPolygonField(Application application, String wktPolygon){
		
		MainApp app = (MainApp)application;
		
		//Log.d("Milog", "Me pasan: " + wktPolygon);
		
		// Quitar la palabra POLYGON
		String textoSustituido = wktPolygon.replace("POLYGON ((", "");
		
		// Quitar los parentesis de cierre ))
		textoSustituido = textoSustituido.replace("))", "");
		
		//Log.d("Milog", "Despues de quitar polygon y parentesis: " + textoSustituido);
		
		ArrayList<GeoPoint> puntosPoligono = null;
		Polygon polygon = null;
		
		String[] separadosComa = textoSustituido.split(", ");

		if(separadosComa != null){
			//Log.d("Milog", "Count de separados coma: " + separadosComa.length);
			for(int i=0; i<separadosComa.length; i++){
				String coordStr = separadosComa[i];
				// Obtener latitud, longitud
				String[] latLonTexto = coordStr.split(" ");

				if(latLonTexto != null){
					//Log.d("Milog", "Count de latlonTexto: " + latLonTexto.length);
					if(latLonTexto.length == 2){
						String longitud = latLonTexto[0];
						String latitud = latLonTexto[1];
						GeoPoint coordenadas = new GeoPoint();
						coordenadas.setLatitude(Double.parseDouble(latitud));
						coordenadas.setLongitude(Double.parseDouble(longitud));
						if(puntosPoligono == null){
							puntosPoligono = new ArrayList<GeoPoint>();
						}
						puntosPoligono.add(coordenadas);
					}
				}
				
				
			}
		}
		
		if(puntosPoligono != null){
			ArrayList<Point> points = new ArrayList<>();
			for(int i=0; i<puntosPoligono.size(); i++){
				GeoPoint punto = puntosPoligono.get(i);
				Point puntoProyectado = (Point) GeometryEngine.project(new Point(punto.getLongitude(), punto.getLatitude()), SpatialReference.create(102100) );
				//Log.d("Milog", "Voy a anadir el siguiente punto al pol’gono: " + punto.getLatitud() + "  " + punto.getLongitud());
				points.add(puntoProyectado);
			}
			polygon = new Polygon(new PointCollection(points));
		}

		return polygon;
	}
	
	
	public static Polyline getPolylineFromWKTLineStringField(Application application, String wktLineString){
		Log.d("Milog", "Inicio de parseo de wkt a coordenadas");
		MainApp app = (MainApp)application;
		
		// Quitar la palabra LINESTRING
		String textoSustituido = wktLineString.replace("LINESTRING (", "");
		
		// Quitar los parŽntesis de cierre ))
		textoSustituido = textoSustituido.replace( ")", "");
		
		ArrayList<GeoPoint> puntosPolyline = null;
		Polyline polyline = null;
		
		String[] separadosComaEspacio = textoSustituido.split(", ");
		if(separadosComaEspacio != null){
			for(int i=0; i<separadosComaEspacio.length; i++){
				String coordStr = separadosComaEspacio[i];
				// Obtener latitud, longitud
				String[] latLonTexto = coordStr.split(" ");
				if(latLonTexto != null){
					if(latLonTexto.length == 2){
						String longitud = latLonTexto[0];
						String latitud = latLonTexto[1];
						GeoPoint coordenadas = new GeoPoint();
						coordenadas.setLatitude(Double.parseDouble(latitud));
						coordenadas.setLongitude(Double.parseDouble(longitud));
						if(puntosPolyline == null){
							puntosPolyline = new ArrayList<GeoPoint>();
						}
						puntosPolyline.add(coordenadas);
					}
				}
			}
		}
		
		if(puntosPolyline == null){
			String[] separadosComa = textoSustituido.split(",");
			if(separadosComa != null){
				for(int i=0; i<separadosComa.length; i++){
					String coordStr = separadosComa[i];
					// Obtener latitud, longitud
					String[] latLonTexto = coordStr.split(" ");
					if(latLonTexto != null){
						if(latLonTexto.length == 2){
							String longitud = latLonTexto[0];
							String latitud = latLonTexto[1];
							GeoPoint coordenadas = new GeoPoint();
							coordenadas.setLatitude(Double.parseDouble(latitud));
							coordenadas.setLongitude(Double.parseDouble(longitud));
							if(puntosPolyline == null){
								puntosPolyline = new ArrayList<GeoPoint>();
							}
							puntosPolyline.add(coordenadas);
						}
					}
				}
			}
		}
		
		Log.d("Milog", "Fin de parseo de wkt a coordenadas");
		
		Log.d("Milog", "Inicio de proyeccion en polyline");
		
		if(puntosPolyline != null){
			Log.d("Milog", "Puntos polyline no es nulo y tiene estos elementos: " + puntosPolyline.size());

			ArrayList<Point> points = new ArrayList<>();
			for(int i=0; i<puntosPolyline.size(); i++){
				GeoPoint punto = puntosPolyline.get(i);
				//Point puntoProyectado = (Point) GeometryEngine.project(new Point(punto.getLongitude(), punto.getLatitude()), SpatialReference.create(102100));
				Point puntoProyectado = new Point(punto.getLongitude(), punto.getLatitude());
				Log.d("Milog", "Voy a anadir el siguiente punto al polyline: " + punto.getLatitude() + "  " + punto.getLongitude());
				Log.d("Pierre Log", "Point polyline: " + puntoProyectado.getX() + "  " + puntoProyectado.getY());
				points.add(puntoProyectado);
			}
			polyline = new Polyline(new PointCollection(points,SpatialReferences.getWgs84()), SpatialReferences.getWgs84());
		}else{
			Log.d("Milog", "Puntos polyline es nulo");
		}
		
		Log.d("Milog", "Fin de proyeccion en polyline");
		
		return polyline;
	}
	
	public static Point getPointFromWKTPointField(Application application, String wktPoint){
		
		MainApp app = (MainApp)application;
		
		// Quitar la palabra POINT
		String textoSustituido = wktPoint.replace("POINT (", "");
		
		// Quitar los parentesis de cierre
		textoSustituido = textoSustituido.replace( ")", "");
		
		Point puntoProyectado = null;
		
		// Obtener latitud, longitud
		String[] latLonTexto = textoSustituido.split(" ");
		if(latLonTexto != null){
			if(latLonTexto.length == 2){
				String longitud = latLonTexto[0];
				String latitud = latLonTexto[1];
				GeoPoint coordenadas = new GeoPoint();
				coordenadas.setLatitude(Double.parseDouble(latitud));
				coordenadas.setLongitude(Double.parseDouble(longitud));
				
				puntoProyectado = (Point) GeometryEngine.project(new Point(coordenadas.getLongitude(), coordenadas.getLatitude()), SpatialReference.create(102100));
			}
		}
		return puntoProyectado;
	}
	
	public static GeoPoint getGeoPuntoFromWKTPointField(String wktPoint){
		// Quitar la palabra POINT
		String textoSustituido = wktPoint.replace("POINT (", "");
		
		// Quitar los parentesis de cierre
		textoSustituido = textoSustituido.replace( ")", "");
		
		GeoPoint geoPunto = null;
		
		// Obtener latitud, longitud
		String[] latLonTexto = textoSustituido.split(" ");
		if(latLonTexto != null){
			if(latLonTexto.length == 2){
				String longitud = latLonTexto[0];
				String latitud = latLonTexto[1];
				geoPunto = new GeoPoint();
				geoPunto.setLatitude(Double.parseDouble(latitud));
				geoPunto.setLongitude(Double.parseDouble(longitud));
			}
		}
		return geoPunto;
	}
	
	
	
	
	public static Point toGeographic(Point pointProjected){
		double mercatorX_lon = pointProjected.getX();
		double mercatorY_lat = pointProjected.getY();
		
	    if (Math.abs(mercatorX_lon) < 180 && Math.abs(mercatorY_lat) < 90)
	        return pointProjected;

	    if ((Math.abs(mercatorX_lon) > 20037508.3427892) || (Math.abs(mercatorY_lat) > 20037508.3427892))
	        return pointProjected;

	    double x = mercatorX_lon;
	    double y = mercatorY_lat;
	    double num3 = x / 6378137.0;
	    double num4 = num3 * 57.295779513082323;
	    double num5 = Math.floor((double)((num4 + 180.0) / 360.0));
	    double num6 = num4 - (num5 * 360.0);
	    double num7 = 1.5707963267948966 - (2.0 * Math.atan(Math.exp((-1.0 * y) / 6378137.0)));
	    mercatorX_lon = num6;
	    mercatorY_lat = num7 * 57.295779513082323;
	    
	    Point pointGeographic = new Point(mercatorX_lon, mercatorY_lat);
	    return pointGeographic;
	}
	
}
