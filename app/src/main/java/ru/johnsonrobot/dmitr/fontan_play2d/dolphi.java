package ru.johnsonrobot.dmitr.fontan_play2d;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by user on 10.02.17.
 */

public class dolphi {

    //алгоритм отображения спрайта не самый лучший
    //в дальнейшем переделать на массив из двух спрайтов и отображать нужную картинку по
    //индексу массива
    private Bitmap dolphin;
    private Bitmap refDolphin;
    private Bitmap viewFish;
    private float sX, sY;
    private float vX, vY;
    private Paint paint;
    private Matrix mat;
    private boolean flip;
    private int dx,dy;

    //Конструктор класса
    public dolphi(Resources res, int idBitmap, float X, float Y)
    {
        dolphin = BitmapFactory.decodeResource(res, idBitmap);
        sX = X;
        sY = Y;
        dx = 1;
        dy = 1;
        paint = new Paint();
        mat = new Matrix();
        mat.preScale(-1, 1);
        int w = dolphin.getWidth();
        int h = dolphin.getHeight();
        refDolphin = Bitmap.createBitmap(dolphin, 0,0,w,h,mat,false);
        //mat.postScale(1.0f, 1.0f);
        //mat.postTranslate(X,Y);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        flip = false;
        viewFish = dolphin;
        vX = 5;
        vY = 1;

    }

    public void setX(float X) {sX = X;}

    public void setY(float Y) {sY = Y;}

    public float getsX() {return sX;}

    public float getsY() {return sY;}

    public void update(Canvas c)
    {
        int w = c.getWidth();
        int h = c.getHeight();

        if (((sX + viewFish.getWidth()) >= w) || (sX <= 0.0f)) { flip = !flip; dx = -dx;}
        if (((sY + viewFish.getHeight())>= h) || (sY <= 0.0f)) { dy = -dy;}

        sX = sX + dx*vX;
        sY = sY + dy*vY;

        if ( flip )
            viewFish = refDolphin;
        else
            viewFish = dolphin;


    }

    public void onDraw(Canvas c)
    {
        c.drawBitmap(viewFish, sX, sY, paint);
    }
}
