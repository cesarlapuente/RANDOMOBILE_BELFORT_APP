package eu.randomobile.pnrlorraine.mod_events.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import eu.randomobile.pnrlorraine.R;
import eu.randomobile.pnrlorraine.mod_global.Util;
import eu.randomobile.pnrlorraine.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.pnrlorraine.mod_global.model.Event;

public class EventsAdapter extends BaseAdapter {
	private Context context;
	private List<Event> events = null;

	public EventsAdapter(final Context c, final List<Event> list) {
		this.events = list;
		this.context = c;
	}

	@Override
	public int getCount() {
		if (events != null) {
			return events.size();
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
		try {
			Typeface tfScalaBold = Util.fontScala_Bold(context);
			Typeface tfBentonBoo = Util.fontBenton_Boo(context);
			if (convertView == null) {
				v = View.inflate(this.context,
						R.layout.mod_events__layout_item_lista_eventos, null);
			} else {
				v = convertView;
			}
			final Event event = this.events.get(position);
			TextView txt = (TextView) v.findViewById(R.id.lblTitulo);
			txt.setTypeface(tfScalaBold);
			txt.setText(event.getTitle());
			txt = (TextView) v.findViewById(R.id.lblDetalle);
			txt.setTypeface(tfBentonBoo);
			txt.setText(event.getBody());
			// Imagen
			if (event.getMainImage() != null) {
				ImageView imgView = (ImageView) v.findViewById(R.id.imgView);
				BitmapManager.INSTANCE.loadBitmap(event.getMainImage(),
						imgView, 80, 60);
			}
			ImageView imgView = (ImageView) v.findViewById(R.id.imgViewFrame);
			if (position % 2 == 0) {
				imgView.setBackgroundResource(R.drawable.frame_pr);
			} else {
				imgView.setBackgroundResource(R.drawable.frame_gr);
			}
		} catch (Exception ex) {
		}
		return v;
	}
}
