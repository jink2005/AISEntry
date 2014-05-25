package cn.aiseminar.aisentry.reader;

import cn.aiseminar.aisentry.*;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.GridView;

public class BookShelfGridView extends GridView {
	private Bitmap mBackgroundImage;

	public BookShelfGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mBackgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.bookshelf_layer_center);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		int count = getChildCount();
		int top = count > 0 ? getChildAt(0).getTop() : 0;
		int backgroundWidth = mBackgroundImage.getWidth();
		int backgroundHeight = mBackgroundImage.getHeight();
		int width = getWidth();
		int height = getHeight();
		
		for (int y = top; y < height; y += backgroundHeight)
		{
			for (int x = 0; x < width; x += backgroundWidth)
			{
				canvas.drawBitmap(mBackgroundImage, x, y, null);
			}
		}
		
		super.dispatchDraw(canvas);
	}

}
