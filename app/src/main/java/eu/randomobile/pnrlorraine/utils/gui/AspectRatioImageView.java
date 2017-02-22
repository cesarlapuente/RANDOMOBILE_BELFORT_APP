package eu.randomobile.pnrlorraine.utils.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AspectRatioImageView extends ImageView {

	public AspectRatioImageView(Context context) {
		super(context);
	}

	public AspectRatioImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AspectRatioImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		try {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			int width = MeasureSpec.getSize(widthMeasureSpec);
//			Drawable drawable = this.getDrawable();
			// int intrinsicHeight = drawable.getIntrinsicHeight();
			// int intrinsicWidth = drawable.getIntrinsicWidth();
			int height = width * getDrawable().getIntrinsicHeight()
					/ getDrawable().getIntrinsicWidth();
			setMeasuredDimension(width, height);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}