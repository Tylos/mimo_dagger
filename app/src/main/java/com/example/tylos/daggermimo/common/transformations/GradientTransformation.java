package com.example.tylos.daggermimo.common.transformations;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

/**
 * Created by tylos on 17/3/15.
 */
public class GradientTransformation implements Transformation {
    final int startingColor;
    final int endColor;
    final Paint paint;

    LinearGradient grad;

    public GradientTransformation(int startingColor, int endColor) {
        this.startingColor = startingColor;
        this.endColor = endColor;
        paint = new Paint();
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        Bitmap gradientable = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(gradientable);

        /* Create your gradient. */
        grad = new LinearGradient(0, 0, 0, bitmap.getHeight(), startingColor, endColor, Shader.TileMode.CLAMP);

        /* Draw your gradient to the top of your bitmap. */

        paint.setStyle(Paint.Style.FILL);
        paint.setShader(grad);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
        bitmap.recycle();
        return gradientable;
    }

    @Override
    public String key() {
        return "gradient" + startingColor + "_" + endColor;
    }
}
