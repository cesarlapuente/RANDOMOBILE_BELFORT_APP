package eu.randomobile.pnrlorraine.mod_discover.list.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.environment.GPS;
import eu.randomobile.pnrlorraine.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.pnrlorraine.mod_global.model.Poi;

public class PoisAdapter extends BaseAdapter {
	private GPS gps = null;
	private Context context;
	private List<Poi> pois = null;

	public PoisAdapter(final Context c, final List<Poi> list, final GPS g) {
		this.pois = list;
		this.context = c;
		this.gps = g;


	}

	public class ViewHolder {
		RelativeLayout layoutFondo;
		ImageView imgView;
		TextView lblTitulo;
		TextView lblDetalle;
		ImageView imgViewFrame;
		ImageView imgViewCategory;
		TextView lblDistancia;
		TextView lblDistanciaNum;
		TextView lblDesnivel;
		TextView lblDesnivelNum;
		TextView lblValoracion;
		ImageView imgViewValoracion;
		int index;
	}

	@Override
	public int getCount() {
		if (pois != null) {
			return pois.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = null;
		ViewHolder holder = null;
		try {
			if (convertView == null) {
				v = View.inflate(this.context,
						R.layout.mod_discover__layout_item_lista_pois, null);
				holder = new ViewHolder();
				holder.layoutFondo = (RelativeLayout) v
						.findViewById(R.id.layoutFondo);
				holder.imgView = (ImageView) v.findViewById(R.id.imgView);
				holder.lblTitulo = (TextView) v.findViewById(R.id.lblTitulo);
				holder.lblDetalle = (TextView) v.findViewById(R.id.lblDetalle);
				holder.lblDistancia = (TextView) v.findViewById(R.id.lblDist);
				holder.lblDistanciaNum = (TextView) v
						.findViewById(R.id.lblDistNum);
				holder.lblDesnivel = (TextView) v
						.findViewById(R.id.lblDesnivel);
				holder.lblDesnivelNum = (TextView) v
						.findViewById(R.id.lblDesnivelNum);
				holder.imgViewFrame = (ImageView) v
						.findViewById(R.id.imgViewFrame);
				holder.imgViewCategory = (ImageView) v
						.findViewById(R.id.imgViewCategory);
				holder.lblValoracion = (TextView) v
						.findViewById(R.id.lblValoracion);
				holder.imgViewValoracion = (ImageView) v
						.findViewById(R.id.imgViewValoracion);

				// Poner fuentes
				final Typeface tfScalaBold = Util.fontScala_Bold(context);
				final Typeface tfBentonBoo = Util.fontBenton_Boo(context);
				holder.lblTitulo.setTypeface(tfScalaBold);
				holder.lblDetalle.setTypeface(tfBentonBoo);
				holder.lblDistancia.setTypeface(tfBentonBoo);
				holder.lblDistanciaNum.setTypeface(tfScalaBold);
				holder.lblDesnivel.setTypeface(tfBentonBoo);
				holder.lblValoracion.setTypeface(tfBentonBoo);
				holder.lblDesnivelNum.setTypeface(tfScalaBold);
				v.setTag(holder);
			} else {
				v = convertView;
				holder = (ViewHolder) convertView.getTag();
			}
			final Poi item = this.pois.get(position);
			holder.lblTitulo.setText(item.getTitle());
			// Body con el tipo de categoria
			if ((item.getCategory() != null) && !item.getCategory().getName().equals("null"))
		    {
				holder.lblDetalle.setText(item.getCategory().getName());
			} else {
				holder.lblDetalle.setText(context.getResources().getString(
						R.string.mod_global__sin_datos));
			}
			if (item.getDistanceMeters() < 1000) {
				int roundedDistMeters = (int) item.getDistanceMeters();
				holder.lblDistanciaNum.setText(roundedDistMeters + " m");
			} else {
				int roundedDistKms = (int) (item.getDistanceMeters() / 1000);
				holder.lblDistanciaNum.setText(roundedDistKms + " Km");
			}
			String valString = context.getResources().getString(
					R.string.mod_discover__nota);
			holder.lblValoracion.setText(valString + " ("
					+ String.valueOf(item.getVote().getNumVotes()) +" "+ context.getResources().getString(R.string.votos)+")");

			// Imagen
			if (item.getMainImage() != null) {
				BitmapManager.INSTANCE.loadBitmap(item.getMainImage(),
						holder.imgView, 80, 60);
			} else {
				Context ctx = v.getContext();
				holder.imgView.setImageDrawable(ctx.getResources().getDrawable(R.drawable.no_picture_2));
				
//				if (item.getCategory() != null
//						&& item.getCategory().getIcon() != null) {
//					// BitmapManager.INSTANCE.loadBitmap(item.getCategory().getIcon(),
//					// holder.imgView, 36, 40);
//					String category;
//					category = item.getCategory().getName();
//					if (category.equals("Chambre d'hôtes")
//							|| category.equals("Hôtellerie")
//							|| category.equals("Hébergement collectif")
//							|| category.equals("Hôtellerie de plein air")
//							|| category.equals("Meublé")
//							|| category.equals("Résidence")) {
//						holder.imgView.setImageResource(R.drawable.icono_hotel);
//					} else if (category.equals("Musée")
//							|| category.equals("Patrimoine Naturel")
//							|| category.equals("Site et Monument")
//							|| category.equals("Office de Tourisme")
//							|| category.equals("Parc et Jardin")) {
//						holder.imgView
//								.setImageResource(R.drawable.icono_descubrir);
//					} else if (category.equals("Restauration"))
//						holder.imgView
//								.setImageResource(R.drawable.icono_restaurante);
//
//				} else {
//					holder.imgView.setImageResource(R.drawable.ic_launcher);
//				}
			}
			if (position % 2 == 0) {
				holder.imgViewFrame.setBackgroundResource(R.drawable.frame_pr);
			} else {
				holder.imgViewFrame.setBackgroundResource(R.drawable.frame_gr);
			}
			// Poner la valoraci—n
			if (item.getVote() != null) {
				int idDrawable = -1;
				if (item.getVote().getValue() <= 0) {
					// Si es menor o igual a 0
					idDrawable = R.drawable.puntuacion_0_estrellas;
				} else if (item.getVote().getValue() > 0
						&& item.getVote().getValue() < 25) {
					// Si est‡ entre 1 y 24
					idDrawable = R.drawable.puntuacion_1_estrellas;
				} else if (item.getVote().getValue() >= 25
						&& item.getVote().getValue() < 50) {
					// Si est‡ entre 25 y 49
					idDrawable = R.drawable.puntuacion_2_estrellas;
				} else if (item.getVote().getValue() >= 50
						&& item.getVote().getValue() < 75) {
					// Si est‡ entre 50 y 74
					idDrawable = R.drawable.puntuacion_3_estrellas;
				} else if (item.getVote().getValue() >= 75
						&& item.getVote().getValue() <= 90) {
					// Si est‡ entre 75 y 90
					idDrawable = R.drawable.puntuacion_4_estrellas;
				} else {
					idDrawable = R.drawable.puntuacion_5_estrellas;
				}
				if (idDrawable != -1) {
					holder.imgViewValoracion.setImageResource(idDrawable);
				}
			} else {
				holder.imgViewValoracion
						.setImageResource(R.drawable.puntuacion_0_estrellas);
			}

			// Poner la categoria
			// Poner la imagen de categoria
			String category;
			if (item.getCategory() != null) {
				category = item.getCategory().getName();
				if (category.equals("Chambre d'hôtes")
						|| category.equals("Hôtellerie")
						|| category.equals("Hébergement collectif")
						|| category.equals("Hôtellerie de plein air")
						|| category.equals("Meublé")
						|| category.equals("Résidence")) {
					holder.imgViewCategory
							.setBackgroundResource(R.drawable.icono_hotel);
				} else if (category.equals("Musée")
						|| category.equals("Patrimoine Naturel")
						|| category.equals("Site et Monument")
						|| category.equals("Office de Tourisme")
						|| category.equals("Parc et Jardin")) {
					holder.imgViewCategory
							.setBackgroundResource(R.drawable.icono_descubrir);
				} else if (category.equals("Restauration")) {
					holder.imgViewCategory
							.setBackgroundResource(R.drawable.icono_restaurante);
				}
			}
			// Desnivel
			int desnivel = 0;
			if (gps != null) {
				if (gps.getLastLocation() != null) {
					desnivel = (int) (item.getCoordinates().getAltitude() - gps
							.getLastLocation().getAltitude());
				}
			}
			String strDesnivel = "";
			if (desnivel > 0) {
				strDesnivel = "+" + desnivel + " m.";
			} else if (desnivel < 0) {
				strDesnivel = "-" + desnivel + " m.";
			}
			holder.lblDesnivelNum.setText(strDesnivel);
		} catch (Exception ex) {
		}
		return v;
	}
}
