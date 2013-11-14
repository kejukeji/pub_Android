package com.keju.maomao.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

/**
 * 图片处理工具类
 * 
 * @author Zhoujun
 * 
 */
public class ImageUtil {

	/**
	 * drawable 转换成 bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth(); // 取 drawable 的长宽
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565; // 取 drawable 的颜色格式
		Bitmap bitmap = Bitmap.createBitmap(width, height, config); // 建立对应
																	// bitmap
		Canvas canvas = new Canvas(bitmap); // 建立对应 bitmap 的画布
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas); // 把 drawable 内容画到画布中
		return bitmap;
	}

	/**
	 * 图片加上圆角效果
	 * 
	 * @param drawable
	 *            需要处理的图片
	 * @param percent
	 *            圆角比例大小
	 * @return
	 */
	public static Bitmap getRoundCornerBitmapWithPic(Drawable drawable, float percent) {
		Bitmap bitmap = drawableToBitmap(drawable);
		return getRoundedCornerBitmapWithPic(bitmap, percent);
	}

	/**
	 * 图片加上圆角效果
	 * 
	 * @param bitmap
	 *            要处理的位图
	 * @param roundPx
	 *            圆角大小
	 * @return 返回处理后的位图
	 */
	public static Bitmap getRoundedCornerBitmapWithPic(Bitmap bitmap, float percent) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, bitmap.getWidth() * percent, bitmap.getHeight() * percent, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 把文本中的表情文字转换成表情图片（图片来自资源文件）
	 * 
	 * @param emotionMap
	 *            表情图片资源文件的Map〈String, Integer〉
	 * @param wbTxt
	 *            整个文本
	 * @param context
	 * @return
	 */
	public static SpannableString changeTextToEmotions(Map<String, Integer> emotionMap, String wbTxt, Context context) {
		SpannableString spann = new SpannableString(wbTxt);
		if (wbTxt != null && !"".equals(wbTxt)) {
			for (Entry<String, Integer> entry : emotionMap.entrySet()) {
				int res = entry.getValue();
				String key = entry.getKey();
				int begin = 0;
				int starts = 0;
				int end = 0;
				while (wbTxt.indexOf(key, begin) != -1) {
					Bitmap bitmap = ImageUtil.getBitMapByRes(context, res);
					Drawable drawable = new BitmapDrawable(bitmap);
					if (drawable != null) {
						drawable.setBounds(5, 5, 30, 30);
						ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);

						starts = wbTxt.indexOf(key, begin);
						end = starts + key.length();
						spann.setSpan(span, starts, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
						begin = starts + 1;
					}
				}
			}

		}
		return spann;
	}

	/**
	 * 获得某个图片资源的BitMap对象
	 * 
	 * @param context
	 * @param drawableId
	 * @return
	 */
	public static Bitmap getBitMapByRes(Context context, int drawableId) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), drawableId, opts);
		opts.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId, opts);
		return bitmap;
	}
	/**
	 * 头像文件名
	 * 
	 * @param sid
	 * @return
	 */
	public static String createAvatarFileName(String sid) {
		return "avatar_" + sid + ".jpg";
	}

	/**
	 * 用当前时间给取得的图片命名
	 * 
	 */
	public static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmsss");
		return dateFormat.format(date) + ".jpg";
	}

}
