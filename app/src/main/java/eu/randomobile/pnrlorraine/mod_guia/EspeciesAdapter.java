package eu.randomobile.pnrlorraine.mod_guia;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.model.Especie;

public class EspeciesAdapter extends BaseAdapter {
	private Context context;
	private List<Especie> especies = null;

	public EspeciesAdapter(final Context c, final List<Especie> list) {
		this.especies = list;
		this.context = c;
	}

	public class ViewHolder {
		RelativeLayout layoutFondo;
		ImageView imgView;
		ImageView imgViewFrame;
		ImageView imgViwType;
		TextView lblTitulo;
		TextView lblType;
		TextView lblDescripcion;
		int index;
	}

	@Override
	public int getCount() {
		if (especies != null) {
			return especies.size();
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
		return Integer.valueOf(especies.get(position).getNid());
	}

	public String getItemNID(int position) {
		return especies.get(position).getNid();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = null;
		ViewHolder holder = null;

		try {
			if (convertView == null) {
				v = View.inflate(this.context, R.layout.item_lista_naturaleza_especies, null);
				holder = new ViewHolder();

				holder.layoutFondo = (RelativeLayout) v.findViewById(R.id.layoutFondo);
				holder.imgView = (ImageView) v.findViewById(R.id.imgView);
				holder.imgViewFrame = (ImageView) v.findViewById(R.id.imgViewFrame);
				holder.imgViwType = (ImageView) v.findViewById(R.id.imageViewType);
				holder.lblTitulo = (TextView) v.findViewById(R.id.lblTitulo);
				holder.lblDescripcion = (TextView) v.findViewById(R.id.lbldescripcionespecie);
				holder.lblType = (TextView) v.findViewById(R.id.lbltypeespecie);

				// Poner fuentes
				final Typeface tfScalaBold = Util.fontScala_Bold(context);
				final Typeface tfBentonBoo = Util.fontBenton_Boo(context);
				//holder.lblTitulo.setTypeface(tfScalaBold);
				//holder.lblDescripcion.setTypeface(tfBentonBoo);
				//holder.lblType.setTypeface(tfBentonBoo);

				v.setTag(holder);

			} else {
				v = convertView;
				holder = (ViewHolder) convertView.getTag();
			}

			final Especie item = this.especies.get(position);

			holder.lblTitulo.setText(item.getTitle());
			holder.lblType.setText(item.getTypeName());
			holder.lblDescripcion.setText(item.getBody());

			Context ctx = v.getContext();

			// Imagen
			if (item.getType()!=0) {
				/*Log.d("Adapter Especies",item.getImage());
				BitmapManager.INSTANCE.loadBitmap(item.getImage(),
						holder.imgView, 80, 60);*/
				switch (item.getType()){
					case 41: //Animal
						holder.imgViwType.setImageDrawable(ctx.getResources().getDrawable(R.drawable.icono_animal_mamifero));
						break;
					case 42: //Vegetal
						holder.imgViwType.setImageDrawable(ctx.getResources().getDrawable(R.drawable.icono_vegetal));
						break;
					default:
						holder.imgViwType.setImageDrawable(ctx.getResources().getDrawable(R.drawable.icono_vegetal));
				}

			}
			if (item.getImage() != null) {
				/*Log.d("Adapter Especies",item.getImage());
				BitmapManager.INSTANCE.loadBitmap(item.getImage(),
						holder.imgView, 80, 60);*/
				ImageLoader.getInstance().displayImage(item.getImage(), holder.imgView);
			} else {

				holder.imgView.setImageDrawable(ctx.getResources().getDrawable(R.drawable.no_picture_2));
			}

			if (position % 2 == 0) {
				holder.imgViewFrame.setBackgroundResource(R.drawable.mascara_rutas2);
			} else {
				holder.imgViewFrame.setBackgroundResource(R.drawable.mascara_rutas1);
			}


		} catch (Exception ex) {
		}
		return v;
	}
}
